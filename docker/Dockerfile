FROM openjdk:8

RUN mkdir /voms-ng
WORKDIR /voms-ng

COPY voms-ng.jar /voms-ng/

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap ${VOMS_NG_JAVA_OPTS} -jar voms-ng.jar

ENV TINI_VERSION v0.18.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]
