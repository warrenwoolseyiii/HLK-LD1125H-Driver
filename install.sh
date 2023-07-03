#!/bin/bash

# Store the current working directory
ORIGINAL_DIR=$(pwd)

# Define the target directory
TARGET_DIR="/home/HLK-LD1125H-Driver"

# Check if the target directory exists, if it does, remove it
if [ -d "$TARGET_DIR" ]; then
    rm -rf "$TARGET_DIR"
fi

# Clone the repository into the /home directory
git clone -b make-install https://github.com/warrenwoolseyiii/HLK-LD1125H-Driver.git /home/HLK-LD1125H-Driver

# Change the working directory
cd /home/HLK-LD1125H-Driver/impl/C

# Run the make commands
make clean
make
make install

# Change back to the original working directory
cd $ORIGINAL_DIR
