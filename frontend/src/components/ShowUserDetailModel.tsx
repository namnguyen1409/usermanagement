import { useCallback, useEffect, useState } from 'react'
import axiosInstance from '../utils/axiosInstance'
import { message, Modal, Spin, Descriptions } from 'antd'

interface ShowUserDetailModelProps {
  open: boolean
  userId: string
  onClose: () => void
}

const ShowUserDetailModel: React.FC<ShowUserDetailModelProps> = ({ open, userId, onClose }) => {
  const [user, setUser] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  const fetchUser = useCallback(async () => {
    try {
      setLoading(true)
      const response = await axiosInstance.get(`/users/${userId}`)
      setUser(response.data.data)
    } catch (error) {
      message.error('Lỗi khi tải thông tin người dùng')
    } finally {
      setLoading(false)
    }
  }, [userId])

  useEffect(() => {
    if (open) {
      fetchUser()
    }
  }, [open, fetchUser])

  return (
    <Modal title='Thông tin người dùng' open={open} onCancel={onClose} footer={null} width={800}>
      {loading ? (
        <Spin />
      ) : user ? (
        <Descriptions bordered column={2} size='small'>
          <Descriptions.Item label='ID'>{user.id}</Descriptions.Item>
          <Descriptions.Item label='Tên đăng nhập'>{user.username}</Descriptions.Item>
          <Descriptions.Item label='Họ'>{user.firstName}</Descriptions.Item>
          <Descriptions.Item label='Tên'>{user.lastName}</Descriptions.Item>
          <Descriptions.Item label='Email'>{user.email}</Descriptions.Item>
          <Descriptions.Item label='SĐT'>{user.phone}</Descriptions.Item>
          <Descriptions.Item label='Giới tính'>{user.gender ? 'Nam' : 'Nữ'}</Descriptions.Item>
          <Descriptions.Item label='Ngày sinh'>{user.birthday}</Descriptions.Item>
          <Descriptions.Item label='Địa chỉ' span={2}>
            {user.address}
          </Descriptions.Item>
          <Descriptions.Item label='Vai trò' span={2}>
            {user.roles.join(', ')}
          </Descriptions.Item>
          <Descriptions.Item label='Ngày tạo'>{new Date(user.createdAt).toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label='Người tạo'>{user.createdBy}</Descriptions.Item>
          <Descriptions.Item label='Ngày cập nhật'>{new Date(user.updatedAt).toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label='Người cập nhật'>{user.updatedBy}</Descriptions.Item>
          <Descriptions.Item label='Đã xóa'>{user.isDeleted ? 'Có' : 'Không'}</Descriptions.Item>
          {user.isDeleted && (
            <>
              <Descriptions.Item label='Ngày xóa'>{new Date(user.deletedAt).toLocaleString()}</Descriptions.Item>
              <Descriptions.Item label='Người xóa'>{user.deletedBy}</Descriptions.Item>
            </>
          )}
          <Descriptions.Item label='Bị khóa'>{user.isLocked ? 'Có' : 'Không'}</Descriptions.Item>
          {user.isLocked && (
            <Descriptions.Item label='Ngày khóa'>{new Date(user.lockedAt).toLocaleString()}</Descriptions.Item>
          )}
        </Descriptions>
      ) : (
        <p>Không tìm thấy thông tin người dùng.</p>
      )}
    </Modal>
  )
}

export default ShowUserDetailModel
