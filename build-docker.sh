#! /bin/bash

DOCKER_BUILDKIT=1 docker build -t registry.gilmariosoftware.com.br/uploader-api:v1.2.0 .

DOCKER_BUILDKIT=1 docker build -t registry.gilmariosoftware.com.br/uploader-api:latest .