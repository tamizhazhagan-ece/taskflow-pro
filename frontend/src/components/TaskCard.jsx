import React from 'react'
import { Calendar } from 'lucide-react'
import Avatar from './Avatar'
import { priorityMeta, formatDate, isOverdue } from '../utils'

export default function TaskCard({ task, onOpen, onDragStart, onDragEnd, dragging }) {
  const pri = priorityMeta(task.priority)
  const overdue = isOverdue(task.dueDate, task.status)

  return (
    <div
      className={`task-card ${dragging ? 'dragging' : ''}`}
      style={{ '--pri-color': pri.color }}
      draggable
      onDragStart={(e) => onDragStart(e, task)}
      onDragEnd={onDragEnd}
      onClick={() => onOpen(task)}
    >
      <div className="task-card-title">{task.title}</div>
      <div className="task-card-row">
        <div className="task-meta-left">
          <span className="priority-dot" style={{ background: pri.color }} />
          {task.dueDate && (
            <span className={`due-chip ${overdue ? 'overdue' : ''}`}>
              <Calendar size={10} style={{ verticalAlign: -1, marginRight: 2 }} />
              {formatDate(task.dueDate)}
            </span>
          )}
        </div>
        {task.assignee && <Avatar user={task.assignee} size="sm" />}
      </div>
    </div>
  )
}
