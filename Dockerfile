FROM openjdk:8

RUN rm -rf /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

VOLUME /etc/zhihuiyun/conf
VOLUME /var/lib/zhihuiyun

ARG ADMIN_PASSWORD='admin123'
ARG ACTIVE_ENV='docker'
ARG LOG_LEVEL='info'

COPY ./spark-yun-backend/spark-yun-main/build/libs/zhihuiyun.jar /opt/zhihuiyun/zhihuiyun.jar
COPY ./spark-yun-backend/spark-yun-main/src/main/resources/application-docker.yml /etc/zhihuiyun/conf/

EXPOSE 8080

ENV ADMIN_PASSWORD=${ADMIN_PASSWORD}
ENV ACTIVE_ENV=${ACTIVE_ENV}
ENV LOG_LEVEL=${LOG_LEVEL}
ENV PARAMS=""
ENV JVMOPTIONS=""

ENTRYPOINT ["sh","-c","java $JVMOPTIONS -jar /opt/zhihuiyun/zhihuiyun.jar --logging.level.root=${LOG_LEVEL} --spring.profiles.active=${ACTIVE_ENV} --isx-app.admin-passwd=${ADMIN_PASSWORD} --spring.config.additional-location=/etc/zhihuiyun/conf/ $PARAMS"]