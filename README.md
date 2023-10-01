# Ecommerce CRUD API

This is a Spring Boot application providing a RESTful API for a simple e-commerce platform, allowing CRUD operations for products, orders, and order items. The application integrates with a PostgreSQL database using Spring Data JPA and is containerized using Docker.

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)
## Introduction

- 

## Features

- CRUD operations for:
    - Products
    - Orders
    - Order items
- Integration with PostgreSQL database using Spring Data JPA.
- Dockerized application for easy setup and scalability.
- API documentation using OpenAPI 3.0.
- Automated tests with JUnit and Mockito.
- Graceful error handling and appropriate HTTP status codes.

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

- Access the OpenAPI documentation at `http://localhost:8080/v3/swagger-ui/index.html`.

## Testing

The application has unit and integration tests written using JUnit and Mockito. 
- Run the tests using:
    - `mvn test`