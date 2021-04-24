FROM node

EXPOSE 4200
EXPOSE 8080

RUN DEBIAN_FRONTEND=noninteractive apt-get update &&\
    apt-get install -y  apt-utils\
    nodejs\
    nginx

ENTRYPOINT [ "/home/entrypoint/docker-entrypoint.sh" ]
CMD nginx -g 'daemon off;'