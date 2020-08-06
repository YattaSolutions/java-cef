#!/bin/bash
rm -rf binary_distrib/linux64/
rm -rf jcef_build
mkdir jcef_build
cd jcef_build
cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Debug ..
make -j6
cd ../tools
./compile.sh linux64
