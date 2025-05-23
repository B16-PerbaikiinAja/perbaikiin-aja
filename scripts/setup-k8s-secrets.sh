#!/bin/bash

# Script to set up Kubernetes secrets for production
# Run this script on your production EC2 instance

echo "Setting up Kubernetes secrets for PerbaikiinAja Production..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "kubectl not found. Please install Kubernetes first."
    exit 1
fi

# Prompt for values
echo "Please enter the following values:"
read -p "Production Database Host (jdbc:postgresql://...): " PROD_PG_HOST
read -p "Production Database User: " PROD_PG_USER
read -s -p "Production Database Password: " PROD_PG_PASS
echo
read -s -p "Production JWT Secret Key: " PROD_JWT_SECRET_KEY
echo
read -p "DockerHub Username: " DOCKERHUB_USERNAME
read -s -p "DockerHub Token: " DOCKERHUB_TOKEN
echo

# Create namespace
echo "Creating namespace..."
kubectl create namespace perbaikiinaja-prod --dry-run=client -o yaml | kubectl apply -f -

# Create application secrets
echo "Creating application secrets..."
kubectl delete secret perbaikiinaja-secrets -n perbaikiinaja-prod --ignore-not-found
kubectl create secret generic perbaikiinaja-secrets \
  --from-literal=PG_HOST="${PROD_PG_HOST}" \
  --from-literal=PG_USER="${PROD_PG_USER}" \
  --from-literal=PG_PASS="${PROD_PG_PASS}" \
  --from-literal=JWT_SECRET_KEY="${PROD_JWT_SECRET_KEY}" \
  --namespace=perbaikiinaja-prod

# Create DockerHub secret
echo "Creating DockerHub secret..."
kubectl delete secret dockerhub-secret -n perbaikiinaja-prod --ignore-not-found
kubectl create secret docker-registry dockerhub-secret \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username="${DOCKERHUB_USERNAME}" \
  --docker-password="${DOCKERHUB_TOKEN}" \
  --namespace=perbaikiinaja-prod

echo "Secrets created successfully!"
echo "You can now deploy your application using the Kubernetes manifests."