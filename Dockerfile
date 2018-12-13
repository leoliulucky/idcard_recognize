FROM java:8
VOLUME /tmp
ADD idcard_recognize-0.0.1-SNAPSHOT.jar idcard_recognize.jar
RUN bash -c 'touch /idcard_recognize.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/idcard_recognize.jar"]