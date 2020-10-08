#!/bin/bash

keytool -genkeypair -alias config-server-key -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -dname "CN=Config Server,OU=Gmax,O=Gmax" -keypass M89ui6tz -keystore config-server.jks -storepass 56Ftn7j8
