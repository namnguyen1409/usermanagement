import { Button, Card, Checkbox, Col, Form, Input, message, notification, Row, Typography } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router'
import { apiPublicCall } from '../utils/axiosPublic'
import { useState } from 'react'
import type { ApiResponse, LoginResponse } from '../types/api.response'
import type { LoginRequest } from '../types/api.request'

const { Title, Text } = Typography

const Login = () => {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleLogin = async (values: any) => {
    const loginRequest: LoginRequest = {
      username: values.username,
      password: values.password,
      rememberMe: values.rememberMe,
    }
    setLoading(true)
    try {
      const response: ApiResponse<LoginResponse> = await apiPublicCall<LoginResponse, LoginRequest>(
        '/auth/login',
        'POST',
        loginRequest,
      )
      if (response.code === 200) {
        localStorage.setItem('accessToken', response.data.token)
        if (response.data.refreshToken) {
          localStorage.setItem('refreshToken', response.data.refreshToken)
        } else {
          localStorage.removeItem('refreshToken')
        }
        if (response.data.loginLogId) {
          localStorage.setItem('loginLogId', response.data.loginLogId)
        }
        message.success('Login successful!')
        navigate('/home')
      } else {
        notification.error(
          {
            message: response.code,
            description: response.message,
            placement: 'topRight',
            duration: 3,
          } as any,
        )
      }
    } catch (error) {
      message.error('Login failed. Please check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card className='max-w-5xl mx-auto my-12 p-0 overflow-hidden shadow-lg rounded-xl border border-gray-200'>
      <Row className='min-h-[500px]'>
        {/* Form Section */}
        <Col xs={24} md={12} className='flex items-center justify-center bg-white'>
          <div className='w-full max-w-sm p-8'>
            <div className='text-center mb-6'>
              <Title level={2} className='!mb-2'>
                Sign in
              </Title>
              <Text type='secondary'>Please enter your details below to sign in.</Text>
            </div>

            <Form name='login_form' layout='vertical' onFinish={handleLogin} className='w-full'>
              <Form.Item name='username' label='Username' rules={[{ required: true }]}>
                <Input prefix={<UserOutlined />} placeholder='Username' className='rounded-md' />
              </Form.Item>

              <Form.Item name='password' label='Password' rules={[{ required: true }]}>
                <Input.Password prefix={<LockOutlined />} placeholder='Password' className='rounded-md' />
              </Form.Item>
              <Form.Item name='rememberMe' valuePropName='checked'>
                <Checkbox className='text-sm'>
                  Remember me
                </Checkbox>
              </Form.Item>

              <Form.Item>
                <Button
                  type='primary'
                  htmlType='submit'
                  block
                  loading={loading}
                  className='rounded-md bg-blue-500 hover:bg-blue-600'
                >
                  Log in
                </Button>
                <div className='text-center mt-4'>
                  <Text type='secondary'>Don't have an account?</Text>{' '}
                  <a className='text-blue-500 hover:underline' onClick={() => navigate('/register')}>
                    Register
                  </a>
                </div>
              </Form.Item>
            </Form>
          </div>
        </Col>

        {/* Image Section */}
        <Col xs={0} md={12} className='hidden md:block'>
          <img
            src='https://images.unsplash.com/photo-1534239697798-120952b76f2b?auto=format&fit=crop&w=1280&q=80'
            alt='Login'
            className='w-full h-full object-cover'
          />
        </Col>
      </Row>
    </Card>
  )
}

export default Login
