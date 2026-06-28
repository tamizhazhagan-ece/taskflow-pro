import React, { useEffect, useState } from 'react'
import { reportsApi } from '../api/reports'
import { projectsApi } from '../api/projects'
import { STATUS_COLUMNS, PRIORITIES, formatDate } from '../utils'

export default function ReportsPage() {
  const [dashboard, setDashboard] = useState(null)
  const [activity, setActivity] = useState([])
  const [projects, setProjects] = useState([])
  const [selectedProject, setSelectedProject] = useState('')
  const [projectReport, setProjectReport] = useState(null)

  useEffect(() => {
    async function load() {
      const [dash, act, projs] = await Promise.all([
        reportsApi.dashboard(),
        reportsApi.activity(),
        projectsApi.list(),
      ])
      setDashboard(dash)
      setActivity(act)
      setProjects(projs)
      if (projs.length > 0) {
        setSelectedProject(String(projs[0].id))
      }
    }
    load()
  }, [])

  useEffect(() => {
    if (!selectedProject) return
    reportsApi.project(selectedProject).then(setProjectReport)
  }, [selectedProject])

  if (!dashboard) {
    return <div className="loading-bar" style={{ marginTop: 40 }} />
  }

  const maxStatus = Math.max(...Object.values(dashboard.tasksByStatus || {}), 1)

  return (
    <>
      <div className="page-header">
        <div>
          <span className="page-eyebrow">Analytics</span>
          <h1>Reports</h1>
          <p className="page-sub">Track team performance, task distribution, and recent activity.</p>
        </div>
      </div>

      <div className="report-grid">
        <div className="stat-card">
          <div className="stat-value">{dashboard.totalProjects}</div>
          <div className="stat-label">Projects</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{dashboard.totalTasks}</div>
          <div className="stat-label">Total Tasks</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{Math.round(dashboard.completionRate)}%</div>
          <div className="stat-label">Completion Rate</div>
        </div>
        <div className="stat-card">
          <div className="stat-value" style={{ color: dashboard.overdueCount > 0 ? 'var(--danger)' : 'var(--success)' }}>
            {dashboard.overdueCount}
          </div>
          <div className="stat-label">Overdue Tasks</div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
        <div className="chart-section">
          <h3>Tasks by Status</h3>
          <div className="bar-chart">
            {STATUS_COLUMNS.map((col) => {
              const count = dashboard.tasksByStatus?.[col.key] || 0
              return (
                <div key={col.key} className="bar-row">
                  <span className="bar-label">{col.label}</span>
                  <div className="bar-track">
                    <div className="bar-fill" style={{ width: `${(count / maxStatus) * 100}%`, background: col.color, minWidth: count > 0 ? 24 : 0 }}>
                      {count > 0 && count}
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        </div>

        {projectReport && (
          <div className="chart-section">
            <h3>Project Priority Distribution</h3>
            <select className="select" style={{ marginBottom: 16, maxWidth: 240 }} value={selectedProject} onChange={(e) => setSelectedProject(e.target.value)}>
              {projects.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
            </select>
            <div className="pie-legend">
              {PRIORITIES.map((pri) => {
                const count = projectReport.tasksByPriority?.[pri.key] || 0
                if (count === 0) return null
                return (
                  <div key={pri.key} className="pie-legend-item">
                    <span className="pie-dot" style={{ background: pri.color }} />
                    {pri.label}: {count}
                  </div>
                )
              })}
            </div>
            {projectReport.memberWorkload?.length > 0 && (
              <div style={{ marginTop: 20 }}>
                <h3 style={{ fontSize: 13, marginBottom: 10 }}>Member Workload</h3>
                {projectReport.memberWorkload.map((m) => (
                  <div key={m.userId} className="bar-row" style={{ marginBottom: 6 }}>
                    <span className="bar-label" style={{ width: 100 }}>{m.userName}</span>
                    <div className="bar-track">
                      <div className="bar-fill" style={{ width: `${(m.taskCount / Math.max(projectReport.totalTasks, 1)) * 100}%`, background: 'var(--accent)' }}>
                        {m.taskCount}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>

      {dashboard.overdueTasks?.length > 0 && (
        <div className="chart-section">
          <h3>Overdue Tasks</h3>
          {dashboard.overdueTasks.map((t) => (
            <div key={t.id} className="activity-item">
              <strong>{t.title}</strong> — due {formatDate(t.dueDate)}
            </div>
          ))}
        </div>
      )}

      <div className="chart-section">
        <h3>Recent Activity (7 days)</h3>
        {activity.length === 0 ? (
          <p style={{ color: 'var(--muted)', fontSize: 13 }}>No recent activity.</p>
        ) : (
          <div className="activity-list">
            {activity.map((item, i) => (
              <div key={i} className="activity-item">
                {item.message}
                <div className="activity-time">{new Date(item.timestamp).toLocaleString()}</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  )
}
