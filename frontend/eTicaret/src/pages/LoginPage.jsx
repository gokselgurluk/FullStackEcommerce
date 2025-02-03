import React, { useState } from 'react';
import { Modal, Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // AuthContext'e erişim
import axiosInstance from '../api/axiosInstance'; // axios instance kullanımı
import ModalComponent from '../components/ModalComponent'; // Modal bileşeni

const LoginPage = () => {
  const { login } = useAuth(); // Global login fonksiyonu
  const navigate = useNavigate(); // Yönlendirme işlemi için useNavigate hook'u
  const [userInfo, setUserInfo] = useState(null); // Kullanıcı bilgilerini tutacak state
  // Form ve kullanıcı verileri
  const [formData, setFormData] = useState({ email: '', password: '' });

  // Şifre görünürlüğü kontrolü
  const [showPassword, setShowPassword] = useState(false);

  // Modal yönetimi
  const [modalData, setModalData] = useState({
    isOpen: false,
    message: '',
    type: '', // 'success' veya 'error'
  });
  const closeModal = () => {
    setModalData({ isOpen: false, message: "", type: "" });

    // Modal kapandıktan sonra yönlendirme işlemi
    if (modalData.type === "success") {
        setTimeout(() => {
            navigate("/"); // Yönlendirme yapılacak sayfa
        
        }, 500); // Modalın kapanmasını bekleyin (500ms gibi)
    }

    if (modalData.type === "error") {
        setTimeout(() => {
            logout();  // Kullanıcıyı çıkış yapmaya yönlendir
            navigate("/login"); // Yönlendirme yapılacak sayfa
        }, 500);
    }

    if (modalData.type === "warring") {
        setTimeout(() => {
            navigate("/email-verify"); // Yönlendirme yapılacak sayfa
        }, 500);
    }
};
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
      const userData = response.data; // Gelen veriyi direkt değişkene al
      // Login sayfasında, sayfa yüklendiğinde
    
      console.log('Backend Response:', userData); // Doğrudan veriyi logla

      if (userData?.accessToken) {
        localStorage.setItem('accessToken', userData.accessToken);
        login(userData.accessToken);

        if (userData.active) {
          setModalData({
            isOpen: true,
            message: 'Giriş başarılı!',
            type: 'success',
          });
        } else {
          setModalData({
            isOpen: true,
            message: 'Hesabınız aktif degil e-mail dogrulaması yapınız',
            type: 'warring',
          });
        }
      } else {
        setModalData({
          isOpen: true,
          message: 'Giriş başarısız. Yanıt eksik!',
          type: 'error',
        });
      }
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || 'Giriş işlemi sırasında bir hata oluştu.';
      setModalData({
        isOpen: true,
        message: errorMessage,
        type: 'error',
      });
    }
  };




  return (
    <div style={{ maxWidth: '400px', margin: 'auto', padding: '20px' }}>
      <h3>Login</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3" controlId="formBasicEmail">
          <Form.Label>Email</Form.Label>
          <Form.Control
            type="email"
            name="email" // 'email' olarak güncellendi
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
