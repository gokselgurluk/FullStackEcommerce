import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom"; // SearchParams için import
import axiosInstance from "../api/axiosInstance"; // Axios Instance kullanıyoruz
import ModalComponent from "../components/ModalComponent"; // Modal ile mesaj gösteriyoruz
import { useNavigate } from "react-router-dom";
const OtpVerify = () => {
  const [searchParams] = useSearchParams();
  const otpFromUrl = searchParams.get("code") || ""; // URL'den kodu al
  const emailFromUrl = searchParams.get("email") || ""; // URL’den email’i al
  const [codeValue, setCodeValue] = useState(["", "", "", "", "", ""]);
  const [message, setMessage] = useState(""); // Hata veya başarı mesajları için state
 const [modalData, setModalData] = useState({ isOpen: false, message: "", type: "" });
 const navigate = useNavigate();
  useEffect(() => {
    if (otpFromUrl.length === 6) {
      setCodeValue(otpFromUrl.split("")); // URL'deki kodu kutulara bölerek yerleştir
    }
  }, [otpFromUrl]);
  const closeModal = (type) => {
    setModalData({ isOpen: false, message: "", type: "" });

    // Modal kapandıktan sonra yönlendirme işlemi
    setTimeout(() => {
      if (type === "success") {
        navigate("/login"); // Kullanıcıyı çıkış yapmaya yönlendir
      }

      if (type === "warning") {
        navigate("/otp-verify");
      }

      if (type === "error") {
        navigate("/login");
      }
    }, 100);
  };

  const showModal = (message, type) => {
    setModalData({ isOpen: true, message, type });
  };

  const handleChange = (index, value) => {
      if (!/^[a-zA-Z0-9]?$/.test(value)) return; // Sadece harf ve rakam izin ver
      const newCodeValue = [...codeValue];
      newCodeValue[index] = value.toUpperCase(); // Küçük harf girilse bile büyüt
      setCodeValue(newCodeValue);
// Eğer bir sonraki kutu varsa, oraya otomatik geçiş yap
if (value && index < codeValue.length - 1) {
  document.getElementById(`otp-${index + 1}`).focus();
}
    // const newOtp = [...codeValue];
    // newOtp[index] = value;
    // setCodeValue(newOtp);
  };

  const handleVerify = async (event) => {
    event.preventDefault(); // Sayfanın yenilenmesini engelle

    try {
        const response = await axiosInstance.post("auth/otp-verification", {
        codeValue: codeValue.join(""), // JSON formatında gönderiyoruz
          email: emailFromUrl,
        });
        console.log(response);
      
        if(response.data.status === true)  {
          showModal("Doğrulama başarılı! Giriş yapabilirsiniz.","success");
        setTimeout(() => {
          navigate("/login");
        }, 500);
      }else{
        showModal(response.data.message||"Doğrulama başarırısız! ","error")
      }
      } catch (error) {
        if(error.response.status === 403)  {
          showModal(error.response?.data?.message||"Baglantı süresi dolmuş veya geçersiz","warning");
        }
        if(error.response.status === 404){
          showModal(error.response?.data?.message||"Kayıt bulunamadı","error");
        }
        
        else{
        setMessage("❌ " + ("Bilinmeyen bir hata ile karşılaşıldı"));
      }
      }
    };

  return (
    <main className="main">
      <div className="form-container">
        <form className="login-form" onSubmit={handleVerify} style={{ maxWidth: "50%" }}>
          <h4 className="form-title">OTP Doğrulama</h4>
          <div className="input-otp-wrapper">
  {codeValue.map((digit, index) => (
    <input
      className="input-otp"
      key={index}
      id={`otp-${index}`} // ID ekledik, focus için
      type="text"
      maxLength={1}
      value={digit}
      onChange={(e) => handleChange(index, e.target.value)}
    />
  ))}
</div>
          <button className="login-button" type="submit" style={{ maxWidth: "200px", marginTop: "15px" }}>
            Doğrula
          </button>
          {message && <p style={{ marginTop: "10px", color: message.startsWith("✅") ? "green" : "red" }}>{message}</p>}
        </form>
        
        <ModalComponent
          isOpen={modalData.isOpen}
          onRequestClose={closeModal}
          message={modalData.message || ""}
          type={modalData.type}
        />
      </div>
    </main>
  );
};

export default OtpVerify;
