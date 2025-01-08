import React, { createContext, useState, useContext, useEffect } from 'react';

// Create AuthContext
const AuthContext = createContext();

// AuthProvider component
export const AuthProvider = ({ children }) => {
  const [isAuth, setIsAuth] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      setIsAuth(true);
    }
  }, []);

  // Log in function
  const login = (token) => {
    localStorage.setItem('accessToken', token);
    setIsAuth(true);
  };

  // Log out function - clears the token and sets isAuth to false
  const logout = () => {
    localStorage.removeItem('accessToken');
    setIsAuth(false);
  };

  return (
    <AuthContext.Provider value={{ isAuth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use the AuthContext
export const useAuth = () => {
  return useContext(AuthContext);
};
