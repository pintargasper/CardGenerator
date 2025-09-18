# ============================
# Project settings
# ============================
$projectRoot       = "..\"
$packageOutputDir  = "$PSScriptRoot\output\windows"
$deployDir         = "$packageOutputDir\deploy"
$customJreDir      = "$packageOutputDir\custom-runtime"
$appImageDir       = "$packageOutputDir"
$jarFile           = "CardGenerator-1.0.0-jar-with-dependencies.jar"
$mainClass         = "com.gasperpintar.cardgenerator.Launcher"
$appName           = "Card Generator"
$appVersion        = "1.0.0"
$vendor            = "Gašper Pintar"
$iconPath          = "$projectRoot\src\main\resources\com\gasperpintar\cardgenerator\images\logo.ico"
$javafxJmodsPath   = "$projectRoot\dev\java\windows\javafx\javafx-jmods-21.0.8"
$jdkPath           = "$projectRoot\dev\java\windows\jdk-21"
$javafxLibPath     = "$projectRoot\dev\java\windows\javafx\javafx-sdk-21.0.8\lib"

# ============================
# Clean package-output folder
# ============================
if (Test-Path $packageOutputDir) { Remove-Item $packageOutputDir -Recurse -Force }

# Create only the deploy folder, because we copy the fat JAR there
New-Item -ItemType Directory -Path $deployDir | Out-Null

# ============================
# Copy the fat JAR
# ============================
Copy-Item "$projectRoot\target\$jarFile" -Destination $deployDir

# ============================
# Create a minimal custom JRE with jlink
# ============================
$javafxModules = "javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.media,javafx.web"

jlink `
  --module-path "$jdkPath\jmods;$javafxJmodsPath" `
  --add-modules java.base,java.desktop,java.logging,java.sql,java.naming,java.management,$javafxModules `
  --output $customJreDir `
  --compress=zip-6 `
  --strip-debug `
  --no-header-files `
  --no-man-pages

# ============================
# Copy all JavaFX DLL files
# ============================
Get-ChildItem -Path "$javafxLibPath" -Filter "*.dll" | ForEach-Object {
    Copy-Item $_.FullName -Destination "$customJreDir\bin"
}

# ============================
# Create app-image with jpackage
# ============================
jpackage `
  --type app-image `
  --input $deployDir `
  --main-jar $jarFile `
  --main-class $mainClass `
  --name $appName `
  --app-version $appVersion `
  --vendor $vendor `
  --icon $iconPath `
  --runtime-image $customJreDir `
  --dest $appImageDir `
  --description "Card Generator allows the creation of any card and also its generation in png format. It also allows downloading images in pdf mode" `
  --copyright "(C) 2025 Gasper Pintar. All rights reserved"

# ============================
# Debug command
# ============================
Write-Host "For debugging JavaFX rendering, use the following command:"
Write-Host "& `"$customJreDir\bin\java.exe`" `"-Dprism.verbose=true`" `"-jar`" `"$deployDir\$jarFile`""
