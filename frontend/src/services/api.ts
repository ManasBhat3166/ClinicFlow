import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('clinic_token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

// Handle expired or invalid authentication
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('clinic_token')
      localStorage.removeItem('clinic_user')

      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    return Promise.reject(error)
  }
)

export default api