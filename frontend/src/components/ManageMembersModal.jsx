import React, { useEffect, useState } from 'react'
import Modal from './Modal'
import Avatar from './Avatar'
import { usersApi } from '../api/users'

export default function ManageMembersModal({ project, onClose, onAdd, onRemove }) {
  const [allUsers, setAllUsers] = useState([])
  const memberIds = new Set(project.members.map((m) => m.id))
  const nonMembers = allUsers.filter((u) => !memberIds.has(u.id))

  useEffect(() => {
    usersApi.list().then(setAllUsers)
  }, [])

  return (
    <Modal title="Project members" onClose={onClose}>
      <div style={{ marginBottom: 20 }}>
        <span className="task-detail-label">Current members</span>
        {project.members.map((m) => (
          <div key={m.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 0' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Avatar user={m} size="sm" />
              <span style={{ fontSize: 13.5, fontWeight: 600 }}>{m.name}</span>
              <span style={{ fontSize: 12, color: 'var(--muted)' }}>{m.role}</span>
            </div>
            {m.id !== project.owner?.id && (
              <button className="btn btn-ghost btn-sm" onClick={() => onRemove(m.id)}>Remove</button>
            )}
          </div>
        ))}
      </div>
      {nonMembers.length > 0 && (
        <div>
          <span className="task-detail-label">Add member</span>
          {nonMembers.map((u) => (
            <div key={u.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 0' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <Avatar user={u} size="sm" />
                <span style={{ fontSize: 13.5 }}>{u.name}</span>
              </div>
              <button className="btn btn-secondary btn-sm" onClick={() => onAdd(u.id)}>Add</button>
            </div>
          ))}
        </div>
      )}
    </Modal>
  )
}
