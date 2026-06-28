import { client } from './client'

export const projectsApi = {
  list: () => client.get('/projects').then((r) => r.data),
  get: (id) => client.get(`/projects/${id}`).then((r) => r.data),
  create: (data) => client.post('/projects', data).then((r) => r.data),
  update: (id, data) => client.put(`/projects/${id}`, data).then((r) => r.data),
  remove: (id) => client.delete(`/projects/${id}`),
  addMember: (id, userId) => client.post(`/projects/${id}/members`, { userId }).then((r) => r.data),
  removeMember: (id, userId) => client.delete(`/projects/${id}/members/${userId}`).then((r) => r.data),
}
