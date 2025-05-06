import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router";

const PrivateRoute = ({ element, allowedRolesPermissions }: 
    { element: React.ReactNode, allowedRolesPermissions: string[] }) => {

    const navigate = useNavigate();
    const accessToken = localStorage.getItem("accessToken");

    if (!accessToken) {
        navigate("/login");
        return null;
    }

    try {
        const decodedToken: {
            iss: string;
            sub: string;
            exp: number;
            iat: number;
            jti: string;
            scope: string;
        } = jwtDecode(accessToken);
        const userPermissions = decodedToken.scope.split(" ");
        console.log("User Permissions:", userPermissions);
        console.log("Allowed Roles Permissions:", allowedRolesPermissions);
        const hasPermission = allowedRolesPermissions.some((permission) =>
            userPermissions.includes(permission)
        );
        if (!hasPermission) {
            navigate("/403");
            return null;
        }
        return <>{element}</>;
    }
    catch (error) {
        console.error("Token decoding error:", error);
        navigate("/login");
        return null;
    }
};

export default PrivateRoute;