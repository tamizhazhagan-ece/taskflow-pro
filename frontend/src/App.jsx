import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './auth/ProtectedRoute'
import TopBar from './components/TopBar'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ProjectBoardPage from './pages/ProjectBoardPage'
import ReportsPage from './pages/ReportsPage'
import AdminPage from './pages/AdminPage'
import { useAuth } from './auth/AuthContext'

function AdminRoute({ children }) {
  const { user } = useAuth()
  if (user?.role !== 'ADMIN') return <Navigate to="/" replace />
  return children
}

function ManagerRoute({ children }) {
  const { user } = useAuth()
  if (user?.role !== 'ADMIN' && user?.role !== 'MANAGER') return <Navigate to="/" replace />
  return children
}

export default function App() {
  const { isAuthenticated } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={isAuthenticated ? <Navigate to="/" /> : <LoginPage />} />
      <Route path="/register" element={isAuthenticated ? <Navigate to="/" /> : <RegisterPage />} />
      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <div className="app-shell">
              <TopBar />
              <main className="main-area">
                <Routes>
                  <Route path="/" element={<DashboardPage />} />
                  <Route path="/projects/:projectId" element={<ProjectBoardPage />} />
                  <Route path="/reports" element={
                    <ManagerRoute><ReportsPage /></ManagerRoute>
                  } />
                  <Route path="/admin" element={
                    <AdminRoute><AdminPage /></AdminRoute>
                  } />
                  <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
              </main>
            </div>
          </ProtectedRoute>
        }
      />
    </Routes>
  )
}
