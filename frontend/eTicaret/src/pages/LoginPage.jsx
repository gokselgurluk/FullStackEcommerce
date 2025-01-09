import React, { useState } from 'react';
import { Modal, Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // AuthContext'e erişim
import axiosInstance from '../api/axiosInstance'; // axios instance kullanımı
import ModalComponent from '../components/ModalComponent'; // Modal bileşeni

const LoginPage = () => {
  const { login } = useAuth(); // Global login fonksiyonu
  const navigate = useNavigate();

  // Form ve kullanıcı verileri
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [userData, setUserData] = useState(null);

  // Şifre görünürlüğü kontrolü
  const [showPassword, setShowPassword] = useState(false);

  // Modal yönetimi
  const [modalData, setModalData] = useState({
    isOpen: false,
    message: '',
    type: '', // 'success' veya 'error'
  });

  // Giriş alanlarının değişimini takip et
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // Şifre görünürlüğü toggle
  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  // Giriş işlemini gönder
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axiosInstance.post('/auth/login', formData);
      console.log('Backend Response:', response.data); // Backend yanıtını logla

      // Backend'den gelen yanıt
      const { accessTokens, username, roles, email } = response.data;
      console.log('AccessToken:', accessTokens); // Access token'ı kontrol et
      console.log('Email:', email); // Email bilgisini kontrol et

      if (accessTokens && email) {
        // AuthContext'e token ve email bilgilerini kaydediyoruz
        login(accessTokens, email);
      } else {
        console.error('Access token veya email undefined/null');
      }

      // Kullanıcı verisini ayarla
      setUserData({ username, roles });
      setModalData({
        isOpen: true,
        message: `Giriş başarılı! Hoşgeldiniz, ${username}.`,
        type: 'success',
      });

      setTimeout(() => {
        setModalData({ isOpen: false });
        navigate('/');
      }, 2000);

    } catch (error) {
      if (error.response) {
        console.error('Backend hatası:', error.response.data);
        setModalData({
          isOpen: true,
          message: error.response.data.data || 'Bir hata oluştu. Lütfen tekrar deneyin.',
          type: 'error',
        });
      } else if (error.request) {
        console.error('İstek hatası:', error.request);
        setModalData({
          isOpen: true,
          message: 'Sunucuya bağlanılamadı. Lütfen internet bağlantınızı kontrol edin.',
          type: 'error',
        });
      } else {
        console.error('Hata:', error.message);
        setModalData({
          isOpen: true,
          message: 'Bilinmeyen bir hata oluştu. Lütfen tekrar deneyin.',
          type: 'error',
        });
      }
    }
  };

  // Modal kapatma
  const closeModal = () => {
    setModalData({ isOpen: false, message: '', type: '' });
  };

  return (
    <div style={{ maxWidth: '400px', margin: 'auto', padding: '20px' }}>
      <h3>Login</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3" controlId="formBasicEmail">
          <Form.Label>Email</Form.Label>
          <Form.Control
            type="email"
            name="email"
            placeholder="Enter email"
            value={formData.email}
            onChange={handleInputChange}
            required
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formBasicPassword">
          <Form.Label>Password</Form.Label>
          <Form.Control
            type={showPassword ? 'text' : 'password'}
            name="password"
            placeholder="Enter Password"
            value={formData.password}
            onChange={handleInputChange}
            required
          />
        </Form.Group>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <Form.Group className="mb-3" controlId="formShowPasswordCheckbox">
            <Form.Check
              type="checkbox"
              label="Show Password"
              onChange={togglePasswordVisibility}
            />
          </Form.Group>
          <Form.Group className="mb-3" controlId="formBasicCheckbox">
            <Form.Check type="checkbox" label="Remember me" />
          </Form.Group>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <Button variant="primary" type="submit">
            Login
          </Button>
          <Button variant="secondary" onClick={() => navigate('/Register')}>
            Create Account
          </Button>
        </div>
      </Form>

      {/* Modal */}
      <ModalComponent
        isOpen={modalData.isOpen}
        onRequestClose={closeModal}
        message={modalData.message}
        type={modalData.type}
      />
    </div>
  );
};

export default LoginPage;
