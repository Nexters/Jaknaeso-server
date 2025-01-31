#!/bin/bash

NCP_ACCESS_KEY=$1
NCP_SECRET_KEY=$2
NCP_API_URL=$3

NCP_CLI_DOWNLOAD_URL="https://www.ncloud.com/api/support/download/5/65"

cd ~

wget -O ncp-cli.zip $NCP_CLI_DOWNLOAD_URL
unzip ncp-cli.zip

mkdir ~/.ncloud

echo "[DEFAULT]" > ~/.ncloud/configure
echo "ncloud_access_key_id = $NCP_ACCESS_KEY" >> ~/.ncloud/configure
echo "ncloud_secret_access_key = $NCP_SECRET_KEY" >> ~/.ncloud/configure
echo "ncloud_api_url = $NCP_API_URL" >> ~/.ncloud/configure