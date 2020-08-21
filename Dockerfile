FROM alpine:3.6
FROM nginx

RUN adduser -h /etc/nginx -D -s /bin/sh nginx

COPY nginx.conf /etc/nginx/

COPY www/ /usr/share/nginx/html/

EXPOSE 8080

CMD ["nginx"]
