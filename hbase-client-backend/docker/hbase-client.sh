pomPath=$(
  cd "$(dirname "$PWD")"
  mvn clean
  mvn package
  pwd
)
echo "the script run path at ${pomPath}"
docker login  10.192.30.61:5000
docker images | grep hbase-client | awk '{print $3}' | xargs docker rmi
sh build.sh 10.192.30.61:5000 library hbase-client v1.0
docker push 10.192.30.61:5000/library/hbase-client:v1.0