# TaskFlow Pro
 
A full-stack project and task management tool for small teams.
 
## Tech Stack
 
- **Backend:** Java 21, Spring Boot 3, MySQL, JWT Authentication
- **Frontend:** React 18, Vite, Axios
 
## Features
 
- Role-based access control (Admin, Manager, Team Lead, Developer)
- Kanban board with drag-and-drop
- JWT authentication with refresh tokens
- File attachments and comments
- Admin user management panel
- Reports and analytics dashboard
 
## Setup
 
### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8+
 
### 1. Start the Backend
```bash
cd backend
mvn spring-boot:run
```
 
### 2. Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
 
Open http://localhost:5173 in your browser.
 
## Default Login (Development Only)
 
| Role      | Email                              | Password    |
|-----------|-------------------------------------|-------------|
| Admin     | tamizhazhagan@taskflowpro.com       | Admin@123   |
| Manager   | rajasekar@taskflowpro.com           | Manager@123 |
| Team Lead | vignesh@taskflowpro.com             | Lead@123    |
| Developer | arul.kumar@taskflowpro.com          | Dev@123     |
