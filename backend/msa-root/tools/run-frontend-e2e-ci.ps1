param(
  [string]$WorkspaceRoot = "C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem",
  [string]$GatewayTokenSecret = "c2lsdmVyLTI1Ni1iaXQtc2VjcmV0LWtleS1mb3ItjwtlbmNvZGluZy1leGFtcGxlCg==",
  [string[]]$Apps = @('web-user', 'web-shop', 'web-admin'),
  [switch]$NoCleanup
)

$ErrorActionPreference = 'Stop'

function Wait-Port {
  param(
    [int]$Port,
    [int]$TimeoutSec = 240
  )

  $deadline = (Get-Date).AddSeconds($TimeoutSec)
  while ((Get-Date) -lt $deadline) {
    $listening = Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue
    if ($listening) {
      Write-Host "[ready] port $Port"
      return
    }
    Start-Sleep -Seconds 2
  }

  throw "Timeout waiting for port $Port"
}

function Wait-Http {
  param(
    [int]$Port,
    [int]$TimeoutSec = 180
  )

  $deadline = (Get-Date).AddSeconds($TimeoutSec)
  while ((Get-Date) -lt $deadline) {
    try {
      $response = Invoke-WebRequest -Uri "http://localhost:$Port" -UseBasicParsing -TimeoutSec 5
      if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
        Write-Host "[ready] http://localhost:$Port"
        return
      }
    } catch {
      Start-Sleep -Seconds 2
    }
  }

  throw "Timeout waiting for frontend http://localhost:$Port"
}

function Start-GradleService {
  param(
    [string]$ServiceTask,
    [string]$WorkingDir,
    [string]$PreCommand = ''
  )

  $command = if ([string]::IsNullOrWhiteSpace($PreCommand)) {
    "Set-Location '$WorkingDir'; .\gradlew.bat $ServiceTask"
  } else {
    "$PreCommand; Set-Location '$WorkingDir'; .\gradlew.bat $ServiceTask"
  }

  return Start-Process powershell -ArgumentList '-NoExit', '-Command', $command -PassThru -WindowStyle Minimized
}

function Start-Frontend {
  param(
    [string]$FrontendDir,
    [int]$Port
  )

  $command = "Set-Location '$FrontendDir'; pnpm dev --port $Port"
  return Start-Process powershell -ArgumentList '-NoExit', '-Command', $command -PassThru -WindowStyle Minimized
}

$portMap = @{
  'web-user' = 3100
  'web-shop' = 3010
  'web-admin' = 3200
}

$startedProcesses = @()

try {
  $msaRoot = Join-Path $WorkspaceRoot 'backend/msa-root'
  $frontendRoot = Join-Path $WorkspaceRoot 'frontend'

  Write-Host '[start] service-discovery'
  $startedProcesses += Start-GradleService -ServiceTask ':service-discovery:bootRun' -WorkingDir $msaRoot
  Wait-Port -Port 8100

  Write-Host '[start] service-auth'
  $startedProcesses += Start-GradleService -ServiceTask ':service-auth:bootRun' -WorkingDir $msaRoot
  Wait-Port -Port 7000

  Write-Host '[start] service-user'
  $startedProcesses += Start-GradleService -ServiceTask ':service-user:bootRun' -WorkingDir $msaRoot
  Wait-Port -Port 8010

  Write-Host '[start] service-store'
  $startedProcesses += Start-GradleService -ServiceTask ':service-store:bootRun' -WorkingDir $msaRoot
  Wait-Port -Port 8020

  Write-Host '[start] service-order'
  $startedProcesses += Start-GradleService -ServiceTask ':service-order:bootRun' -WorkingDir $msaRoot
  Wait-Port -Port 8040

  Write-Host '[start] service-gateway with TOKEN_SECRET'
  $secretCommand = "`$env:TOKEN_SECRET='$GatewayTokenSecret'"
  $startedProcesses += Start-GradleService -ServiceTask ':service-gateway:bootRun' -WorkingDir $msaRoot -PreCommand $secretCommand
  Wait-Port -Port 8000

  foreach ($app in $Apps) {
    if (-not $portMap.ContainsKey($app)) {
      throw "Unsupported app '$app'. Supported: web-user, web-shop, web-admin"
    }

    $port = [int]$portMap[$app]
    $appRoot = Join-Path $frontendRoot $app

    Write-Host "[start] $app on $port"
    $startedProcesses += Start-Frontend -FrontendDir $appRoot -Port $port
    Wait-Http -Port $port

    Write-Host "[run] $app pnpm e2e"
    Push-Location $appRoot
    try {
      pnpm e2e
      if ($LASTEXITCODE -ne 0) {
        throw "$app e2e failed with exit code $LASTEXITCODE"
      }
    } finally {
      Pop-Location
    }

    Write-Host "[done] $app e2e passed"
  }
} finally {
  if (-not $NoCleanup) {
    foreach ($process in $startedProcesses) {
      if ($process -and -not $process.HasExited) {
        Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
      }
    }
    Write-Host '[cleanup] started processes stopped'
  } else {
    Write-Host '[cleanup] skipped by -NoCleanup'
  }
}
