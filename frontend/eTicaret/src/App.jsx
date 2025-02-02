import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext'; // AuthProvider'ı import et
import PrivateRoute from './components/PrivateRoute'; // PrivateRoute'ı import et
import HomePage from './pages/HomePage'; // Ana sayfa bileşeni
import LoginPage from './pages/LoginPage'; // Giriş sayfası
import ProfilePage from './pages/ProfilePage'; // Profil sayfası
import RegisterPage from './pages/RegisterPage'; // Kayıt sayfası
import EmailVerifyPage from './pages/EmailVerifyPage'; // E-posta doğrulama sayfası
import NavbarComponent from './components/Navbar'; // Navbar bileşeni
import { CartProvider } from './context/CartContext'; // CartContext'i import et
import CartPage from './pages/CartPage'; // Sepet sayfası

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
