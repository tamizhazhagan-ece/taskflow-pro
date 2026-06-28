import { client } from './client'

export const commentsApi = {
  listForTask: (taskId) => client.get(`/tasks/${taskId}/comments`).then((r) => r.data),
  add: (taskId, data) => client.post(`/tasks/${taskId}/comments`, data).then((r) => r.data),
  remove: (id) => client.delete(`/comments/${id}`),
}

export const attachmentsApi = {
  listForTask: (taskId) => client.get(`/tasks/${taskId}/attachments`).then((r) => r.data),
  upload: (taskId, file) => {
    const form = new FormData()
    form.append('file', file)
    return client.post(`/tasks/${taskId}/attachments`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then((r) => r.data)
  },
  downloadUrl: (id) => `http://localhost:8080/api/attachments/${id}/download`,
  remove: (id) => client.delete(`/attachments/${id}`),
}
