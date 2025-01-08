// src/components/PrivateRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ children }) => {
  const { isAuth } = useAuth();

  return isAuth ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;
