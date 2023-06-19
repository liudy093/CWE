FROM registry.cn-shenzhen.aliyuncs.com/1120161630/lk:1.0
MAINTAINER lk
WORKDIR /usr/local
ADD src /usr/local/src
ADD Lib/*.jar /usr/local/
#ADD Lib/commons-pool2-2.4.3.jar /usr/local/
#ADD Lib/jedis-3.0.0.jar /usr/local/
#ADD Lib/log4j-1.2.17.jar /usr/local/
#ADD Lib/slf4j-api-1.7.2.jar /usr/local/
#ADD Lib/slf4j-log4j12-1.7.22.jar /usr/local/
ADD Lib/grpc_lib/*.jar /usr/local/
ADD Lib/prometheus/*.jar /usr/local/
#ADD Lib /usr/local/lib
RUN find src/bit/workflowScheduler/ -name *.java > source.list
#RUN javac -d . -encoding UTF-8 -cp ./jedis-3.3.0.jar:./simpleclient-0.9.0.jar:./simpleclient_common-0.9.0.jar:./simpleclient_hotspot-0.9.0.jar:./simpleclient_httpserver-0.9.0.jar:./simpleclient_pushgateway-0.9.0.jar:./animal-sniffer-annotations-1.17.jar:./commons-codec-1.3.jar:./commons-io-2.6.jar:./commons-lang3-3.5.jar:./commons-logging-1.1.1.jar:./error_prone_annotations-2.2.0.jar:./google-auth-library-credentials-0.9.0.jar:./google-auth-library-oauth2-http-0.9.0.jar:./google-http-client-1.19.0.jar:./google-http-client-jackson2-1.19.0.jar:./grpc-alts-1.15.0.jar:./grpc-auth-1.15.0.jar:./grpc-context-1.15.0.jar:./grpc-core-1.15.0.jar:./grpc-grpclb-1.15.0.jar:./grpc-netty-1.15.0.jar:./grpc-netty-shaded-1.15.0.jar:./grpc-protobuf-1.15.0.jar:./grpc-protobuf-lite-1.15.0.jar:./grpc-stub-1.15.0.jar:./grpc-testing-1.15.0.jar:./gson-2.7.jar:./guava-20.0.jar:./hamcrest-core-1.3.jar:./httpclient-4.0.1.jar:./httpcore-4.0.1.jar:./jackson-core-2.1.3.jar:./javax.annotation-api-1.3.1.jar:./jsr305-3.0.0.jar:./junit-4.12.jar:./mockito-core-1.9.5.jar:./netty-buffer-4.1.27.Final.jar:./netty-codec-4.1.27.Final.jar:./netty-codec-http-4.1.27.Final.jar:./netty-codec-http2-4.1.27.Final.jar:./netty-codec-socks-4.1.27.Final.jar:./netty-common-4.1.27.Final.jar:./netty-handler-4.1.27.Final.jar:./netty-handler-proxy-4.1.27.Final.jar:./netty-resolver-4.1.27.Final.jar:./netty-tcnative-boringssl-static-2.0.7.Final.jar:./netty-transport-4.1.27.Final.jar:./objenesis-1.0.jar:./opencensus-api-0.12.3.jar:./opencensus-contrib-grpc-metrics-0.12.3.jar:./proto-google-common-protos-1.0.0.jar:./protobuf-java-3.5.1.jar:./protobuf-java-util-3.5.1.jar:./snappy-java-1.1.7.3.jar @source.list
RUN javac -d . -encoding UTF-8 -cp ./commons-pool2-2.4.3.jar:./jedis-3.0.0.jar:./log4j-1.2.17.jar:./slf4j-api-1.7.2.jar:./slf4j-log4j12-1.7.22.jar:./simpleclient-0.9.0.jar:./simpleclient_common-0.9.0.jar:./simpleclient_hotspot-0.9.0.jar:./simpleclient_httpserver-0.9.0.jar:./simpleclient_pushgateway-0.9.0.jar:./animal-sniffer-annotations-1.17.jar:./commons-codec-1.3.jar:./commons-io-2.6.jar:./commons-lang3-3.5.jar:./commons-logging-1.1.1.jar:./error_prone_annotations-2.2.0.jar:./google-auth-library-credentials-0.9.0.jar:./google-auth-library-oauth2-http-0.9.0.jar:./google-http-client-1.19.0.jar:./google-http-client-jackson2-1.19.0.jar:./grpc-alts-1.15.0.jar:./grpc-auth-1.15.0.jar:./grpc-context-1.15.0.jar:./grpc-core-1.15.0.jar:./grpc-grpclb-1.15.0.jar:./grpc-netty-1.15.0.jar:./grpc-netty-shaded-1.15.0.jar:./grpc-protobuf-1.15.0.jar:./grpc-protobuf-lite-1.15.0.jar:./grpc-stub-1.15.0.jar:./grpc-testing-1.15.0.jar:./gson-2.7.jar:./guava-20.0.jar:./hamcrest-core-1.3.jar:./httpclient-4.0.1.jar:./httpcore-4.0.1.jar:./jackson-core-2.1.3.jar:./javax.annotation-api-1.3.1.jar:./jsr305-3.0.0.jar:./junit-4.12.jar:./mockito-core-1.9.5.jar:./netty-buffer-4.1.27.Final.jar:./netty-codec-4.1.27.Final.jar:./netty-codec-http-4.1.27.Final.jar:./netty-codec-http2-4.1.27.Final.jar:./netty-codec-socks-4.1.27.Final.jar:./netty-common-4.1.27.Final.jar:./netty-handler-4.1.27.Final.jar:./netty-handler-proxy-4.1.27.Final.jar:./netty-resolver-4.1.27.Final.jar:./netty-tcnative-boringssl-static-2.0.7.Final.jar:./netty-transport-4.1.27.Final.jar:./objenesis-1.0.jar:./opencensus-api-0.12.3.jar:./opencensus-contrib-grpc-metrics-0.12.3.jar:./proto-google-common-protos-1.0.0.jar:./protobuf-java-3.5.1.jar:./protobuf-java-util-3.5.1.jar:./snappy-java-1.1.7.3.jar @source.list
#COPY bin /usr/local
ADD MANIFEST.MF /usr/local
ADD org /usr/local/org
RUN jar -cvfm workflow-image.jar MANIFEST.MF bit org *.jar
ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8
#RUN rm -rf ./bit 
#ENTRYPOINT ["java","-jar","-Xms2g","-Xmx2g","workflow-image.jar"]
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=50"
#ENTRYPOINT ["java","-jar","${JAVA_OPTS}","workflow-image.jar"]
ENTRYPOINT java -jar ${JAVA_OPTS} workflow-image.jar