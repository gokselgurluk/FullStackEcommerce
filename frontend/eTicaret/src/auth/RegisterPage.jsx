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
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.,])[A-Za-z\d@$!%*?&.,]{8,}$/;
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
            navigate("/register"); // İstediğiniz sayfaya yönlendirin
        }
        if (modalData.type === "error") {
            navigate("/register"); // İstediğiniz sayfaya yönlendirin
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

 


    const handleSubmit = async (e) => {
        e.preventDefault();

        // 🔴 Şifreler eşleşiyor mu kontrolü
        if (formData.password !== formData.confirmPassword) {
            setModalData({ isOpen: true, message: "Şifreler uyuşmuyor!", type: "error" });
            return;
        }

        // 🔴 Şifre regex kontrolü
        if (!passwordRegex.test(formData.password)) {
            setModalData({
                isOpen: true,
                message: "Şifreniz en az 8 karakter, bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir!",
                type: "error"
            });
            return;
        }
        try {
            const response = await axios.post("http://localhost:8080/auth/register", formData);
            console.log("Backend yanıtı:", response.data);

            const userData = response.data.data; // Kullanıcı bilgileri
            setModalData({
                isOpen: true,
                message: `Hoşgeldiniz, ${userData.username} ${userData.surname}`,
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
                    <h4 className="form-title">Hesap Oluştur</h4>
                    <div>
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


                        <div className="input-wrapper">
                            <input
                                className="input-field"
                                type={showPassword ? "text" : "password"}
                                name="password" // 🔹 name ekledik
                                value={formData.password} // 🔹 formData'dan alınıyor
                                onChange={handleInputChange} // 🔹 Güncellenmiş fonksiyon
                                placeholder="Yeni Şifre"
                                required
                            />
                            <button className="password-toggle" tabIndex="-1" type="button" onClick={togglePasswordVisibility}>
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                        </div>

                        <div className="input-wrapper">
                            <input
                                className="input-field"
                                type={showPassword ? "text" : "password"}
                                name="confirmPassword" // 🔹 name ekledik
                                value={formData.confirmPassword} // 🔹 formData'dan alınıyor
                                onChange={handleInputChange} // 🔹 Güncellenmiş fonksiyon
                                placeholder="Şifreyi Onayla"
                                required
                            />
                            <button className="password-toggle" tabIndex="-1" type="button" onClick={togglePasswordVisibility}>
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                        </div>




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
                    <button className='login-button' type="submit" style={{ maxWidth: "200px" }}>Hesap Oluştur</button>


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
