import React, { useState } from 'react'
import Modal from './Modal'
import { PRIORITIES } from '../utils'

export default function CreateTaskModal({ status, members, onClose, onCreate }) {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [priority, setPriority] = useState('MEDIUM')
  const [assigneeId, setAssigneeId] = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    if (!title.trim()) return
    onCreate({
      title: title.trim(),
      description,
      status,
      priority,
      assigneeId: assigneeId ? Number(assigneeId) : null,
    })
  }

  return (
    <Modal title="New task" onClose={onClose} footer={
      <>
        <button className="btn btn-ghost" onClick={onClose}>Cancel</button>
        <button className="btn btn-primary" onClick={handleSubmit}>Add task</button>
      </>
    }>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label>Title</label>
          <input className="input" value={title} onChange={(e) => setTitle(e.target.value)} required autoFocus />
        </div>
        <div className="field">
          <label>Description</label>
          <textarea className="textarea" value={description} onChange={(e) => setDescription(e.target.value)} />
        </div>
        <div className="field-row">
          <div className="field">
            <label>Priority</label>
            <select className="select" value={priority} onChange={(e) => setPriority(e.target.value)}>
              {PRIORITIES.map((p) => <option key={p.key} value={p.key}>{p.label}</option>)}
            </select>
          </div>
          <div className="field">
            <label>Assignee</label>
            <select className="select" value={assigneeId} onChange={(e) => setAssigneeId(e.target.value)}>
              <option value="">Unassigned</option>
              {members.map((m) => <option key={m.id} value={m.id}>{m.name}</option>)}
            </select>
          </div>
        </div>
      </form>
    </Modal>
  )
}
