# perbaikiin-aja

## Main App

**Context Diagram**

![context diagram drawio](https://github.com/user-attachments/assets/bd13b0cd-f115-4866-8463-7c697e0f04fd)

**Container Diagram**

![container diagram drawio](https://github.com/user-attachments/assets/cc7add40-2275-4e82-b85b-0535658a5f17)


## Features

## Fitur 2 dan 3 (karena component dan code saling overlap)

Component Diagram
![component diagram fitur 2 dan 3](assets/component-diagram-feature-2-and-3.png)

Code Diagram
![code diagram fitur 2 dan 3](assets/Feature3andFeature2CodeDiagram.png)

## Review

**Container Diagram**

![container diagram review drawio](https://github.com/user-attachments/assets/a3e7d1a3-bced-4be2-9cf3-e97c5d52182a)

* **Front End (React.js)**

  * Delivers static content to users.
  * Handles review-related actions:

    * Posting reviews after service completion (POST).
    * Viewing technician reviews (GET).
    * Modifying existing reviews (PUT).
    * Delete existing review (DELETE)

* **Back End (Spring Boot REST API)**

  * Provides API endpoints for communication between frontend and database.
  * Handles JSON-based requests and responses.
  * Key endpoints:

    * `/reviews` - General review operations.
    * `/reviews/{id}` - Access or modify specific reviews.
    * `/reviews/technician/{technician_id}` - Fetch reviews for a specific technician.

* **Database (PostgreSQL)**

  * Stores review entities, including ratings and comments.
  * Supports data persistence for efficient role-based interactions.
  * Integrates with the backend for reading and writing review data.

## Payment Method
**Container Diagram**

![container diagram payment method](assets/PaymentMethodContainerDiagram.png)

**Code Diagram**

![code diagram payment method](assets/PaymentMethodCodeDiagram.png)