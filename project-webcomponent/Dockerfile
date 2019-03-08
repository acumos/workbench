#FROM dockercentral.it.att.com:5100/com.att.ajsc.public/node:8.11.1-alpine as build-stage
FROM dockercentral.it.att.com:5100/com.att.dockercentral.public/ubuntu:16.04

ENV http_proxy http://one.proxy.att.com:8080
ENV https_proxy http://one.proxy.att.com:8080

RUN apt-get update
RUN apt-get install -y nodejs
RUN apt-get install -y npm
RUN ln -s /usr/bin/nodejs /usr/bin/node

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
RUN echo "$LOG_TAG Install java8" && \
    apt-get -y update && \
    apt-get install -y openjdk-8-jdk

RUN mkdir /opt/app
ADD src/main/webapp/ /opt/app
ADD target/project-acumos-0.0.1-SNAPSHOT.war /opt/app/app.war
WORKDIR /opt/app

RUN npm install -g http-server
RUN npm install --unsafe-perm

# Build component
RUN npm run build

EXPOSE 8080
#CMD ["npm", "run", "http-server"]
#CMD ["http-server", "-s"] 
RUN bash -c 'touch app.war'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container","-jar","app.war"]