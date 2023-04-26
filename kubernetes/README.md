## Follow the instructions below to run the app in Kubernetes

### Tools needed
<!-- Check files indexing, Lorenzo guarda se le istruzioni linux sono sensate-->
**Minikube** is needed to locally run kubernetes. <br>
If you have not installed it yet, follow the instruction in the [install-minikube](install-minikube.md) markdown file.

**Kompose** is needed to convert docker-compose file to kubernetes .yaml files. <br>
If you have not installed it yet, follow the instruction in the [install-kompose](install-kompose.md) markdown file.



---


#### Kubernetes will download microservices images from docker hub, if those are not updated (or you want to update them) do the following before dealing with kubernetes:

- Delete old containers and images
- Run the docker compose in order to **create images locally**
  - In our application
    - `docker-compose-development.yml` for the backend in the root folder
    - `docker-compose.yml` for the frontend in the root of the frontend repository
- Run the following for tagging images to prepare them for upload
```bash
docker tag scavolini-reservation scavolini/comand_app:reservation
docker tag scavolini-order scavolini/comand_app:order
docker tag scavolini-waiter scavolini/comand_app:waiter
docker tag scavolini-kitchen scavolini/comand_app:kitchen
docker tag scavolini-menu scavolini/comand_app:menu
docker tag scavolini-gateway scavolini/comand_app:gateway
```
- If needed also tag the frontend image
```bash
docker tag frontend scavolini/comand_app:frontend
```

- Run the following for uploading images to docker hub
```bash
docker push scavolini/comand_app:reservation
docker push scavolini/comand_app:order
docker push scavolini/comand_app:waiter
docker push scavolini/comand_app:kitchen
docker push scavolini/comand_app:menu
docker push scavolini/comand_app:gateway
```
- If needed also push the frontend image
```bash
docker push scavolini/comand_app:frontend
```

_Note: if needed you can also tag and push single images instead of all of them_

---

### Run the following commands in the Node hosting the app to run it in Kubernetes
#### Run them in the folder containing the compose file that pulls the images from docker-hub (in our application `docker-compose.yml`)

---

1. If kubernetes .yaml files are not present
```bash
kompose convert
```

<br>

2. Start kubernetes cluster
```bash
minikube start
```

> **NOTE**: if you started minikube before and you have uploaded new images you have first to delete 
> minikube container, image and **volume** as it will use the old image otherwise stored in its volume
> instead of downloading the new one from docker hub.

<br>

3. Create deployments, services, volumes and networks
```bash
kubectl apply -f .
```

<br>

4. Ensure all pods are running without errors of type ``CrashLoopBackOff``
```bash
kubectl get pods
```

5. If not run the following and re-start from point 1
```bash
kubectl delete -f .
```
<br>

6. Expose ports of microservices needed. Example:
```bash
kubectl port-forward svc/gateway 8080:8080
kubectl port-forward svc/frontend 80:80
```
<br>

### WARNING:
> For as Kubernetes works there are no options to make dependencies between pods.
<br> It's normal that some pods will crash and restart sometimes before the system is up and running.
<br> This because they cannot wait for other pods with some dependency
<br> (Example: reservation pod (microservice) will restart sometimes because it cannot connect to his database.
<br> Sometimes it happens that some pods will stuck in ``ContainerCreating`` state and then they crash generating a
``CrashLoopBackOff`` error (can be seen from ``kubectl get pods`` cmd) . In this case proceed as explained in point 5.
<br> It can take from very few up to 10 minutes for the system to be up and running.