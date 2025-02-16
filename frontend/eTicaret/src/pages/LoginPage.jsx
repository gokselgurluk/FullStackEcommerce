import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axiosInstance from '../api/axiosInstance';
import ModalComponent from '../components/ModalComponent';
import { FaGoogle, FaFacebook, FaApple } from "react-icons/fa"; // react-icons paketi
import { Eye, EyeOff, Mail } from "lucide-react";

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);  // Default checked
  const [modalData, setModalData] = useState({
    isOpen: false,
    message: '',
    type: '', // 'success', 'error', 'warning'
  });

  const closeModal = () => {
    setModalData({ isOpen: false, message: "", type: "" });
    // Modal kapandıktan sonra yönlendirme işlemi
    if (modalData.type === "warning") {
      navigate("/email-verify"); // E-mail doğrulaması sayfasına yönlendir
    }
    if (modalData.type === "error") {
      navigate("/login"); // Hata durumu için login sayfasına yönlendir
    }
    if (modalData.type === "success") {
      navigate("/"); // Giriş başarılıysa dashboard'a yönlendir
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const handleRememberMeChange = (e) => {
    setRememberMe(e.target.checked);
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Sayfa yenilenmesini engelle
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
          message: 'Giriş başarısız!',
          type: 'error', // Giriş başarısızsa error tipi
        });
      }
     } catch (error) {
        console.log(error.response);
        // Hesap aktif değilse (E-mail doğrulaması gerekliyse)
        if (error.response?.data === "Hesap Aktif Degil") {
          const errorMessage = `${error.response.data} E-mail doğrulaması gerekli.`;
          setModalData({
            isOpen: true,
            message: errorMessage,
            type: 'warning', // "warning" tipini kullanıyoruz
          });
        } else if (error.response?.data?.message) {
          // Eğer response'da message varsa
          setModalData({
            isOpen: true,
            message: `${error.response.data.message} `,
            type: 'error', // Error tipi
          });
        } else {
          // Genel hata mesajı
          setModalData({
            isOpen: true,
            message: 'Bir hata oluştu. Lütfen tekrar deneyin.',
            type: 'error', // Error tipi
          });
        }
      }
    }
  return (
    <main className="login-container">
      <div className="login-left">
        <div className='login-left-clip-box'></div>
        <div className='logo-container'></div>

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
            <input
              type="email"
              name="email"
              className="input-field"
              placeholder="Email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
            <Mail className="mail-toggle" />
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
            <button className="password-toggle" type="button" onClick={togglePasswordVisibility}>
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>

          <div className="cointainer">
            <a className="forget-password" href="/forget-password">Forget Password</a>

            <div className="remember-me-container">
              <input
                type="checkbox"
                className="remember-me-checkbox"
                id="rememberMe"
                checked={rememberMe}
                onChange={handleRememberMeChange}
              />
              <label htmlFor="rememberMe" className="remember-me-label">Remember me</label>
           
          </div>
          </div>
          <button type="submit" className="login-button">Login</button>
        
          <div className="signup-container">
            <div className="signup-text">
              Don’t have an account ? <a href="/register" className="signup-link">Sign Up</a>
            </div>
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
