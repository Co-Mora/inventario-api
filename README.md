 Sistema de Gestión de Inventario
 
![diagram](https://github.com/user-attachments/assets/ecadb5f0-5dbb-40bf-8086-a58a7e66666d)

This is a reactive Spring Boot-based inventory management system. It uses Kafka for messaging, MongoDB as the database, and is containerized with Docker for easy setup. This project allows managing products with CRUD operations and notification events using Kafka.


Step 1: Run Docker Compose

Run the following command to build and start the containers:

`docker-compose up --build`

Kafka Topic

The system uses Kafka to handle notifications for product changes (create, update, delete). To create the Kafka topic manually (if required):

	1. Connect to the Kafka container:
    $ docker exec -it kafka bash
	2. Create a topic for product events:
    $ kafka-topics --create --topic product-events --bootstrap-server kafka:9092 --partitions 1 --replication-factor 1