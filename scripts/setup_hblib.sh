
if [ -d hb-lib ]; then
   echo "hb-lib exists..."
   exit
fi
mkdir hb-lib
#export JBOSS_HOME=/Users/formica/MyApp/Servers/jboss-as-7.1.1.Final

alljars=`cat jarlist.txt | awk '{print $NF}' | egrep *jar | sed  's/\([0-9]\.\)\{2,3\}/*/' `

for ajar in $alljars; do
   jarpath=`find $JBOSS_HOME/modules -name $ajar -print`
   if [ "x${jarpath}" != "x" ]; then
      cp $jarpath hb-lib 
   fi
done

for ajar in $alljars; do
   jarpath=`find -L external -name $ajar -print`
   if [ "x${jarpath}" != "x" ]; then
     cp $jarpath hb-lib 
   fi
done

