Write-Host "Fixing missing final newlines..."

$rootDir = git rev-parse --show-toplevel
$extensions = @("*.java", "*.xml", "*.md", "*.properties", "*.txt", "*.yml", "*.yaml", "*.sh", "*.ps1")
$excludeDirs = @(".git", ".idea", "target")
$fixed = 0

foreach ($ext in $extensions) {
    $files = Get-ChildItem -Path $rootDir -Filter $ext -Recurse -File -ErrorAction SilentlyContinue |
        Where-Object {
            $path = $_.FullName
            $exclude = $false
            foreach ($dir in $excludeDirs) {
                if ($path -match [regex]::Escape([IO.Path]::DirectorySeparatorChar + $dir + [IO.Path]::DirectorySeparatorChar)) {
                    $exclude = $true
                    break
                }
            }
            -not $exclude
        }

    foreach ($file in $files) {
        $length = $file.Length
        if ($length -eq 0) {
            [System.IO.File]::WriteAllText($file.FullName, "`n")
            Write-Host "  fixed (empty): $($file.FullName)"
            $fixed++
        } else {
            $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
            $lastByte = $bytes[$bytes.Length - 1]
            if ($lastByte -ne 10) {
                [System.IO.File]::AppendAllText($file.FullName, "`n")
                Write-Host "  fixed: $($file.FullName)"
                $fixed++
            }
        }
    }
}

$gitignoreFile = Join-Path $rootDir ".gitignore"
if (Test-Path $gitignoreFile) {
    $bytes = [System.IO.File]::ReadAllBytes($gitignoreFile)
    if ($bytes.Length -eq 0) {
        [System.IO.File]::WriteAllText($gitignoreFile, "`n")
        Write-Host "  fixed (empty): $gitignoreFile"
        $fixed++
    } elseif ($bytes[$bytes.Length - 1] -ne 10) {
        [System.IO.File]::AppendAllText($gitignoreFile, "`n")
        Write-Host "  fixed: $gitignoreFile"
        $fixed++
    }
}

$editorconfigFile = Join-Path $rootDir ".editorconfig"
if (Test-Path $editorconfigFile) {
    $bytes = [System.IO.File]::ReadAllBytes($editorconfigFile)
    if ($bytes.Length -eq 0) {
        [System.IO.File]::WriteAllText($editorconfigFile, "`n")
        Write-Host "  fixed (empty): $editorconfigFile"
        $fixed++
    } elseif ($bytes[$bytes.Length - 1] -ne 10) {
        [System.IO.File]::AppendAllText($editorconfigFile, "`n")
        Write-Host "  fixed: $editorconfigFile"
        $fixed++
    }
}

Write-Host "Done. Fixed $fixed file(s)."
