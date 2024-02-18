#! /bin/bash

docker login

docker push gilmariokpslow/uploader-api:latest

docker push gilmariokpslow/uploader-api:v1.2.0