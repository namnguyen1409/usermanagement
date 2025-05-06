import { Drawer, Form, Input, Button, DatePicker, Switch, message } from "antd";
import dayjs from "dayjs";
import { useEffect } from "react";
import axiosInstance from "../utils/axiosInstance";

const { TextArea } = Input;

interface EditProfileDrawerProps {
    open: boolean;
    onClose: () => void;
    profile: {
        username: string;
        firstName: string;
        lastName: string;
        email: string;
        phone?: string;
        gender?: boolean;
        birthday?: string;
        address?: string;
    };
    onUpdated: () => void;
}

const EditProfileDrawer: React.FC<EditProfileDrawerProps> = ({ open, onClose, profile, onUpdated }) => {
    const [form] = Form.useForm();

    useEffect(() => {
        if (open && profile) {
            form.setFieldsValue({
                ...profile,
                birthday: dayjs(profile.birthday),
            });
        }
    }, [open, profile]);

    const handleSubmit = async (values: any) => {
        try {
            await axiosInstance.put("/profile", {
                ...values,
                birthday: values.birthday.format("YYYY-MM-DD"),
            });
            message.success("Cập nhật thông tin thành công");
            onUpdated();
            onClose();
        } catch (error) {
            message.error("Lỗi khi cập nhật thông tin");
        }
    };

    return (
        <Drawer
            title="Chỉnh sửa thông tin"
            width={480}
            onClose={onClose}
            open={open}
        >
            <Form layout="vertical" form={form} onFinish={handleSubmit}>
                <Form.Item name="username" label="Tên đăng nhập"
                    rules={[{ required: true, min: 6, max: 20 }, { pattern: /^[a-zA-Z0-9_]+$/, message: "Chỉ chứa chữ cái, số, dấu gạch dưới" }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="firstName" label="Họ"
                    rules={[{ required: true, min: 2, max: 20 }, { pattern: /^[\p{L}\s]+$/u, message: "Chỉ chứa chữ cái và khoảng trắng" }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="lastName" label="Tên"
                    rules={[{ required: true, min: 2, max: 20 }, { pattern: /^[\p{L}\s]+$/u, message: "Chỉ chứa chữ cái và khoảng trắng" }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="email" label="Email"
                    rules={[{ type: "email", required: true }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="phone" label="Số điện thoại"
                    rules={[{ pattern: /^[0-9]{10}$/, message: "Phải đủ 10 chữ số" }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="gender" label="Giới tính">
                    <Switch checkedChildren="Nam" unCheckedChildren="Nữ" />
                </Form.Item>

                <Form.Item name="birthday" label="Ngày sinh">
                    <DatePicker format="YYYY-MM-DD" style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item name="address" label="Địa chỉ"
                    rules={[{ min: 10, max: 255 }, { pattern: /^[\p{L}\p{N}\s,.\-]+$/u, message: "Ký tự không hợp lệ trong địa chỉ" }]}>
                    <TextArea rows={3} />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" block>
                        Lưu thay đổi
                    </Button>
                </Form.Item>
            </Form>
        </Drawer>
    );
};

export default EditProfileDrawer;
