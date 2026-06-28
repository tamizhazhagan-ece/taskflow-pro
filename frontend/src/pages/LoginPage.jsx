import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, password)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || err.response?.data?.message || 'Invalid email or password.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ animation: 'modal-in 0.2s ease' }}>
        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 28 }}>
          <div className="brand-mark" style={{
            width: 40, height: 40, borderRadius: 12, background: 'var(--accent)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'white', fontWeight: 800, fontSize: 18, fontFamily: 'var(--font-display)'
          }}>T</div>
          <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 20, color: 'var(--text)' }}>
            TaskFlow Pro
          </span>
        </div>

        <h1 style={{ fontSize: 26, marginBottom: 6, color: 'var(--text)' }}>Welcome back</h1>
        <p style={{ color: 'var(--muted)', marginBottom: 28, fontSize: 14 }}>
          Sign in to your account to continue.
        </p>

        {error && (
          <div className="auth-error" style={{ marginBottom: 20 }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
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
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
              <label htmlFor="password" style={{ margin: 0 }}>Password</label>
              <a
                href="#"
                style={{ fontSize: 12, color: 'var(--accent)', fontWeight: 600 }}
                onClick={(e) => e.preventDefault()}
              >
                Forgot password?
              </a>
            </div>
            <input
              id="password"
              className="input"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>

          <button
            className="btn btn-primary"
            style={{ width: '100%', padding: '11px', fontSize: 15, borderRadius: 'var(--radius-sm)' }}
            disabled={loading}
            type="submit"
          >
            {loading ? (
              <span style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: 'center' }}>
                <span className="loading-bar" style={{ width: 16, height: 16, borderRadius: '50%', flexShrink: 0 }} />
                Signing in…
              </span>
            ) : 'Sign In'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: 20, fontSize: 13, color: 'var(--muted)' }}>
          Don't have an account?{' '}
          <a href="/register" style={{ color: 'var(--accent)', fontWeight: 600 }}>Create one</a>
        </p>

        <div style={{
          marginTop: 16, paddingTop: 16, borderTop: '1px solid var(--border)',
          textAlign: 'center', color: 'var(--muted)', fontSize: 12
        }}>
          © {new Date().getFullYear()} TaskFlow Pro. All rights reserved.
        </div>
      </div>
    </div>
  )
}
