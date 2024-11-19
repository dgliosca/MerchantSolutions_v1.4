Markdown
# Merchant Solutions - Technical Exercise

## Project Overview

This project implements a backend service for a blind auction platform, as outlined in the technical exercise. The service handles product registration, bid submission, and auction completion. It interacts with a simplified user service for token validation.

## Technical Choices

### Library/Language
* **Kotlin**
* **http4k:** is a lightweight, functional, and composable library for building HTTP applications and services in Kotlin.
* **Jackson** 

### Database
* **H2 Database:** Selected as an embedded database for ease of setup and testing.

### Testing
* **JUnit:** Used for writing tests.

## Project Structure
This project adheres to the Hexagonal Architecture (also known as Ports and Adapters Architecture) to promote loose coupling, testability, and maintainability.

## Additional Considerations
While this exercise focused on core functionality, a production-ready system would require additional considerations:

* **Error Handling**: A comprehensive error handling strategy should be in place to handle exceptions gracefully, log errors, and provide informative error messages to users.
* **Security**: A mechanism like OAuth2 or Openid Connect for authentication/authorisation.
* **Database transaction**: Handling database transaction effectively to handle more complex concurrency scenarios.
* **Logging**: Implementing robust logging mechanisms.
