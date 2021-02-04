# sudo apt-get install language-pack-de language-pack-gnome-de
# sudo update-locale LANG=de_DE.UTF-8
# sudo apt install git cmake openjdk-14-jdk python3 openjdk-14-jre build-essential libgtk2.0-dev maven
# git clone https://github.com/YattaSolutions/java-cef
# git clone https://github.com/YattaSolutions/org.eclipse.swt.browser.chromium.git
# chmod +x rebuild.sh
# add edit .bashrc
#     export JAVA_HOME=/usr/lib/jvm/java-14-openjdk-amd64/
#     export PATH=$PATH:$JAVA_HOME/bin
  
# add to Run Config
# -Djava.library.path=${workspace_loc}${project_path}/jcef/binary_distrib/linux64/bin:${workspace_loc}${project_path}/jcef/binary_distrib/linux64/bin/lib/linux64:${workspace_loc}${project_path}/$jcef_debug_natives:/usr/lib/jvm/java-14-openjdk-amd64/jre/lib/amd64/
# sudo ln -s /usr/bin/python3 /usr/bin/python
# add org/cef/browser/mac/CefBrowserWindowMac.java to tools/distrib/EXCLUDE_FILES.txt


#!/bin/bash
rm -rf binary_distrib/linux64/
rm -rf jcef_build
mkdir jcef_build
cd jcef_build
#cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Debug ..
cmake -G "Unix Makefiles" -DCMAKE_BUILD_TYPE=Release ..
make -j6
cd ../tools
./compile.sh linux64
./make_distrib.sh linux64
cd ..
cp binary_distrib/* ../org.eclipse.swt.browser.chromium/org.eclipse.swt.browser.chromium/ -r
# extract ../org.eclipse.swt.browser.chromium/jcef/binary_distrib/linux64/bin/gluegen-rt-natives-linux-amd64.jar ../org.eclipse.swt.browser.chromium/jcef/binary_distrib/linux64/bin/lib/linux64 
# extract ../org.eclipse.swt.browser.chromium/jcef/binary_distrib/linux64/bin/jogl-all-natives-linux-amd64.jar ../org.eclipse.swt.browser.chromium/jcef/binary_distrib/linux64/bin/lib/linux64
