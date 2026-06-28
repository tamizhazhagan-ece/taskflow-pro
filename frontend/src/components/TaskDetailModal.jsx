import React, { useEffect, useState } from 'react'
import { Trash2 } from 'lucide-react'
import Modal from './Modal'
import Avatar from './Avatar'
import CommentList from './CommentList'
import AttachmentList from './AttachmentList'
import { STATUS_COLUMNS, PRIORITIES, formatDateTime } from '../utils'
import { commentsApi, attachmentsApi } from '../api/comments'

export default function TaskDetailModal({ task, members, onClose, onUpdate, onDelete }) {
  const [title, setTitle] = useState(task.title)
  const [description, setDescription] = useState(task.description || '')
  const [comments, setComments] = useState([])
  const [attachments, setAttachments] = useState([])
  const [loadingExtras, setLoadingExtras] = useState(true)

  useEffect(() => {
    let active = true
    async function load() {
      setLoadingExtras(true)
      const [c, a] = await Promise.all([
        commentsApi.listForTask(task.id),
        attachmentsApi.listForTask(task.id),
      ])
      if (active) {
        setComments(c)
        setAttachments(a)
        setLoadingExtras(false)
      }
    }
    load()
    return () => { active = false }
  }, [task.id])

  function saveTitle() {
    if (title.trim() && title.trim() !== task.title) {
      onUpdate(task.id, { title: title.trim() })
    }
  }

  function saveDescription() {
    if (description !== (task.description || '')) {
      onUpdate(task.id, { description })
    }
  }

  async function handleAddComment(content) {
    const created = await commentsApi.add(task.id, { content })
    setComments((prev) => [...prev, created])
  }

  async function handleDeleteComment(id) {
    await commentsApi.remove(id)
    setComments((prev) => prev.filter((c) => c.id !== id))
  }

  async function handleUpload(file) {
    const created = await attachmentsApi.upload(task.id, file)
    setAttachments((prev) => [...prev, created])
  }

  async function handleRemoveAttachment(id) {
    await attachmentsApi.remove(id)
    setAttachments((prev) => prev.filter((a) => a.id !== id))
  }

  return (
    <Modal
      title=""
      onClose={onClose}
      wide
      footer={
        <button className="btn btn-danger" onClick={() => onDelete(task.id)}>
          <Trash2 size={14} /> Delete task
        </button>
      }
    >
      <div className="task-detail-grid">
        <div>
          <input
            className="input"
            style={{ fontSize: 18, fontWeight: 700, fontFamily: 'var(--font-display)', border: 'none', padding: '4px 0', marginBottom: 6, background: 'transparent' }}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onBlur={saveTitle}
          />
          <p style={{ fontSize: 11.5, color: 'var(--muted-light)', marginBottom: 16 }}>
            Created {formatDateTime(task.createdAt)} · Updated {formatDateTime(task.updatedAt)}
          </p>

          <div className="task-detail-section">
            <span className="task-detail-label">Description</span>
            <textarea
              className="textarea"
              style={{ minHeight: 100 }}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              onBlur={saveDescription}
              placeholder="Add a description…"
            />
          </div>

          <div className="task-detail-section">
            <span className="task-detail-label">Comments</span>
            {loadingExtras ? <div className="loading-bar" /> : (
              <CommentList comments={comments} onAdd={handleAddComment} onDelete={handleDeleteComment} />
            )}
          </div>

          <div className="task-detail-section">
            <span className="task-detail-label">Attachments</span>
            {loadingExtras ? <div className="loading-bar" /> : (
              <AttachmentList attachments={attachments} onUpload={handleUpload} onRemove={handleRemoveAttachment} />
            )}
          </div>
        </div>

        <div>
          <div className="side-field">
            <span className="task-detail-label">Status</span>
            <select className="select" value={task.status} onChange={(e) => onUpdate(task.id, { status: e.target.value })}>
              {STATUS_COLUMNS.map((s) => <option key={s.key} value={s.key}>{s.label}</option>)}
            </select>
          </div>
          <div className="side-field">
            <span className="task-detail-label">Priority</span>
            <select className="select" value={task.priority} onChange={(e) => onUpdate(task.id, { priority: e.target.value })}>
              {PRIORITIES.map((p) => <option key={p.key} value={p.key}>{p.label}</option>)}
            </select>
          </div>
          <div className="side-field">
            <span className="task-detail-label">Due date</span>
            <input
              className="input"
              type="date"
              value={task.dueDate || ''}
              onChange={(e) => onUpdate(task.id, { dueDate: e.target.value || null })}
            />
          </div>
          <div className="side-field">
            <span className="task-detail-label">Assignee</span>
            <div className="member-chip-list">
              <button
                type="button"
                className="member-chip"
                style={{ background: !task.assignee ? 'var(--accent-soft)' : 'var(--bg)' }}
                onClick={() => onUpdate(task.id, { clearAssignee: true })}
              >
                <Avatar user={null} size="sm" /> Unassigned
              </button>
              {members.map((m) => (
                <button
                  type="button"
                  key={m.id}
                  className="member-chip"
                  style={{ background: task.assignee?.id === m.id ? 'var(--accent-soft)' : 'var(--bg)' }}
                  onClick={() => onUpdate(task.id, { assigneeId: m.id })}
                >
                  <Avatar user={m} size="sm" /> {m.name}
                </button>
              ))}
            </div>
          </div>
          <div className="side-field">
            <span className="task-detail-label">Reporter</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Avatar user={task.reporter} size="sm" />
              <span style={{ fontSize: 13.5 }}>{task.reporter?.name || 'Unknown'}</span>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  )
}
