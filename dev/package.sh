#!/bin/bash

# ============================
# Project settings (portable)
# ============================
PROJECT_ROOT="$(realpath "$(dirname "$0")/..")"
PACKAGE_OUTPUT_DIR="$PROJECT_ROOT/dev/output/linux"
DEPLOY_DIR="$PACKAGE_OUTPUT_DIR/deploy"
CUSTOM_JRE_DIR="$PACKAGE_OUTPUT_DIR/custom-runtime"
APP_IMAGE_DIR="$PACKAGE_OUTPUT_DIR"

JAR_FILE="CardGenerator-1.2.0-alpha+1.jar"
MAIN_CLASS="com.gasperpintar.cardgenerator.Launcher"
APP_NAME="Card Generator"
APP_VERSION="1.2.0"
VENDOR="Gašper Pintar"
ICON_PATH="$PROJECT_ROOT/src/main/resources/com/gasperpintar/cardgenerator/images/logo.png"

JDK_PATH="$PROJECT_ROOT/dev/java/linux/jdk-25.0.1"
JAVAFX_JMODS_PATH="$PROJECT_ROOT/dev/java/linux/javafx-jmods-25.0.1"
JAVAFX_LIB_PATH="$PROJECT_ROOT/dev/java/linux/javafx-sdk-25.0.1/lib"

# ============================
# Pre-checks (must be first)
# ============================
if [ ! -f "$PROJECT_ROOT/target/$JAR_FILE" ]; then
    echo "ERROR: JAR file does not exist: $PROJECT_ROOT/target/$JAR_FILE"
    echo "First build the project with: mvn package"
    exit 1
fi

if [ ! -d "$JAVAFX_JMODS_PATH" ]; then
    echo "ERROR: JavaFX jmods folder does not exist: $JAVAFX_JMODS_PATH"
    exit 1
fi

if [ ! -d "$JAVAFX_LIB_PATH" ]; then
    echo "ERROR: JavaFX lib folder does not exist: $JAVAFX_LIB_PATH"
    exit 1
fi

# ============================
# Clean output folder
# ============================
rm -rf "$PACKAGE_OUTPUT_DIR"
mkdir -p "$DEPLOY_DIR"

# ============================
# Copy JAR
# ============================
cp "$PROJECT_ROOT/target/$JAR_FILE" "$DEPLOY_DIR/"

# ============================
# jlink: create minimal JRE
# ============================
JAVA_FX_MODULES="javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.media,javafx.web"

"$JDK_PATH/bin/jlink" \
    --module-path "$JDK_PATH/jmods:$JAVAFX_JMODS_PATH" \
    --add-modules java.base,java.desktop,java.logging,java.sql,java.naming,java.management,$JAVA_FX_MODULES \
    --output "$CUSTOM_JRE_DIR" \
    --compress=zip-6 \
    --strip-debug \
    --no-header-files \
    --no-man-pages

# ============================
# Copy JavaFX .so libraries
# ============================
mkdir -p "$CUSTOM_JRE_DIR/lib"
find "$JAVAFX_LIB_PATH" -name "*.so" -exec cp {} "$CUSTOM_JRE_DIR/lib" \;

# ============================
# jpackage: build app-image
# ============================
"$JDK_PATH/bin/jpackage" \
    --type rpm \
    --input "$DEPLOY_DIR" \
    --main-jar "$JAR_FILE" \
    --main-class "$MAIN_CLASS" \
    --name "$APP_NAME" \
    --app-version "$APP_VERSION" \
    --vendor "$VENDOR" \
    --icon "$ICON_PATH" \
    --runtime-image "$CUSTOM_JRE_DIR" \
    --dest "$APP_IMAGE_DIR" \
    --description "Card Generator is a desktop application for creating and generating custom playing cards. Using Excel spreadsheets and customizable fxml templates, you can quickly create entire decks of playing cards without having to design each card individually" \
    --copyright "(C) 2025 Gasper Pintar"

# ============================
# Debug instructions
# ============================
echo ""
echo "=============================================="
echo "JavaFX debug run command:"
echo "$CUSTOM_JRE_DIR/bin/java -Dprism.verbose=true -jar $DEPLOY_DIR/$JAR_FILE"
echo "=============================================="
