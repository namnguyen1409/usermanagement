import { Route, Routes } from "react-router"
import GuestLayout from "../components/GuestLayout"
import Login from "../pages/Login"
import Register from "../pages/Register"
import MainLayout from "../components/MainLayout"
import Home from "../pages/Home"
import PrivateRoute from "../components/PrivateRoute"

const AppRouter = () => {
    return (
        <Routes>
            <Route element={<GuestLayout />}>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
            </Route>
            <Route element={<MainLayout />}>
                <Route
                    path="/home"
                    element={
                        <PrivateRoute
                            element={<Home />}
                            allowedRolesPermissions={["ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"]}
                        />
                    }
                />
            </Route>
        </Routes>
    )
}

export default AppRouter