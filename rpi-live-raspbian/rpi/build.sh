#!/bin/bash

this_path=$(cd `dirname $0`;pwd)  # $(pwd)
echo "pwd=${this_path}"




cd ${this_path}/../third/curl/lib
ln -s libcurl.so.4.5.0 libcurl.so
ln -s libcurl.so.4.5.0 libcurl.so.4

ln -s libcrypto.so.3 libcrypto.so
ln -s libssl.so.3 libssl.so

cd ${this_path}

make clean && make 
echo "success"
