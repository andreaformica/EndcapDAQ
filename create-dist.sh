# set env variables
wdir=$PWD
distdir=./dist
targetdir=$1
if [ "x$targetdir" == "x" ]; then
   targetdir=EndcapDim-0.0.1-SNAPSHOT
fi
# Remove all files under dist
rm -rf ${distdir}/*
# Create directory for distribution
mkdir ${distdir}/ecgit_v1
# Copy scripts in ecgit_v1
cp scripts/* ${distdir}/ecgit_v1
# Copy created distribution in EndcapDim/target
cp -r EndcapDim/target/${targetdir}-bin/${targetdir} ${distdir}/ecgit_v1/
# create tar
cd $distdir/ecgit_v1
ln -s ./${targetdir} run
cd ../
tar cvf ecdim.tar ./ecgit_v1 
cd $wdir
echo "End creation of distribution tar file in $distdir/ecdim.tar"
