### Run these commands in the Node hosting the app to run it in Kubernetes
### Run them in the folder containing the compose file (es. docker-compose-raspi)

<br>

1. If kubernetes .yaml files are not present

>kompose convert -f .\docker-compose-raspi.yml

<br>

2. Start kubernetes cluster
>minikube start

<br>

3. Create deployments, services, volumes and networks
>kubectl apply -f .

<br>

4. Ensure all pods are running without errors of type ``CrashLoopBackOff``
>kubectl get pods
    
5. If not run the following and re-start from point 1
>kubectl delete -f .

<br>

6. Expose ports of microservices needed. Example:
>kubectl port-forward svc/reservation 8080:8080

<br>

### WARNING:
> For as Kubernetes works there are no options to make dependencies between pods.
<br> It's normal that some pods will crash and restart sometimes before the system is up and running.
<br> This because they cannot wait for other pods with some dependency
<br> (Example: reservation pod (microservice) will restart sometimes because it cannot connect to his database.
<br> Sometimes it happens that some pods will stuck in ``ContainerCreating`` state and then they crash generating a
``CrashLoopBackOff`` error (can be seen from ``kubectl get pods`` cmd) . In this case proceed as explained in point 5.
<br> It can take from very few up to 10 minutes for the system to be up and running.