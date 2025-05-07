import { useState } from 'react'
import ReusableTable from '../components/ReusableTable'
import CreateUserDrawer from '../components/CreateUserDrawer'
import EditUserDrawer from '../components/EditUserDrawer'
import { FloatButton, Button, Space, Tooltip, Select } from 'antd'
import { ClockCircleOutlined, EditOutlined, EyeOutlined, UserAddOutlined } from '@ant-design/icons'
import CanAccess from '../components/CanAccess'
import ShowUserDetailModel from '../components/ShowUserDetailModel'
import ShowUserLoginHistoryModel from '../components/ShowUserLoginHistoryModel'

interface User {
  id: string
  username: string
  firstName: string
  lastName: string
  email: string
  phone: string
  gender: boolean
  birthday: string
  address: string
  roles: string[]
}

const Users = () => {
  const [createUserDrawerOpen, setCreateUserDrawerOpen] = useState(false)
  const [editUserDrawerOpen, setEditUserDrawerOpen] = useState(false)
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null)
  const [showUserDetails, setShowUserDetails] = useState(false)
  const [showUserLoginHistory, setShowUserLoginHistory] = useState(false)

  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'username',
    'firstName',
    'lastName',
    'email',
    'action'
  ])

  const userColumns = [
    {
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by username',
        by: ['username']
      }
    },
    {
      title: 'First Name',
      dataIndex: 'firstName',
      key: 'firstName',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by first name',
        by: ['firstName']
      }
    },
    {
      title: 'Last Name',
      dataIndex: 'lastName',
      key: 'lastName',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by last name',
        by: ['lastName']
      }
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by email',
        by: ['email']
      }
    },
    {
      title: 'Phone',
      dataIndex: 'phone',
      key: 'phone',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by phone',
        by: ['phone']
      }
    },
    {
      title: 'Gender',
      dataIndex: 'gender',
      key: 'gender',
      render: (gender: boolean) => (gender ? 'Male' : 'Female'),
      sorter: true,
      filter: {
        type: 'select',
        options: [
          {
            label: 'male',
            value: true
          },
          {
            label: 'female',
            value: false
          }
        ],
        placeholder: 'Choose gender',
        by: ['gender']
      }
    },
    {
      title: 'Birthday',
      dataIndex: 'birthday',
      key: 'birthday',
      sorter: true,
      filter: {
        type: 'date',
        placeholder: 'Search by birthday',
        by: ['birthdayFrom', 'birthdayTo'],
        format: 'YYYY-MM-DD'
      }
    },
    {
      title: 'Address',
      dataIndex: 'address',
      key: 'address',
      sorter: true
    },
    {
      title: 'Roles',
      dataIndex: 'roles',
      key: 'roles',
      render: (roles: string[]) => roles.join(', ')
    },
    {
      title: 'Revoked Permissions',
      dataIndex: 'revokedPermissions',
      key: 'revokedPermissions',
      render: (revokedPermissions: string[]) => revokedPermissions.join(', ')
    },
    {
      title: 'Action',
      key: 'action',
      fixed: 'right' as const,
      render: (_: any, record: User) => (
        <Space size='middle'>
          <CanAccess rolePermissions={['EDIT_USER']}>
            <Tooltip title='Chỉnh sửa người dùng'>
              <Button
                icon={<EditOutlined />}
                onClick={() => {
                  setSelectedUserId(record.id)
                  setEditUserDrawerOpen(true)
                }}
              />
            </Tooltip>
          </CanAccess>

          <CanAccess rolePermissions={['VIEW_USER']}>
            <Tooltip title='Xem thông tin chi tiết'>
              <Button
                icon={<EyeOutlined />}
                onClick={() => {
                  setSelectedUserId(record.id)
                  setShowUserDetails(true)
                }}
              />
            </Tooltip>
          </CanAccess>

          <CanAccess rolePermissions={['VIEW_USER']}>
            <Tooltip title='Xem lịch sử đăng nhập'>
              <Button
                icon={<ClockCircleOutlined />}
                onClick={() => {
                  setSelectedUserId(record.id)
                  setShowUserLoginHistory(true)
                }}
              />
            </Tooltip>
          </CanAccess>
        </Space>
      )
    }
  ]

  return (
    <>
      <h1 className='text-2xl font-bold mb-4'>Users</h1>

      <Select
        mode='multiple'
        allowClear
        placeholder='Select columns to display'
        defaultValue={visibleColumns}
        style={{ width: '100%', marginBottom: 16 }}
        onChange={(value) => {
          setVisibleColumns(value)
        }}
      >
        {userColumns.map((column) => (
          <Select.Option key={column.key} value={column.key}>
            {column.title}
          </Select.Option>
        ))}
      </Select>

      <ReusableTable<User>
        apiUrl='/users'
        columns={userColumns}
        rowKey='id'
        scroll={{ x: 'max-content' }}
        visibleColumns={visibleColumns}
      />

      <CreateUserDrawer
        open={createUserDrawerOpen}
        onClose={() => setCreateUserDrawerOpen(false)}
        onCreated={() => {
          setCreateUserDrawerOpen(false)
        }}
      />

      <EditUserDrawer
        open={editUserDrawerOpen}
        userId={selectedUserId || ''}
        onClose={() => setEditUserDrawerOpen(false)}
        onCreated={() => {
          setEditUserDrawerOpen(false)
        }}
      />

      <ShowUserDetailModel
        open={showUserDetails}
        userId={selectedUserId || ''}
        onClose={() => setShowUserDetails(false)}
      />
      <ShowUserLoginHistoryModel
        open={showUserLoginHistory}
        userId={selectedUserId || ''}
        onClose={() => setShowUserLoginHistory(false)}
      />

      <CanAccess rolePermissions={['ADD_USER']}>
        <FloatButton
          shape='square'
          type='primary'
          style={{ insetInlineEnd: 24 }}
          icon={<UserAddOutlined />}
          onClick={() => {
            setCreateUserDrawerOpen(true)
          }}
          tooltip={<span className='text-sm'>Create User</span>}
        />
      </CanAccess>
    </>
  )
}

export default Users
