#!/bin/bash

# Exit on any error
set -e

echo "Starting monitoring setup..."

# Clean up existing directories with sudo to avoid permission issues
sudo rm -rf ~/perbaikiinaja/prometheus
sudo rm -rf ~/perbaikiinaja/grafana

# Create fresh directories
mkdir -p ~/perbaikiinaja
sudo mkdir -p ~/perbaikiinaja/prometheus
sudo mkdir -p ~/perbaikiinaja/grafana/provisioning/datasources
sudo mkdir -p ~/perbaikiinaja/grafana/provisioning/dashboards

# Set proper permissions
sudo chown -R $(whoami):$(whoami) ~/perbaikiinaja

# Create docker-compose.yml
cat > ~/perbaikiinaja/docker-compose.yml << 'EOT'
version: '3'
services:
  prometheus:
    image: prom/prometheus:v2.44.0
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus:/etc/prometheus
    networks:
      - monitoring-network
    restart: unless-stopped
    
  grafana:
    image: grafana/grafana:9.5.2
    container_name: grafana
    ports:
      - "3000:3000"
    volumes:
      - ./grafana/provisioning:/etc/grafana/provisioning
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    networks:
      - monitoring-network
    restart: unless-stopped
    depends_on:
      - prometheus
      
  perbaikiinaja-backend:
    image: ${DOCKERHUB_USERNAME}/perbaikiinaja-backend:staging
    container_name: perbaikiinaja-backend
    restart: always
    ports:
      - "${PORT}:8080"
    environment:
      - PG_HOST=${PG_HOST}
      - PG_USER=${PG_USER}
      - PG_PASS=${PG_PASS}
      - JWT_SECRET_KEY=${JWT_SECRET_KEY}
      - PORT=8080
    networks:
      - monitoring-network

networks:
  monitoring-network:
    driver: bridge
EOT

# Create complete prometheus.yml with global settings
cat > ~/perbaikiinaja/prometheus/prometheus.yml << 'EOT'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
        
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['perbaikiinaja-backend:8080']
        labels:
          application: 'Perbaikiinaja Spring Boot Application'
EOT

# Create Grafana datasource configuration
mkdir -p ~/perbaikiinaja/grafana/provisioning/datasources
cat > ~/perbaikiinaja/grafana/provisioning/datasources/datasources.yml << 'EOT'
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
EOT

# Create a dashboard configuration
cat > ~/perbaikiinaja/grafana/provisioning/dashboards/dashboard.yml << 'EOT'
apiVersion: 1

providers:
- name: 'Spring Boot Dashboard'
  orgId: 1
  folder: ''
  type: file
  disableDeletion: false
  editable: true
  options:
    path: /etc/grafana/provisioning/dashboards
EOT

# Create a Spring Boot dashboard
cat > ~/perbaikiinaja/grafana/provisioning/dashboards/spring-boot-dashboard.json << 'EOT'
{
  "annotations": {
    "list": [
      {
        "builtIn": 1,
        "datasource": "-- Grafana --",
        "enable": true,
        "hide": true,
        "iconColor": "rgba(0, 211, 255, 1)",
        "name": "Annotations & Alerts",
        "type": "dashboard"
      }
    ]
  },
  "editable": true,
  "gnetId": null,
  "graphTooltip": 0,
  "id": null,
  "links": [],
  "panels": [
    {
      "aliasColors": {},
      "bars": false,
      "dashLength": 10,
      "dashes": false,
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "custom": {}
        },
        "overrides": []
      },
      "fill": 1,
      "fillGradient": 0,
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "hiddenSeries": false,
      "id": 2,
      "legend": {
        "avg": false,
        "current": false,
        "max": false,
        "min": false,
        "show": true,
        "total": false,
        "values": false
      },
      "lines": true,
      "linewidth": 1,
      "nullPointMode": "null",
      "options": {
        "alertThreshold": true
      },
      "percentage": false,
      "pluginVersion": "7.3.5",
      "pointradius": 2,
      "points": false,
      "renderer": "flot",
      "seriesOverrides": [],
      "spaceLength": 10,
      "stack": false,
      "steppedLine": false,
      "targets": [
        {
          "expr": "sum by(job) (rate(http_server_requests_seconds_count[1m]))",
          "interval": "",
          "legendFormat": "HTTP Request Rate",
          "refId": "A"
        }
      ],
      "thresholds": [],
      "timeFrom": null,
      "timeRegions": [],
      "timeShift": null,
      "title": "HTTP Request Rate",
      "tooltip": {
        "shared": true,
        "sort": 0,
        "value_type": "individual"
      },
      "type": "graph",
      "xaxis": {
        "buckets": null,
        "mode": "time",
        "name": null,
        "show": true,
        "values": []
      },
      "yaxes": [
        {
          "format": "short",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        },
        {
          "format": "short",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        }
      ],
      "yaxis": {
        "align": false,
        "alignLevel": null
      }
    },
    {
      "aliasColors": {},
      "bars": false,
      "dashLength": 10,
      "dashes": false,
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "custom": {}
        },
        "overrides": []
      },
      "fill": 1,
      "fillGradient": 0,
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 12,
        "y": 0
      },
      "hiddenSeries": false,
      "id": 4,
      "legend": {
        "avg": false,
        "current": false,
        "max": false,
        "min": false,
        "show": true,
        "total": false,
        "values": false
      },
      "lines": true,
      "linewidth": 1,
      "nullPointMode": "null",
      "options": {
        "alertThreshold": true
      },
      "percentage": false,
      "pluginVersion": "7.3.5",
      "pointradius": 2,
      "points": false,
      "renderer": "flot",
      "seriesOverrides": [],
      "spaceLength": 10,
      "stack": false,
      "steppedLine": false,
      "targets": [
        {
          "expr": "system_cpu_usage",
          "interval": "",
          "legendFormat": "CPU Usage",
          "refId": "A"
        }
      ],
      "thresholds": [],
      "timeFrom": null,
      "timeRegions": [],
      "timeShift": null,
      "title": "CPU Usage",
      "tooltip": {
        "shared": true,
        "sort": 0,
        "value_type": "individual"
      },
      "type": "graph",
      "xaxis": {
        "buckets": null,
        "mode": "time",
        "name": null,
        "show": true,
        "values": []
      },
      "yaxes": [
        {
          "format": "short",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        },
        {
          "format": "short",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        }
      ],
      "yaxis": {
        "align": false,
        "alignLevel": null
      }
    },
    {
      "datasource": "Prometheus",
      "fieldConfig": {
        "defaults": {
          "custom": {},
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 80
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 8
      },
      "id": 6,
      "options": {
        "reduceOptions": {
          "calcs": [
            "mean"
          ],
          "fields": "",
          "values": false
        },
        "showThreshold": false,
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "pluginVersion": "7.3.5",
      "targets": [
        {
          "expr": "jvm_memory_used_bytes{area=\"heap\"}",
          "interval": "",
          "legendFormat": "Heap Memory Used",
          "refId": "A"
        }
      ],
      "timeFrom": null,
      "timeShift": null,
      "title": "JVM Heap Memory",
      "type": "gauge"
    }
  ],
  "refresh": "5s",
  "schemaVersion": 26,
  "style": "dark",
  "tags": [],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-15m",
    "to": "now"
  },
  "timepicker": {
    "refresh_intervals": [
      "5s",
      "10s",
      "30s",
      "1m",
      "5m",
      "15m",
      "30m",
      "1h",
      "2h",
      "1d"
    ]
  },
  "timezone": "",
  "title": "Spring Boot Dashboard",
  "uid": "spring-boot-metrics",
  "version": 0
}
EOT

# Export environment variables for envsubst
export PG_HOST="${PG_HOST}"
export PG_USER="${PG_USER}"
export PG_PASS="${PG_PASS}"
export JWT_SECRET_KEY="${JWT_SECRET_KEY}"
export PORT="${PORT}"
export DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME}"

# Substitute environment variables in docker-compose.yml
cd ~/perbaikiinaja
envsubst < docker-compose.yml > docker-compose-temp.yml
mv docker-compose-temp.yml docker-compose.yml

# Ensure Docker is running
sudo service docker start

# Pull the latest image
docker pull ${DOCKERHUB_USERNAME}/perbaikiinaja-backend:staging

# Stop and remove existing containers
docker-compose down

# Start the containers
docker-compose up -d

echo "Waiting for containers to start..."
sleep 10

# Check if containers are running
if docker ps --filter "name=perbaikiinaja-backend" --format "{{.Names}}" | grep -q perbaikiinaja-backend; then
  echo "✅ Application container is running successfully"
  echo "Application should be accessible at http://${EC2_HOST}:${PORT}"
else
  echo "❌ Error: Application container failed to start"
  docker logs perbaikiinaja-backend
  exit 1
fi

# Check monitoring services
if docker ps --filter "name=prometheus" --format "{{.Names}}" | grep -q prometheus; then
  echo "✅ Prometheus is running successfully"
  echo "Prometheus UI is accessible at http://${EC2_HOST}:9090"
else
  echo "❌ Error: Prometheus container failed to start"
  docker logs prometheus
  exit 1
fi

if docker ps --filter "name=grafana" --format "{{.Names}}" | grep -q grafana; then
  echo "✅ Grafana is running successfully"
  echo "Grafana dashboard is accessible at http://${EC2_HOST}:3000"
  echo "Default credentials: admin/admin"
else
  echo "❌ Error: Grafana container failed to start"
  docker logs grafana
  exit 1
fi

echo "Monitoring setup completed successfully! 🚀"
echo "Prometheus URL: http://${EC2_HOST}:9090"
echo "Grafana URL: http://${EC2_HOST}:3000 (login with admin/admin)"
