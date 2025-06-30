# Online Examination System – Backend

This Spring Boot application provides the RESTful API for an online examination platform, including user authentication, exam & question management, and automated grading.

---

## Table of Contents

1. [Features](#features)  
2. [Tech Stack](#tech-stack)  
3. [Getting Started](#getting-started)  
   - [Prerequisites](#prerequisites)  
   - [Installation](#installation)  
   - [Configuration](#configuration)  
   - [Running](#running)  
4. [API Endpoints](#api-endpoints)  
5. [Testing](#testing)  
6. [Deployment](#deployment)  
7. [Contributing](#contributing)  
8. [License](#license)  

---

## Features

- **User Management**: Registration & login with JWT, role-based access (ADMIN, INSTRUCTOR, STUDENT)  
- **Exam CRUD**: Create, list, update & delete exams  
- **Question Bank**: MCQs & descriptive questions with tagging  
- **Automated Grading**: Instant scoring for MCQs; instructor review for descriptive answers  
- **Analytics**: Endpoints for exam performance reports  

---

## Tech Stack

- **Java 17** & **Spring Boot 3.x**  
- **Spring Security** (JWT)  
- **Spring Data JPA** (Hibernate)  
- **MySQL** (or PostgreSQL)  
- **Maven**  
- **MapStruct** for DTO mapping  
- **Liquibase** (optional) for DB migrations  

---

## Getting Started

### Prerequisites

- Java 17+ JDK  
- Maven 3.6+  
- MySQL server (or PostgreSQL)  
- (Optional) Docker & Docker Compose  

### Installation

```bash
git clone https://github.com/haswanth04/springbootsdp.git
cd springbootsdp
mvn clean install
