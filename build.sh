#!/bin/bash

# Simple build script — no Maven or Gradle needed.
# Just requires a JDK (Java 8 or above).

set -e

SRC_DIR="src"
OUT_DIR="out"

echo "Compiling..."
mkdir -p "$OUT_DIR"
javac -d "$OUT_DIR" "$SRC_DIR"/phonebook/*.java

echo "Done. Run with:  java -cp out phonebook.Main"
