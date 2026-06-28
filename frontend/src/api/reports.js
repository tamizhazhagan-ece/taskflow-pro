import { client } from './client'

export const reportsApi = {
  dashboard: () => client.get('/reports/dashboard').then((r) => r.data),
  project: (id) => client.get(`/reports/projects/${id}`).then((r) => r.data),
  overdue: () => client.get('/reports/tasks/overdue').then((r) => r.data),
  activity: () => client.get('/reports/activity').then((r) => r.data),
}
