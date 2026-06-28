import axios from 'axios'

const TOKEN_KEY = 'taskflow_token'

const client = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config.url?.includes('/auth/')) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('taskflow_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export { TOKEN_KEY, client }
