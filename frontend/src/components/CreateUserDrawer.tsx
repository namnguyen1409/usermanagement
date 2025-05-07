import { Button, DatePicker, Drawer, Form, Input, message, Select, Skeleton, Switch } from 'antd'
import TextArea from 'antd/es/input/TextArea'
import { useCallback, useEffect, useMemo, useState } from 'react'
import { LockOutlined } from '@ant-design/icons'
import axiosInstance from '../utils/axiosInstance'
import CanAccess from './CanAccess'
import type dayjs from 'dayjs'

interface CreateUserDrawerProps {
  open: boolean
  onClose: () => void
  onCreated: () => void
}

interface Role {
  id: string
  name: string
  permissions: { id: string; name: string }[]
}

interface UserCreationForm {
    username: string
    firstName: string
    lastName: string
    password: string
    confirmPassword: string
    email: string
    phone: string
    gemder: boolean
    birthday: dayjs.Dayjs
    address: string
    roleList?: string[]
    revokedPermissionList?: string[]   
}

const CreateUserDrawer: React.FC<CreateUserDrawerProps> = ({ open, onClose, onCreated }) => {
  const [form] = Form.useForm()
  const [roles, setRoles] = useState<Role[]>([])
  const [permissions, setPermissions] = useState<string[]>([])
  const [loading, setLoading] = useState(true)

  const fetchRoles = useCallback(async () => {
    try {
      const response = await axiosInstance.get('/roles')
      setRoles(response.data.data)
    } catch {
      message.error('Lỗi khi tải danh sách quyền')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchRoles()
  }, [fetchRoles])

  const handleSubmit = async (values: UserCreationForm) => {
    try {
      await axiosInstance.post('/users/create', {
        ...values,
        birthday: values.birthday?.format('YYYY-MM-DD')
      })
      message.success('Tạo người dùng thành công')
      onCreated()
      onClose()
    } catch {
      message.error('Lỗi khi tạo người dùng')
    }
  }

  const handleRoleChange = (selectedRoleNames: string[]) => {
    const selectedRoles = roles.filter((r) => selectedRoleNames.includes(r.name))
    const newPermissions = selectedRoles.flatMap((r) => r.permissions.map((p) => p.name))
    setPermissions(newPermissions)

    // Loại bỏ các quyền bị thu hồi không còn hợp lệ
    const currentRevoked = form.getFieldValue('revokedPermissionList') || []
    const validRevoked = currentRevoked.filter((p: string) => newPermissions.includes(p))
    form.setFieldsValue({ revokedPermissionList: validRevoked })
  }

  const roleOptions = useMemo(() => roles.map((role) => ({ label: role.name, value: role.name })), [roles])

  const permissionOptions = useMemo(() => permissions.map((p) => ({ label: p, value: p })), [permissions])

  return (
    <Drawer title='Tạo người dùng mới' width={480} onClose={onClose} open={open} destroyOnClose={false}>
      {loading ? (
        <Skeleton active paragraph={{ rows: 10 }} />
      ) : (
        <Form layout='vertical' form={form} onFinish={handleSubmit}>
          <Form.Item
            name='username'
            label='Tên đăng nhập'
            rules={[
              { required: true, min: 6, max: 20 },
              {
                pattern: /^[a-zA-Z0-9_]+$/,
                message: 'Chỉ chứa chữ cái, số, dấu gạch dưới'
              }
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name='firstName'
            label='Họ'
            rules={[
              { required: true, min: 2, max: 20 },
              {
                pattern: /^[\p{L}\s]+$/u,
                message: 'Chỉ chứa chữ cái và khoảng trắng'
              }
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name='lastName'
            label='Tên'
            rules={[
              { required: true, min: 2, max: 20 },
              {
                pattern: /^[\p{L}\s]+$/u,
                message: 'Chỉ chứa chữ cái và khoảng trắng'
              }
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name='password'
            label='Password'
            rules={[
              { required: true },
              { min: 6, max: 100 },
              {
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/,
                message: 'Phải có chữ hoa, chữ thường, số và ký tự đặc biệt'
              }
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder='Password' />
          </Form.Item>

          <Form.Item
            name='confirmPassword'
            label='Xác nhận mật khẩu'
            dependencies={['password']}
            rules={[
              { required: true, message: 'Xác nhận mật khẩu!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve()
                  }
                  return Promise.reject(new Error('Mật khẩu không khớp!'))
                }
              })
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder='Confirm Password' />
          </Form.Item>

          <Form.Item name='email' label='Email' rules={[{ type: 'email', required: true }]}>
            <Input />
          </Form.Item>

          <Form.Item
            name='phone'
            label='Số điện thoại'
            rules={[{ pattern: /^[0-9]{10}$/, message: 'Phải đủ 10 chữ số' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item name='gender' label='Giới tính'>
            <Switch checkedChildren='Nam' unCheckedChildren='Nữ' />
          </Form.Item>

          <Form.Item name='birthday' label='Ngày sinh'>
            <DatePicker format='YYYY-MM-DD' style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name='address'
            label='Địa chỉ'
            rules={[
              { min: 10, max: 255 },
              {
                pattern: /^[\p{L}\p{N}\s,.\-]+$/u,
                message: 'Ký tự không hợp lệ trong địa chỉ'
              }
            ]}
          >
            <TextArea rows={3} />
          </Form.Item>

          <CanAccess rolePermissions={['ROLE_SUPER_ADMIN']}>
            <Form.Item name='roleList' label='Vai trò'>
              <Select
                mode='multiple'
                allowClear
                placeholder='Chọn vai trò'
                style={{ width: '100%' }}
                options={roleOptions}
                onChange={handleRoleChange}
              />
            </Form.Item>

            <Form.Item name='revokedPermissionList' label='Quyền bị thu hồi'>
              <Select
                mode='multiple'
                allowClear
                placeholder='Chọn quyền bị thu hồi'
                style={{ width: '100%' }}
                options={permissionOptions}
              />
            </Form.Item>
          </CanAccess>

          <Form.Item>
            <Button type='primary' htmlType='submit' block>
              Lưu thay đổi
            </Button>
          </Form.Item>
        </Form>
      )}
    </Drawer>
  )
}

export default CreateUserDrawer
