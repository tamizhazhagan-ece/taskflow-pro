import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(name, email, password)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ animation: 'modal-in 0.2s ease' }}>
        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 28 }}>
          <div style={{
            width: 40, height: 40, borderRadius: 12, background: 'var(--accent)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'white', fontWeight: 800, fontSize: 18, fontFamily: 'var(--font-display)'
          }}>T</div>
          <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 20, color: 'var(--text)' }}>
            TaskFlow Pro
          </span>
        </div>

        <h1 style={{ fontSize: 26, marginBottom: 6, color: 'var(--text)' }}>Create account</h1>
        <p style={{ color: 'var(--muted)', marginBottom: 28, fontSize: 14 }}>
          Join your team on TaskFlow Pro.
        </p>

        {error && (
          <div className="auth-error" style={{ marginBottom: 20 }}>{error}</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="name">Full Name</label>
            <input
              id="name"
              className="input"
              placeholder="Your full name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              autoComplete="name"
              required
            />
          </div>
          <div className="field">
            <label htmlFor="email">Email address</label>
            <input
              id="email"
              className="input"
              type="email"
              placeholder="you@taskflowpro.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              required
            />
          </div>
          <div className="field" style={{ marginBottom: 24 }}>
            <label htmlFor="password">Password</label>
            <input
              id="password"
              className="input"
              type="password"
              placeholder="Min. 6 characters"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="new-password"
              minLength={6}
              required
            />
          </div>

          <button
            className="btn btn-primary"
            style={{ width: '100%', padding: '11px', fontSize: 15, borderRadius: 'var(--radius-sm)' }}
            disabled={loading}
            type="submit"
          >
            {loading ? 'Creating account…' : 'Create Account'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: 20, fontSize: 13, color: 'var(--muted)' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: 'var(--accent)', fontWeight: 600 }}>Sign in</Link>
        </p>

        <div style={{
          marginTop: 20, paddingTop: 20, borderTop: '1px solid var(--border)',
          textAlign: 'center', color: 'var(--muted)', fontSize: 12
        }}>
          © {new Date().getFullYear()} TaskFlow Pro. All rights reserved.
        </div>
      </div>
    </div>
  )
}
