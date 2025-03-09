import React, { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance";
import { useAuth } from "../context/AuthContext";
import ModalComponent from "../components/ModalComponent";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff, Mail,Loader } from "lucide-react";

const EmailVerifyPage = () => {
  const { isAuth } = useAuth();
  const [countdown, setCountdown] = useState(0);
  const [isResendDisabled, setIsResendDisabled] = useState(false);
  const [modalData, setModalData] = useState({ isOpen: false, message: "", type: "" });
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const accessToken = localStorage.getItem("accessToken");

  useEffect(() => {
    if (accessToken) {
      setEmail(getEmailFromToken(accessToken)); // Token'dan email al
    }
  }, [accessToken]);

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
    }, 100);
  };

  const showModal = (message, type) => {
    setModalData({ isOpen: true, message, type });
  };

  const getEmailFromToken = (accessToken) => {
    try {
      return jwtDecode(accessToken)?.sub || "";
    } catch (error) {
      console.error("Token çözümleme hatası:", error);
      return "";
    }
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    setIsResendDisabled(true); // İşlem başladı, butonu devre dışı bırak
    if (!accessToken) {
      showModal("Oturum süresi doldu, tekrar giriş yapmalısınız!", "warning");
      return;
    }

    try {
      const response = await axiosInstance.post(
        '/api/send-activation-email',
        {},
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      if (response.data && response.data.data) {
        const expiryTimeISO = response.data.data.emailExpiryDate;
        const expiryTimeMillis = Date.parse(expiryTimeISO);
        const remainingSeconds = Math.max(0, Math.floor((expiryTimeMillis - Date.now()) / 1000));

        startCountdown(remainingSeconds || 120);
        setIsResendDisabled(true);
        setMessage("✅ Email gönderildi mesaj kutunuzu kontrol edin.");
      }
    } catch (error) {
      if (error.response && error.response.status === 403) {
        showModal("Oturum süresi doldu, tekrar giriş yapmalısınız!", "warning");
        setTimeout(() => {
          navigate("/login");
        },500);
      } else if (error.response?.data?.message) {
        setError("❌ " + (error.response?.data?.message || "Kod yanlış veya süresi dolmuş!"));
      } else {
        setError("❌ Kod gönderilirken hata oluştu.");
      }
    }
    finally {
      setIsResendDisabled(false); // İşlem tamamlandı, butonu tekrar aktif hale getir
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

  if (!isAuth) {
    navigate("/login");
    return null;
  }

  return (
    <main className="main">
      <div className="form-container">
        <form className="form" onSubmit={handleSubmit}>
          <h4 className="form-title">E-posta Doğrulama</h4>
          <div>
            <div className="input-wrapper">
              <Mail className="mail-toggle" />
              <input
                className="input-field"
                type="email"
                value={email}
                onChange={handleEmailChange}
                placeholder="E-posta adresinizi girin"
                required
                disabled='true'
              />
            </div>
          </div>
          <p style={{ marginTop: "10px", color: "#555" }}>
            Kodun süresi dolmak üzere: <b>{countdown} saniye</b>
          </p>
          <button type="submit" className="login-button" disabled={isResendDisabled} style={{ maxWidth: "200px" }}>
            {isResendDisabled ? <Loader className="spinner" size={20} /> : "Kod Gönder"}
          </button>
          {/* <button className="login-button" type="submit">
            Kod Gönder
          </button> */}
          {message && <p style={{ color: "green" }}>{message}</p>}
          {error && <p style={{ color: "red" }}>{error}</p>}
        </form>


        <ModalComponent
          isOpen={modalData.isOpen}
          onRequestClose={closeModal}
          message={modalData.message}
          type={modalData.type}
        />
      </div>
    </main>
  );
};

export default EmailVerifyPage;
