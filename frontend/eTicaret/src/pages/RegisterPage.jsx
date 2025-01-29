import { Modal, Form, Button } from 'react-bootstrap'; // React-Bootstrap bileşenleri
import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Yönlendirme için gerekli
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

    const navigate = useNavigate(); // Yönlendirme işlemi için useNavigate hook'u

    // Form alanlarını kontrol etme
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Form gönderimi
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8080/auth/register", formData);
            console.log("Backend yanıtı:", response.data);

            const userData = response.data.data; // Kullanıcı bilgileri
            setModalData({
                isOpen: true,
                message: `Kayıt başarılı! Hoşgeldiniz, ${userData.username}.`,
                type: "success",
            });

            // Modal kapandıktan sonra e-posta doğrulama sayfasına yönlendir
            setTimeout(() => {
                navigate("/email-verify"); // Email doğrulama sayfasına yönlendir
            }, 2000);
        } catch (error) {
            let errorMessage = "Bilinmeyen bir hata oluştu. Lütfen tekrar deneyin.";

            if (error.response) {
                // Backend'den gelen hata
                console.error("Backend hatası:", error.response.data);
                errorMessage =
                    error.response.data.data === "500"
                        ? "Bir hata oluştu. Lütfen tekrar deneyin."
                        : `Boş alanları doldurun.\n${error.response.data.message}`;
            } else if (error.request) {
                // İstek gönderildi ama yanıt alınamadı
                console.error("İstek hatası:", error.request);
                errorMessage = "Sunucuya bağlanılamadı. Lütfen internet bağlantınızı kontrol edin.";
            } else {
                // Diğer hata durumları
                console.error("Hata:", error.message);
            }

            setModalData({
                isOpen: true,
                message: errorMessage,
                type: "error",
            });
        }
    };

    // Modal'ı kapatma
    const closeModal = () => {
        setModalData({ ...modalData, isOpen: false });
    };

    return (
        <div className="container mt-4">
            <h2>Kayıt Ol</h2>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicUsername">
                    <Form.Label>Kullanıcı Adı</Form.Label>
                    <Form.Control
                        type="text"
                        name="username"
                        placeholder="Kullanıcı Adınızı Girin"
                        value={formData.username}
                        onChange={handleInputChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicSurname">
                    <Form.Label>Soyad</Form.Label>
                    <Form.Control
                        type="text"
                        name="surname"
                        placeholder="Soyadınızı Girin"
                        value={formData.surname}
                        onChange={handleInputChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label>Şifre</Form.Label>
                    <Form.Control
                        type="password"
                        name="password"
                        placeholder="Şifrenizi Girin"
                        value={formData.password}
                        onChange={handleInputChange}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Email</Form.Label>
                    <Form.Control
                        type="email"
                        name="email"
                        placeholder="Email Adresinizi Girin"
                        value={formData.email}
                        onChange={handleInputChange}
                        required
                    />
                </Form.Group>

                <Button variant="primary" type="submit">
                    Kayıt Ol
                </Button>
            </Form>

            {/* Modal Bileşeni */}
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
