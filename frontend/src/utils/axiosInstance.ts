import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  },
  timeout: 5000,
  withCredentials: true
})

let isRefreshing = false
let failedQueue: any[] = []

const processQueue = (error: any, token: string | null) => {
  failedQueue.forEach((prom) => {
    error ? prom.reject(error) : prom.resolve(token)
  })
  failedQueue = []
}

axiosInstance.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers['Authorization'] = `Bearer ${accessToken}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error?.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then((token) => {
          originalRequest.headers['Authorization'] = `Bearer ${token}`
          return axiosInstance(originalRequest)
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject(error)
      }

      return new Promise((resolve, reject) => {
        axios
          .post(`${import.meta.env.VITE_API_URL}/auth/refresh-token`, {
            token: refreshToken
          })
          .then(({ data }) => {
            const newToken = data?.data?.token

            if (newToken) {
              localStorage.setItem('accessToken', newToken)
              axiosInstance.defaults.headers['Authorization'] = `Bearer ${newToken}`
              originalRequest.headers['Authorization'] = `Bearer ${newToken}`
              processQueue(null, newToken)
              resolve(axiosInstance(originalRequest))
            } else {
              throw new Error('Token refresh failed: empty token')
            }
          })
          .catch((err) => {
            processQueue(err, null)
            reject(err)
          })
          .finally(() => {
            isRefreshing = false
          })
      })
    }

    return Promise.reject(error)
  }
)

export default axiosInstance
