export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
export interface ApiError {
  status: number;
  error: string;
  message: string;
  path: string;
  errors?: { field: string; message: string }[];
}
export interface UserView {
  id: string;
  name: string;
  email: string;
  roles: string[];
}
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserView;
}
