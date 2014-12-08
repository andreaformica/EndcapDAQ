#!/bin/sh


if [ ! -d run ]; then
   echo "run directory does not exists...create it using ant copy-jars"
   exit
fi

quitecdim=false
debug=false
singlesrv=false
javauserargs=""
dimcommand=""
commandargs=""
type=""
altype=""
cycleid=""
macosx_javalib=""

ossys=`uname`
if [ "x${ossys}" == "xDarwin" ]; then
   macosx_javalib="-Djava.library.path=${DIMDIR}/darwin/"
fi

# Create the classpath using jar files inside the run directory
alljars=`ls -1 run/*jar `
prjclasspath=
for ajar in $alljars; do
#   echo "Appending jar $ajar to classpath"
   jarpath="${ajar}:"
   prjclasspath=${prjclasspath}${jarpath}
done
#echo "using classpath $prjclasspath"

function startCli {
  keytype=$1
  keydim=$2
  keyargs=$3
  echo "starting command using $keytype $keydim $keyargs"
  
  java $javauserargs $macosx_javalib -cp "${prjclasspath}." atlas.mdt.dcs.cli.EcDcsCli -T $keytype -C $keydim -f $keyargs 
}

function startCliCycle {
  keytype=$1
  keycommand=$2
  keycycle=$3
  echo "starting command using $keytype $keydim $keyargs"
  
  java $javauserargs $macosx_javalib -cp "${prjclasspath}." atlas.mdt.dcs.cli.EcDcsCli -C $keycommand -T $keytype --cycle $keycycle 
}


# Parse script's command line arguments
while getopts "f:b:dt:c:hj:" flag
do
    echo "Parse option $flag with args $OPTARG ..."
    case "$flag" in
        d) debug=true;;
        t) type="$OPTARG";;
        b) cycleid="$OPTARG";;
        c) dimcommand="$OPTARG";;
        f) commandargs="$OPTARG";;
        j) javauserargs="$OPTARG";;
        h|?)
            echo "Usage: $0 [-tcfbdh] [-j <Java arguments>]"
            echo "    -j  pass arguments to the Java process, e.g. -j \"-Dprop1=value1 -Dprop2=value2\""
            echo "    -t  set the image type (BImage, AlD, AlR, AlT)"
            echo "    -c  set the command [storeImages|listAlByCycle]"
            echo "    -f  set the file name"
            echo "    -b  set the cycle id"
            echo "    -d  debug this script"
            echo "    -h  this help message"
            exit 1;;
    esac
done

echo "Arguments are..."
echo "   cli = $dimcommand "
echo "   type = $type "
echo "   javaargs = $javauserargs "
echo "   macosx specific path = $macosx_javalib"

# Launch command line
if [ "x$cycleid" != "x" ]; then
   echo "Retrieve images by cycle"	
   startCliCycle $type $dimcommand $cycleid
else 
   echo "Launch command line client"
   startCli $type $dimcommand $commandargs
fi