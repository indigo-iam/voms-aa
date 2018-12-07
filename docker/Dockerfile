FROM openjdk:8

RUN mkdir /voms-aa
WORKDIR /voms-aa

COPY voms-aa.jar /voms-aa/

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap ${VOMS_NG_JAVA_OPTS} -jar voms-aa.jar

ENV TINI_VERSION v0.18.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]
