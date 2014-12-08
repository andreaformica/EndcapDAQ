#!/bin/sh

if [ ! -d run ]; then
   echo "run directory does not exists...create it using ant copy-jars"
   exit
fi

macosx_javalib=""

ossys=`uname`
if [ "x${ossys}" == "xDarwin" ]; then
   macosx_javalib="-Djava.library.path=${DIMDIR}/darwin/"
fi

alljars=`ls -1 run/*jar `
prjclasspath=
for ajar in $alljars; do
   #echo "Appending jar $ajar to classpath"
   jarpath="${ajar}:"
   prjclasspath=${prjclasspath}${jarpath}
done
echo "using classpath $prjclasspath"

#java ${macosx_javalib} -cp "${prjclasspath}." ec.dim.EcDimSender AL_B  "2011/08/01-00:00:00:GMT"

java ${macosx_javalib} -cp "${prjclasspath}." ec.dim.EcDimMonitoringServer $1  

