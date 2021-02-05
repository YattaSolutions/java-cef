export JAVA_HOME=`/usr/libexec/java_home -v 11`
rm -r jcef-build
mkdir jcef-build
cd jcef-build

cmake -G "Xcode" -DPROJECT_ARCH="x86_64" ..
cd ../tools

# ./make_distrib.sh macosx64
