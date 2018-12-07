#!/bin/bash
#
# Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
