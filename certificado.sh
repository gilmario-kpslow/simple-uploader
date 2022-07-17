#!/bin/bash
openssl genrsa -out src/main/resources/rsaPrivateKey.pem 4096
openssl rsa -pubout -in src/main/resources/rsaPrivateKey.pem -out src/main/resources/publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in src/main/resources/rsaPrivateKey.pem -outform pem -out src/main/resources/privateKey.pem