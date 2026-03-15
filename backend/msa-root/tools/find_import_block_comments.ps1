# Find Java files that contain line comments between import statements
# Usage (from project root in cmd.exe):
#   powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\find_import_block_comments.ps1

$root = Get-Location
Get-ChildItem -Path $root -Recurse -Include *.java | ForEach-Object {
    $path = $_.FullName
    try {
        $lines = Get-Content -LiteralPath $path -ErrorAction Stop
    } catch {
        return
    }
    $importIndices = @()
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^[\s]*import\b') {
            $importIndices += $i
        }
    }
    if ($importIndices.Count -gt 0) {
        $first = $importIndices[0]
        $last = $importIndices[-1]
        $found = $false
        for ($j = $first + 1; $j -lt $last; $j++) {
            if ($lines[$j] -match '^[\s]*//') {
                if (-not $found) {
                    Write-Output "Problematic file: $path"
                    $found = $true
                }
                $linenum = $j + 1
                Write-Output "  Line $linenum : $($lines[$j].Trim())"
            }
        }
        if ($found) { Write-Output "" }
    }
}
