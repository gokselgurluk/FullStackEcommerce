import { Modal, Form, Button } from 'react-bootstrap'; // React-Bootstrap bileşenleri
import axios from 'axios';
import React, { useState } from 'react';
import ModalComponent from "../components/ModalComponent"; // ModalComponent'ı kullanıyoruz

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        username: '',
        surname: '',
        password: '',
        email: '',
    });

    // Modal durumu ve mesajları
    const [modalData, setModalData] = useState({
        isOpen: false,
        message: '',
        type: '', // 'success' veya 'error'
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
      e.preventDefault();
      try {
        const response = await axios.post("http://localhost:8080/auth/register", formData);
        console.log("Backend yanıtı:", response.data); // Yanıtı konsola yazdır
        
        const userData = response.data.data; // Kullanıcı bilgileri
        setModalData({
          isOpen: true,
          message: `Kayıt başarılı!
        Hoşgeldiniz, ${userData.username}.
        Email: ${userData.email}
        Rol: ${userData.roleEnum}
        Kayıt Tarihi: ${new Date(userData.createdAt).toLocaleString()}`,
          type: "success",
        });
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

    const closeModal = () => {
      setModalData({ ...modalData, isOpen: false });
    };

    return (
        <div>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicUsername">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        type="text"
                        name="username"
                        placeholder="Enter Username"
                        value={formData.username}
                        onChange={handleInputChange}
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicSurname">
                    <Form.Label>Surname</Form.Label>
                    <Form.Control
                        type="text"
                        name="surname"
                        placeholder="Enter Surname"
                        value={formData.surname}
                        onChange={handleInputChange}
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleInputChange}
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Email address</Form.Label>
                    <Form.Control
                        type="email"
                        name="email"
                        placeholder="Enter email"
                        value={formData.email}
                        onChange={handleInputChange}
                    />
                </Form.Group>

                <Button variant="primary" type="submit">
                    Submit
                </Button>
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

export default RegisterPage;
