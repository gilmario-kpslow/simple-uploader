#! /bin/bash

DOCKER_BUILDKIT=1 docker build -t gilmariokpslow/uploader-api:v1.1.0 .

DOCKER_BUILDKIT=1 docker build -t gilmariokpslow/uploader-api:latest .