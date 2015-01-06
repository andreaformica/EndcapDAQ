# Instructions for building a distribution
Use mvn clean package install 
for every single module for the moment.
Then go to EndcapDim and execute mvn clean assembly:directory
in order to create the distribution directory that could then
be deployed in point 1 machines for endcap.

This should be replaced by a unique step once I learn how to do it
with assembly plugin of maven.


