param(
  [string]$WorkspaceRoot = "C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem",
  [string]$GatewayTokenSecret = "c2lsdmVyLTI1Ni1iaXQtc2VjcmV0LWtleS1mb3ItjwtlbmNvZGluZy1leGFtcGxlCg==",
  [int]$FrontendPort = 3100,
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

$startedProcesses = @()

try {
  $msaRoot = Join-Path $WorkspaceRoot 'backend/msa-root'
  $webUserRoot = Join-Path $WorkspaceRoot 'frontend/web-user'

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

  Write-Host "[start] web-user on $FrontendPort"
  $startedProcesses += Start-Frontend -FrontendDir $webUserRoot -Port $FrontendPort

  $frontendDeadline = (Get-Date).AddSeconds(180)
  $frontendReady = $false
  while ((Get-Date) -lt $frontendDeadline) {
    try {
      $response = Invoke-WebRequest -Uri "http://localhost:$FrontendPort" -UseBasicParsing -TimeoutSec 5
      if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
        $frontendReady = $true
        break
      }
    } catch {
      Start-Sleep -Seconds 2
    }
  }

  if (-not $frontendReady) {
    throw "Frontend did not become ready on port $FrontendPort"
  }

  Write-Host '[run] pnpm e2e'
  Push-Location $webUserRoot
  try {
    pnpm e2e
    if ($LASTEXITCODE -ne 0) {
      throw "pnpm e2e failed with exit code $LASTEXITCODE"
    }
  } finally {
    Pop-Location
  }

  Write-Host '[done] web-user E2E passed'
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
