#!/bin/bash
set -ex

# The current script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}"  )" && pwd  )"

for f in ${DIR}/../target/voms-ng-*.jar; do
  JAR=${f}
  break
done

if [[ ! -r ${JAR} ]]; then
  echo "voms-ng jar not found"
  exit 1
fi

imagename="indigoiam/voms-ng"

cd ${DIR}
cp ${JAR} voms-ng.jar

docker build --rm=true --no-cache=true -t ${imagename} .

rm voms-ng.jar
