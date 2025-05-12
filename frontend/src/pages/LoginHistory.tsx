import dayjs from 'dayjs'
import ReusableTable from '../components/ReusableTable'
import { Button, message, Popconfirm, Select, Space, Tag } from 'antd'
import { CheckCircleFilled, LogoutOutlined } from '@ant-design/icons'
import axiosInstance from '../utils/axiosInstance'
import { useState } from 'react'

interface LoginHistory {
  id: string
  createdAt: string
  expiredAt: string
  userAgent: string
  ipAddress: string
  device: string
  browser: string
  browserVersion: string
  os: string
  osVersion: string
  success: boolean
  logout: boolean
}


const LoginHistory = () => {

  const [visibleColumns, setVisibleColumns] = useState<string[]>(
    localStorage.getItem('loginHistoryTableColumns')?.split(',') || []
  )
  const handleVisibleColumnsChange = (value: string[]) => {
    setVisibleColumns(value)
    localStorage.setItem('loginHistoryTableColumns', value.join(','))
  }

  const loginHistoryColumns = [
    {
      title: 'Login Time',
      dataIndex: 'createdAt',
      key: 'createdAt',
      sorter: true,
      render: (text: string) => dayjs(text).format('HH:mm:ss DD/MM/YYYY'),
      filter: {
        type: 'date',
        placeholder: 'Search by createdAt',
        by: ['createdAtFrom', 'createdAtTo'],
        format: "YYYY-MM-DDTHH:mm:ss",
        viewFormat: "HH:mm:ss DD/MM/YYYY",
        showTime: true
      }
    },{
      title: 'Expired Time',
      dataIndex: 'expiredAt',
      key: 'expiredAt',
      sorter: true,
      render: (text: string) => dayjs(text).format('HH:mm:ss DD/MM/YYYY'),
      filter: {
        type: 'date',
        placeholder: 'Search by expiredAt',
        by: ['expiredAtFrom', 'expiredAtTo'],
        format: "YYYY-MM-DDTHH:mm:ss",
        viewFormat: "HH:mm:ss DD/MM/YYYY",
        showTime: true
      }
    },
    {
      title: 'User Agent',
      dataIndex: 'userAgent',
      key: 'userAgent',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by userAgent',
        by: ['userAgent']
      }
    },
    {
      title: 'IP Address',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by ipAddress',
        by: ['ipAddress']
      }
    },
    {
      title: 'Device',
      dataIndex: 'device',
      key: 'device',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by device',
        by: ['device']
      }
    },
    {
      title: 'Browser',
      dataIndex: 'browser',
      key: 'browser',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by browser',
        by: ['browser']
      }
    },
    {
      title: 'Browser Version',
      dataIndex: 'browserVersion',
      key: 'browserVersion',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by browserVersion',
        by: ['browserVersion']
      }
    },
    {
      title: 'OS',
      dataIndex: 'os',
      key: 'os',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by os',
        by: ['os']
      }
    },
    {
      title: 'OS Version',
      dataIndex: 'osVersion',
      key: 'osVersion',
      sorter: true,
      filter: {
        type: 'text',
        placeholder: 'Search by osVersion',
        by: ['osVersion']
      }
    },
    {
      title: 'Status',
      dataIndex: 'success',
      key: 'success',
      sorter: true,
      filter: {
        type: 'select',
        placeholder: 'Search by status',
        by: ['success'],
        options: [
          { label: 'Success', value: true },
          { label: 'Failed', value: false }
        ]
      },
      render: (text: boolean) => (text ? <Tag color='green'>Success</Tag> : <Tag color='red'>Failed</Tag>)
    },
    {
      title: 'Is Active',
      dataIndex: 'logout',
      key: 'logout',
      sorter: true,
      filter: {
        type: 'select',
        placeholder: 'Search by isActive',
        by: ['logout'],
        options: [
          { label: 'Active', value: false },
          { label: 'Inactive', value: true }
        ]
      },
      render: (text: boolean) => (text ? <Tag color='red'>Inactive</Tag> : <Tag color='green'>Active</Tag>)
    },
    {
      title: 'Actions',
      dataIndex: 'actions',
      key: 'actions',
      fixed: 'right' as const,
      render: (_: any, record: LoginHistory) => {
        if (!record.logout) {
          return (
            <Space size='middle'>
              <Popconfirm
                title='Are you sure to revoke this login history?'
                onConfirm={async () => {
                  try {
                    await axiosInstance.post(`/profile/revoke/${record.id}`)
                    message.success('Revoke login history successfully')
                  } catch (error) {
                    message.error('Error revoking login history')
                  }
                }}
                okText='Yes'
                cancelText='No'
              >
                <Button danger block>
                  <LogoutOutlined />
                </Button>
              </Popconfirm>
      
              {localStorage.getItem('loginLogId') === record.id && (
                <Tag color='green'>
                  <CheckCircleFilled />
                </Tag>
              )}
            </Space>
          );
        }
        return null;
      }
    }
  ]

  return (
    <>
      <h1 className='text-2xl font-bold mb-4'>Login History</h1>
      
      <Select
        mode='multiple'
        allowClear
        placeholder='Select columns to display'
        defaultValue={visibleColumns}
        style={{ width: '100%', marginBottom: 16 }}
        onChange={(value) => {
          handleVisibleColumnsChange(value)
        }}
      >
        {loginHistoryColumns.map((column) => (
          <Select.Option key={column.key} value={column.key}>
            {column.title}
          </Select.Option>
        ))}
      </Select>
      <ReusableTable<LoginHistory>
        apiUrl='/profile/login-history'
        columns={loginHistoryColumns}
        visibleColumns={visibleColumns}
        rowKey='id'
        defaultPageSize={5}
      />
    </>
  )
}

export default LoginHistory
