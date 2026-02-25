# Loads .env into current PowerShell session (Process scope)
if (-Not (Test-Path -Path .env)) { Write-Error ".env not found"; exit 1 }
Get-Content .env | Where-Object { $_ -and -not $_.StartsWith("#") } | ForEach-Object {
  $parts = $_ -split '=', 2
  $name = $parts[0].Trim()
  $value = if ($parts.Length -gt 1) { $parts[1] } else { '' }
  [System.Environment]::SetEnvironmentVariable($name, $value, 'Process')
}
Write-Host "Loaded .env into process environment"
