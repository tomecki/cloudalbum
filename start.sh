#!/usr/bin/env bash
set -v
cd sr/cloudalbum
git pull
if [ ! -f hosts ] 
then
    echo "discovering hosts"
    ./discoverHosts.sh > hosts
    echo "total hosts discovered: "
    cat hosts
    ./generateZones.py < hosts
fi


if [ ! -f private_key.pem ]
then
    openssl genrsa -out private_key.pem 1024
    openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem  -out private_key.der -nocrypt
    openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
fi
ant
cat selectedHosts | cut -d ',' -f 1 | cut -d '/' -f 5 > hostbuffer

# Run Query Signer
queryNode=`cat hostbuffer | head -n 1`
echo "Running Query Signer on "$queryNode
ssh "td366732@"$queryNode < QuerySigner.sh
echo "Query Signer up and running at "$queryNode

# Run Agent-Fetcher

for nodehost in `cat hostbuffer |  sed ':a;N;$!ba;s/\n/ /g'`
do
    echo "running singlenode script on "$nodehost
    ssh "td366732@"$nodehost < singlenode.sh
    echo "singlenode continuing"
done

rm hostbuffer
set +v
