import React, { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { LogOut, ShieldCheck } from 'lucide-react'
import { useAuth } from '../auth/AuthContext'
import Avatar from './Avatar'
import NotificationPanel from './NotificationPanel'
import RoleBadge from './RoleBadge'

export default function TopBar() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const isAdmin = user?.role === 'ADMIN'
  const isManagerOrAbove = user?.role === 'ADMIN' || user?.role === 'MANAGER'

  function handleLogout() {
    logout()
    navigate('/login')
  }

  function active(path) {
    return location.pathname === path ? 'active' : ''
  }

  return (
    <header className="topbar">
      <Link to="/" className="brand">
        <span className="brand-mark" style={{
          width: 32, height: 32, borderRadius: 8, background: 'var(--accent)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'white', fontWeight: 800, fontSize: 15
        }}>T</span>
        TaskFlow Pro
      </Link>

      <nav className="topbar-nav">
        <Link to="/" className={`nav-link ${active('/')}`}>Dashboard</Link>
        {isManagerOrAbove && (
          <Link to="/reports" className={`nav-link ${active('/reports')}`}>Reports</Link>
        )}
        {isAdmin && (
          <Link to="/admin" className={`nav-link ${active('/admin')}`}>
            <ShieldCheck size={14} style={{ verticalAlign: -2, marginRight: 4 }} />
            Admin
          </Link>
        )}
      </nav>

      <div className="topbar-right">
        <NotificationPanel />
        <div className="user-menu">
          <button className="user-menu-trigger" onClick={() => setMenuOpen(!menuOpen)}>
            <Avatar user={user} size="sm" />
            <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)' }}>{user?.name}</span>
          </button>
          {menuOpen && (
            <div className="user-menu-dropdown" onClick={() => setMenuOpen(false)}>
              <div style={{ padding: '10px 12px', borderBottom: '1px solid var(--border)' }}>
                <div style={{ fontWeight: 700, fontSize: 13, color: 'var(--text)', marginBottom: 4 }}>{user?.name}</div>
                <div style={{ fontSize: 11.5, color: 'var(--muted)', marginBottom: 6 }}>{user?.email}</div>
                <RoleBadge role={user?.role} />
                {user?.department && (
                  <div style={{ fontSize: 11, color: 'var(--muted-light)', marginTop: 4 }}>{user.department}</div>
                )}
              </div>
              <button className="user-menu-item" onClick={handleLogout} style={{ color: 'var(--danger)' }}>
                <LogOut size={14} style={{ verticalAlign: -2, marginRight: 6 }} /> Sign out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
