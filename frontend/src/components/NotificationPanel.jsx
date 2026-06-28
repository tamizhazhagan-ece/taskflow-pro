import React, { useEffect, useState } from 'react'
import { Bell, CheckCheck } from 'lucide-react'
import { notificationsApi } from '../api/notifications'
import { formatDateTime } from '../utils'

export default function NotificationPanel() {
  const [open, setOpen] = useState(false)
  const [notifications, setNotifications] = useState([])
  const [unread, setUnread] = useState(0)

  async function load() {
    const [list, count] = await Promise.all([
      notificationsApi.list(),
      notificationsApi.unreadCount(),
    ])
    setNotifications(list)
    setUnread(count.count)
  }

  useEffect(() => {
    load()
    const interval = setInterval(load, 30000)
    return () => clearInterval(interval)
  }, [])

  async function markRead(id) {
    await notificationsApi.markRead(id)
    load()
  }

  async function markAllRead() {
    await notificationsApi.markAllRead()
    load()
  }

  return (
    <div className="notif-bell">
      <button className="btn btn-ghost btn-sm" onClick={() => { setOpen(!open); if (!open) load() }}>
        <Bell size={18} />
        {unread > 0 && <span className="notif-badge">{unread > 9 ? '9+' : unread}</span>}
      </button>
      {open && (
        <div className="notif-panel">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 14px', borderBottom: '1px solid var(--border)' }}>
            <span style={{ fontWeight: 700, fontSize: 13 }}>Notifications</span>
            {unread > 0 && (
              <button className="btn btn-ghost btn-sm" onClick={markAllRead}>
                <CheckCheck size={14} /> Mark all read
              </button>
            )}
          </div>
          {notifications.length === 0 ? (
            <div style={{ padding: 20, textAlign: 'center', color: 'var(--muted)', fontSize: 13 }}>No notifications</div>
          ) : (
            notifications.slice(0, 15).map((n) => (
              <div
                key={n.id}
                className={`notif-item ${!n.read ? 'unread' : ''}`}
                onClick={() => !n.read && markRead(n.id)}
                style={{ cursor: n.read ? 'default' : 'pointer' }}
              >
                <div className="notif-item-type">{n.type.replace(/_/g, ' ')}</div>
                <div className="notif-item-msg">{n.message}</div>
                <div className="notif-item-time">{formatDateTime(n.createdAt)}</div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  )
}
