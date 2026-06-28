import React from 'react'
import { roleMeta } from '../utils'

export default function RoleBadge({ role, size = 'sm' }) {
  const meta = roleMeta(role)
  return (
    <span className={`role-badge ${meta.cls}`} style={size === 'lg' ? { fontSize: 12, padding: '4px 12px' } : {}}>
      {meta.label}
    </span>
  )
}
