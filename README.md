# Levi9 Challenge Backend

Welcome to the **Levi9 Challenge Backend** repository! This is a project developed to manage teams, players, and matches for a gaming application. The backend is built using Spring Boot and provides RESTful APIs to perform various operations.

## Project Description

The **Levi9 Challenge Backend** is designed to handle the management of teams, players, and matches in a gaming environment. It allows users to create, update, delete, and retrieve information about teams and players, as well as schedule and record matches between teams. The application also calculates ELO ratings for players based on match outcomes.

## Technologies Used

- **Java 17**: The primary programming language used for building the application.
- **Spring Boot**: Framework used to create stand-alone, production-grade Spring-based applications.
- **Maven**: Build automation tool used for managing project dependencies and building the project.
- **H2 database**: In memory database used to store application data.
- **Docker**: Platform used to develop, ship, and run applications inside containers.
- **Docker Compose**: Tool for defining and running multi-container Docker applications.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java Development Kit (JDK) 17** installed. You can download it from [here](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html).
  
OR
- **Docker** installed. Get Docker from [here](https://www.docker.com/get-started).

## Installation

First, clone the repository to your local machine:
git clone https://github.com/theShus/Levi9Challange2024


You have multiple options to run the application:

    Using an IDE (IntelliJ IDEA or Eclipse)
        Open the project in your preferred IDE.
        Ensure all dependencies are downloaded.
        Run the Levi9Api main class.
        The application will start and listen on port 8080.

Using the JAR File

**Build the project using Maven:**

    mvn clean package -DskipTests

**Run the generated JAR file:**

    java -jar target/Levi9Challange-1.0.jar

The application will start and listen on port 8080.


**Using Docker Compose**

If you have Docker installed, you can run the application along with a PostgreSQL database using Docker Compose.
Navigate to the project directory and run:

    docker-compose up

This command will start both the backend, the api is exposed on local port 8080.


To stop the Docker containers, run:

    docker-compose down

  

## API Endpoints

**Matches**

Add Match

    POST /matches

    Request Body:
    {
      "team1Id": "dacfe004-42d8-4938-8e1c-a1fe46739cb6",
      "team2Id": "7265bc21-46bc-40e3-a2d5-3338c8cc7495",
      "winningTeamId": "dacfe004-42d8-4938-8e1c-a1fe46739cb6",
      "duration": 3
    }

    Response:
      200 OK: "Match created successfully"

Get All Matches

    GET /matches

    Response:
        200 OK: Returns a list of all matches.

Players

**Create Player**

    POST /players/create
    
    Request Body:
    {
      "nickname": "Player1",
      "teamId": "dacfe004-42d8-4938-8e1c-a1fe46739cb6"
    }

    Response:
      200 OK: Returns the created player details.

Get Player by ID

    GET /players/{id}

    Response:
      200 OK: Returns the player details.

Get All Players

    GET /players

    Response:
      200 OK: Returns a list of all players.

Update Player

    PUT /players/{id}

    Request Body:
    {
      "nickname": "UpdatedPlayer1",
      "teamId": "7265bc21-46bc-40e3-a2d5-3338c8cc7495"
    }
    
    Response:
        200 OK: Returns the updated player details.

Delete Player

    DELETE /players/{id}

    Response:
        204 No Content: Player successfully deleted.

**Teams**

Create Team
    
    POST /teams
    
    Request Body:
    {
      "teamName": "Team Alpha"
    }
    
    
    Response:
        200 OK: Returns the created team details.

Get Team by ID

    GET /teams/{id}
        
    Response:
        200 OK: Returns the team details.

Delete Team

    DELETE /teams/{id}
    
    Response:
        204 No Content: Team successfully deleted.

Swap Players Between Teams
    
    POST /teams/swap-players
    
    Request Body:
    {
      "team1Id": "dacfe004-42d8-4938-8e1c-a1fe46739cb6",
      "team2Id": "7265bc21-46bc-40e3-a2d5-3338c8cc7495",
      "team1PlayerIds": [
        "player-id-1",
        "player-id-2"
      ],
      "team2PlayerIds": [
        "player-id-3",
        "player-id-4"
      ]
    }

    Response:
        200 OK: Players successfully swapped.
