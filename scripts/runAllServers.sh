#!/bin/sh

# run 4 servers listening to different DIM channels
# The channels have been defined by Keith

if [ ! -d run ]; then
   echo "run directory does not exists...create it using ant copy-jars"
   exit
fi

quitecdim=false
debug=false
singlesrv=false
javauserargs=""
dimcommand=""
type=""
macosx_javalib=""
macosx_jmx=""
ossys=`uname`
jmxport=3333

NOLOG="Yes"

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

function startDimServer {
  keytype=$1
  keydim=$2
  keylog=$3
  keyclass=$4

  echo "starting server using $keytype $keydim $keylog"

if [ "x${ossys}" == "xDarwin" ]; then
   macosx_jmx=" -Dcom.sun.management.jmxremote.port=${jmxport} \
     -Dcom.sun.management.jmxremote.ssl=false \
     -Dcom.sun.management.jmxremote.authenticate=false "
fi

  if [ "x$NOLOG" == "xYes" ]; then
#     keylog=" >& /dev/null 1>&2 & "
     echo "starting server using $keytype $keydim $keylog"
     echo "Use command java $javauserargs $macosx_javalib -cp "${prjclasspath}." $keyclass -T $keytype -C $keydim"
     nohup java $javauserargs $macosx_javalib -cp "${prjclasspath}." $keyclass -T $keytype -C $keydim >& /dev/null 1>&2 &
     runpid=$!
  else
#     keylog=" >& ${keylog} & "
     echo "starting server using $keytype $keydim $keylog"
     nohup java $javauserargs $macosx_javalib -cp "${prjclasspath}." $keyclass -T $keytype -C $keydim >& $keylog &
     runpid=$!
  fi

#  echo "starting server using $keytype $keydim $keylog"
#  nohup java $javauserargs $macosx_javalib -cp "${prjclasspath}." $keyclass -T $keytype -C $keydim >& $keylog
#  runpid=$!


#  nohup java $javauserargs $macosx_javalib $macosx_jmx -cp "${prjclasspath}." $keyclass -T $keytype -C $keydim $keylog   
  jmxport=$(($jmxport+1))
#  runpid=$!
  echo "Track pid $runpid"
  logPid $runpid
}

function logPid {
  keyrunpid=$1
  if [ $? != 0 ]; then
    echo "error starting dim server"
    exit 1
  fi
  sleep 1
  #pgrep -l -P ${keyrunpid}
  #jdimpid=`pgrep -P ${keyrunpid} java`
  echo "java Process PID is ${keyrunpid}"
  echo "$HOSTNAME ${keyrunpid}" >> jdimpid
}

# Parse script's command line arguments
while getopts "sqdt:c:hj:n:" flag
do
    echo "Parse option $flag with args $OPTARG ..."
    case "$flag" in
        s) singlesrv=true;;
        q) quitecdim=true;;
        d) debug=true;;
        n) NOLOG="$OPTARG";;
        t) type="$OPTARG";;
        c) dimcommand="$OPTARG";;
        j) javauserargs="$OPTARG";;
        h|?)
            echo "Usage: $0 [-sqtcdh] [-j <Java arguments>]"
            echo "    -j  pass arguments to the Java process, e.g. -j \"-Dprop1=value1 -Dprop2=value2\""
            echo "    -s  start only one server"
            echo "    -q  shut down the started servers"
            echo "    -n  [Yes | No] send log to /dev/null"
            echo "    -t  for single server start up, set the image type (AlB, AlD, AlR, AlT)"
            echo "    -c  for single server start up, set the dim channel destination"
            echo "    -d  debug this script"
            echo "    -h  this help message"
            exit 1;;
    esac
done

echo "Arguments are..."
echo "   dim = $dimcommand "
echo "   type = $type "
echo "   singlesrv = $singlesrv "
echo "   javaargs = $javauserargs "
echo "   macosx specific path = $macosx_javalib"

# If the user wishes to quit DB dim server
if [ $quitecdim = true ]; then
    echo "Shutting down the DB Dim server(s)"
    echo "Watch log files for completion"
    procids=`ps -A | grep "EcDim" | egrep java | awk '{print $1}'`    
    procids=`pgrep -f "EcDim"`
    echo "Found process id to kill = $procids"
    for apid in $procids; do 
    	echo "Kill process $apid"
    	kill $apid
    done	
#    killall EcDimServerByType
    exit 0
fi


if [ "$singlesrv" == "false" ]; then

echo "Launch 4 servers..."
 startDimServer AlB "LTX_PROTOCOL/STEPRESULT/CHB" console_alb.out ec.dim.EcDimServerByType
 startDimServer AlD "LTX_PROTOCOL/STEPRESULT/CHD" console_ald.out ec.dim.EcDimServerByType
 startDimServer AlT "LTX_PROTOCOL/STEPRESULT/CHT" console_alt.out ec.dim.EcDimServerByType
 startDimServer AlR "LTX_PROTOCOL/STEPRESULT/CHR" console_alr.out ec.dim.EcDimServerByType
echo "Launch cycle server..."
 startDimServer "" "" cyclesrv.out ec.dim.EcDimCycleServer

else
 startDimServer $type $dimcommand console.out ec.dim.EcDimServerByType
fi


