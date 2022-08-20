#! /bin/bash

docker login

docker push gilmariokpslow/uploader-api:latest

docker push gilmariokpslow/uploader-api:v1.1.0