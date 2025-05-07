import { Route, Routes } from 'react-router'
import GuestLayout from '../components/GuestLayout'
import Login from '../pages/Login'
import Register from '../pages/Register'
import MainLayout from '../components/MainLayout'
import Home from '../pages/Home'
import PrivateRoute from '../components/PrivateRoute'
import Users from '../pages/Users'
import LoginHistory from '../pages/LoginHistory'
import NotFound from '../pages/NotFound'

const AppRouter = () => {
  return (
    <Routes>
      <Route element={<GuestLayout />}>
        <Route path='/login' element={<Login />} />
        <Route path='/register' element={<Register />} />
      </Route>
      <Route element={<MainLayout />}>
        <Route
          path='/'
          element={
            <PrivateRoute
              element={<Home />}
              allowedRolesPermissions={['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}
            />
          }
        />
        <Route
          path='/home'
          element={
            <PrivateRoute
              element={<Home />}
              allowedRolesPermissions={['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}
            />
          }
        />
        <Route
          path='/login-history'
          element={
            <PrivateRoute
              element={<LoginHistory />}
              allowedRolesPermissions={['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN']}
            />
          }
        />
        <Route
          path='/users'
          element={<PrivateRoute element={<Users />} allowedRolesPermissions={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN']} />}
        />
      </Route>
      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}

export default AppRouter
