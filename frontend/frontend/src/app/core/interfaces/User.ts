export interface UserRequest {
  email: string;
  password: string;
}
export interface UserResponse {
  id: string;
  name: string;
  email: string;
  role: string;
}

export interface LoginResponse extends UserResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}
