# Network
run this command to create a docker network to make the database container able to communicate with the backend container: 
```bash
docker network create chatapp-network
```

# Database Creation
With this command a postgres container will be executed: 
```bash
docker run --name postgres-db --restart=unless-stopped --network chatapp-network -e POSTGRES_USER=root -e POSTGRES_PASSWORD=root -e POSTGRES_DB=login_data_db -p 5432:5432 -d postgres
```