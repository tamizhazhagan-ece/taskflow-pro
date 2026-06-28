export const STATUS_COLUMNS = [
  { key: 'TODO',        label: 'To Do',        color: '#6B7280' },
  { key: 'IN_PROGRESS', label: 'In Progress',  color: '#2563EB' },
  { key: 'CODE_REVIEW', label: 'Code Review',  color: '#7C3AED' },
  { key: 'TESTING',     label: 'Testing',      color: '#D97706' },
  { key: 'DONE',        label: 'Done',         color: '#059669' },
  { key: 'BLOCKED',     label: 'Blocked',      color: '#DC2626' },
]

export const PRIORITIES = [
  { key: 'LOW',      label: 'Low',      color: '#6B7280' },
  { key: 'MEDIUM',   label: 'Medium',   color: '#2563EB' },
  { key: 'HIGH',     label: 'High',     color: '#D97706' },
  { key: 'CRITICAL', label: 'Critical', color: '#DC2626' },
]

export const ROLES = [
  { key: 'DEVELOPER',  label: 'Developer',  cls: 'developer' },
  { key: 'TEAM_LEAD',  label: 'Team Lead',  cls: 'team-lead' },
  { key: 'MANAGER',    label: 'Manager',    cls: 'manager'   },
  { key: 'ADMIN',      label: 'Admin',      cls: 'admin'     },
]

export const DEPARTMENTS = [
  'Software Development',
  'Quality Assurance',
  'UI/UX',
  'DevOps',
  'Project Management',
]

export function statusMeta(key) {
  return STATUS_COLUMNS.find((s) => s.key === key) || STATUS_COLUMNS[0]
}

export function priorityMeta(key) {
  return PRIORITIES.find((p) => p.key === key) || PRIORITIES[1]
}

export function roleMeta(key) {
  return ROLES.find((r) => r.key === key) || ROLES[0]
}

export function initials(name) {
  if (!name) return '?'
  const parts = name.trim().split(/\s+/)
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

export function formatDate(value) {
  if (!value) return null
  return new Date(value).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
}

export function formatDateTime(value) {
  if (!value) return null
  return new Date(value).toLocaleString(undefined, { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' })
}

export function isOverdue(dueDate, status) {
  if (!dueDate || status === 'DONE') return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return new Date(dueDate) < today
}

export function formatFileSize(bytes) {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

export function canCreateProjects(role) {
  return role === 'ADMIN' || role === 'MANAGER'
}

export function canCreateTasks(role) {
  return role !== 'DEVELOPER'
}

export function canManageUsers(role) {
  return role === 'ADMIN'
}

export function canViewAdmin(role) {
  return role === 'ADMIN'
}
