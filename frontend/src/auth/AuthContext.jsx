import React, { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { authApi } from '../api/auth'
import { TOKEN_KEY } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('taskflow_user')
    return stored ? JSON.parse(stored) : null
  })
  const [loading, setLoading] = useState(!!localStorage.getItem(TOKEN_KEY))

  const loadUser = useCallback(async () => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (!token) {
      setLoading(false)
      return
    }
    try {
      const me = await authApi.me()
      setUser(me)
      localStorage.setItem('taskflow_user', JSON.stringify(me))
    } catch {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('taskflow_user')
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadUser()
  }, [loadUser])

  async function login(email, password) {
    const res = await authApi.login(email, password)
    localStorage.setItem(TOKEN_KEY, res.token)
    localStorage.setItem('taskflow_user', JSON.stringify(res.user))
    setUser(res.user)
    return res
  }

  async function register(name, email, password) {
    const res = await authApi.register(name, email, password)
    localStorage.setItem(TOKEN_KEY, res.token)
    localStorage.setItem('taskflow_user', JSON.stringify(res.user))
    setUser(res.user)
    return res
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem('taskflow_user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
