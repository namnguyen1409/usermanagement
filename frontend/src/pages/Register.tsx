import { Button, Card, Col, DatePicker, Form, Input, message, Row, Switch, Typography } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router'
import axiosPublic from '../utils/axiosPublic'
import { useState } from 'react'

const { Title, Text } = Typography

const Register = () => {
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleRegister = async (values: any) => {
    const { username, password, firstName, lastName, email, phone, gender, birthday, address } = values
    setLoading(true)
    try {
      const response = await axiosPublic.post('/auth/register', {
        username,
        password,
        firstName,
        lastName,
        email,
        phone,
        gender,
        birthday,
        address
      })
      localStorage.setItem('accessToken', response.data.data.token)
      message.success('Register successful!')
      navigate('/home')
    } catch (error) {
      message.error('Register failed. Please check your infomation.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card className='max-w-5xl mx-auto my-12 p-0 overflow-hidden shadow-lg rounded-xl border border-gray-200'>
      <Row className='min-h-[500px]'>
        <Col xs={24} md={24} className='flex items-center justify-center bg-white'>
          <div className='w-full max-w-6xl p-8'>
            <div className='text-center mb-6'>
              <Title level={2} className='!mb-2'>
                Register
              </Title>
              <Text type='secondary'>Please enter your details below to register.</Text>
            </div>

            <Form name='Register_form' layout='vertical' onFinish={handleRegister} className='w-full'>
              <Row gutter={24}>
                <Col xs={24} md={12}>
                  <Form.Item
                    name='username'
                    label='Username'
                    rules={[
                      { required: true, message: 'Please input your username!' },
                      {
                        min: 6,
                        max: 100,
                        message: 'Username must be between 6 and 100 characters'
                      },
                      {
                        pattern: /^[a-zA-Z0-9_]+$/,
                        message: 'Only letters, numbers, and underscores allowed'
                      }
                    ]}
                  >
                    <Input prefix={<UserOutlined />} placeholder='Username' className='rounded-md' />
                  </Form.Item>

                  <Form.Item
                    name='password'
                    label='Password'
                    rules={[
                      { required: true, message: 'Please input your password!' },
                      {
                        min: 6,
                        max: 100,
                        message: 'Password must be between 6 and 100 characters'
                      },
                      {
                        pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/,
                        message: 'Must contain uppercase, lowercase, number, special char'
                      }
                    ]}
                  >
                    <Input.Password prefix={<LockOutlined />} placeholder='Password' className='rounded-md' />
                  </Form.Item>

                  <Form.Item
                    name='confirmPassword'
                    label='Confirm Password'
                    dependencies={['password']}
                    rules={[
                      { required: true, message: 'Please confirm your password!' },
                      ({ getFieldValue }) => ({
                        validator(_, value) {
                          if (!value || getFieldValue('password') === value) {
                            return Promise.resolve()
                          }
                          return Promise.reject(new Error('Passwords do not match!'))
                        }
                      })
                    ]}
                  >
                    <Input.Password prefix={<LockOutlined />} placeholder='Confirm Password' className='rounded-md' />
                  </Form.Item>

                  <Form.Item
                    name='email'
                    label='Email'
                    rules={[
                      { required: true, message: 'Please input your email!' },
                      { type: 'email', message: 'Invalid email!' }
                    ]}
                  >
                    <Input placeholder='Email' className='rounded-md' />
                  </Form.Item>

                  <Form.Item
                    name='phone'
                    label='Phone'
                    rules={[
                      { required: true, message: 'Please input your phone number!' },
                      { pattern: /^\d{10}$/, message: 'Must be 10 digits' }
                    ]}
                  >
                    <Input placeholder='Phone' className='rounded-md' />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Row gutter={16}>
                    <Col xs={24} md={12}>
                      <Form.Item name='firstName' label='First Name' rules={[{ required: true }]}>
                        <Input placeholder='First Name' className='rounded-md' />
                      </Form.Item>
                    </Col>
                    <Col xs={24} md={12}>
                      <Form.Item name='lastName' label='Last Name' rules={[{ required: true }]}>
                        <Input placeholder='Last Name' className='rounded-md' />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Form.Item
                    name='gender'
                    label='Gender'
                    valuePropName='checked'
                    rules={[{ required: true, message: 'Choose gender' }]}
                  >
                    <Switch defaultChecked checkedChildren='Male' unCheckedChildren='Female' />
                  </Form.Item>

                  <Form.Item
                    name='birthday'
                    label='Birthday'
                    rules={[
                      { required: true, message: 'Choose birthday' },
                      {
                        validator(_, value) {
                          if (
                            !value ||
                            (new Date().getFullYear() - new Date(value).getFullYear() >= 18 &&
                              new Date().getFullYear() - new Date(value).getFullYear() <= 100)
                          ) {
                            return Promise.resolve()
                          }
                          return Promise.reject(new Error('Age must be between 18 and 100'))
                        }
                      }
                    ]}
                  >
                    <DatePicker format='YYYY-MM-DD' className='w-full' />
                  </Form.Item>

                  <Form.Item name='address' label='Address' rules={[{ required: true }]}>
                    <Input placeholder='Address' className='rounded-md' />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item>
                <Button
                  type='primary'
                  htmlType='submit'
                  block
                  loading={loading}
                  className='rounded-md bg-blue-500 hover:bg-blue-600'
                >
                  Register
                </Button>
                <div className='text-center mt-4'>
                  <Text type='secondary'>Have an account?</Text>{' '}
                  <a onClick={() => navigate('/login')} className='text-blue-500 hover:underline'>
                    Login
                  </a>
                </div>
              </Form.Item>
            </Form>
          </div>
        </Col>
      </Row>
    </Card>
  )
}

export default Register
