REM rm -rf binary_distrib/linux64/
REM rm -rf jcef_build
md jcef_build
cd jcef_build
REM #cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Debug ..
REM cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Release ..
cmake -G "Visual Studio 16"  -DCMAKE_BUILD_TYPE=Release -X x64 ..
REM make -j6
cd ../tools
compile win64
make_distrib win64
cd ..
REM cp binary_distrib/* ../org.eclipse.swt.browser.chromium/org.eclipse.swt.browser.chromium/ -r
