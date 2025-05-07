import { useRolePermission } from "../hooks/useRolePermission";

const CanAccess = ({
    rolePermissions,
    blockRolePermission,
    children
}: {
    rolePermissions?: string[];
    blockRolePermission?: string[];
    children: React.ReactNode;
}) => {
    const userRolePermissions = useRolePermission();

    // Nếu có danh sách block, kiểm tra người dùng có permission bị block không
    const hasBlockRolePermission = blockRolePermission
        ? userRolePermissions.some((permission) =>
            blockRolePermission.includes(permission)
        )
        : false;

    if (hasBlockRolePermission) return null;

    // Nếu không có rolePermissions được truyền vào, mặc định cho phép truy cập
    if (!rolePermissions || rolePermissions.length === 0) {
        return <>{children}</>;
    }

    // Kiểm tra nếu người dùng có ít nhất một quyền trong danh sách
    const hasPermission = userRolePermissions.some((permission) =>
        rolePermissions.includes(permission)
    );

    return hasPermission ? <>{children}</> : null;
};

export default CanAccess;
