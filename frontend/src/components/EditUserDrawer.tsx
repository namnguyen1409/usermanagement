import { Button, DatePicker, Drawer, Form, Input, message, Popconfirm, Select, Switch } from 'antd'

import axiosInstance from '../utils/axiosInstance'
import TextArea from 'antd/es/input/TextArea'
import { useCallback, useEffect, useState } from 'react'
import CanAccess from './CanAccess'
import dayjs from 'dayjs'
import { useRolePermission } from '../hooks/useRolePermission'

const API_ENDPOINTS = {
  USER: '/users',
  ROLES: '/roles',
  RESTORE_USER: '/users/restore',
  UNLOCK_USER: '/users/unlock'
}

interface EditUserDrawerProps {
  open: boolean
  userId: string
  onClose: () => void
  onCreated: () => void
}

interface Role {
  id: string
  name: string
  permissions: {
    id: string
    name: string
  }[]
}

const EditUserDrawer: React.FC<EditUserDrawerProps> = ({ open, onClose, onCreated, userId }) => {
  const [form] = Form.useForm()
  const [roles, setRoles] = useState<Role[]>([])
  const [permissions, setPermissions] = useState<string[]>([])
  const [user, setUser] = useState<any>(null)

  const fetchUser = useCallback(async () => {
    try {
      const response = await axiosInstance.get(`${API_ENDPOINTS.USER}/${userId}`)
      const userData = response.data.data
      setUser(userData)
      form.setFieldsValue({
        ...userData,
        birthday: dayjs(userData.birthday)
      })
    } catch (error) {
      message.error('Lỗi khi tải thông tin người dùng')
    }
  }, [userId, form])

  const fetchRoles = useCallback(async () => {
    try {
      const response = await axiosInstance.get(API_ENDPOINTS.ROLES)
      setRoles(response.data.data)
    } catch (error) {
      message.error('Lỗi khi tải danh sách quyền')
    }
  }, [])

  useEffect(() => {
    if (open) {
      fetchUser()
      fetchRoles()
    } else {
      form.resetFields()
      form.setFieldsValue({ birthday: null }) // Explicitly reset the birthday field to null
    }
  }, [open, fetchUser, fetchRoles, form])

  const handleSubmit = async (values: any) => {
    try {
      await axiosInstance.put(`${API_ENDPOINTS.USER}/${userId}`, {
        ...values,
        birthday: values.birthday.format('YYYY-MM-DD')
      })
      message.success('Edit successfully')
      onCreated()
      onClose()
    } catch (error) {
      message.error('Edit failed')
    }
  }

  const handleAction = async (action: string, successMessage: string, errorMessage: string) => {
    try {
      await axiosInstance.post(action)
      message.success(successMessage)
      onCreated()
      onClose()
    } catch (error) {
      message.error(errorMessage)
    }
  }

  const userRolePermissions = useRolePermission()

  const canRemoveOtherUser = () => {
    if (userRolePermissions.includes('ROLE_SUPER_ADMIN')) {
      return !user?.roles?.includes('SUPER_ADMIN')
    }
    return !user?.roles?.includes('ADMIN')
  }

  return (
    <Drawer title='Edit User' width={480} onClose={onClose} open={open}>
      {canRemoveOtherUser() && !user?.isDeleted && (
        <Popconfirm
          title='Are you sure to delete this user?'
          onConfirm={() =>
            handleAction(
              `${API_ENDPOINTS.USER}/${userId}`,
              'Xóa người dùng thành công',
              'Lỗi khi xóa người dùng'
            )
          }
          okText='Yes'
          cancelText='No'
        >
          <Button danger block>
            Xóa người dùng
          </Button>
        </Popconfirm>
      )}

      {canRemoveOtherUser() && user?.isDeleted && (
        <Popconfirm
          title='Are you sure to restore this user?'
          onConfirm={() =>
            handleAction(
              `${API_ENDPOINTS.RESTORE_USER}/${userId}`,
              'Khôi phục người dùng thành công',
              'Lỗi khi khôi phục người dùng'
            )
          }
          okText='Yes'
          cancelText='No'
        >
          <Button type='primary' block>
            Khôi phục người dùng
          </Button>
        </Popconfirm>
      )}

      {canRemoveOtherUser() && user?.isLocked && (
        <Popconfirm
          title='Are you sure to unlock this user?'
          onConfirm={() =>
            handleAction(
              `${API_ENDPOINTS.UNLOCK_USER}/${userId}`,
              'Mở khóa người dùng thành công',
              'Lỗi khi mở khóa người dùng'
            )
          }
          okText='Yes'
          cancelText='No'
        >
          <Button type='primary' block>
            Mở khóa người dùng
          </Button>
        </Popconfirm>
      )}

      <Form layout='vertical' form={form} onFinish={handleSubmit} initialValues={user}>
        <Form.Item
          name='username'
          label='Tên đăng nhập'
          rules={[
            { min: 6, max: 20 },
            { pattern: /^[a-zA-Z0-9_]+$/, message: 'Chỉ chứa chữ cái, số, dấu gạch dưới' }
          ]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          name='firstName'
          label='Họ'
          rules={[
            { min: 2, max: 20 },
            { pattern: /^[\p{L}\s]+$/u, message: 'Chỉ chứa chữ cái và khoảng trắng' }
          ]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          name='lastName'
          label='Tên'
          rules={[
            { min: 2, max: 20 },
            { pattern: /^[\p{L}\s]+$/u, message: 'Chỉ chứa chữ cái và khoảng trắng' }
          ]}
        >
          <Input />
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
            { pattern: /^[\p{L}\p{N}\s,.\-]+$/u, message: 'Ký tự không hợp lệ trong địa chỉ' }
          ]}
        >
          <TextArea rows={3} />
        </Form.Item>

        <CanAccess rolePermissions={['ROLE_SUPER_ADMIN']}>
          <Form.Item name='roles' label='Vai trò'>
            <Select
              mode='multiple'
              allowClear
              placeholder='Chọn vai trò'
              style={{ width: '100%' }}
              options={roles.map((role) => ({
                label: role.name,
                value: role.name
              }))}
              onChange={(value) => {
                const selectedRoles = roles.filter((role) => value.includes(role.name))
                const selectedPermissions = selectedRoles.flatMap((role) =>
                  role.permissions.map((permission) => permission.name)
                )
                setPermissions(selectedPermissions)

                const currentRevoked = form.getFieldValue('revokedPermissions') || []
                const validRevoked = currentRevoked.filter((p: string) => selectedPermissions.includes(p))
                form.setFieldsValue({ revokedPermissions: validRevoked })
              }}
            />
          </Form.Item>
          <Form.Item name='revokedPermissions' label='Quyền bị thu hồi'>
            <Select
              mode='multiple'
              allowClear
              placeholder='Chọn quyền bị thu hồi'
              style={{ width: '100%' }}
              options={permissions.map((permission) => ({
                label: permission,
                value: permission
              }))}
            />
          </Form.Item>
        </CanAccess>
        <Form.Item>
          <Button type='primary' htmlType='submit' block>
            Lưu thay đổi
          </Button>
        </Form.Item>
      </Form>
    </Drawer>
  )
}

export default EditUserDrawer
