# RafBook-Backend
 
## Overview

This is a Java Spring backend for a messaging application that supports real-time communication between students, group
channels, notifications, and media sharing. It provides endpoints for managing users, channels, messages, and
notifications, with optional video and audio communication support. The backend is designed to support mobile, web, and
desktop applications as clients.

---

## Features

- **User Communication**: Supports text, image, and video messages.
- **Group Channels**: Organized channels for specific courses, departments, and academic years.
- **Notifications**: Broadcast notifications similar to a mailing list.
- **User Management**: Admin module for managing user registrations and permissions.
- **Optional Video/Audio Communication**: Optional support for video/audio calls between users.

---

## Technologies

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security**
- **Swagger**
- **WebSockets** for real-time messaging
- **Redis** (for real-time message caching)
- **PostgreSQL** (for persistent data storage)
- **Docker** (for containerized deployment)

## Requirements

- **Java 17**
- **Maven 3+**
- **PostgreSQL 13+**
- **Redis** (optional, for real-time messaging)
- **Docker** (for containerization, optional)

---

# API Endpoints

### 1. **Authentication**

- **POST** `/api/auth/register`: Register a new user.
- **POST** `/api/auth/login`: Authenticate and retrieve a JWT token.

### 2. **User Management**

- **GET** `/api/users`: List all users (admin-only).
- **GET** `/api/users/{id}`: Get user details.
- **PUT** `/api/users/{id}`: Update user details (admin-only).
- **DELETE** `/api/users/{id}`: Delete a user (admin-only).

### 3. **Messaging**

- **POST** `/api/messages`: Send a message in a channel.
- **GET** `/api/messages/{channelId}`: Retrieve all messages for a specific channel.
- **GET** `/api/messages/{channelId}/latest`: Retrieve latest messages (for real-time updates).

### 4. **Channels**

- **POST** `/api/channels`: Create a new channel (admin or professor).
- **GET** `/api/channels`: List all channels for the authenticated user.
- **PUT** `/api/channels/{channelId}`: Update a channel’s settings (admin-only).
- **DELETE** `/api/channels/{channelId}`: Delete a channel (admin-only).

### 5. **Notifications**

- **POST** `/api/notifications`: Send a broadcast notification.
- **GET** `/api/notifications`: Retrieve all notifications for the user.

### 6. **Optional Video/Audio Communication**

- **POST** `/api/calls/start`: Initiate a video/audio call (if supported).
- **POST** `/api/calls/end`: End a call.
- **GET** `/api/calls/history`: Retrieve call history for a user.

---

# WebSocket Endpoints

- **`/ws/chat`**: Primary endpoint for real-time messaging using WebSocket connections.

### WebSocket Subscriptions

- **`/topic/messages/{channelId}`**: Subscribe to messages in a specific channel.
- **`/topic/notifications`**: Subscribe to real-time notifications.

---

# Database Structure

### Tables

- **`users`**: Stores user information, including authentication details.
- **`channels`**: Stores channel information (name, type, associated course or department).
- **`messages`**: Stores message data (sender, timestamp, content, media URLs).
- **`notifications`**: Stores system-wide notifications.
- **`calls`** (optional): Stores data on audio and video calls, if enabled.

---

# Security

### JWT Authentication

- Used for securing API endpoints, requiring users to authenticate and obtain a JWT token for access.

### Role-Based Access Control

- Roles include **admin**, **professor**, and **student**, each with specific permissions:
    - **Admin**: Full access, including user and channel management.
    - **Professor**: Access to manage channels related to their courses.
    - **Student**: Access to view channels and communicate within allowed channels.

### Password Encryption

- User passwords are securely hashed using **bcrypt** for added security against unauthorized access.

### WebSocket Security

- JWT token authentication is required for establishing WebSocket connections, ensuring only authenticated users have
  access to real-time messaging.

### MAC Address Verification

- **MAC Address** is required during user registration, adding an extra layer of device-level verification.

---

# Testing

The backend uses **JUnit** for unit testing, **JaCoCo** for code coverage, and **Cucumber** for integration testing.
This testing setup ensures both functionality and quality of the application.

## Unit Testing with JUnit

JUnit is used for unit testing individual components and services.

### Instructions

- **Test Location**: Place unit tests in the `src/test/java` directory. Each test class should correspond to a single
  service or component class and follow the naming convention `ClassNameTest.java`.
- **Running Tests**:
    - Run all tests with the command:
      ```bash
      ./mvnw test
      ```
- **Annotations**: Use `@Test` for individual test methods, `@BeforeEach` for setup tasks, and `@AfterEach` for cleanup
  tasks if needed.

## Code Coverage with JaCoCo

JaCoCo is used to measure code coverage of the project to ensure sufficient test coverage.

### Instructions

1. **JaCoCo Configuration**: Ensure JaCoCo is configured in the `pom.xml` under the `<build>` section. Add a `<plugin>`
   section for `jacoco-maven-plugin`.
2. **Generating Coverage Reports**:
    - Run the following command to generate a coverage report:
      ```bash
      ./mvnw test jacoco:report
      ```
    - Reports will be generated in the `target/site/jacoco` directory.

3. **Viewing Reports**:
    - Open `target/site/jacoco/index.html` in a browser to view the coverage report.

## Integration Testing with Cucumber

Cucumber is used for integration tests, allowing end-to-end testing of features in a behavior-driven manner.

### Instructions

1. **Feature Files**:
    - Write feature files in the `src/test/resources/features` directory.
    - Use the `.feature` file format to describe scenarios in Gherkin syntax.

2. **Step Definitions**:
    - Create step definition classes in `src/test/java` to map Gherkin steps to Java code.
    - Each step should correspond to an action or assertion in the integration flow.

3. **Running Cucumber Tests**:
    - Run all Cucumber tests with the command:
      ```bash
      ./mvnw test -Dcucumber.features=src/test/resources/features
      ```

4. **Reports**:
    - Configure Cucumber to generate HTML or JSON reports by specifying a plugin in the `@CucumberOptions` annotation.

With this setup, the backend application is thoroughly tested, providing assurance in both unit-level and
integration-level functionality.

---

## Using Git with Forks and Creating Pull Requests

### 1. Forking a Repository

1. Go to the repository you want to fork on GitHub.
2. Click the `Fork` button at the top right of the repository page.
3. This will create a copy of the repository under your GitHub account.

### 2. Cloning the Forked Repository

1. Navigate to your forked repository on GitHub.
2. Click the `Code` button and copy the URL.
3. Open your terminal and run the following command to clone the repository:

```bash
git clone <your-forked-repo-url>
```

4. Navigate into the cloned repository:

```bash
cd <repository-name>
```

### 3. Setting Up the Upstream Remote

1. Add the original repository as an upstream remote:

```bash
git remote add upstream <original-repo-url>
```

2. Verify the new upstream remote:

```bash
git remote -v
```

### 4. Creating a New Branch

1. Create a new branch for your changes:

```bash
git checkout -b <new-branch-name>
```

### 5. Making Changes and Committing

1. Make your changes to the code.
2. Stage the changes:

```bash
git add .
```

3. Commit the changes:

```bash
git commit -m "Description of the changes"
```

### 6. Pushing Changes to Your Fork

1. Push the changes to your forked repository:

```bash
git push origin <new-branch-name>
```

### 7. Creating a Pull Request

1. Go to your forked repository on GitHub.
2. Click the `Compare & pull request` button.
3. Ensure the base repository is the original repository and the base branch is the branch you want to merge into.
4. Provide a title and description for your pull request.
5. Click `Create pull request`.

### 8. Keeping Your Fork Updated

1. Fetch the latest changes from the upstream repository:

```bash
git fetch upstream
```

2. Merge the changes into your local branch:

```bash
git checkout <branch-name>
git merge upstream/<branch-name>
```

3. Push the updated branch to your fork:

```bash
git push origin <branch-name>
```

This tutorial covers the basic workflow for using Git with forks and creating pull requests.

---
