export interface ApiResponse<T> {
    code: number
    message: string
    data: T
}

export interface LoginResponse {
    token: string
    refreshToken?: string
    isAuthenticated?: boolean
    loginLogId?: string
}