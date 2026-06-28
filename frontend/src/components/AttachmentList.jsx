import React, { useRef } from 'react'
import { Download, Trash2, Paperclip } from 'lucide-react'
import { attachmentsApi } from '../api/comments'
import { formatFileSize } from '../utils'

export default function AttachmentList({ attachments, onUpload, onRemove }) {
  const inputRef = useRef()

  return (
    <div>
      {attachments.map((a) => (
        <div key={a.id} className="attachment-row">
          <div className="attachment-left">
            <Paperclip size={14} color="var(--muted)" />
            <div>
              <div className="attachment-name">{a.originalFileName}</div>
              <div className="attachment-size">{formatFileSize(a.size)}</div>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 6 }}>
            <a className="btn btn-ghost btn-sm" href={attachmentsApi.downloadUrl(a.id)} target="_blank" rel="noreferrer">
              <Download size={13} />
            </a>
            {onRemove && (
              <button className="btn btn-ghost btn-sm" onClick={() => onRemove(a.id)}>
                <Trash2 size={13} />
              </button>
            )}
          </div>
        </div>
      ))}
      <div className="upload-zone" onClick={() => inputRef.current?.click()}>
        Click to upload a file (max 15 MB)
        <input ref={inputRef} type="file" hidden onChange={(e) => { if (e.target.files[0]) onUpload(e.target.files[0]); e.target.value = '' }} />
      </div>
    </div>
  )
}
