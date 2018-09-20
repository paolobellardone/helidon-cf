
# Helidon Example: codicefiscale

This example implements a simple REST service using MicroProfile

## Prerequisites

1. Maven 3.5 or newer
2. Java SE 8 or newer
3. Docker 17 or newer (if you want to build and run docker images)
4. Kubernetes minikube v0.24 or newer (if you want to deploy to Kubernetes)
   or access to a Kubernetes 1.7.4 or newer cluster
5. Kubectl 1.7.4 or newer for deploying to Kubernetes

Verify prerequisites
```
java --version
mvn --version
docker --version
minikube version
kubectl version --short
```

## Build

```
mvn package
```

## Start the application

```
java -jar target/codicefiscale.jar
```

## Exercise the application

```
curl -H "Content-Type: application/json" -X POST -d "{ \"name\": \"Mario\", \"surname\": \"Rossi\", \"gender\": \"M\", \"day\": \"01\", \"month\": \"01\", \"year\": \"1970\", \"town\": \"Roma\" }" http://localhost:8080/evalcf
{"taxCode":"MRARSS70A01H501R"}
```

## Build the Docker Image

```
docker build -t codicefiscale target
```

## Start the application with Docker

```
docker run --rm -p 8080:8080 codicefiscale:latest
```

Exercise the application as described above

## Deploy the application to Kubernetes

```
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f target/app.yaml            # Deploy application
kubectl get service codicefiscale            # Verify deployed service
```
