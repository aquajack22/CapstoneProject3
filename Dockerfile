FROM tomcat:latest

LABEL maintainer="Jagannath Rajagopalan"

ADD ./target/demo-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]
