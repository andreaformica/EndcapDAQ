#!/bin/sh

if [ ! -d run ]; then
   echo "run directory does not exists...create it using ant copy-jars"
   exit
fi

singlesrv=false
quitecdim=false
debug=false
javauserargs=""
dimcommand=""
type=""

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

function startDimServer {
  keyclass=$1
  keytype=$2
  keydim=$3
  keyaction=$4
  keylog=$5
  echo "starting server using $keytype $keydim $keylog"
  
  nohup java $javauserargs $macosx_javalib -cp "${prjclasspath}." $keyclass -T $keytype -C $keydim -A $keyaction >& $keylog &  
  runpid=$!
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
  echo "${keyrunpid}" >> jdimsenderpid
}


# Parse script's command line arguments
while getopts "sqdt:c:hj:" flag
do
    echo "Parse option $flag with args $OPTARG ..."
    case "$flag" in
        q) quitecdim=true;;
        d) debug=true;;
        s) singlesrv=true;;
        t) type="$OPTARG";;
        c) dimcommand="$OPTARG";;
        j) javauserargs="$OPTARG";;
        h|?)
            echo "Usage: $0 [-sqtcdh] [-j <Java arguments>]"
            echo "    -j  pass arguments to the Java process, e.g. -j \"-Dprop1=value1 -Dprop2=value2\""
            echo "    -q  shut down the started servers"
            echo "    -s  start only one server"
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
echo "   javaargs = $javauserargs "
echo "   macosx specific path = $macosx_javalib"

# If the user wishes to quit DB dim server
if [ $quitecdim = true ]; then
    echo "Shutting down the DB Dim server and sender processes"
    echo "Watch log files for completion"
    procids=`ps -A | grep "EcDimSender" | egrep java | awk '{print $1}'`    
    procids=`pgrep -f "EcDimSender"`
    echo "Found process id to kill = $procids"
    for apid in $procids; do 
    	echo "Kill process $apid"
    	kill $apid
    done	
    exit 0
fi

# startDimServer ec.dim.EcDimCycleServer "" "" "" cyclesrv.out 
if [ "$singlesrv" == "false" ]; then

echo "Launch 4 servers..."
 startDimServer ec.dim.EcDimSender AlB "LTX_PROTOCOL/STEPRESULT/CHB" docycle sender_alb.out 
 startDimServer ec.dim.EcDimSender AlD "LTX_PROTOCOL/STEPRESULT/CHD" docycle sender_ald.out 
 startDimServer ec.dim.EcDimSender AlT "LTX_PROTOCOL/STEPRESULT/CHT" docycle sender_alt.out 
 startDimServer ec.dim.EcDimSender AlR "LTX_PROTOCOL/STEPRESULT/CHR" docycle sender_alr.out 
else
 startDimServer ec.dim.EcDimSender $type $dimcommand docycle sender.out
fi


#java ${macosx_javalib} -cp "${prjclasspath}." ec.dim.EcDimSender -T "AlT" -A "endcycle"


