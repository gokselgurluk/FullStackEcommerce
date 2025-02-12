import { Modal, Form, Button } from 'react-bootstrap'; // React-Bootstrap bileşenleri
import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Yönlendirme için gerekli
import ModalComponent from "../components/ModalComponent"; // ModalComponent'ı kullanıyoruz
import { Eye, EyeOff, Mail } from "lucide-react";
const RegisterPage = () => {
    const navigate = useNavigate(); // Yönlendirme işlemi için useNavigate hook'u
    const [showPassword, setShowPassword] = useState(false);
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
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

    const closeModal = () => {
        setModalData({ isOpen: false, message: "", type: "" });
        // Modal kapandıktan sonra yönlendirme işlemi
        // Eğer modal kapatılırken message "enter" ise, yönlendirme yap
        if (modalData.type === "warning") {
            navigate("/login"); // İstediğiniz sayfaya yönlendirin
        }
        if (modalData.type === "error") {
            navigate("/login"); // İstediğiniz sayfaya yönlendirin
        }

        if (modalData.type === "success") {
            navigate("/login"); // İstediğiniz sayfaya yönlendirin
        }

    };

    // Form alanlarını kontrol etme
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    // Form gönderimi
    const handleSubmit = async (e) => {
        e.preventDefault();
         // Şifrelerin eşleştiğini kontrol et
    if (password !== confirmPassword) {
        setModal({ open: true, message: "Şifreler uyuşmuyor!", type: "error" });
        return;
    }
        try {
            const response = await axios.post("http://localhost:8080/auth/register", formData);
            console.log("Backend yanıtı:", response.data);

            const userData = response.data.data; // Kullanıcı bilgileri
            setModalData({
                isOpen: true,
                message: `Kayıt başarılı! Hoşgeldiniz, ${userData.username}.`,
                type: "success",
            });
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

    const togglePasswordVisibility = () => {
        setShowPassword((prev) => !prev);
    };


    return (
        < main className="main">
            <div className="form-container">
                <form className="form" onSubmit={handleSubmit} >
                    <h4 className="form-title">Kayıt Ol</h4>
                    <div >
                        <div className="input-wrapper">
                            <input className='input-field'
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleInputChange}
                                placeholder="Kullanıcı Adı"
                                required
                            />

                        </div>
                    </div>
                    <div >
                        <div className="input-wrapper">
                            <input className='input-field'
                                type="text"
                                name="surname"
                                value={formData.surname}
                                onChange={handleInputChange}
                                placeholder="Soyadınızı Girin"
                                required
                            />

                        </div>
                    </div>
                    <div >
                        <div className="input-wrapper">
                            <input
                                className="input-field"
                                type={showPassword ? "text" : "password"}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Yeni Şifre"
                                required
                            />
                            <button className="password-toggle" type="button" onClick={togglePasswordVisibility}>
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                        </div>
                    </div>
                    <div >
                        <div className="input-wrapper">
                            <input
                                className="input-field"
                                type={showPassword ? "text" : "password"}
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Şifreyi Onayla"
                                required
                            />
                            <button className="password-toggle" type="button" onClick={togglePasswordVisibility}>
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                        </div>
                    </div>

                    <div >
                        <div className="input-wrapper">
                            <input className='input-field'
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                placeholder="Email Adresinizi Girin"
                                required
                            />
                            <Mail className="mail-toggle" />
                        </div>
                    </div>

                    <button className='login-button' type="submit" style={{ maxWidth: "200px" }}>Kayıt Ol</button>
                </form>

                {/* Modal Bileşeni */}
                <ModalComponent
                    isOpen={modalData.isOpen}
                    onRequestClose={closeModal}
                    message={modalData.message}
                    type={modalData.type}
                />
            </div>
        </main >
    );
};

export default RegisterPage;
