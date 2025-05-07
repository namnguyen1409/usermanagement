import { useEffect, useState } from 'react'
import axiosInstance from '../utils/axiosInstance'
import { Card, Avatar, Descriptions, Spin, Typography, Tag, Button, Popconfirm } from 'antd'
import { UserOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import EditProfileDrawer from '../components/EditProfileDrawer'
import { useNavigate } from 'react-router'
import ChangePasswordDrawer from '../components/ChangePasswordDrawer'
import CanAccess from '../components/CanAccess'

const { Title } = Typography

type Profile = {
  id: string
  username: string
  firstName: string
  lastName: string
  email: string
  phone: string
  gender: boolean
  birthday: string
  address: string
  isDeleted: boolean
  roles: string[]
  revokedPermissions: string[]
}

const Home = () => {
  const [profile, setProfile] = useState<Profile | null>(null)
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [changePasswordOpen, setChangePasswordOpen] = useState(false)
  const [loading, setLoading] = useState(false)

  const navigate = useNavigate()

  const fetchProfile = async () => {
    setLoading(true)
    try {
      const response = await axiosInstance.get('/profile')
      setProfile(response.data.data)
    } catch (error) {
      console.error('Error fetching profile:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProfile()
  }, [])

  if (loading) {
    return (
      <div className='flex items-center justify-center min-h-screen'>
        <Spin size='large' />
      </div>
    )
  }

  return (
    <div className='flex items-center justify-center bg-gray-50 px-4'>
      {profile && (
        <Card
          className='w-full max-w-3xl shadow-lg rounded-xl'
          title={
            <div className='flex items-center gap-4 h-24'>
              <Avatar size={64} icon={<UserOutlined />} />
              <div>
                <Title level={4} className='!mb-0'>
                  {profile.firstName} {profile.lastName}
                </Title>
                <span className='text-gray-500'>@{profile.username}</span>
              </div>
            </div>
          }
        >
          <Descriptions column={1} layout='vertical' bordered size='middle' className='rounded-md'>
            <Descriptions.Item label='Email'>{profile.email}</Descriptions.Item>
            <Descriptions.Item label='Phone'>{profile.phone}</Descriptions.Item>
            <Descriptions.Item label='Gender'>{profile.gender ? 'Male' : 'Female'}</Descriptions.Item>
            <Descriptions.Item label='Birthday'>{dayjs(profile.birthday).format('DD/MM/YYYY')}</Descriptions.Item>
            <Descriptions.Item label='Address'>{profile.address}</Descriptions.Item>
            <Descriptions.Item label='Roles'>
              {profile.roles.map((role) => (
                <Tag color='blue' key={role}>
                  {role}
                </Tag>
              ))}
            </Descriptions.Item>
            <Descriptions.Item label='Revoked Permissions'>
              {profile.revokedPermissions.length > 0 ? (
                profile.revokedPermissions.map((permission) => (
                  <Tag color='red' key={permission}>
                    {permission}
                  </Tag>
                ))
              ) : (
                <span>No revoked permissions</span>
              )}
            </Descriptions.Item>
          </Descriptions>
          <EditProfileDrawer
            open={drawerOpen}
            onClose={() => setDrawerOpen(false)}
            profile={profile}
            onUpdated={fetchProfile}
          />
          <ChangePasswordDrawer
            open={changePasswordOpen}
            onClose={() => setChangePasswordOpen(false)}
            onChangePassword={() => setChangePasswordOpen(false)}
          />

          <Button
            type='primary'
            className='mt-4 mr-1'
            onClick={() => {
              setDrawerOpen(true)
            }}
          >
            Edit Profile
          </Button>

          <Button
            type='default'
            className='mt-2 mr-1'
            onClick={() => {
              setChangePasswordOpen(true)
            }}
          >
            Change Password
          </Button>

          <CanAccess blockRolePermission={['ROLE_SUPER_ADMIN']}>
            <Popconfirm
              title='Are you sure to delete your account?'
              onConfirm={async () => {
                try {
                  await axiosInstance.delete('/profile')
                  navigate('/login')
                } catch (error) {
                  console.error('Error deleting account:', error)
                }
              }}
              okText='Yes'
              cancelText='No'
            >
              <Button danger className='mt-2'>
                Delete Account
              </Button>
            </Popconfirm>
          </CanAccess>
        </Card>
      )}
    </div>
  )
}

export default Home
