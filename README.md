# RafBook-Backend

## Overview

This is a Java Spring backend for a messaging application that supports real-time communication between students, group channels, notifications, and media sharing. It provides endpoints for managing users, channels, messages, and notifications, with optional video and audio communication support. The backend is designed to support mobile, web, and desktop applications as clients.

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
- JWT token authentication is required for establishing WebSocket connections, ensuring only authenticated users have access to real-time messaging.

### MAC Address Verification
- **MAC Address** is required during user registration, adding an extra layer of device-level verification.

