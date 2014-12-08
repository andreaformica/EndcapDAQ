#!/bin/csh

set prjclasspath
foreach x (run/*.jar)
     echo Found  $x
     set prjclasspath = ${prjclasspath}:${x}
end
echo "using classpath $prjclasspath"

java -cp "${prjclasspath}:." ec.dim.EcDimSender AL_B  "2011/08/01-00:00:00:GMT"
