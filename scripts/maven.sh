#!/bin/bash
set -e

# Location of this script.
readonly SCRIPT_DIR=$( cd $(dirname $0); pwd -P)

# The modules to build, sign, and zip.
readonly MODULES=("libvcs4j-api" "libvcs4j" "libvcs4j-tools")

# Directory containing the modules to process.
readonly BASE_DIR="$SCRIPT_DIR/.."

echo "Enter passphrase:"
read -s PASSPHRASE

echo "[INFO] Building modules"
pushd $BASE_DIR > /dev/null
	./gradlew clean build -x test
popd > /dev/null

echo "[INFO] Signing modules"
for module in "${MODULES[@]}"
do
	pushd "$BASE_DIR/$module/build/libs" > /dev/null
		for file in *
		do
			echo "$PASSPHRASE" | gpg --no-tty --passphrase-fd 0 -u 0B9BB494 --detach-sign -o "$file.asc" "$file"
		done
	popd > /dev/null
done

echo "[INFO] Zipping modules"
for module in "${MODULES[@]}"
do
	pushd "$BASE_DIR/$module/build/libs" > /dev/null
		zip "$module.zip" ./*
	popd > /dev/null
done
