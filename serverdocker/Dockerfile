FROM gradle

RUN apt update\
    && apt-get install -y libtcnative-1  cron software-properties-common ffmpeg\
    && rm -rf /var/lib/apt/lists/*

RUN add-apt-repository ppa:deadsnakes/ppa\
    && apt update\
    && apt install -y python3.7 python3-pip
ENV TZ=America/Montreal
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN pip3 install pipenv
WORKDIR /app/server/videoEncoder
RUN pipenv install ffmpeg-python boto3 mysql-connector-python
RUN mkdir /root/.aws
RUN echo "[default]" >> /root/.aws/credentials
RUN echo "aws_access_key_id = AWS_ACCESS_KEY_HERE" >> /root/.aws/credentials
RUN echo "aws_secret_access_key = AWS_SECRET_KEY_HERE" >> /root/.aws/credentials
RUN ( crontab -l 2>/dev/null | grep -Fv ntpdate ; printf -- "*/5 * * * * /usr/bin/flock -n /tmp/my.lockfile /app/server/videoEncoder/encoding_job.sh \r\n" ) | crontab

EXPOSE 3306
EXPOSE 8080

ENTRYPOINT ["/app/entrypoint/entrypoint.sh"]
CMD gradle build

