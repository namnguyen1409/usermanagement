import dayjs from 'dayjs'
import ReusableTable from '../components/ReusableTable'
import { Tag } from 'antd'

interface LoginHistory {
  id: string
  createdAt: string
  userAgent: string
  ipAddress: string
  device: string
  browser: string
  browserVersion: string
  os: string
  osVersion: string
}

const loginHistoryColumns = [
  {
    title: 'Login Time',
    dataIndex: 'createdAt',
    key: 'createdAt',
    sorter: true,
    render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    filter: {
      type: 'date',
      placeholder: 'Search by createdAt',
      by: ['createdAtFrom', 'createdAtTo']
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
  }
]

const LoginHistory = () => {
  return (
    <>
      <ReusableTable<LoginHistory>
        apiUrl='/profile/login-history'
        columns={loginHistoryColumns}
        rowKey='id'
        defaultPageSize={5}
      />
    </>
  )
}

export default LoginHistory
