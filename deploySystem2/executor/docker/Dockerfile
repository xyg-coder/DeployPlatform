FROM openjdk:12-ea-20-jdk-alpine3.8

MAINTAINER Xinyuan Gui

RUN apk add --no-cache g++

RUN apk add --no-cache python3 && \
    python3 -m ensurepip && \
    rm -r /usr/lib/python*/ensurepip && \
    pip3 install --upgrade pip setuptools && \
    if [ ! -e /usr/bin/pip ]; then ln -s pip3 /usr/bin/pip ; fi && \
    if [[ ! -e /usr/bin/python ]]; then ln -sf /usr/bin/python3 /usr/bin/python; fi && \
    rm -r /root/.cache

COPY docker_java_impl.sh /implsh/docker_java_impl.sh
COPY docker_cpp_impl.sh /implsh/docker_cpp_impl.sh
COPY docker_python3_impl.sh /implsh/docker_python3_impl.sh