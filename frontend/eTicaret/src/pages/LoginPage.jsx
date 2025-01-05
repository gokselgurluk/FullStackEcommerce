import React, { useState } from 'react';
import { Modal, Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ModalComponent from '../components/ModalComponent'; // Modal bileşeni

const LoginPage = () => {
  const navigate = useNavigate();

  // Form ve kullanıcı verileri
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [userData, setUserData] = useState(null);

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

  // Giriş işlemini gönder
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/auth/login', formData);
      const { accessTokens, username, roles } = response.data;

      // Token'ı localStorage'a kaydet
      localStorage.setItem('accessToken', accessTokens);

      // Kullanıcı bilgilerini güncelle
      setUserData({ username, roles });

      // Başarı modalını aç
      setModalData({
        isOpen: true,
        message: `Giriş başarılı! Hoşgeldiniz, ${username}.`,
        type: 'success',
      });

      // Başarı durumunda yönlendirme
      setTimeout(() => {
        setModalData({ isOpen: false }); // Modal'ı kapat
        navigate('/'); // Ana sayfaya yönlendir
      }, 2000);
    } catch (error) {
      if (error.response) {
        // Eğer backend'den gelen bir yanıt varsa
        console.error("Backend hatası:", error.response.data);  // Hata detaylarını konsola yazdır
        setModalData({
          isOpen: true,
          message: error.response.data.data || "Bir hata oluştu. Lütfen tekrar deneyin.",
          type: "error",
        });
      } else if (error.request) {
        // Eğer istek gönderildi ama yanıt alınamadıysa
        console.error("İstek hatası:", error.request);
        setModalData({
          isOpen: true,
          message: "Sunucuya bağlanılamadı. Lütfen internet bağlantınızı kontrol edin.",
          type: "error",
        });
      } else {
        // Genel bir hata
        console.error("Hata:", error.message);
        setModalData({
          isOpen: true,
          message: "Bilinmeyen bir hata oluştu. Lütfen tekrar deneyin.",
          type: "error",
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
        <Form.Group className="mb-3" controlId="formBasicUsername">
          <Form.Label>Username</Form.Label>
          <Form.Control
            type="text"
            name="username"
            placeholder="Enter Username"
            value={formData.username}
            onChange={handleInputChange}
            required
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formBasicPassword">
          <Form.Label>Password</Form.Label>
          <Form.Control
            type="password"
            name="password"
            placeholder="Enter Password"
            value={formData.password}
            onChange={handleInputChange}
            required
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="formBasicCheckbox">
          <Form.Check type="checkbox" label="Remember me" />
        </Form.Group>

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
