#!bin/bash

RELEASES_DIR="releases"
VERSION_DIR="$RELEASES_DIR/$1"
PLUGIN_DIR="$RELEASES_DIR/$1/admob-plugin"

if [[ ! -e $RELEASES_DIR ]]; then
    mkdir -p $RELEASES_DIR
fi

mkdir -p $PLUGIN_DIR
cp -r ../admob-lib $VERSION_DIR
cp ../config/GodotAdmob.gdap ../admob-plugin/godotadmob/build/outputs/aar/GodotAdMob.${1}.release.aar $PLUGIN_DIR

cd $VERSION_DIR
zip -r "../GodotAdmobPlugin-${1}.zip" .
