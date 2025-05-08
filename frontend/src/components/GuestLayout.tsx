import type React from 'react'
import { Layout, theme } from 'antd'
import Title from 'antd/es/typography/Title'
import { Outlet } from 'react-router'
import { Footer } from 'antd/es/layout/layout'
const { Header, Content } = Layout

const GuestLayout: React.FC = () => {
  const {
    token: { colorBgContainer, borderRadiusLG }
  } = theme.useToken()

  return (
    <Layout className='!min-h-screen'>
      <Header style={{ background: colorBgContainer }} className='px-4 py-2 shadow'>
        <Title level={3} className='p-4 text-center m-0'>
          Welcome to user management system
        </Title>
      </Header>
      <Content
        style={{
          background: colorBgContainer,
          borderRadius: borderRadiusLG
        }}
        className='flex-1 flex items-center justify-center'
      >
        <Outlet />
      </Content>
      <Footer className='text-center'>User Management System ©2025 Created by Namnguyen</Footer>
    </Layout>
  )
}

export default GuestLayout
