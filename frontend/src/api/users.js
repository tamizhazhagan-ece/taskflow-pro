import { client } from './client'

export const usersApi = {
  // For project member picker (all authenticated users)
  list: () => client.get('/users').then((r) => r.data),

  // Admin panel endpoints
  adminList: (params) => client.get('/users/admin', { params }).then((r) => r.data),
  getById: (id) => client.get(`/users/${id}`).then((r) => r.data),
  create: (data) => client.post('/users/admin', data).then((r) => r.data),
  update: (id, data) => client.put(`/users/${id}`, data).then((r) => r.data),
  delete: (id) => client.delete(`/users/${id}`).then((r) => r.data),
  resetPassword: (id, newPassword) => client.post(`/users/${id}/reset-password`, { newPassword }).then((r) => r.data),
  activate: (id) => client.post(`/users/${id}/activate`).then((r) => r.data),
  deactivate: (id) => client.post(`/users/${id}/deactivate`).then((r) => r.data),
}
