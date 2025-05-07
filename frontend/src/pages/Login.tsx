import { Button, Card, Col, Form, Input, message, Row, Typography } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router'
import axiosPublic from '../utils/axiosPublic'
import { useState } from 'react'

const { Title, Text } = Typography

const Login = () => {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleLogin = async (values: any) => {
    const { username, password } = values
    setLoading(true)
    try {
      const response = await axiosPublic.post('/auth/login', { username, password })
      localStorage.setItem('accessToken', response.data.data.token)
      message.success('Login successful!')
      navigate('/home')
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
