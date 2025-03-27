# RafBook-Backend

## Overview  

RafBook-Backend is a **Java Spring** backend designed for **messaging and audio/video communication** in **educational environments**. It provides a **scalable and secure** platform for students, professors, and administrators to communicate effectively.  

The system includes:  
- **Backend** deployed on a **Kubernetes cluster** with **OpenSearch** for logs and **Grafana** for metrics.  
- A **React frontend** for web users.  
- **Desktop applications** for both **users** and **administrators**.  
- **Mobile applications** for **iOS (Swift)** and **Android (Kotlin)**.  
- A **configurable administration panel** for system settings and management.  

This setup ensures **real-time collaboration, security, and flexibility** across multiple platforms.  

---

## Features  

### User Service  
- **User Management** – Create, update, and delete user accounts.  
- **Role-Based Access Control (RBAC)** – Assign and manage permissions based on user roles (students, professors, administrators).  
- **Authentication & Authorization** – Secure login with JWT-based authentication.  
- **User Profiles** – Manage user details, preferences, and settings.  

### Channel Management  
- **Study Programs & Categories** – Organize communication channels based on study types, programs, and categories.  
- **Channel Creation & Moderation** – Administrators can create, configure, and manage channels for discussions and lessons.  
- **Access Control** – Define user access permissions per channel or category.  
- **Announcements & Notifications** – Send important updates to specific channels or user groups.  

### Messaging Service  
- **Real-Time Messaging** – Supports text messages, audio messages, and file sharing.  
- **Cloud Storage** – All media and files are uploaded and stored securely on the cloud for easy access.  
- **Message Broadcasting** – Send instant notifications and messages to users or groups in real-time.  

### Voice Service  
- **P2P Audio Communication** – Enables peer-to-peer (P2P) voice calls between users, ensuring low-latency, high-quality audio communication.  
- **Voice Channels** – Users can connect to dedicated voice channels for audio meetings and discussions.  
- **Voice Channel Caching** – A cache mechanism is used to store and display all users currently in the voice channel, providing real-time visibility of participants in the channel.  

### Orchestration Service  
- **Application Initialization** – Coordinates the sending of data to initialize the application on the first request, ensuring smooth startup and configuration.  
- **Service Orchestration** – Manages the deployment and communication between various services to ensure that all components work seamlessly.  

More features related to **system administration** and **real-time data management** will be detailed in later sections.


---

## Technologies  

### Backend Framework & Language  
- **Java 17** – The backend is developed using the latest version of Java, ensuring robust, modern features and performance.  
- **Spring Boot 3** – A powerful framework for building microservices and web applications, providing simplicity and efficiency for backend development.  
- **Spring Data JPA** – Simplifies database operations using JPA for object-relational mapping and provides a consistent repository pattern.  
- **Spring Security** – Provides comprehensive security features such as authentication, authorization, and protection against common security threats.  
- **Swagger** – Automatically generates and documents API endpoints for ease of integration and testing.

### Real-Time Communication  
- **WebSockets** – Used for real-time messaging, allowing persistent, two-way communication between the client and server, essential for chat features and live notifications.  
- **Firebase Cloud Messaging (FCM)** – Provides push notifications for real-time messaging on mobile and web applications, enabling instant notifications and updates to users.  
- **Redis** – In-memory data structure store used for caching real-time messages, channel data, and session states, ensuring fast access to frequently accessed data.  

### Voice Communication  
- **P2P Audio Calls** – Voice communication between users is powered by a peer-to-peer (P2P) architecture, ensuring minimal latency and high-quality audio.  
- **WebRTC** – For facilitating real-time audio communication between peers directly within the browser and mobile apps without needing additional plugins.  

### Cloud Storage & Media Handling  
- **Cloud Storage** – Files and media, including text, images, and audio messages, are uploaded to a cloud storage solution, ensuring scalability and accessibility.  
- **Firebase Storage** – Provides cloud storage for media files (audio, images, etc.), with tight integration into Firebase's suite of services. This allows for secure and scalable storage of user-uploaded content.  

### Infrastructure & Containerization  
- **Kubernetes** – The backend is deployed on a Kubernetes cluster, providing automated deployment, scaling, and management of containerized applications. Kubernetes ensures high availability and fault tolerance, and can dynamically scale based on demand.  
- **Docker** – Containerizes the backend services, allowing for consistent deployment across different environments, such as development, testing, and production.  
- **Helm** – Manages Kubernetes deployments using Helm charts, providing an easy way to define, install, and upgrade complex Kubernetes applications.

### Monitoring & Observability  
- **Grafana** – Used for visualizing real-time metrics, providing an intuitive dashboard for tracking application performance, health, and resource usage.  
- **Prometheus** – Collects and stores metrics from the system, feeding data into Grafana for detailed insights and monitoring.  
- **OpenSearch** – Powers centralized logging and search capabilities, allowing easy querying and exploration of logs for troubleshooting, performance analysis, and audit purposes.

### Database & Data Storage  
- **PostgreSQL** – A powerful relational database for storing structured data such as user information, messages, and channel metadata.  
- **Redis (Cache)** – Redis is used not only for caching real-time data but also for enhancing the responsiveness of the system by storing temporary session states and frequently accessed data.

### Message Queuing & Orchestration  
- **Kafka** – Used for stream processing and handling large-scale message queuing. Kafka is ideal for distributing messages in real-time across services and ensures durability and high throughput for event-driven architecture.  - (not yet implemented)
- **Orchestration Service** – Ensures smooth coordination between different services and manages the initialization of data on the first request, allowing seamless interaction across the system.  

### Mobile & Desktop Apps  
- **React** – The frontend is built using React, a powerful JavaScript library for creating user interfaces, ensuring responsiveness and interactivity.  
- **Swift** – The mobile app for iOS users is developed using Swift, ensuring smooth performance and integration with Apple devices.  
- **Kotlin** – The mobile app for Android users is developed using Kotlin, taking advantage of modern features to provide a native experience.  
- **Electron** – The desktop applications for both users and administrators are built using Electron, which allows for building cross-platform desktop applications using web technologies.

### CI/CD & Version Control  
- **Git** – Version control is managed with Git, allowing collaboration and easy tracking of code changes.  
- **GitHub Actions** – Automates the build, testing, and deployment pipelines, ensuring a seamless continuous integration and delivery process.  
- **Docker Compose** – Used for defining and running multi-container Docker applications during development, ensuring that all services run in an isolated and reproducible environment.

### Security & Compliance  
- **JWT** – JSON Web Tokens are used for secure authentication, allowing users to authenticate once and receive a token that grants access to protected API endpoints.  
- **OAuth2** – Provides secure delegated access for external systems to access user data with user consent, often used for integrations with third-party services.  
- **Bcrypt** – Used for hashing and securing user passwords to protect against unauthorized access.  
- **SSL/TLS** – Ensures secure communication between the client and server by encrypting data over the network.

This combination of technologies enables a robust, scalable, and secure messaging platform with real-time communication, comprehensive administration tools, and high availability for educational environments.

---

# API Endpoints

### 1. **Authentication**

- **POST** `/users/auth/register`: Register a new user.
- **POST** `/users/auth/login`: Authenticate and retrieve a JWT token.

### 2. **User Management**

- **GET** `/users`: List all users (admin-only).
- **GET** `/users/{id}`: Get user details.
- **PUT** `/users/{id}`: Update user details (admin-only).
- **DELETE** `/users/{id}`: Delete a user (admin-only).
- **PATCH** `/users/{id}`: Patch user details (admin-only).
- **PATCH** `/users/{id}/addRole/{role}`: Add role to user (admin-only).
- **PATCH** `/users/{id}/removeRole/{role}`: Remove role from user (admin-only).

### 3. **Messaging**

- **POST** `/messages`: Send a message in a channel.
- **GET** `/messages/channel/{channelId}/{start}/{end}`: Retrieve all messages for a specific channel.
- **GET** `/messages/{id}`: Retrieve a message by ID.
- **PUT** `/messages/{id}`: Edit a message.
- **DELETE** `/messages/{id}`: Delete a message.
- **POST** `/messages/upload-file`: Upload a file in a message.

### 4. **Categories**

- **GET** `/categories/names`: Get all category names.
- **POST** `/categories`: Add a new category.
- **POST** `/categories/bulk-import`: Bulk import categories.

### 5. **Studies**

- **POST** `/studies`: Create a new study.
- **GET** `/studies`: List all studies.

### 6. **Study Programs**

- **POST** `/study-programs`: Create a new study program.
- **GET** `/study-programs/by-studies`: List all study programs by studies.

### 7. **Text Channels**

- **POST** `/text-channel`: Create a new text channel.
- **GET** `/text-channel`: List all text channels.
- **GET** `/text-channel/{id}`: Get a text channel by ID.
- **GET** `/text-channel/for-user`: List all text channels for the authenticated user.
- **PUT** `/text-channel/add-roles/{id}`: Add roles to a text channel.
- **PATCH** `/text-channel/remove-roles/{id}`: Remove roles from a text channel.

### 8. **Voice Channels**

- **POST** `/voice-channel`: Create a new voice channel.
- **GET** `/voice-channel`: List all voice channels.
- **GET** `/voice-channel/{id}`: Get a voice channel by ID.
- **DELETE** `/voice-channel/{id}`: Delete a voice channel.

### 9. **Roles**

- **GET** `/roles`: List all roles.

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
