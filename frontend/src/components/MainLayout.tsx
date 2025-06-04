import React, { useState } from 'react'
import { HistoryOutlined, LogoutOutlined, MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined, UserSwitchOutlined } from '@ant-design/icons'
import { Button, Layout, Menu, theme } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router'
import { FaUserGroup } from 'react-icons/fa6'
import { useRolePermission } from '../hooks/useRolePermission'
import { Footer } from 'antd/es/layout/layout'
import { logout } from '../utils/axiosLogout'

const { Header, Sider, Content } = Layout

const MainLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const rolePermissions = useRolePermission()

  const {
    token: { colorBgContainer, borderRadiusLG }
  } = theme.useToken()

  // Set key active theo pathname
  const getSelectedKey = () => {
    if (location.pathname.includes('/home')) return '1'
    if (location.pathname.includes('/login-history')) return '2'
    if (location.pathname.includes('/users')) return '3'
    return '3'
  }

  return (
    <Layout className='!min-h-screen'>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div className='text-white text-center py-4 text-xl font-bold'>{collapsed ? <UserSwitchOutlined /> : 'USER MANAGE'}</div>
        <Menu
          theme='dark'
          mode='inline'
          selectedKeys={[getSelectedKey()]}
          items={[
            {
              key: '1',
              icon: <UserOutlined />,
              onClick: () => navigate('/home'),
              label: 'Profile'
            },
            {
              key: '2',
              icon: <HistoryOutlined />,
              onClick: () => navigate('/login-history'),
              label: 'Login History'
            },
            ...(rolePermissions.includes('ROLE_ADMIN') || rolePermissions.includes('ROLE_SUPER_ADMIN')
              ? [
                {
                  key: '3',
                  icon: <FaUserGroup />,
                  onClick: () => navigate('/users'),
                  label: 'Users Management'
                }
              ]
              : []
            ),
            {
              key: '4',
              icon: <LogoutOutlined />,
              onClick: async () => {
                const confirmLogout = window.confirm('Are you sure you want to logout?')
                if (confirmLogout) {
                  logout()
                }
              },
              label: 'Logout'
            }
          ]}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }}>
          <Button
            type='text'
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{
              fontSize: '16px',
              width: 64,
              height: 64
            }}
          />

        </Header>
        <Content
          style={{
            margin: '24px',
            padding: 24,
            minHeight: 'calc(100vh - 200px)',
            background: colorBgContainer,
            borderRadius: borderRadiusLG
          }}
        >
          <Outlet />
        </Content>
        <Footer className='text-center'>
          User Management System ©2025 Created by Namnguyen
        </Footer>
      </Layout>
    </Layout>
  )
}

export default MainLayout
