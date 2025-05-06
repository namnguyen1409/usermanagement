import { useRolePermission } from "../hooks/useRolePermission";

const canAccess = ({
    rolePermissions,
    children
} :{
    rolePermissions: string[],
    children: React.ReactNode
}) => {
    const userRolePermissions = useRolePermission();
    const hasPermission = userRolePermissions.some((permission) =>
        rolePermissions.includes(permission)
    );
    return hasPermission ? <>{children}</> : null;
}

export default canAccess;