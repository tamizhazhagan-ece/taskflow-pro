import React, { useEffect } from 'react'
import { X } from 'lucide-react'

export default function Modal({ title, children, onClose, footer, wide }) {
  useEffect(() => {
    function onKey(e) {
      if (e.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className={`modal ${wide ? 'modal-wide' : ''}`} onClick={(e) => e.stopPropagation()}>
        {title && (
          <div className="modal-head">
            <h2 style={{ fontSize: 17 }}>{title}</h2>
            <button className="modal-close" onClick={onClose}><X size={18} /></button>
          </div>
        )}
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-foot">{footer}</div>}
      </div>
    </div>
  )
}
