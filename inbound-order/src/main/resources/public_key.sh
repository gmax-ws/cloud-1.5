#!/bin/bash

keytool -list -rfc --keystore jwt.jks | openssl x509 -inform pem -pubkey
