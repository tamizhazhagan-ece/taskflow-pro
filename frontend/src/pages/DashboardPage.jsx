import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Plus, FolderKanban, AlertTriangle } from 'lucide-react'
import { projectsApi } from '../api/projects'
import { reportsApi } from '../api/reports'
import CreateProjectModal from '../components/CreateProjectModal'
import Avatar from '../components/Avatar'
import RoleBadge from '../components/RoleBadge'
import Toast from '../components/Toast'
import { useAuth } from '../auth/AuthContext'
import { canCreateProjects, initials } from '../utils'

export default function DashboardPage() {
  const { user } = useAuth()
  const [projects, setProjects] = useState(null)
  const [report, setReport] = useState(null)
  const [showCreate, setShowCreate] = useState(false)
  const [toast, setToast] = useState('')
  const navigate = useNavigate()
  const canCreate = canCreateProjects(user?.role)

  async function load() {
    const [data, rep] = await Promise.all([
      projectsApi.list(),
      reportsApi.dashboard().catch(() => null),
    ])
    setProjects(data)
    setReport(rep)
  }

  useEffect(() => { load() }, [])

  async function handleCreate(payload) {
    const created = await projectsApi.create(payload)
    setShowCreate(false)
    setToast('Project created')
    navigate(`/projects/${created.id}`)
  }

  const overdueCount = report?.overdueCount || 0
  const totalTasks   = report?.totalTasks   || 0
  const doneTasks    = report?.tasksByStatus?.DONE || 0
  const inProgress   = (report?.tasksByStatus?.IN_PROGRESS || 0) + (report?.tasksByStatus?.CODE_REVIEW || 0) + (report?.tasksByStatus?.TESTING || 0)

  return (
    <>
      {/* Profile Hero Banner */}
      <div className="profile-hero">
        <div className="profile-hero-avatar">
          {initials(user?.name)}
        </div>
        <div>
          <div className="profile-hero-name">Welcome back, {user?.name?.split(' ')[0]}!</div>
          <div className="profile-hero-sub">
            {user?.department || 'TaskFlow Pro'} &middot; {projects?.length || 0} project{projects?.length !== 1 ? 's' : ''} assigned
          </div>
        </div>
        <div className="profile-hero-badge">{user?.role?.replace('_', ' ')}</div>
      </div>

      {/* Stats Row */}
      {report && (
        <div className="stats-row">
          <div className="stat-card">
            <div className="stat-value">{projects?.length || 0}</div>
            <div className="stat-label">Projects</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{totalTasks}</div>
            <div className="stat-label">Total Tasks</div>
          </div>
          <div className="stat-card">
            <div className="stat-value" style={{ color: 'var(--success)' }}>{doneTasks}</div>
            <div className="stat-label">Completed</div>
          </div>
          <div className="stat-card">
            <div className="stat-value" style={{ color: 'var(--accent)' }}>{inProgress}</div>
            <div className="stat-label">In Progress</div>
          </div>
          {overdueCount > 0 && (
            <div className="stat-card">
              <div className="stat-value" style={{ color: 'var(--danger)' }}>{overdueCount}</div>
              <div className="stat-label">Overdue</div>
            </div>
          )}
        </div>
      )}

      <div className="page-header">
        <div>
          <span className="page-eyebrow">Workspace</span>
          <h1>Your projects</h1>
          <p className="page-sub">Plan work, track progress and keep the whole team in sync.</p>
        </div>
        {canCreate && (
          <button className="btn btn-primary" onClick={() => setShowCreate(true)}>
            <Plus size={16} /> New project
          </button>
        )}
      </div>

      {overdueCount > 0 && (
        <div className="alert-banner">
          <AlertTriangle size={18} />
          You have {overdueCount} overdue task{overdueCount > 1 ? 's' : ''}. Check the Reports page for details.
        </div>
      )}

      {projects === null ? (
        <div className="loading-bar" />
      ) : projects.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon"><FolderKanban size={26} /></div>
          <h3>No projects yet</h3>
          <p>{canCreate ? 'Create your first project to start organizing tasks on a board.' : 'You have not been added to any projects yet.'}</p>
          {canCreate && (
            <button className="btn btn-primary" style={{ marginTop: 16 }} onClick={() => setShowCreate(true)}>
              <Plus size={16} /> New project
            </button>
          )}
        </div>
      ) : (
        <div className="project-grid">
          {projects.map((p) => {
            const pct = p.taskCount > 0 ? Math.round((p.doneCount / p.taskCount) * 100) : 0
            return (
              <div key={p.id} className="project-card" onClick={() => navigate(`/projects/${p.id}`)} style={{ cursor: 'pointer' }}>
                <div className="project-card-top">
                  <div style={{ display: 'flex', gap: 10 }}>
                    <span className="project-color-dot" style={{ background: p.color }} />
                    <h3>{p.name}</h3>
                  </div>
                </div>
                <p className="project-desc">{p.description || 'No description yet.'}</p>
                <div className="project-progress-track">
                  <div className="project-progress-fill" style={{ width: `${pct}%`, background: p.color }} />
                </div>
                <div className="project-card-footer">
                  <div className="avatar-stack">
                    {p.members.slice(0, 4).map((m) => <Avatar key={m.id} user={m} size="sm" />)}
                  </div>
                  <span className="project-meta">{pct}% complete &middot; {p.doneCount}/{p.taskCount}</span>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {showCreate && <CreateProjectModal onClose={() => setShowCreate(false)} onCreate={handleCreate} />}
      <Toast message={toast} onClose={() => setToast('')} />
    </>
  )
}
