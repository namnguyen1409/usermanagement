import axios from 'axios'


export const logout = async () => {
  try {
    console.log('Logging out...')
    
    await axios.post(
      `${import.meta.env.VITE_API_URL}/auth/logout`,
      {},
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}`
        },
        withCredentials: true
      }
    )
    localStorage.removeItem('accessToken')
    localStorage.removeItem('rememberMe')
  } catch (err) {
    console.error('Logout failed:', err)
  }
}
