import React from 'react'
import { initials } from '../utils'

export default function Avatar({ user, size = 'md' }) {
  const color = user?.avatarColor || '#6366f1'
  const label = user ? initials(user.name) : '?'
  return (
    <span className={`avatar avatar-${size}`} style={{ background: color }} title={user?.name}>
      {label}
    </span>
  )
}
