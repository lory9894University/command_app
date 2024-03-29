# ComandApp
## Description:
This projects contains two distinguished cross-platform applications (web app + android) whose backend is based on a microservice architecture.
The application scopes are:
- **ComandApp - Cliente**: An application for the customers of the restaurant. The customers can:
  - **Book a table** for a specific date and time and, if they want, provide their order in advance.
  - **Pre-order** a meal to be prepared for the time of the reservation.
  - Order a **Takeaway** meal and pick it up at the restaurant.
  - Order a **Delivery** meal and have it delivered at their home.
- **ComandApp - Gestore**: An application for the restaurant manager. The manager can:
  - **Visualize and manage the preparations on the kitchen side**, changing the state of the preparations between "waiting", "underway" and "ready".
  - **Visualize and manage the preparations on the waiter side**, changing the state of the cooked preparations between "to deliver" and "delivered".
  - **Visualize and manage the bookings**, approving or rejecting them.

## Microservices overview
- Menu
  - Handles the list of meals and drinks available. 
  - Can add, remove or update a meal or a drink.
- Kitchen
  - Handles the list of preparation for the kitchen side.
  - The preparation can be in one of the following states:
    - Waiting (the preparation is waiting to be cooked)
    - Underway (the preparation is being cooked)
    - Ready (the preparation is ready to be delivered)
  - The preparation state can be updated by the kitchen staff.
- Waiter
  - Handles the list of preparation for the waiter side.
  - The preparation can be in one of the following states:
    - To deliver
    - Delivered
  - The preparation state can be updated by the waiter staff.
- Order
  - Receives the particular kind of order from the customer.
  - Sends the preparations to cook to the kitchen based on policies.
  - Provides the list of orders to the staff.
  - Accepts or rejects the orders which are not in restaurant orders.
- Reservation
  - Receives the reservation from the customer.
  - Sends the reservation to the order microservice.
  - Accepts or rejects the reservation or pre-order.

## Kubernetes
For kubernetes deployment follow the instruction in the [kubernetes](kubernetes/README.md) folder

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
