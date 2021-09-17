
FROM debian:buster-slim

ENV DEBIAN_FRONTEND=noninteractive

WORKDIR /opt
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
    && curl \
        -L \
        -o openjdk.tar.gz \
        https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_linux-x64_bin.tar.gz \
    && mkdir jdk \
    && tar zxf openjdk.tar.gz -C jdk --strip-components=1 \
    && rm -rf openjdk.tar.gz \
    && apt-get -y --purge autoremove curl \
    && ln -sf /opt/jdk/bin/* /usr/local/bin/ \
    && rm -rf /var/lib/apt/lists/* \
    && java  --version \
    && javac --version \
    && jlink --version
# basic smoke test

WORKDIR "/"
RUN java --version

RUN apt-get update
RUN apt-get install bash
RUN apt-get install -y curl

RUN mkdir shell
RUN mkdir www
RUN mkdir web
RUN mkdir js
RUN mkdir target
RUN mkdir -p sql/create
RUN mkdir logs
RUN mkdir SampleBids
RUN mkdir Campaigns
run mkdir query

COPY target/RTB5-0.0.1-SNAPSHOT-jar-with-dependencies.jar target/

COPY wait-for-it.sh /
RUN chmod +x /wait-for-it.sh

COPY tools/* /
COPY sql/create/* sql/create/
COPY shell/ /shell

COPY query/ query/

COPY www/index.html /www
COPY www/css/ css/
COPY www/fonts/ fonts/
COPY www/assets/ assets/
copy www/campaigns campaigns/

COPY log4j.properties /
COPY SampleBids /SampleBids

EXPOSE 8080 5701

CMD ./rtb4free
