# Comand_app
## Description:
TODO Brief description of the project
## Kubernetes
for kubernetes deployment follow the instruction in the [kubernetes](kubernetes/README.md) folder

## Docker
Docker deployment can be done separately for each microservice or using docker-compose.

All the pre-built images are available on [Docker Hub](https://hub.docker.com/repository/docker/scavolini/comand_app/general) or can be Built using Dockerfile in the root of each microservice folder.

### Docker-compose
Two docker-compose files are available in the root of the project:
- docker-compose.yml
- docker-compose-development.yml

The first one pulls the images from Docker Hub and the second one builds the images locally.
To use the first one run the following command:
```bash
docker-compose up
```
To use the second one first the backend must be started
```bash
docker-compose -f docker-compose-development.yml up
```
and then the frontend can be started. go to the [frontend repository](https://github.com/lory9894/command_app_frontend) or open the submodule folder and run:
```bash
docker-compose docker-compose up
```
