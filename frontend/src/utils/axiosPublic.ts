import axios from 'axios'
import type { ApiResponse } from '../types/api.response'

const axiosPublic = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const apiPublicCall = async <T, F>(
  url: string,
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  data?: F,
): Promise<ApiResponse<T>> => {
  try {
    const response: ApiResponse<T> = (await axiosPublic({
      url,
      method,
      data,
    })).data

    console.log('API Response:', response)

    return response
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const statusCode = error.response?.status || 500;
      const apiErrorCode = error.response?.data?.code || statusCode;
      const apiErrorMessage = error.response?.data?.message || 'An unexpected error occurred';


      return {
        code: apiErrorCode,
        message: apiErrorMessage,
        data: error.response?.data || 'An unexpected error occurred',
      } as ApiResponse<T>
    }
    return {
      code: 500,
      message: 'An unexpected error occurred',
      data: 'An unexpected error occurred',
    } as ApiResponse<T>
  }
}
export default axiosPublic
