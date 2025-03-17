import axios from 'axios';


const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem('refreshToken');  // Güncel değeri localStorage'den al
        if (!refreshToken) {
          console.error("❌ Refresh Token Bulunamadı!");
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          window.location.href = '/login';
          return Promise.reject(error);
        }

        console.log("📌 Refresh Token Request: Gönderilen Token:", refreshToken);
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/auth/refresh-token`, { tokenValue: refreshToken });

        if (response.data.accessToken) {
          localStorage.setItem('accessToken', response.data.accessToken);
          originalRequest.headers['Authorization'] = `Bearer ${response.data.accessToken}`;
          return axiosInstance(originalRequest);
        } else {
          console.error("❌ Yeni access token alınamadı!");
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          window.location.href = '/login';
        }
      } catch (err) {
        console.error("❌ Refresh Token Request Hatası:", err);
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
