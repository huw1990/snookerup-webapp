#!/usr/bin/env bash
set -xeu pipefail

# 1. Update OS packages & Install Docker, AWS CLI, Certbot
apt-get update -y
apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release unzip certbot cron

# Add Docker official GPG key & repo
mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Install AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-aarch64.zip" -o "awscliv2.zip"
unzip -q awscliv2.zip
./aws/install
rm -rf aws awscliv2.zip

# Enable and start Docker
systemctl enable docker
systemctl start docker

# 2. Setup 2GB Swap Memory (Crucial for 2GB RAM t4g.small server)
if [ ! -f /swapfile ]; then
    fallocate -l 2G /swapfile
    chmod 600 /swapfile
    mkswap /swapfile
    swapon /swapfile
    echo '/swapfile none swap sw 0 0' >> /etc/fstab
    echo 'vm.swappiness=10' >> /etc/sysctl.conf
    sysctl -p
fi

# 3. Create Application Folder Structure
APP_DIR="/opt/${app_name}"
mkdir -p $${APP_DIR}/nginx/conf.d
mkdir -p $${APP_DIR}/nginx/certs
mkdir -p $${APP_DIR}/backups
mkdir -p $${APP_DIR}/data/postgres
mkdir -p $${APP_DIR}/data/mongo

# 4. Generate Random Passwords & Application Configs
POSTGRES_PASS=$(openssl rand -base64 24 | tr -dc 'a-zA-Z0-9')
MONGO_PASS=$(openssl rand -base64 24 | tr -dc 'a-zA-Z0-9')

COGNITO_ISSUER_URI="https://cognito-idp.${aws_region}.amazonaws.com/${cognito_user_pool_id}"
COGNITO_LOGOUT_URL="https://${cognito_domain_prefix}.auth.${aws_region}.amazoncognito.com/logout?client_id=${cognito_client_id}&logout_uri=https://${domain_name}"

# Create environment file
cat <<EOF > $${APP_DIR}/.env
APP_NAME=${app_name}
AWS_REGION=${aws_region}
DOMAIN_NAME=${domain_name}
ECR_REPO_URI=${ecr_repo_uri}
ECR_REPO_URI_SEEDER=${ecr_repo_uri_seeder}
POSTGRES_DB=snookerup_db
POSTGRES_USER=snookerup_user
POSTGRES_PASSWORD=$${POSTGRES_PASS}
MONGO_INITDB_ROOT_USERNAME=mongo_admin
MONGO_INITDB_ROOT_PASSWORD=$${MONGO_PASS}
COGNITO_USER_POOL_ID=${cognito_user_pool_id}
COGNITO_CLIENT_ID=${cognito_client_id}
COGNITO_CLIENT_SECRET=${cognito_client_secret}
COGNITO_ISSUER_URI=$${COGNITO_ISSUER_URI}
COGNITO_LOGOUT_URL=$${COGNITO_LOGOUT_URL}
INVITE_CODES=${invite_codes}
EOF
chmod 600 $${APP_DIR}/.env

# 5. Write docker-compose.yml
cat <<EOF > $${APP_DIR}/docker-compose.yml
version: '3.8'

services:
  nginx:
    image: nginx:alpine
    container_name: snookerup-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/conf.d:/etc/nginx/conf.d:ro
      - ./nginx/certs:/etc/letsencrypt:ro
      - /var/www/certbot:/var/www/certbot:ro
    depends_on:
      - app
    networks:
      - app-network
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  app:
    image: $${ECR_REPO_URI}:latest
    container_name: snookerup-app
    restart: unless-stopped
    environment:
      - SPRING_PROFILES_ACTIVE=aws
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/$${POSTGRES_DB}
      - SPRING_DATASOURCE_USERNAME=$${POSTGRES_USER}
      - SPRING_DATASOURCE_PASSWORD=$${POSTGRES_PASSWORD}
      - SPRING_DATA_MONGODB_HOST=mongo
      - SPRING_DATA_MONGODB_PORT=27017
      - SPRING_DATA_MONGODB_DATABASE=snookerup_catalog
      - MONGO_INITDB_ROOT_USERNAME=$${MONGO_INITDB_ROOT_USERNAME}
      - MONGO_INITDB_ROOT_PASSWORD=$${MONGO_INITDB_ROOT_PASSWORD}
      - SPRING_DATA_MONGODB_USERNAME=$${MONGO_INITDB_ROOT_USERNAME}
      - SPRING_DATA_MONGODB_PASSWORD=$${MONGO_INITDB_ROOT_PASSWORD}
      - SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE=admin
      - COGNITO_USER_POOL_ID=$${COGNITO_USER_POOL_ID}
      - COGNITO_ISSUER_URI=$${COGNITO_ISSUER_URI}
      - COGNITO_CLIENT_ID=$${COGNITO_CLIENT_ID}
      - COGNITO_CLIENT_SECRET=$${COGNITO_CLIENT_SECRET}
      - COGNITO_LOGOUT_URL=$${COGNITO_LOGOUT_URL}
      - SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=$${COGNITO_ISSUER_URI}
      - AWS_REGION=$${AWS_REGION}
      - INVITE_CODES=$${INVITE_CODES}
      - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_COGNITO_REDIRECT_URI: "{baseUrl}/login/oauth2/code/{registrationId}"
    depends_on:
      postgres:
        condition: service_healthy
      mongo:
        condition: service_healthy
    networks:
      - app-network
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  postgres:
    image: postgres:16-alpine
    container_name: snookerup-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: $${POSTGRES_DB}
      POSTGRES_USER: $${POSTGRES_USER}
      POSTGRES_PASSWORD: $${POSTGRES_PASSWORD}
    volumes:
      - ./data/postgres:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER} -d $${POSTGRES_DB}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-network
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  mongo:
    image: mongo:7.0
    container_name: snookerup-mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: $${MONGO_INITDB_ROOT_USERNAME}
      MONGO_INITDB_ROOT_PASSWORD: $${MONGO_INITDB_ROOT_PASSWORD}
    volumes:
      - ./data/mongo:/data/db
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-network
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  seeder:
      image: $${ECR_REPO_URI_SEEDER}:latest
      container_name: snookerup-seeder
      profiles: ["tools"]
      environment:
        - MONGO_HOST=mongo
        - MONGO_PORT=27017
        - MONGO_DATABASE=snookerup_catalog
        - MONGO_USERNAME=$${MONGO_INITDB_ROOT_USERNAME}
        - MONGO_PASSWORD=$${MONGO_INITDB_ROOT_PASSWORD}
        - MONGO_AUTH_DB=admin
      depends_on:
        mongo:
          condition: service_healthy
      networks:
        - app-network

networks:
  app-network:
    driver: bridge
EOF

# 6. Generate Temporary Nginx Bootstrap Config
cat <<EOF > $${APP_DIR}/nginx/conf.d/app.conf
server {
    listen 80;
    server_name ${domain_name};

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 200 "Bootstrapping TLS certificates...";
        add_header Content-Type text/plain;
    }
}
EOF

# Create directory for certbot challenge
mkdir -p /var/www/certbot
chmod -R 755 /var/www/certbot

# Start temporary Nginx container for ACME challenge
docker run -d \
  --name nginx-bootstrap \
  -p 80:80 \
  -v /var/www/certbot:/var/www/certbot \
  -v $${APP_DIR}/nginx/conf.d/app.conf:/etc/nginx/conf.d/default.conf:ro \
  nginx:alpine

# 7. Setup systemd Service for Docker Compose
cat <<EOF > /etc/systemd/system/${app_name}.service
[Unit]
Description=SnookerUp Docker Compose Application Stack
Requires=docker.service
After=docker.service

[Service]
Type=simple
WorkingDirectory=$${APP_DIR}
ExecStart=/usr/bin/docker compose up
ExecStop=/usr/bin/docker compose down
Restart=always
RestartSec=10s

[Install]
WantedBy=multi-user.target
EOF

cd $${APP_DIR}
aws ecr get-login-password --region ${aws_region} | docker login --username AWS --password-stdin ${ecr_repo_uri} || true
docker compose pull app || echo "WARNING: ECR image not found yet. Ready for first deploy pipeline run."

# Enable and attempt to start service
systemctl daemon-reload
systemctl enable ${app_name}.service
systemctl start ${app_name}.service || true

# 8. Obtain Let's Encrypt Certificate with retry mechanism
for i in {1..5}; do
  certbot certonly --webroot -w /var/www/certbot \
      -d ${domain_name} \
      --email ${admin_email} \
      --agree-tos --non-interactive && break || sleep 15
done

# Copy generated certificates into the Docker volume mount path
cp -rL /etc/letsencrypt/* $${APP_DIR}/nginx/certs/

# Stop temporary bootstrap container
docker stop nginx-bootstrap && docker rm nginx-bootstrap

# 9. Write Production Nginx Config with HTTPS and Proxying
if [ -d "/etc/letsencrypt/live/${domain_name}" ]; then
    cat <<EOF > $${APP_DIR}/nginx/conf.d/app.conf
server {
    listen 80;
    server_name ${domain_name};

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name ${domain_name};

    ssl_certificate /etc/letsencrypt/live/${domain_name}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${domain_name}/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    client_max_body_size 20M;

    location / {
        proxy_pass http://app:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF
    # Restart Nginx container to pick up certs
    cd $${APP_DIR} && docker compose restart nginx
fi

# 10. Automated Let's Encrypt Certificate Renewal
cat << EOF > $${APP_DIR}/renew-certs.sh
#!/bin/bash
certbot renew --webroot -w /var/www/certbot --quiet
cp -rL /etc/letsencrypt/* $${APP_DIR}/nginx/certs/
docker exec snookerup-nginx nginx -s reload
EOF

chmod +x $${APP_DIR}/renew-certs.sh

# Add to root crontab (runs twice daily at 2 AM and 2 PM)
(crontab -l 2>/dev/null; echo "0 2,14 * * * $${APP_DIR}/renew-certs.sh >> /var/log/certbot-renewal.log 2>&1") | crontab -

# 11. Configure Daily Backup Cron Job
cat <<EOF > /usr/local/bin/snookerup-backup.sh
#!/usr/bin/env bash
set -euo pipefail

APP_DIR="$${APP_DIR}"
BACKUP_DIR="\$${APP_DIR}/backups"
TIMESTAMP=\$$(date +%Y%m%d_%H%M%S)

source \$${APP_DIR}/.env

# 1. Dump Postgres
docker exec snookerup-postgres pg_dump -U \$${POSTGRES_USER} \$${POSTGRES_DB} | gzip > "\$${BACKUP_DIR}/postgres_\$${TIMESTAMP}.sql.gz"

# 2. Dump Mongo
docker exec snookerup-mongo mongodump --username \$${MONGO_INITDB_ROOT_USERNAME} --password \$${MONGO_INITDB_ROOT_PASSWORD} --authenticationDatabase admin --archive | gzip > "\$${BACKUP_DIR}/mongo_\$${TIMESTAMP}.archive.gz"

# 3. Sync backups off the server to S3
aws s3 sync \$${BACKUP_DIR} s3://${s3_backup_bucket}/database-backups/ --region ${aws_region}

# 4. Clean up local dumps older than 7 days
find \$${BACKUP_DIR} -type f -mtime +7 -delete
EOF

chmod +x /usr/local/bin/snookerup-backup.sh

# Run backup daily at 02:00 AM
echo "0 2 * * * root /usr/local/bin/snookerup-backup.sh > /var/log/snookerup-backup.log 2>&1" > /etc/cron.d/snookerup-backup

echo "=== Cloud-init finished successfully ==="