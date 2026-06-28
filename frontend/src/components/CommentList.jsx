import React, { useState } from 'react'
import Avatar from './Avatar'
import { formatDateTime } from '../utils'

export default function CommentList({ comments, onAdd, onDelete }) {
  const [text, setText] = useState('')

  function submit(e) {
    e.preventDefault()
    if (!text.trim()) return
    onAdd(text.trim())
    setText('')
  }

  return (
    <div>
      {comments.map((c) => (
        <div key={c.id} className="comment">
          <Avatar user={c.author} size="sm" />
          <div className="comment-bubble">
            <div className="comment-head">
              <span className="comment-author">{c.author.name}</span>
              <span className="comment-time">{formatDateTime(c.createdAt)}</span>
              {onDelete && (
                <button className="btn btn-ghost btn-sm" style={{ marginLeft: 'auto', padding: '2px 6px' }} onClick={() => onDelete(c.id)}>
                  Delete
                </button>
              )}
            </div>
            <div className="comment-text">{c.content}</div>
          </div>
        </div>
      ))}
      <form onSubmit={submit} style={{ display: 'flex', gap: 8, marginTop: 8 }}>
        <input className="input" placeholder="Write a comment…" value={text} onChange={(e) => setText(e.target.value)} />
        <button className="btn btn-primary btn-sm" type="submit">Post</button>
      </form>
    </div>
  )
}
