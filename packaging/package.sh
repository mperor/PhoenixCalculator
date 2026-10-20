#!/usr/bin/env bash
# Builds Phoenix Calculator packages for the current OS with jlink + jpackage (JDK 25+ on PATH or in JAVA_HOME).
#
# Usage:   packaging/package.sh [version]     version: x.y.z, defaults to the POM version without -SNAPSHOT
# Output:  target/release/PhoenixCalculator-<version>-<os>-<arch>.<ext>
#          Windows: .msi installer (needs WiX 3 or 4+) and portable .zip
#          macOS:   .dmg
#          Linux:   .deb and portable .tar.gz
set -euo pipefail

cd "$(dirname "$0")/.."

APP_NAME="PhoenixCalculator"
MAIN_MODULE="phoenix/eu.mapidev.calculator.phoenix.ApplicationLauncher"

if [[ -n "${JAVA_HOME:-}" ]]; then
    JPACKAGE="$JAVA_HOME/bin/jpackage"
    JAR="$JAVA_HOME/bin/jar"
else
    JPACKAGE="jpackage"
    JAR="jar"
fi

VERSION="${1:-}"
if [[ -z "$VERSION" ]]; then
    VERSION="$(./mvnw -B -q help:evaluate -Dexpression=project.version -DforceStdout)"
    VERSION="${VERSION%-SNAPSHOT}"
fi
# jpackage needs 1-3 numeric components; pad 1.0 to 1.0.0
[[ "$VERSION" =~ ^[0-9]+\.[0-9]+$ ]] && VERSION="$VERSION.0"
if [[ ! "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Invalid version '$VERSION', expected x.y.z" >&2
    exit 1
fi

case "$(uname -s)" in
    Linux*)               OS="linux" ;;
    Darwin*)              OS="macos" ;;
    MINGW*|MSYS*|CYGWIN*) OS="windows" ;;
    *) echo "Unsupported OS: $(uname -s)" >&2; exit 1 ;;
esac
case "$(uname -m)" in
    x86_64|amd64)  ARCH="x64" ;;
    arm64|aarch64) ARCH="arm64" ;;
    *)             ARCH="$(uname -m)" ;;
esac
PLATFORM="$OS-$ARCH"

echo "Packaging $APP_NAME $VERSION for $PLATFORM"

# jpackage marks the launcher read-only, which "mvn clean" cannot delete on Windows
rm -rf target/jpackage target/release
./mvnw -B -q clean package -DskipTests javafx:jlink

mkdir -p target/jpackage target/release

COMMON_OPTIONS=(
    --name "$APP_NAME"
    --app-version "$VERSION"
    --vendor "mapidev"
    --description "Calculator built with JavaFX"
    --runtime-image target/image
    --module "$MAIN_MODULE"
    --java-options "--enable-native-access=javafx.graphics"
)

package() {
    local type="$1"; shift
    "$JPACKAGE" --type "$type" --dest "target/jpackage/$type" "${COMMON_OPTIONS[@]}" "$@"
}

release_file() {
    echo "target/release/$APP_NAME-$VERSION-$PLATFORM.$1"
}

case "$OS" in
    windows)
        package app-image --icon packaging/icon.ico
        (cd target/jpackage/app-image && "$JAR" --create --no-manifest --file "../../../$(release_file zip)" "$APP_NAME")
        package msi --icon packaging/icon.ico --win-menu --win-shortcut --win-dir-chooser \
            --win-upgrade-uuid "6f1c3b0e-2d4a-4a7e-9b55-5c3f6d1e8a21"
        mv target/jpackage/msi/*.msi "$(release_file msi)"
        ;;
    macos)
        package dmg --icon packaging/icon.icns
        mv target/jpackage/dmg/*.dmg "$(release_file dmg)"
        ;;
    linux)
        package app-image --icon src/main/resources/img/icon.png
        tar -czf "$(release_file tar.gz)" -C target/jpackage/app-image "$APP_NAME"
        package deb --icon src/main/resources/img/icon.png --linux-shortcut --linux-menu-group Utility
        mv target/jpackage/deb/*.deb "$(release_file deb)"
        ;;
esac

echo "Created:"
ls -l target/release
