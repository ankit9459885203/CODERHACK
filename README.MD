# Coderhack Leaderboard API

**This project implements a RESTful API service for managing a single contest leaderboard for a coding platform, as specified in the Coderhack challenge. It handles user registrations, score updates, and automatic badge assignments, with data persistence managed by MongoDB.**

## Table of Contents

* [Features](https://www.google.com/search?q=%23features "null")
* [Architecture](https://www.google.com/search?q=%23architecture "null")
* [Technologies Used](https://www.google.com/search?q=%23technologies-used "null")
* [Setup Instructions](https://www.google.com/search?q=%23setup-instructions "null")
* [API Endpoints](https://www.google.com/search?q=%23api-endpoints "null")
* [Postman Collection](https://www.google.com/search?q=%23postman-collection "null")
* [Testing](https://www.google.com/search?q=%23testing "null")

## Features

* **User Registration:** Register new users with a unique ID and username.
* **Score Management:** Update user scores (0-100).
* **Dynamic Badge Assignment:** Automatically awards badges (`<span class="selected">CODE_NINJA</span>`, `<span class="selected">CODE_CHAMP</span>`, `<span class="selected">CODE_MASTER</span>`) based on score thresholds. Badges are retained once earned.
* **User Retrieval:** Fetch individual user details or a list of all registered users.
* **Leaderboard Sorting:** All users are retrieved sorted by score in ascending order.
* **User Deregistration:** Delete users from the leaderboard.
* **Validation & Error Handling:** Basic input validation and appropriate HTTP status codes (e.g., 400 Bad Request, 404 Not Found, 409 Conflict).

## Architecture

**The application follows a standard layered architecture for Spring Boot applications:**

* **Controller Layer :** Handles incoming HTTP requests, delegates business logic to the service layer, and returns HTTP responses.
* **Service Layer :** Contains the core business logic, interacts with the repository layer, and applies validation and badge awarding rules.
* **Repository Layer  :** Interfaces with the MongoDB database using Spring Data MongoDB to perform CRUD operations on `<span class="selected">User</span>` entities.
* **Entity Layer :** Defines the data model (`<span class="selected">User</span>` and `<span class="selected">Badge</span>` enum) that maps to MongoDB documents. Lombok is used for boilerplate code reduction in the `<span class="selected">User</span>` entity.

## Technologies Used

* **Java 17**
* **Spring Boot 3.2.3:** Framework for building the RESTful API.
* **Spring Data MongoDB:** For seamless interaction with MongoDB.
* **Lombok:** To reduce boilerplate code in entities (getters, setters, constructors).
* **MongoDB:** NoSQL database for data persistence.
* **Gradle:** Build automation tool.
* **JUnit 5 & Mockito:** For unit testing.

## Testing

**The project includes JUnit 5 unit tests for the **`<span class="selected">UserService</span>` layer.

**To run the unit tests:    *./gradlew test**

**To run the full assessment tests (which typically involve API calls against a running instance of your application):
        ./runAssessment.sh
