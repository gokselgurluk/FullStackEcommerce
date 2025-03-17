import React, { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance"; // Axios Instance kullanıyoruz
import ModalComponent from "../components/ModalComponent"; // ModalComponent'ı kullanıyoruz
import { Eye, EyeOff } from "lucide-react";
const ResetPassword = () => {
   
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [modal, setModal] = useState({ open: false, message: "", type: "" });
    const [showPassword, setShowPassword] = useState(false);
    const [searchParams] = useSearchParams();
    const resetPasswordToken = searchParams.get("token");  // URL’den token’ı al
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.,])[A-Za-z\d@$!%*?&.,]{8,}$/;
    
    // Modal durumu ve mesajları
    const [modalData, setModalData] = useState({
        isOpen: false,
        message: '',
        type: '', // 'success' veya 'error'
    });
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
    
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");
      // 🔴 Şifreler eşleşiyor mu kontrolü
      if (password !== confirmPassword) {
        setError("❌ " +"Şifreler uyuşmuyor !");
        return;
    }
      // 🔴 Şifre regex kontrolü
      if (!passwordRegex.test(password)) {
        setError(
            <>
              ❌ Şifre Format Hatası<br />
              Şifreniz en az 8 karakter<br />
              Bir büyük harf<br />
              Bir küçük harf<br />
              Bir rakam<br />
              Bir özel karakter içermelidir!
            </>
          );
                  return;
    }
        
        try {
            // Backend'e POST isteği at
            const response = await axiosInstance.post("/auth/reset-password", {
                password,
                confirmPassword,
                resetPasswordToken
            });
           if(response.status==200){
            showModal(response?.data+"  "+response?.data?.message, "success");
           }else{
            showModal(response.data.message,"warning")
           }
      
        } catch (error) {
            console.log("Backend yanıtı:", error.response.data);
            if(error.response.status === 400){
                showModal(error.response.data.message+"baglantınızın süresi dolmuş", "error");
            }else{
                showModal(error.response.message);
            }
           
            
         
        }
    };
    const togglePasswordVisibility = () => {
        setShowPassword((prev) => !prev);
      };
    

    return (
        <main className="main">
        <div className="form-container">
          <form className="form" onSubmit={handleSubmit}>
                    <h4 className="form-title">Şifre Sıfırla</h4>
                    <div>
                        <div className="input-wrapper">
                            <input
                                className="input-field"
                                type={showPassword ? "text" : "password"}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Yeni Şifre"
                                required
                            />
                            <button className="password-toggle" type="button" onClick={togglePasswordVisibility} tabIndex={-1}>
                                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                            </button>
                           
                        </div>
                    
                        <div className="input-wrapper">
                            <input
                                className="input-field"
                                type={showPassword ? "text" : "password"}
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Şifreyi Onayla"
                                required
                            />
                             <button className="password-toggle" type="button" onClick={togglePasswordVisibility}tabIndex={-1}>
                            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                        </div>
                        <div>
                        {error && <p style={{ color: "red" }}>{error}</p>}
                        </div>
                    </div>
                    <button className="login-button" type="submit" style={{maxWidth: "200px"}}>Şifreyi Sıfırla</button>
                </form>

                {/* Modal Bileşeni */}
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

export default ResetPassword;
