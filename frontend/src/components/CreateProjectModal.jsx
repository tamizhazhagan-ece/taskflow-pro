import React, { useState } from 'react'
import Modal from './Modal'

const COLORS = ['#6366f1', '#8b5cf6', '#ec4899', '#10b981', '#f59e0b', '#3b82f6', '#ef4444']

export default function CreateProjectModal({ onClose, onCreate }) {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [color, setColor] = useState(COLORS[0])

  function handleSubmit(e) {
    e.preventDefault()
    if (!name.trim()) return
    onCreate({ name: name.trim(), description, color })
  }

  return (
    <Modal title="New project" onClose={onClose} footer={
      <>
        <button className="btn btn-ghost" onClick={onClose}>Cancel</button>
        <button className="btn btn-primary" onClick={handleSubmit}>Create</button>
      </>
    }>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label>Project name</label>
          <input className="input" value={name} onChange={(e) => setName(e.target.value)} required autoFocus />
        </div>
        <div className="field">
          <label>Description</label>
          <textarea className="textarea" value={description} onChange={(e) => setDescription(e.target.value)} />
        </div>
        <div className="field">
          <label>Color</label>
          <div style={{ display: 'flex', gap: 8 }}>
            {COLORS.map((c) => (
              <button
                key={c}
                type="button"
                onClick={() => setColor(c)}
                style={{
                  width: 28, height: 28, borderRadius: '50%', background: c,
                  border: color === c ? '2px solid white' : '2px solid transparent',
                  outline: color === c ? `2px solid ${c}` : 'none',
                }}
              />
            ))}
          </div>
        </div>
      </form>
    </Modal>
  )
}
