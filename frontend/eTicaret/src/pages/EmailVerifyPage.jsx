import React, { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance";
import { useAuth } from "../context/AuthContext";
import ModalComponent from "../components/ModalComponent";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const EmailVerifyPage = () => {
  const { isAuth } = useAuth();
  const [countdown, setCountdown] = useState(0);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [modalData, setModalData] = useState({ isOpen: false, message: "", type: "" });
  const navigate = useNavigate();
  const accessToken = localStorage.getItem("accessToken");
  
  const closeModal = () => {
    setModalData({ isOpen: false, message: "", type: "" });
    if (modalData.type === "warning") {
      navigate("/login");
    }
    if (modalData.type === "warning") {
      navigate("/login");
    }
  };
  const showModal = (message, type) => {
    setModalData({ isOpen: true, message, type });
  };

  const getEmailFromToken = (accessToken) => {
    try {
      return jwtDecode(accessToken)?.sub || null;
    } catch (error) {
      console.error("Token çözümleme hatası:", error);
      return null;
    }
  };

  const sendVerificationCode = async () => {
    if (!accessToken) {
      showModal("Oturum süresi doldu, tekrar giriş yapmalısınız!", "warning");
      return;
    }
  
    try {
      const response = await axiosInstance.post(
        '/api/send-activation-email',
        {}, // Boş body ekle
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
  
      if (response.data && response.data.data) {
        console.log("code info: ", response.data.data);
        
        const expiryTimeISO = response.data.data.expiryTime; // Tarih formatında geliyor
        const expiryTimeMillis = new Date(expiryTimeISO).getTime(); // Timestamp'e çevir
  
        const remainingSeconds = Math.max(0, Math.floor((expiryTimeMillis - Date.now()) / 1000));
        
        startCountdown(remainingSeconds || 120);
        setIsResendDisabled(true);
        showModal("E-posta gönderildi!", "success");
      }
    } catch (error) {
      if (error.response && error.response.status === 403) {
        showModal("Oturum süresi doldu, tekrar giriş yapmalısınız!", "warning");
        navigate("/login");
      } else {
        showModal("Kod gönderilirken hata oluştu.", "error");
      }
    }
  };
  

  const startCountdown = (seconds) => {
    setCountdown(seconds);
    const interval = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          setIsResendDisabled(false);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

 

  return (
    <div style={{ maxWidth: "400px", margin: "auto", padding: "20px" }}>
      <h2>Email Doğrulama</h2>
      {isAuth ? (
        <>
          <p>Email: <b>{getEmailFromToken(accessToken)}</b></p>
          <p>Lütfen e-postanıza gönderilen doğrulama kodunu girin:</p>
          <p style={{ marginTop: "20px", color: "#555" }}>Kodun süresi dolmak üzere: <b>{countdown} saniye</b></p>
          <button
            onClick={sendVerificationCode}
            disabled={isResendDisabled}
            style={{
              width: "100%", padding: "10px", backgroundColor: isResendDisabled ? "#ccc" : "#28A745",
              color: "#fff", border: "none", borderRadius: "4px", cursor: isResendDisabled ? "not-allowed" : "pointer", marginTop: "10px"
            }}
          >
           Kod Gönder
          </button>
        </>
      ) : navigate("/login")}

      <ModalComponent 
        isOpen={modalData.isOpen} 
        onRequestClose={closeModal} 
        message={modalData.message} 
        type={modalData.type} 
      />
    </div>
  );
};

export default EmailVerifyPage;
