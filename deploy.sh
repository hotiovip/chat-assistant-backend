#!/bin/bash

# Pull the latest changes from GitHub
git pull

# Build the Docker image (make sure your Dockerfile is in the project root)
docker build -t chatapp-backend .

# Stop the running container (optional)
docker container stop chatapp-backend

# Remove the old container (optional)
docker container rm chatapp-backend

# Run the new container with the built image
docker run -d --name chatapp-backend --network chatapp-network --restart=unless-stopped -p 8080:8080 chatapp-backend