import React, { useEffect, useState, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ArrowLeft, Plus, Users as UsersIcon } from 'lucide-react'
import { projectsApi } from '../api/projects'
import { tasksApi } from '../api/tasks'
import Avatar from '../components/Avatar'
import TaskCard from '../components/TaskCard'
import CreateTaskModal from '../components/CreateTaskModal'
import TaskDetailModal from '../components/TaskDetailModal'
import ManageMembersModal from '../components/ManageMembersModal'
import Toast from '../components/Toast'
import { STATUS_COLUMNS } from '../utils'
import { useAuth } from '../auth/AuthContext'

export default function ProjectBoardPage() {
  const { projectId } = useParams()
  const { user } = useAuth()
  const [project, setProject] = useState(null)
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [addingTo, setAddingTo] = useState(null)
  const [openTaskId, setOpenTaskId] = useState(null)
  const [showMembers, setShowMembers] = useState(false)
  const [draggedTask, setDraggedTask] = useState(null)
  const [dragOverColumn, setDragOverColumn] = useState(null)
  const [toast, setToast] = useState('')
  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER' || user?.role === 'TEAM_LEAD'

  const loadAll = useCallback(async () => {
    const [p, t] = await Promise.all([projectsApi.get(projectId), tasksApi.listForProject(projectId)])
    setProject(p)
    setTasks(t)
    setLoading(false)
  }, [projectId])

  useEffect(() => {
    loadAll()
  }, [loadAll])

  async function refreshTasks() {
    setTasks(await tasksApi.listForProject(projectId))
  }

  async function refreshProject() {
    setProject(await projectsApi.get(projectId))
  }

  async function handleCreateTask(payload) {
    await tasksApi.create(projectId, payload)
    setAddingTo(null)
    await refreshTasks()
    setToast('Task added')
  }

  async function handleUpdateTask(taskId, payload) {
    await tasksApi.update(taskId, payload)
    await refreshTasks()
  }

  async function handleDeleteTask(taskId) {
    await tasksApi.remove(taskId)
    setOpenTaskId(null)
    await refreshTasks()
    setToast('Task deleted')
  }

  function columnTasks(statusKey) {
    return tasks.filter((t) => t.status === statusKey).sort((a, b) => a.position - b.position)
  }

  function handleDragStart(e, task) {
    setDraggedTask(task)
    e.dataTransfer.setData('text/plain', String(task.id))
    e.dataTransfer.effectAllowed = 'move'
  }

  function handleDragEnd() {
    setDraggedTask(null)
    setDragOverColumn(null)
  }

  async function dropInto(statusKey, position) {
    if (!draggedTask) return
    if (draggedTask.status === statusKey && draggedTask.position === position) {
      setDraggedTask(null)
      setDragOverColumn(null)
      return
    }
    await tasksApi.move(draggedTask.id, statusKey, position)
    setDraggedTask(null)
    setDragOverColumn(null)
    await refreshTasks()
  }

  function handleColumnDragOver(e, statusKey) {
    e.preventDefault()
    setDragOverColumn(statusKey)
  }

  async function handleColumnDrop(e, statusKey) {
    e.preventDefault()
    const target = columnTasks(statusKey).filter((t) => t.id !== draggedTask?.id)
    await dropInto(statusKey, target.length)
  }

  async function handleCardDrop(e, statusKey, targetTask) {
    e.preventDefault()
    e.stopPropagation()
    const colTasks = columnTasks(statusKey).filter((t) => t.id !== draggedTask?.id)
    const idx = colTasks.findIndex((t) => t.id === targetTask.id)
    await dropInto(statusKey, idx >= 0 ? idx : colTasks.length)
  }

  if (loading || !project) {
    return <div className="loading-bar" style={{ marginTop: 40 }} />
  }

  const openTask = tasks.find((t) => t.id === openTaskId)

  return (
    <>
      <Link to="/" className="btn btn-ghost btn-sm" style={{ marginBottom: 14 }}>
        <ArrowLeft size={14} /> All projects
      </Link>

      <div className="board-header">
        <div className="board-title-row">
          <span className="board-color-bar" style={{ background: project.color }} />
          <div>
            <h1 style={{ fontSize: 22 }}>{project.name}</h1>
            {project.description && <p className="page-sub" style={{ marginTop: 2 }}>{project.description}</p>}
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div className="avatar-stack">
            {project.members.slice(0, 5).map((m) => <Avatar key={m.id} user={m} size="sm" />)}
          </div>
          {canManage && (
            <button className="btn btn-secondary btn-sm" onClick={() => setShowMembers(true)}>
              <UsersIcon size={14} /> Members
            </button>
          )}
        </div>
      </div>

      <div className="board">
        {STATUS_COLUMNS.map((col) => {
          const list = columnTasks(col.key)
          return (
            <div
              key={col.key}
              className={`board-column ${dragOverColumn === col.key ? 'drag-over' : ''}`}
              style={{ '--col-color': col.color }}
              onDragOver={(e) => handleColumnDragOver(e, col.key)}
              onDrop={(e) => handleColumnDrop(e, col.key)}
              onDragLeave={() => setDragOverColumn((c) => (c === col.key ? null : c))}
            >
              <div className="board-column-head">
                <h4>{col.label}</h4>
                <span className="column-count">{list.length}</span>
              </div>
              <div className="column-cards">
                {list.map((task) => (
                  <div key={task.id} onDrop={(e) => handleCardDrop(e, col.key, task)} onDragOver={(e) => e.preventDefault()}>
                    <TaskCard
                      task={task}
                      onOpen={(t) => setOpenTaskId(t.id)}
                      onDragStart={handleDragStart}
                      onDragEnd={handleDragEnd}
                      dragging={draggedTask?.id === task.id}
                    />
                  </div>
                ))}
              </div>
              <button className="add-card-btn" onClick={() => setAddingTo(col.key)}>
                <Plus size={13} style={{ verticalAlign: -2 }} /> Add task
              </button>
            </div>
          )
        })}
      </div>

      {addingTo && (
        <CreateTaskModal
          status={addingTo}
          members={project.members}
          onClose={() => setAddingTo(null)}
          onCreate={handleCreateTask}
        />
      )}

      {openTask && (
        <TaskDetailModal
          task={openTask}
          members={project.members}
          onClose={() => setOpenTaskId(null)}
          onUpdate={handleUpdateTask}
          onDelete={handleDeleteTask}
        />
      )}

      {showMembers && (
        <ManageMembersModal
          project={project}
          onClose={() => setShowMembers(false)}
          onAdd={async (userId) => { await projectsApi.addMember(project.id, userId); await refreshProject() }}
          onRemove={async (userId) => { await projectsApi.removeMember(project.id, userId); await refreshProject() }}
        />
      )}

      <Toast message={toast} onClose={() => setToast('')} />
    </>
  )
}
