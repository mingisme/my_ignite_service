# my_ignite_service

## Install orbstack
https://orbstack.dev

## Docker image build
docker build -t my-ignite:1.0 .

## Start my-ignite
kubectl apply -f k8s/myignite.yaml

## Test my-ignite cluster

### Enter into pod1
kubectl exec my-ignite-service-6444d6b8f6-jtqnr -it -- bash

### Run curl to put cache
curl --location 'localhost:8080/api/set' \
--header 'Content-Type: application/json' \
--data '{
"key":"hello",
"value":"ignite1"
}'

### Enter into pod2
kubectl exec my-ignite-service-6444d6b8f6-m25wn -it -- bash

### Run curl to get cache
curl --location 'localhost:8080/api/get?key=hello'

## Forward port 8080
kubectl port-forward svc/my-ignite-service-1 8080:8080

## Test monitor in web browser or with curl
curl --location 'localhost:8080/monitor/nodes'

curl --location 'localhost:8080/monitor/key-location?key=hello'




