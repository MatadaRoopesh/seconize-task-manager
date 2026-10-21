# Seconize Task Manager
### AI-Powered Project and Task Management System

A full-stack proof of concept developed for the Seconize Full Stack Developer Internship Screening Assignment. The application combines project and task management with AI-assisted task generation and management.

## Live Application

- **Frontend:** https://seconize-task-manager.vercel.app
- **Backend API:** https://seconize-task-manager.onrender.com/api
- **GitHub Repository:** https://github.com/MatadaRoopesh/seconize-task-manager

## Technology Stack

- **Frontend:** Angular
- **Backend:** Java, Spring Boot, REST APIs
- **Database:** PostgreSQL
- **AI Integration:** Gemini AI
- **Deployment:** Vercel (frontend), Render (backend)

## Features

- Create and manage projects
- Create, update, and delete tasks
- Organize tasks by status, priority, assignee, and deadline
- Manage subtasks
- View tasks using a Kanban board
- View project and task progress on a dashboard
- Generate tasks with AI assistance
- Edit and delete projects through the user interface

> Note: The features above should be described as completed only if they work in the deployed application. Update this list if any feature is still incomplete.

## Project Structure

- `frontend/` — Angular frontend application
- `src/` — Spring Boot backend source code
- `pom.xml` — Maven dependencies and build configuration
- `mvnw` — Maven Wrapper
- `Dockerfile` — Backend container configuration
- `postman/` and `.postman/` — API testing resources

## Running the Backend Locally

### Prerequisites

- Java Development Kit (JDK)
- PostgreSQL
- Node.js and npm for the frontend

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/MatadaRoopesh/seconize-task-manager.git
   cd seconize-task-manager
   ```

2. Create a PostgreSQL database named `seconize_task_manager`.

3. Configure the database connection and any required environment variables using your application's configuration.

4. Start the backend:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Start the frontend in a separate terminal:

   ```bash
   cd frontend
   npm install
   npm start
   ```

   If the project uses a different Angular start script, use the script defined in `frontend/package.json`.

## API

The backend exposes REST endpoints for project and task management under `/api`. Check the controller classes and application configuration for the exact endpoints and required request bodies.

## AI Integration

The application integrates Gemini AI to assist with project and task management. AI-generated actions should be validated before application data is changed.

## Assignment Deliverables

This repository contains the application source code and deployment configuration. A complete submission should also include the database schema, sample data or setup instructions, architecture documentation, and screenshots or a short demonstration video.

## Author

**Matada Roopesh**

## License

Created for the Seconize Internship Screening Assignment.
