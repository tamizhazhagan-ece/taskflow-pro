import { client } from './client'

export const tasksApi = {
  listForProject: (projectId) => client.get(`/projects/${projectId}/tasks`).then((r) => r.data),
  get: (id) => client.get(`/tasks/${id}`).then((r) => r.data),
  create: (projectId, data) => client.post(`/projects/${projectId}/tasks`, data).then((r) => r.data),
  update: (id, data) => client.put(`/tasks/${id}`, data).then((r) => r.data),
  move: (id, status, position) => client.patch(`/tasks/${id}/move`, { status, position }).then((r) => r.data),
  remove: (id) => client.delete(`/tasks/${id}`),
}
