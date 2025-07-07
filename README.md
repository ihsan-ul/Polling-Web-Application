Polling Web Application
This project is a fully functional web-based polling (voting) system built with Java Spring Boot and Maven.It demonstrates the design and development of a modern web service combining a RESTful backend with a simple web frontend.

Features:
Role-based functionality:
- Admin interface (admin.html): manage candidates and monitor votes.
- Voter interface (voter.html): securely cast votes.
Spring Boot & Spring Security:
- Ensures modularity, scalability, and security for user actions.
RESTful controllers:
- Manage backend operations for both voters and admins.
Data persistence:
- Uses Spring Data JPA repositories (CandidateModelRepository, VoteRepository) to store and query data.
Service layer:
- Encapsulates business logic, providing clean separation from controllers.
Embedded database:
- Packaged with local DB files, making the app self-contained for demo and testing.
Maven build system:
- Simplifies dependency management and deployment.

Technologies Used:
Java 17+
Spring Boot
Spring Security
Spring Data JPA
Embedded Database
Maven
HTML,CSS, & Vanilla JavaScript

Purpose & learning outcomes:
This project showcases how to build a secure, data-driven web service, applying best practices in:
Backend architecture (controllers, services, repositories).
Security configuration and access control.
Integrating frontend views with backend services.
Deploying a complete Java web application using Maven.
