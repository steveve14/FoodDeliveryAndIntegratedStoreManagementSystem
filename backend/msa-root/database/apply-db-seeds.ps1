#!/usr/bin/env pwsh
# apply-db-seeds.ps1 — DB 시드 데이터 순차 적용 스크립트
#
# 사용법:
#   .\apply-db-seeds.ps1                          # 기본값 사용
#   .\apply-db-seeds.ps1 -Host 192.168.0.25 -Port 6000 -User postgres -Password 1234
#
# 전제조건: psql (PostgreSQL client) 이 PATH에 있어야 합니다.

param(
    [string]$Host     = "192.168.0.25",
    [string]$Port     = "6000",
    [string]$User     = "postgres",
    [string]$Password = "1234"
)

$ErrorActionPreference = "Stop"
$ScriptDir = $PSScriptRoot

$env:PGPASSWORD = $Password

Write-Host "=== FoodDelivery DB 시드 적용 시작 ===" -ForegroundColor Cyan
Write-Host "Host: $Host:$Port / User: $User" -ForegroundColor Gray

# 시드 파일 순서 정의 (07~11: 데이터 시드)
$SeedFiles = @(
    @{ File = "07_remote_db_user_seed.sql";     DB = "db_user"     },
    @{ File = "08_remote_db_store_seed.sql";    DB = "db_store"    },
    @{ File = "09_remote_db_order_seed.sql";    DB = "db_order"    },
    @{ File = "10_remote_db_delivery_seed.sql"; DB = "db_delivery" },
    @{ File = "11_remote_db_event_seed.sql";    DB = "db_event"    }
)

$SuccessCount = 0
$FailCount    = 0

foreach ($seed in $SeedFiles) {
    $FilePath = Join-Path $ScriptDir $seed.File
    if (-not (Test-Path $FilePath)) {
        Write-Warning "파일 없음: $FilePath — 건너뜁니다."
        $FailCount++
        continue
    }

    Write-Host "`n[→] $($seed.File) → $($seed.DB)..." -NoNewline
    try {
        $result = & psql -h $Host -p $Port -U $User -d $seed.DB -f $FilePath 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host " FAILED" -ForegroundColor Red
            Write-Host $result -ForegroundColor DarkRed
            $FailCount++
        } else {
            Write-Host " OK" -ForegroundColor Green
            $SuccessCount++
        }
    } catch {
        Write-Host " ERROR: $_" -ForegroundColor Red
        $FailCount++
    }
}

Write-Host "`n=== 결과: 성공 $SuccessCount / 실패 $FailCount ===" -ForegroundColor $(if ($FailCount -eq 0) { "Green" } else { "Yellow" })

if ($FailCount -gt 0) {
    Write-Host "일부 시드 적용에 실패했습니다. 오류 내용을 확인하세요." -ForegroundColor Yellow
    exit 1
}
Write-Host "모든 시드 데이터 적용 완료!" -ForegroundColor Green
