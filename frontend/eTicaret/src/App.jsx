import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext'; // AuthProvider'ı import et
import PrivateRoute from './components/PrivateRoute'; // PrivateRoute'ı import et
import HomePage from './pages/HomePage'; // Ana sayfa bileşeni
import LoginPage from './pages/LoginPage'; // Giriş sayfası
import ProfilePage from './pages/ProfilePage'; // Profil sayfası
import RegisterPage from './auth/RegisterPage'; // Kayıt sayfası
import EmailVerifyPage from './auth/EmailVerifyPage'; // E-posta doğrulama sayfası
import NavbarComponent from './components/Navbar'; // Navbar bileşeni
import { CartProvider } from './context/CartContext'; // CartContext'i import et
import CartPage from './pages/CartPage'; // Sepet sayfası
import ActivateAccountPage from './auth/ActivateAccountPage'; // Sepet sayfası
import ForgotPassword from './auth/ForgotPassword'; // Sepet sayfası
import ResetPassword from './auth/ResetPassword'; // Sepet sayfası
import 'bootstrap/dist/css/bootstrap.min.css';

const App = () => {
  return (
    <AuthProvider> {/* AuthProvider ile uygulamanın alt yapısını sarmalı */}
      <CartProvider> {/* CartProvider ile sepet verilerini sarmalı */}
        <Router> {/* Router ile yönlendirme işlemlerini kontrol et */}
          <NavbarComponent /> {/* Navbar bileşenini ekle */}
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/activate-account" element={<ActivateAccountPage />} />
            <Route path="/forget-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />
            {/* PrivateRoute ile ProfilePage'i koru */}
            <Route path="/profile" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
            <Route path="/email-verify" element={<EmailVerifyPage />} />
            <Route path="/cart" element={<PrivateRoute><CartPage /></PrivateRoute>} />
          </Routes>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
};

export default App;
