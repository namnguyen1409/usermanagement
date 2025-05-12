import { useRolePermission } from '../hooks/useRolePermission'

const CanAccess = ({
  rolePermissions,
  blockRolePermission,
  children
}: {
  rolePermissions?: string[]
  blockRolePermission?: string[]
  children: React.ReactNode
}) => {
  const userRolePermissions = useRolePermission()

  const hasBlockRolePermission = blockRolePermission
    ? userRolePermissions.some((permission) => blockRolePermission.includes(permission))
    : false

  if (hasBlockRolePermission) return null

  if (!rolePermissions || rolePermissions.length === 0) {
    return <>{children}</>
  }

  const hasPermission = userRolePermissions.some((permission) => rolePermissions.includes(permission))

  return hasPermission ? <>{children}</> : null
}

export default CanAccess
