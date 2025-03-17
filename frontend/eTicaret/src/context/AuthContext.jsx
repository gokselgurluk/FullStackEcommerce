import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuth, setIsAuth] = useState(!!localStorage.getItem('accessToken'));
  const [refreshToken, setRefeshToken] = useState(localStorage.getItem('refreshToken') || "");


  const login = (accessToken, refreshToken) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setIsAuth(true);
    setRefeshToken(refreshToken);  // Burada refreshToken state'ini güncelle
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setIsAuth(false);
    setRefeshToken("");  // Email bilgisini temizle
  };

  return (
    <AuthContext.Provider value={{ isAuth, refreshToken, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
