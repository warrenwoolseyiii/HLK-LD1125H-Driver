#!/bin/bash
uncrustify -c format/format.cfg --no-backup impl/C/src/*.c
dos2unix impl/C/src/*.c
