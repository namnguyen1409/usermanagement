import { jwtDecode } from 'jwt-decode'

export const useRolePermission = () => {
  const token = localStorage.getItem('accessToken')
  if (!token) return []

  try {
    const decoded: {
      scope: string
    } = jwtDecode(token)

    return decoded.scope.split(' ')
  } catch (error) {
    console.error('Invalid token', error)
    return []
  }
}
