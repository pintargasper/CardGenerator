#!/bin/bash
# ============================
# Project settings
# ============================
PROJECT_ROOT="../"
PACKAGE_OUTPUT_DIR="$PROJECT_ROOT/dev/output/linux"
DEPLOY_DIR="$PACKAGE_OUTPUT_DIR/deploy"
CUSTOM_JRE_DIR="$PACKAGE_OUTPUT_DIR/custom-runtime"
APP_IMAGE_DIR="$PACKAGE_OUTPUT_DIR"
JAR_FILE="CardGenerator-1.0.0-jar-with-dependencies.jar"
JAR_FILE_FINAL="CardGenerator-1.0.0.jar"
MAIN_CLASS="com.gasperpintar.cardgenerator.Launcher"
APP_NAME="Card Generator"
APP_VERSION="1.0.0"
VENDOR="Gašper Pintar"
ICON_PATH="$PROJECT_ROOT/src/main/resources/com/gasperpintar/cardgenerator/images/logo.png"

# Windows-side paths
WINDOWS_JDK_PATH="$PROJECT_ROOT/dev/java/linux/jdk-21"
WINDOWS_JAVAFX_JMODS_PATH="$PROJECT_ROOT/dev/java/linux/javafx/javafx-jmods-21.0.8"
WINDOWS_JAVAFX_LIB_PATH="$PROJECT_ROOT/dev/java/linux/javafx/javafx-sdk-21.0.8"

# Linux-side paths
LINUX_BASE="$HOME/dev/java"
JDK_PATH="$LINUX_BASE/jdk-21"
JAVAFX_JMODS_PATH="$LINUX_BASE/javafx-jmods-21.0.8"
JAVAFX_LIB_PATH="$LINUX_BASE/javafx-sdk-21.0.8/lib"

# ============================
# Clean package output folder
# ============================
if [ -d "$PACKAGE_OUTPUT_DIR" ]; then
    echo "Cleaning package output folder..."
    rm -rf "$PACKAGE_OUTPUT_DIR"
fi
mkdir -p "$DEPLOY_DIR"

# ============================
# Clear Linux side dev/java folder BEFORE copying
# ============================
if [ -d "$LINUX_BASE" ]; then
    echo "Cleaning Linux dev/java folder..."
    rm -rf "$LINUX_BASE"
fi
mkdir -p "$LINUX_BASE"

# ============================
# Copy JDK + JavaFX from Windows to Linux WSL
# ============================
mkdir -p "$LINUX_BASE"
rm -rf "$JDK_PATH" "$JAVAFX_JMODS_PATH" "$JAVAFX_LIB_PATH"

cp -r "$WINDOWS_JDK_PATH" "$LINUX_BASE/"
cp -r "$WINDOWS_JAVAFX_JMODS_PATH" "$LINUX_BASE/"
cp -r "$WINDOWS_JAVAFX_LIB_PATH" "$LINUX_BASE/"

# ============================
# Copy the fat JAR
# ============================
cp "$PROJECT_ROOT/target/$JAR_FILE" "$DEPLOY_DIR/$JAR_FILE_FINAL"

# ============================
# Create a minimal custom JRE with jlink
# ============================
JAVA_FX_MODULES="javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.media,javafx.web"

"$JDK_PATH/bin/jlink" \
    --module-path "$JDK_PATH/jmods:$JAVAFX_JMODS_PATH" \
    --add-modules java.base,java.desktop,java.logging,java.sql,java.naming,java.management,$JAVA_FX_MODULES \
    --output "$CUSTOM_JRE_DIR" \
    --compress zip-6 \
    --strip-debug \
    --no-header-files \
    --no-man-pages

# ============================
# Copy all JavaFX shared libraries (.so files)
# ============================
mkdir -p "$CUSTOM_JRE_DIR/lib"
find "$JAVAFX_LIB_PATH" -name "*.so" -exec cp {} "$CUSTOM_JRE_DIR/lib" \;

# ============================
# Create app-image with jpackage
# ============================
# {"app-image", "rpm", "deb"}
"$JDK_PATH/bin/jpackage" \
    --type rpm \
    --input "$DEPLOY_DIR" \
    --main-jar "$JAR_FILE_FINAL" \
    --main-class "$MAIN_CLASS" \
    --name "$APP_NAME" \
    --app-version "$APP_VERSION" \
    --vendor "$VENDOR" \
    --icon "$ICON_PATH" \
    --runtime-image "$CUSTOM_JRE_DIR" \
    --dest "$APP_IMAGE_DIR" \
    --description "Card Generator allows the creation of any card and also its generation in png format. It also allows downloading images in pdf mode" \
    --copyright "(C) 2025 Gasper Pintar. All rights reserved"

# ============================
# Debug command
# ============================
echo "For debugging JavaFX rendering, use the following command:"
echo "$CUSTOM_JRE_DIR/bin/java -Dprism.verbose=true -jar $DEPLOY_DIR/$JAR_FILE_FINAL"
