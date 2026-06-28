import React, { useEffect, useState, useCallback } from 'react'
import { Plus, Search, Edit2, Trash2, RefreshCw, UserCheck, UserX, X, Eye, EyeOff } from 'lucide-react'
import { usersApi } from '../api/users'
import Avatar from '../components/Avatar'
import RoleBadge from '../components/RoleBadge'
import Toast from '../components/Toast'
import { useAuth } from '../auth/AuthContext'
import { ROLES, DEPARTMENTS } from '../utils'

const PAGE_SIZE = 10

export default function AdminPage() {
  const { user: currentUser } = useAuth()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [toast, setToast] = useState('')
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState('')
  const [deptFilter, setDeptFilter] = useState('')
  const [page, setPage] = useState(1)

  // Modal state
  const [modal, setModal] = useState(null) // 'create' | 'edit' | 'reset' | 'delete'
  const [selected, setSelected] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const data = await usersApi.adminList({ role: roleFilter, department: deptFilter, search })
      setUsers(data)
      setPage(1)
    } catch (e) {
      setToast('Failed to load users')
    } finally {
      setLoading(false)
    }
  }, [roleFilter, deptFilter, search])

  useEffect(() => { load() }, [load])

  // Pagination
  const totalPages = Math.ceil(users.length / PAGE_SIZE)
  const paged = users.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)

  function openEdit(u) { setSelected(u); setModal('edit') }
  function openDelete(u) { setSelected(u); setModal('delete') }
  function openReset(u) { setSelected(u); setModal('reset') }

  async function handleToggleActive(u) {
    try {
      const updated = u.active ? await usersApi.deactivate(u.id) : await usersApi.activate(u.id)
      setUsers((prev) => prev.map((x) => x.id === u.id ? updated : x))
      setToast(updated.active ? `${u.name} activated` : `${u.name} deactivated`)
    } catch (e) {
      setToast(e.response?.data?.error || 'Action failed')
    }
  }

  async function handleDelete() {
    try {
      await usersApi.delete(selected.id)
      setUsers((prev) => prev.filter((x) => x.id !== selected.id))
      setToast(`${selected.name} deleted`)
      setModal(null)
    } catch (e) {
      setToast(e.response?.data?.error || 'Delete failed')
    }
  }

  return (
    <div className="admin-page">
      <div className="page-header">
        <div>
          <span className="page-eyebrow">Administration</span>
          <h1>User Management</h1>
          <p className="page-sub">Manage team members, roles, departments, and account access.</p>
        </div>
        <button className="btn btn-primary" onClick={() => setModal('create')}>
          <Plus size={16} /> Add User
        </button>
      </div>

      {/* Toolbar */}
      <div className="admin-toolbar">
        <div style={{ position: 'relative', flex: 1, minWidth: 220 }}>
          <Search size={14} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--muted)' }} />
          <input
            className="input"
            placeholder="Search name or email…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ paddingLeft: 32 }}
          />
        </div>
        <select className="input select" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
          <option value="">All Roles</option>
          {ROLES.map((r) => <option key={r.key} value={r.key}>{r.label}</option>)}
        </select>
        <select className="input select" value={deptFilter} onChange={(e) => setDeptFilter(e.target.value)}>
          <option value="">All Departments</option>
          {DEPARTMENTS.map((d) => <option key={d} value={d}>{d}</option>)}
        </select>
        <button className="btn btn-secondary" onClick={load} title="Refresh">
          <RefreshCw size={14} />
        </button>
      </div>

      {/* Summary chips */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 16, flexWrap: 'wrap' }}>
        {ROLES.map((r) => {
          const count = users.filter((u) => u.role === r.key).length
          if (!count) return null
          return (
            <button
              key={r.key}
              className={`role-badge ${r.cls}`}
              style={{ cursor: 'pointer', border: 'none' }}
              onClick={() => setRoleFilter(roleFilter === r.key ? '' : r.key)}
            >
              {r.label}: {count}
            </button>
          )
        })}
      </div>

      {/* Table */}
      <div className="admin-table-wrap">
        {loading ? (
          <div className="loading-bar" />
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Role</th>
                <th>Department</th>
                <th>Status</th>
                <th>Joined</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {paged.length === 0 && (
                <tr>
                  <td colSpan={6} style={{ textAlign: 'center', color: 'var(--muted)', padding: 40 }}>
                    No users found
                  </td>
                </tr>
              )}
              {paged.map((u) => (
                <tr key={u.id}>
                  <td>
                    <div className="user-info-cell">
                      <Avatar user={u} size="md" />
                      <div>
                        <div style={{ fontWeight: 600, color: 'var(--text)' }}>{u.name}</div>
                        <div style={{ fontSize: 12, color: 'var(--muted)' }}>{u.email}</div>
                      </div>
                    </div>
                  </td>
                  <td><RoleBadge role={u.role} /></td>
                  <td style={{ color: 'var(--muted)', fontSize: 13 }}>{u.department || '—'}</td>
                  <td>
                    <span className={`active-badge ${u.active ? 'active' : 'inactive'}`}>
                      {u.active ? '● Active' : '○ Inactive'}
                    </span>
                  </td>
                  <td style={{ color: 'var(--muted)', fontSize: 12 }}>
                    {u.id === currentUser?.id ? <em style={{ color: 'var(--accent)' }}>You</em> : '—'}
                  </td>
                  <td>
                    <div className="admin-actions">
                      <button className="btn btn-secondary btn-sm" title="Edit" onClick={() => openEdit(u)}>
                        <Edit2 size={13} />
                      </button>
                      <button className="btn btn-secondary btn-sm" title="Reset Password" onClick={() => openReset(u)}>
                        <RefreshCw size={13} />
                      </button>
                      <button
                        className="btn btn-secondary btn-sm"
                        title={u.active ? 'Deactivate' : 'Activate'}
                        onClick={() => handleToggleActive(u)}
                      >
                        {u.active ? <UserX size={13} /> : <UserCheck size={13} />}
                      </button>
                      {u.id !== currentUser?.id && (
                        <button className="btn btn-danger btn-sm" title="Delete" onClick={() => openDelete(u)}>
                          <Trash2 size={13} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="pagination">
          <button className="page-btn" disabled={page === 1} onClick={() => setPage(p => p - 1)}>‹</button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button key={i} className={`page-btn ${page === i + 1 ? 'active' : ''}`} onClick={() => setPage(i + 1)}>
              {i + 1}
            </button>
          ))}
          <button className="page-btn" disabled={page === totalPages} onClick={() => setPage(p => p + 1)}>›</button>
        </div>
      )}

      {/* Modals */}
      {modal === 'create' && (
        <UserFormModal
          title="Add New User"
          currentUser={currentUser}
          onClose={() => setModal(null)}
          onSave={async (data) => {
            try {
              const created = await usersApi.create(data)
              setUsers((prev) => [created, ...prev])
              setToast(`${created.name} created`)
              setModal(null)
            } catch (e) {
              throw e
            }
          }}
        />
      )}

      {modal === 'edit' && selected && (
        <UserFormModal
          title="Edit User"
          initial={selected}
          currentUser={currentUser}
          onClose={() => setModal(null)}
          onSave={async (data) => {
            try {
              const updated = await usersApi.update(selected.id, data)
              setUsers((prev) => prev.map((x) => x.id === selected.id ? updated : x))
              setToast(`${updated.name} updated`)
              setModal(null)
            } catch (e) {
              throw e
            }
          }}
        />
      )}

      {modal === 'reset' && selected && (
        <ResetPasswordModal
          user={selected}
          onClose={() => setModal(null)}
          onSave={async (pwd) => {
            await usersApi.resetPassword(selected.id, pwd)
            setToast(`Password reset for ${selected.name}`)
            setModal(null)
          }}
        />
      )}

      {modal === 'delete' && selected && (
        <ConfirmModal
          title="Delete User"
          message={`Are you sure you want to permanently delete ${selected.name}? This cannot be undone.`}
          danger
          onClose={() => setModal(null)}
          onConfirm={handleDelete}
        />
      )}

      <Toast message={toast} onClose={() => setToast('')} />
    </div>
  )
}

/* ── Sub-components ─────────────────────────────────── */

function UserFormModal({ title, initial, currentUser, onClose, onSave }) {
  const [form, setForm] = useState({
    name: initial?.name || '',
    email: initial?.email || '',
    password: '',
    role: initial?.role || 'DEVELOPER',
    department: initial?.department || '',
  })
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const isCreate = !initial

  function set(key, val) { setForm((f) => ({ ...f, [key]: val })) }

  async function handleSubmit() {
    if (!form.name.trim() || !form.email.trim()) { setError('Name and email are required'); return }
    if (isCreate && !form.password) { setError('Password is required'); return }
    setSaving(true)
    setError('')
    try {
      const payload = { ...form }
      if (!isCreate && !payload.password) delete payload.password
      await onSave(payload)
    } catch (e) {
      setError(e.response?.data?.error || e.response?.data?.message || 'Save failed')
      setSaving(false)
    }
  }

  // Only admin can assign admin role
  const availableRoles = currentUser?.role === 'ADMIN' ? ROLES : ROLES.filter((r) => r.key !== 'ADMIN')

  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-head">
          <h3>{title}</h3>
          <button className="modal-close" onClick={onClose}><X size={18} /></button>
        </div>
        <div className="modal-body">
          {error && <div className="auth-error" style={{ marginBottom: 14 }}>{error}</div>}
          <div className="field-row">
            <div className="field">
              <label>Full Name *</label>
              <input className="input" value={form.name} onChange={(e) => set('name', e.target.value)} />
            </div>
          </div>
          <div className="field">
            <label>Email Address *</label>
            <input className="input" type="email" value={form.email} onChange={(e) => set('email', e.target.value)} />
          </div>
          {isCreate && (
            <div className="field">
              <label>Password *</label>
              <input className="input" type="password" value={form.password} onChange={(e) => set('password', e.target.value)} minLength={6} />
            </div>
          )}
          <div className="field-row">
            <div className="field">
              <label>Role *</label>
              <select className="input select" value={form.role} onChange={(e) => set('role', e.target.value)}>
                {availableRoles.map((r) => <option key={r.key} value={r.key}>{r.label}</option>)}
              </select>
            </div>
            <div className="field">
              <label>Department</label>
              <select className="input select" value={form.department} onChange={(e) => set('department', e.target.value)}>
                <option value="">Select department…</option>
                {DEPARTMENTS.map((d) => <option key={d} value={d}>{d}</option>)}
              </select>
            </div>
          </div>
        </div>
        <div className="modal-foot">
          <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={saving}>
            {saving ? 'Saving…' : isCreate ? 'Create User' : 'Save Changes'}
          </button>
        </div>
      </div>
    </div>
  )
}

function ResetPasswordModal({ user, onClose, onSave }) {
  const [pwd, setPwd] = useState('')
  const [confirm, setConfirm] = useState('')
  const [show, setShow] = useState(false)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit() {
    if (pwd.length < 6) { setError('Minimum 6 characters'); return }
    if (pwd !== confirm) { setError('Passwords do not match'); return }
    setSaving(true)
    try {
      await onSave(pwd)
    } catch (e) {
      setError(e.response?.data?.error || 'Failed')
      setSaving(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-head">
          <h3>Reset Password</h3>
          <button className="modal-close" onClick={onClose}><X size={18} /></button>
        </div>
        <div className="modal-body">
          <p style={{ color: 'var(--muted)', marginBottom: 16, fontSize: 13 }}>
            Setting a new password for <strong>{user.name}</strong>.
          </p>
          {error && <div className="auth-error" style={{ marginBottom: 14 }}>{error}</div>}
          <div className="field" style={{ position: 'relative' }}>
            <label>New Password</label>
            <input className="input" type={show ? 'text' : 'password'} value={pwd} onChange={(e) => setPwd(e.target.value)} style={{ paddingRight: 36 }} />
            <button type="button" onClick={() => setShow(!show)} style={{ position: 'absolute', right: 10, bottom: 10, background: 'none', border: 'none', color: 'var(--muted)' }}>
              {show ? <EyeOff size={15} /> : <Eye size={15} />}
            </button>
          </div>
          <div className="field">
            <label>Confirm Password</label>
            <input className="input" type={show ? 'text' : 'password'} value={confirm} onChange={(e) => setConfirm(e.target.value)} />
          </div>
        </div>
        <div className="modal-foot">
          <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={saving}>{saving ? 'Resetting…' : 'Reset Password'}</button>
        </div>
      </div>
    </div>
  )
}

function ConfirmModal({ title, message, danger, onClose, onConfirm }) {
  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal" style={{ maxWidth: 420 }}>
        <div className="modal-head">
          <h3>{title}</h3>
          <button className="modal-close" onClick={onClose}><X size={18} /></button>
        </div>
        <div className="modal-body">
          <p style={{ color: 'var(--muted)', fontSize: 14 }}>{message}</p>
        </div>
        <div className="modal-foot">
          <button className="btn btn-secondary" onClick={onClose}>Cancel</button>
          <button className={`btn ${danger ? 'btn-danger' : 'btn-primary'}`} onClick={onConfirm}>Confirm</button>
        </div>
      </div>
    </div>
  )
}
