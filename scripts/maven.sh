#!/bin/bash
set -e

# Location of this script.
readonly SCRIPT_DIR=$( cd $(dirname $0); pwd -P)

# The modules to build, sign, and zip.
readonly MODULES=("libvcs4j-api" "libvcs4j" "libvcs4j-tools")

# Directory containing the modules to be processed.
readonly BASE_DIR="${SCRIPT_DIR}/.."

# Directory containing the distribution files.
readonly DIST_DIR="${BASE_DIR}/dist"
mkdir -p $DIST_DIR

# GPG key to be used for singing the distribution files.
readonly GPG_KEY_ID="EAA00B4FB1C54AA72284D636F71E150CAD9EA40F"

echo "[INFO] Building modules"
pushd $BASE_DIR > /dev/null
	./gradlew clean build -x test
	./gradlew publish
popd > /dev/null

echo "[INFO] Signing modules"
for module in "${MODULES[@]}"
do
	in_dir="${BASE_DIR}/${module}/build"
	out_dir="${DIST_DIR}/${module}"
	mkdir -p $out_dir
	pushd "${in_dir}/libs" > /dev/null
		for file in *
		do
			out_file="${out_dir}/${file}"
			cp $file $out_file
			gpg --no-tty -u $GPG_KEY_ID --detach-sign -o "${out_file}.asc" "${out_file}"
		done
	popd > /dev/null
	out_file="${out_dir}/pom.xml"
	cp "${in_dir}/publications/mavenJava/pom-default.xml" "${out_file}"
	gpg --no-tty -u $GPG_KEY_ID --detach-sign -o "${out_file}.asc" "${out_file}"
done

echo "[INFO] Zipping modules"
for module in "${MODULES[@]}"
do
	pushd "${DIST_DIR}/${module}" > /dev/null
		zip "${DIST_DIR}/${module}.zip" ./*
	popd > /dev/null
done
