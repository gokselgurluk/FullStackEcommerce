import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axiosInstance from '../api/axiosInstance';
import ModalComponent from '../components/ModalComponent';
import { FaGoogle, FaFacebook, FaApple } from "react-icons/fa"; // react-icons paketi
import { Eye, EyeOff,Mail } from "lucide-react";

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [modalData, setModalData] = useState({ isOpen: false, message: '', type: '' });

  const closeModal = () => {
    setModalData({ isOpen: false, message: "", type: "" });
    if (modalData.type === "success") {
      setTimeout(() => {
        navigate("/");
      }, 500);
    } else if (modalData.type === "error") {
      setTimeout(() => {
        navigate("/login");
      }, 500);
    } else if (modalData.type === "warring") {
      setTimeout(() => {
        navigate("/email-verify");
      }, 500);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axiosInstance.post('/auth/login', formData);
      const userData = response.data;
      if (userData?.accessToken) {
        localStorage.setItem('accessToken', userData.accessToken);
        login(userData.accessToken);
        setModalData({
          isOpen: true,
          message: userData.active ? 'Giriş başarılı!' : 'Hesabınız aktif değil, e-mail doğrulaması yapınız.',
          type: userData.active ? 'success' : 'warring',
        });
      } else {
        setModalData({
          isOpen: true,
          message: 'Giriş başarısız. Yanıt eksik!',
          type: 'error',
        });
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Giriş işlemi sırasında bir hata oluştu.';
      setModalData({
        isOpen: true,
        message: errorMessage,
        type: 'error',
      });
    }
  };

  return (
    <main className="login-container">
      <div className="login-left">
        <div className='login-left-clip-box'></div>
        <div className='logo-container'>
          <img src="/images/Logo.png" alt="Logo" />
        </div>
     

        {/* Giriş Formu */}
        <form className="login-form" onSubmit={handleSubmit}>
        <h2 className="form-title">Login In</h2>
             {/* Sosyal Medya Butonları */}
        <div className="social-login">
        <button className="social-btn google"><FaGoogle size={20} /> </button>
        <button className="social-btn facebook"><FaFacebook size={20} /> </button>
        <button className="social-btn apple"><FaApple size={20} /> </button>
        </div>
        {/* Ayırıcı Çizgi */}
        <div className="divider">
          <span>Or continue </span>
        </div>
        <div className="input-wrapper">
        <Mail className="mail-toggle" />
          <input
            type="email"
            name="email"
            className="input-field"
            placeholder="Email"
            value={formData.email}
            onChange={handleInputChange}
            required
          />
           
          </div>
          <div className="input-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              className="input-field"
              placeholder="Password"
              value={formData.password}
              onChange={handleInputChange}
              required
            />
            <a className="forget-password" href="/forget-password">Forget Password</a>
            <button className="password-toggle" type="button" onClick={togglePasswordVisibility}>
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>
          <button type="submit" className="login-button">Login</button>
          <div className='Sign-Up'>
            <p>Don’t have an account? <a href="/register">Sign Up</a></p>
          </div>
        </form>
      </div>

      <div className="login-right">
        <img className='svg' src="/images/shopping-cart.svg" alt="Market Arabası" />
      </div>

      <ModalComponent
        isOpen={modalData.isOpen}
        onRequestClose={closeModal}
        message={modalData.message}
        type={modalData.type}
      />
    </main>
  );
};

export default LoginPage;
