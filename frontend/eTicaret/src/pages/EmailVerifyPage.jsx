import React, { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance";
import { useAuth } from "../context/AuthContext";
import ModalComponent from "../components/ModalComponent";

const EmailVerifyPage = () => {
  const { isAuth } = useAuth();
  const [code, setCode] = useState("");
  const [countdown, setCountdown] = useState(0);
  const [isResendDisabled, setIsResendDisabled] = useState(true);
  const [message, setMessage] = useState("");
  const [userInfo, setUserInfo] = useState(null);
  const [expiryTime, setExpiryTime] = useState(null);
  const accessToken = localStorage.getItem("accessToken");

  const [modalData, setModalData] = useState({
    isOpen: false,
    message: "",
    type: "",
  });

  useEffect(() => {
    if (isAuth && accessToken) {
      const fetchUserInfo = async () => {
        try {
          const response = await axiosInstance.get("/api/users/getUserInfo", {
            headers: { Authorization: `Bearer ${accessToken}` },
          });
          if (response.status === 200) {
            setUserInfo(response.data.data);
          } else {
            setModalData({ isOpen: true, message: "Kullanıcı bilgileri alınamadı.", type: "error" });
          }
        } catch (error) {
          setModalData({ isOpen: true, message: "Kullanıcı bilgileri alınırken bir hata oluştu.", type: "error" });
        }
      };
      fetchUserInfo();
      sendVerificationCode();
    } else {
      setModalData({ isOpen: true, message: "Giriş yapmalısınız!", type: "error" });
    }
  }, [isAuth, accessToken]);




  const sendVerificationCode = async () => {
    try {
      const response = await axiosInstance.post("/api/send-activation-email", {}, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      console.log("mail send info : ",response);
      if (response.status === 200) {
        const expirySeconds = response.data.expiryTime || 120;
        setExpiryTime(Date.now() + expirySeconds * 1000);
        startCountdown(expirySeconds);
        setIsResendDisabled(true);
        setModalData({ isOpen: true, message: "E-posta gönderildi!", type: "success" });
      }
    } catch (error) {
      setModalData({ isOpen: true, message: "Kod gönderilirken hata oluştu.", type: "error" });
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



// send kod gonderımı bu kısım da


const handleVerify = async () => {
  if (!code) {
    setMessage("Lütfen bir kod girin.");
    return;
  }
  try {
    const response = await axiosInstance.post(
      `/auth/verifyAccount?code=${code}`,
      {
        headers: { Authorization: `Bearer ${accessToken}` },
      }
    );

    console.log("code dogrulama response: ", response.data.message);

    if (response.data.message.trim() === "Hesap Dogrulama Başarılı:") {
      setModalData({ isOpen: true, message: "Email başarıyla doğrulandı!", type: "success" });
    } else {
      setModalData({ isOpen: true, message: "Doğrulama başarısız oldu. Lütfen tekrar deneyin.", type: "error" });
    }
  } catch (error) {
    setModalData({ isOpen: true, message: "Doğrulama başarısız oldu. Lütfen tekrar deneyin.", type: "error" });
  }
};

const closeModal = () => {
  setModalData({ isOpen: false, message: "", type: "" });
};

  return (
    <div style={{ maxWidth: "400px", margin: "auto", padding: "20px" }}>
      <h2>Email Verification</h2>
      {isAuth ? (
        userInfo ? (
          <>
            <p>Email: <b>{userInfo.email}</b></p>
            <p>Lütfen e-postanıza gönderilen doğrulama kodunu girin:</p>
            <input
              type="text"
              placeholder="Doğrulama kodunu girin"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              style={{ width: "100%", padding: "10px", marginBottom: "10px", border: "1px solid #ccc", borderRadius: "4px" }}
            />
            <button onClick={handleVerify} style={{ width: "100%", padding: "10px", backgroundColor: "#007BFF", color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer" }}>
              Doğrula
            </button>
            <p style={{ marginTop: "20px", color: "#555" }}>Kodun süresi dolmak üzere: <b>{countdown} saniye</b></p>
            <button
              onClick={sendVerificationCode}
              disabled={isResendDisabled}
              style={{ width: "100%", padding: "10px", backgroundColor: isResendDisabled ? "#ccc" : "#28A745", color: "#fff", border: "none", borderRadius: "4px", cursor: isResendDisabled ? "not-allowed" : "pointer", marginTop: "10px" }}
            >
              Tekrar Kod Gönder
            </button>
          </>
        ) : (
          <p>Kullanıcı bilgileri yükleniyor...</p>
        )
      ) : null}

      <ModalComponent isOpen={modalData.isOpen} onRequestClose={closeModal} message={modalData.message} type={modalData.type} />
    </div>
  );
};

export default EmailVerifyPage;
