#!/bin/bash

# Script to deploy PerbaikiinAja to Kubernetes
# Run this script on your production EC2 instance

echo "Deploying PerbaikiinAja to Kubernetes..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "kubectl not found. Please install Kubernetes first."
    exit 1
fi

# Check if k8s directory exists
if [ ! -d "k8s" ]; then
    echo "k8s directory not found. Please ensure you're in the project root directory."
    exit 1
fi

cd k8s

# Apply namespace
echo "Creating namespace..."
kubectl apply -f namespace.yaml

# Apply ConfigMap
echo "Applying ConfigMap..."
kubectl apply -f configmap.yaml

# Apply Deployment
echo "Applying Deployment..."
kubectl apply -f deployment.yaml

# Apply Service
echo "Applying Service..."
kubectl apply -f service.yaml

# Wait for deployment to be ready
echo "Waiting for deployment to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/perbaikiinaja-backend -n perbaikiinaja-prod

# Check status
echo "Deployment Status:"
kubectl get pods -n perbaikiinaja-prod
kubectl get services -n perbaikiinaja-prod

# Get service URL
NODE_PORT=$(kubectl get service perbaikiinaja-backend-service -n perbaikiinaja-prod -o jsonpath='{.spec.ports[0].nodePort}')
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)

echo
echo "==================================="
echo "Deployment completed successfully!"
echo "Application URL: http://${PUBLIC_IP}:${NODE_PORT}"
echo "==================================="