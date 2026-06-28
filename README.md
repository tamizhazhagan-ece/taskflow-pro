# TaskFlow Pro

Full-stack Project Management Tool built with Spring Boot + React.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3, MySQL, JWT
- **Frontend:** React 18, Vite, Axios

## Features
- Role-based access control (Admin, Manager, Team Lead, Developer)
- Kanban board with drag-and-drop
- JWT authentication
- Admin user management panel
- Tamil seed data with 17 users across 3 teams

## Setup

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

## Login (Development Only)
| Role | Email | Password |
|------|-------|----------|
| Admin | tamizhazhagan@taskflowpro.com | Admin@123 |
| Manager | rajasekar@taskflowpro.com | Manager@123 |
| Team Lead | vignesh@taskflowpro.com | Lead@123 |
| Developer | arul.kumar@taskflowpro.com | Dev@123 |