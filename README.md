# Ecommerce CRUD API

This is a Spring Boot application providing a RESTful API for a simple e-commerce platform, allowing CRUD operations for
products, orders, and order items. The application integrates with a PostgreSQL database using Spring Data JPA and is
containerized using Docker.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)

## Introduction

- Since it was not mentioned in the Challenge document, I assumed the following for the data:
- Product: Id, Name, Price, unitsInStock
- OrderItem: Id, ProductId, Quantity, OrderItemPrice(ProductPrice * Quantity)
- Order: Id, CustomerName, Address, ListOfOrderItems, TotalPrice(Sum of OrdemItemPrice in List)
- Constraints:
    - There cannot be Products with the same name, and Products are not deleted along with the OrderItem, just their stock quantity
      changes
    - When an OrderItem is Created, its "quantity" must not surpass the Products "unitsInStock"
    - When OrderItem is created, it deducts the stock of the Product for its quantity
    - A Product cannot be Deleted or Updated while an OrderItem is active with its ID
    - When an Order is created with an OrderItem, The OrderItem cannot be altered or deleted, unless the Order is deleted or
      updated to remove it
    - If an OrderItem is deleted and is not inside an Order, Product gets it´s stock refilled, Otherwise its considered sold to the customer
    - Order is the terminal operation, it holds an Array of OrderItems for the customer and TotalPrice for the
      OrderItems, and once it is deleted, the Product´s stock does not return
    - A Customer can only have one Order open at a time, and since we don´t have CRUD operations for them, it will be
      filtered by name
- I opted to use Lombok for readability and simplicity
- Errors are handled by GlobalExceptionHandler, and they all have a default format of ErrorResponse
- There are 60 Tests in total, covering the Controllers and Services that run after every `mv clean install`
  or `mvn test`

## Features

- CRUD operations for:
    - Products
    - Orders
    - OrderItems
- Integration with PostgreSQL database using Spring Data JPA.
- Dockerized application for easy setup and scalability.
- API documentation using OpenAPI 3.0.
- Automated tests with JUnit and Mockito.
- Error handling and appropriate HTTP status codes.
- Lombok used to minimize boilerplate code and improve readability

## Prerequisites

- Java 17
- Maven
- Docker & Docker Compose
- Lombok

## Setup & Installation

1. Build the project:
    - `mvn clean install`

2. Set up the database:
    - `docker-compose up -d db`

3. Build the Docker image:
    - `docker build -t ecommerce-crud-api .`

## Running the Application

- Using Docker:
    - `docker-compose up`

- Access the Swagger UI to Interact with the Endpoints:
    - `http://localhost:8080/v3/swagger-ui/index.html`

- Path for ApiDocs:
    - `http://localhost:8080/v3/api-docs`

## Testing

The application has unit and integration tests written using JUnit and Mockito.

- Run the tests using:
    - `mvn test`