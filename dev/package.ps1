# ============================
# Project settings (portable, no fixed absolute paths)
# ============================
$projectRoot       = Resolve-Path "$PSScriptRoot\.." | Select-Object -ExpandProperty Path  # project root = parent of build-tools
$packageOutputDir  = "$PSScriptRoot\output\windows"           # all generated files will be here
$deployDir         = "$packageOutputDir\deploy"               # fat JAR
$customJreDir      = "$packageOutputDir\custom-runtime"       # minimal JRE
$appImageDir       = "$packageOutputDir"                      # app-image with exe
$jarFile           = "CardGenerator-1.2.0-alpha+1.jar"
$mainClass         = "com.gasperpintar.cardgenerator.Launcher"
$appName           = "Card Generator"
$appVersion        = "1.2.0"
$vendor            = "Gašper Pintar"
$iconPath          = "$projectRoot\src\main\resources\com\gasperpintar\cardgenerator\images\logo.ico"
$imagePath         = "$projectRoot\src\main\resources\com\gasperpintar\cardgenerator\images\logo.bmp"
$javafxJmodsPath   = "$PSScriptRoot\java\windows\javafx-jmods-25.0.1"
$jdkPath           = "$PSScriptRoot\java\windows\jdk-25.0.1"
$javafxLibPath     = "$PSScriptRoot\java\windows\javafx-sdk-25.0.1\lib"

# ============================
# Pre-checks for required files and folders (MUST BE FIRST!)
# ============================
if (!(Test-Path "$projectRoot\target\$jarFile")) {
    Write-Host "ERROR: JAR file does not exist: $projectRoot\target\$jarFile" -ForegroundColor Red
    Write-Host "First build the project with: mvn package" -ForegroundColor Yellow
    exit 1
}
if (!(Test-Path $javafxJmodsPath)) {
    Write-Host "ERROR: JavaFX jmods folder does not exist: $javafxJmodsPath" -ForegroundColor Red
    Write-Host "Check that JavaFX jmods are correctly installed." -ForegroundColor Yellow
    exit 1
}
if (!(Test-Path $javafxLibPath)) {
    Write-Host "ERROR: JavaFX lib folder does not exist: $javafxLibPath" -ForegroundColor Red
    Write-Host "Check that JavaFX SDK is correctly installed." -ForegroundColor Yellow
    exit 1
}

# ============================
# Clean package-output folder
# ============================
if (Test-Path $packageOutputDir) { Remove-Item $packageOutputDir -Recurse -Force }

# Create only the deploy folder, because we copy the fat JAR there
New-Item -ItemType Directory -Path $deployDir -Force | Out-Null

# ============================
# Copy the fat JAR
# ============================
Copy-Item "$projectRoot\target\$jarFile" -Destination $deployDir

# ============================
# Create a minimal custom JRE with jlink
# ============================
$javafxModules = "javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.media,javafx.web"

& "$jdkPath\bin\jlink.exe" `
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
# Create bin folder if it does not exist
if (!(Test-Path "$customJreDir\bin")) { New-Item -ItemType Directory -Path "$customJreDir\bin" -Force | Out-Null }
Get-ChildItem -Path "$javafxLibPath" -Filter "*.dll" | ForEach-Object {
    Copy-Item $_.FullName -Destination "$customJreDir\bin"
}

# ============================
# Create app-image with jpackage
# ============================
& "$jdkPath\bin\jpackage.exe" `
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
  --description "Card Generator is a desktop application for creating and generating custom playing cards. Using Excel spreadsheets and customizable fxml templates, you can quickly create entire decks of playing cards without having to design each card individually" `
  --copyright "(C) 2025 Gasper Pintar"

# ============================
# Copy app-icon
# ============================
# Create icon folder if it does not exist
if (!(Test-Path "$packageOutputDir\$appName")) { New-Item -ItemType Directory -Path "$packageOutputDir\$appName" -Force | Out-Null }
Copy-Item "$imagePath" -Destination "$packageOutputDir\$appName\icon.bmp"

# ============================
# Debug command
# ============================
Write-Host "For debugging JavaFX rendering, use the following command:"
Write-Host "& `\"$customJreDir\bin\java.exe`\" `\"-Dprism.verbose=true`\" `\"-jar`\" `\"$deployDir\$jarFile`\""
