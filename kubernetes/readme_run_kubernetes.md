### Run these commands in the Node hosting the app to run it in Kubernetes
### Run them in the folder containing the compose file (es. docker-compose-raspi)

<br>

if kubernetes .yaml files are not present

>kompose convert -f .\docker-compose-raspi.yml

<br>

start kubernetes cluster
>minikube start

<br>

create deployments, services, volumes and networks
>kubectl apply -f .

<br>

ensure all pods are running without errors
>kubectl get pods
    
if not run
>kubectl delete -f .

<br>

expose ports of microservices needed example:
>kubectl port-forward svc/reservation 8080:8080