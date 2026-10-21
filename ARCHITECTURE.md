# Seconize Task Manager — Architecture Overview

## Purpose
A proof of concept for AI-assisted project and task management, prepared for the Seconize Full Stack Developer Internship Screening Assignment.

## Technology stack
- Frontend: Angular
- Backend: Java and Spring Boot REST APIs
- Database: PostgreSQL
- AI provider: Gemini AI
- Deployment: Vercel frontend and Render backend (verify current deployments)

## High-level architecture
```text
User
 |
 v
Angular frontend --HTTP requests--> Spring Boot REST API
                                      |          |
                                      v          v
                                Service layer  Gemini AI service
                                      |
                                      v
                              Spring Data / JPA
                                      |
                                      v
                                  PostgreSQL
```

The browser should access application data through the backend API. Database access belongs in the backend. AI requests should be handled by the backend so provider credentials are not exposed in Angular.

## Main components
- Controllers expose project and task REST endpoints.
- Services apply business rules and coordinate operations.
- Repositories persist and retrieve entities from PostgreSQL.
- Entities/enums represent project/task data and allowed status values.
- Exception handling returns useful HTTP errors for invalid or missing resources.
- AI integration interprets user prompts and proposes answers or task actions. Validate generated actions before saving changes.

## Core data model
- Project: name, description, start date, deadline, status, creation timestamp.
- Task: title, description, project association, assignee, priority, status, deadline, creation timestamp.
- Subtasks, comments, and activity history belong to tasks only where matching backend persistence and API behavior are implemented. UI elements alone do not prove that data is saved.

A project can contain many tasks. A task may be associated with a project.

## Typical request flow
1. User performs an action in Angular.
2. Angular sends an HTTP request to Spring Boot.
3. Controller validates the request and delegates to a service.
4. Service applies business rules and calls a repository.
5. Repository reads or writes PostgreSQL.
6. API response returns to Angular and the UI refreshes.
7. For AI requests, backend calls Gemini, validates the response, and only then performs an allowed operation.

## Validation and security
- Validate required fields, dates, status, and priority.
- Keep AI keys in backend environment variables; never commit secrets.
- Treat model output as untrusted input. Do not execute arbitrary generated SQL or code.
- Verify referenced projects/tasks before applying AI-generated actions.
- Configure CORS and authentication appropriately.
- Avoid real credentials or personal data in sample data and screenshots.

## Deployment references
- Frontend: https://seconize-task-manager.vercel.app 
- Backend base URL: https://seconize-task-manager.onrender.com/api
- Repository: https://github.com/MatadaRoopesh/seconize-task-manager

## Documentation limitation
This overview is based on the implementation details currently known. Compare entity fields, endpoint paths, database columns, and AI behavior with the current source code. Do not describe comments, activity history, authentication, or natural-language task mutations as completed unless verified end to end.
