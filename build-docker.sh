#! /bin/bash

DOCKER_BUILDKIT=1 docker build -t gilmariokpslow/uploader-api:v1.2.0 .

DOCKER_BUILDKIT=1 docker build -t gilmariokpslow/uploader-api:latest .