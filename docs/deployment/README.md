# Deployment Documentation

This directory documents how SmartOps Platform is run locally first and deployed to AWS later.

## Deployment strategy

### Current phase
- modular monolith
- one Spring Boot deployable application
- PostgreSQL
- Kafka
- Docker Compose for local development

### Target phase
- microservices-oriented deployment model
- containerized services
- managed AWS infrastructure
- environment-specific configuration

## Local development deployment

Main local runtime components:
- SmartOps application
- PostgreSQL
- Kafka
- Kafka UI

Recommended tooling:
- Java 21
- Maven 3.9+
- Docker Desktop
- Docker Compose

Local workflow:
1. start Docker Compose
2. run PostgreSQL and Kafka
3. start the Spring Boot application from the IDE
4. let Flyway create or update schemas
5. verify APIs through Swagger or Postman
6. inspect events with Kafka UI

## Environment profiles

Recommended Spring profiles:
- `local`
- `test`
- `docker`
- `aws`

## Configuration sources

Typical configuration comes from:
- `application.yml`
- environment-specific Spring profile files
- environment variables
- secret management for cloud deployment

## AWS target architecture

Recommended first cloud target:
- **Amazon ECS/Fargate** for application containers
- **Amazon RDS PostgreSQL** for relational data
- **Amazon MSK** or equivalent Kafka hosting strategy
- **Amazon CloudWatch** for logs and metrics
- **AWS Secrets Manager** or **SSM Parameter Store** for secrets
- **Application Load Balancer** for external HTTP entry point

## Deployment principles

### Principle 1
Application artifacts should be containerized.

### Principle 2
Infrastructure configuration should be reproducible.

### Principle 3
Runtime configuration must be externalized.

### Principle 4
Observability must be available from day one:
- structured logs
- health checks
- metrics
- correlation IDs

## CI/CD direction

Recommended future pipeline steps:
1. build
2. unit test
3. integration test
4. package container image
5. publish artifact
6. deploy to target environment
7. run smoke tests

## Next documentation to add here

As implementation progresses, this folder should be extended with:
- Docker Compose reference
- Dockerfile reference
- ECS task-definition notes
- RDS and networking notes
- environment-variable catalog
- CI/CD pipeline documentation
