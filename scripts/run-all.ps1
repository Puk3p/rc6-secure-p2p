$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $RootDir

powershell -ExecutionPolicy Bypass -File ".\scripts\orchestrate.ps1"

Write-Host ""
Write-Host "Ready to run the local 3-node demo."
Write-Host "Open three separate PowerShell terminals and start the nodes in this order:"
Write-Host ""
Write-Host "  Terminal 1: java -jar node-app\target\node-app.jar node-configs\node-a.properties"
Write-Host "  Terminal 2: java -jar node-app\target\node-app.jar node-configs\node-b.properties"
Write-Host "  Terminal 3: java -jar node-app\target\node-app.jar node-configs\node-c.properties"
Write-Host ""
Write-Host "Then try from node-a:"
Write-Host "  peers"
Write-Host "  send node-b Salut de la node-a"
