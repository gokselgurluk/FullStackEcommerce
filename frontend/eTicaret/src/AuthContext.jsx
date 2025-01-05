import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

// AuthContext'i oluşturuyoruz
const AuthContext = createContext();

// useAuth hook'u, AuthContext'e erişim sağlar
export const useAuth = () => {
  return useContext(AuthContext);
};

// AuthProvider, kullanıcı doğrulama ve token yönetimi sağlar
export const AuthProvider = ({ children }) => {
  const [isAuth, setIsAuth] = useState(false);
  const [loading, setLoading] = useState(true); // Yükleniyor durumu

  useEffect(() => {
    // Sayfa yüklendiğinde, token'ın geçerliliğini kontrol et
    const checkAuthStatus = async () => {
      const accessToken = localStorage.getItem('accessToken');
      const refreshToken = localStorage.getItem('refreshToken');
      
      if (accessToken) {
        // Access token varsa, kullanıcıyı giriş yapmış sayıyoruz
        setIsAuth(true);
        setLoading(false);
      } else if (refreshToken) {
        // Kullanıcı login değilse, refresh token'ı kullanmaya gerek yoktur
        // Yalnızca refresh token ile giriş yapılabilir, bunun için login olmamız gerekir
        setIsAuth(false);
        setLoading(false);
      } else {
        // Token yoksa, kullanıcıyı çıkartıyoruz
        setIsAuth(false);
        setLoading(false);
      }
    };

    checkAuthStatus();
  }, []);

  // Login işlemi
  const login = (accessToken, refreshToken) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setIsAuth(true);
  };

  // Logout işlemi
  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setIsAuth(false);
  };

  // AuthProvider, isAuth durumunu ve login/logout fonksiyonlarını children'a sağlar
  return (
    <AuthContext.Provider value={{ isAuth, login, logout }}>
      {!loading && children} {/* Yükleniyor ise, children'i render etme */}
    </AuthContext.Provider>
  );
};
