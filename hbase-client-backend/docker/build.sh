#! /bin/bash
harborAddress=$1
harborNamespace=$2
imageName=$3
version=$4
scriptPath=$(
  cd "$(dirname "$0")"
  pwd
)
echo "the script run path at $scriptPath "
#pomPath=$(dirname $scriptPath)
#cd $pomPath && mvn package -DskipTests
cd $scriptPath && yes | cp -r ../target/*.jar ./
cd $scriptPath && docker build -t $harborAddress/$harborNamespace/$imageName:$version .
cd $scriptPath && rm -rf *.jar