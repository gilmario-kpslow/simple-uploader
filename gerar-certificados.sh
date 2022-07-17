#!/bin/bash
#
echo "0"
openssl genrsa -out src/main/resources/privatekey.key 4096
echo "1"
openssl req -new -key src/main/resources/privatekey.key -out src/main/resources/request.csr -config configuration.cnf
echo "2"
openssl x509 -req -days 3650 -in src/main/resources/request.csr -signkey src/main/resources/privatekey.key -out src/main/resources/cert.crt
echo "3"
cat src/main/resources/privatekey.key src/main/resources/cert.crt > src/main/resources/certificado.pem
cat src/main/resources/cert.crt > src/main/resources/public.pem