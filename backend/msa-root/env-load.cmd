@echo off
REM Loads key=value pairs from .env into current cmd session (ignores comments and blank lines)
if not exist .env (
  echo .env not found
  exit /b 1
)
for /f "usebackq tokens=* delims=" %%a in (`type .env ^| findstr /r /v "^#"`) do (
  for /f "tokens=1* delims==" %%b in ("%%a") do (
    set "%%b=%%c"
  )
)
echo Loaded environment variables from .env
pause
