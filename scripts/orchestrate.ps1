$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $RootDir

function Run-Step {
    param(
        [string] $Title,
        [scriptblock] $Command
    )

    Write-Host ""
    Write-Host "==> $Title"
    & $Command
}

function Run-Maven {
    param([Parameter(ValueFromRemainingArguments = $true)] [string[]] $MavenArgs)

    if (Test-Path ".\mvnw.cmd") {
        & ".\mvnw.cmd" @MavenArgs
    } elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
        & mvn @MavenArgs
    } else {
        throw "Maven not found. Install Maven or use the Maven Wrapper."
    }
}

Write-Host "RC6 Secure P2P orchestrator (Windows)"
Write-Host "Project root: $RootDir"

Run-Step "Install Git hooks" { & powershell -ExecutionPolicy Bypass -File ".\scripts\setup-git-hooks.ps1" }
Run-Step "Normalize final newlines" { & powershell -ExecutionPolicy Bypass -File ".\scripts\fix-eof.ps1" }
Run-Step "Apply Java formatting" { Run-Maven spotless:apply --batch-mode --no-transfer-progress }
Run-Step "Check formatting" { Run-Maven spotless:check --batch-mode --no-transfer-progress }
Run-Step "Compile all modules" { Run-Maven compile --batch-mode --no-transfer-progress }
Run-Step "Run tests" { Run-Maven test --batch-mode --no-transfer-progress }
Run-Step "Package application" { Run-Maven package -DskipTests --batch-mode --no-transfer-progress }

Write-Host ""
Write-Host "Orchestration completed successfully."
