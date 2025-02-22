import React, { createContext, useContext, useState } from 'react';
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuth, setIsAuth] = useState(!!localStorage.getItem('accessToken'));
  const [isActive, setIsActive] = useState(false); // Kullanıcının aktiflik durumu
  
  const login = (accessToken) => {
    localStorage.setItem('accessToken', accessToken);
    setIsAuth(true);
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    setIsAuth(false);
  };

  return (
    <AuthContext.Provider value={{ isAuth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);