import React, { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import ModalComponent from "../components/ModalComponent";
import { useAuth } from "../context/AuthContext";

const ActivateAccountPage = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { isAuth,logout } = useAuth(); // Auth durumu
    const tokenValue = searchParams.get("token");  // URL'den token'ı al
    const [modalData, setModalData] = useState({
        isOpen: false,
        message: "",
        type: "",
    });

    const showModal = (message, type) => {
        setModalData({ isOpen: true, message, type });
    };

    const closeModal = (type) => {
        setModalData({ isOpen: false, message: "", type: "" });
    
        // Modal kapandıktan sonra yönlendirme işlemi
        setTimeout(() => {
            if (type === "success") {
                logout();  // Kullanıcıyı çıkış yapmaya yönlendir
            }
    
            if (type === "warning") {
                logout();  
                navigate("/login"); 
            }
    
            if (type === "error") {
                navigate("/email-verify"); 
            }
        }, 500);
    };
    useEffect(() => {
        if (isAuth) {
             if (tokenValue) {
            verifyEmail(tokenValue);
            } 
            else {
                showModal("Link geçersiz Giriş Sayfasına Yönelendiriliyorsunuz!", "error");
                setTimeout(() => {
                    closeModal("error");
                }, 3000);
            }
        } else {
            showModal("Oturum kapalı Giriş Sayfasına Yönelendiriliyorsunuz!", "warning");
            setTimeout(() => {
                closeModal("warning");
            }, 3000);
        }
        
    }, [tokenValue, isAuth]);  
    

    const verifyEmail = async (tokenValue) => {
        try {

            const response = await axiosInstance.post(
                "/auth/activate-account", 
                { tokenValue }, // Token'ı JSON formatında gönderiyoruz
                {
                    headers: {
                        "Content-Type": "application/json",  // JSON formatı
                    }
                }
            );
            console.log("Email doğrulama başarılı:", response.data);
            setModalData({ isOpen: true, message: "Doğrulama başarılı!", type: "success" });
            setTimeout(() => {
                closeModal("success");
            }, 5000);
        } catch (error) {
            console.error("Doğrulama hatası:", error);
            setModalData({ isOpen: true, message: "Doğrulama başarısız. Token geçersiz veya süresi dolmuş!", type: "error" });
        }
    };
    
    return (
        <div style={{ maxWidth: "400px", margin: "auto", padding: "20px" }}>
            <h2>Email Verification</h2>
            <p>E-posta doğrulama işlemi yapılıyor...</p>
            <ModalComponent 
                isOpen={modalData.isOpen} 
                onRequestClose={closeModal} 
                message={modalData.message} 
                type={modalData.type} 
            />
        </div>
    );
};

export default ActivateAccountPage;
