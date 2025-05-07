import { useEffect, useState } from 'react'
import { message } from 'antd'
import { jwtDecode } from 'jwt-decode'
import { useNavigate } from 'react-router'

const PrivateRoute = ({
  element,
  allowedRolesPermissions
}: {
  element: React.ReactNode
  allowedRolesPermissions: string[]
}) => {
  const navigate = useNavigate()
  const [isAuthorized, setIsAuthorized] = useState<boolean | null>(null) // null = đang kiểm tra

  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken')

    if (!accessToken) {
      navigate('/login', { replace: true })
      return
    }

    try {
      const decodedToken: {
        scope: string
        [key: string]: any
      } = jwtDecode(accessToken)

      const userPermissions = decodedToken.scope?.split(' ') || []

      const hasPermission = allowedRolesPermissions.some((permission) => userPermissions.includes(permission))

      if (!hasPermission) {
        message.error('You do not have permission to access this page.')
        navigate('/login', { replace: true })
      } else {
        setIsAuthorized(true)
      }
    } catch (error) {
      console.error('Token decoding error:', error)
      navigate('/login', { replace: true })
    }
  }, [allowedRolesPermissions, navigate])

  // Optional: loading indicator while checking auth
  if (isAuthorized === null) return null

  return <>{element}</>
}

export default PrivateRoute
