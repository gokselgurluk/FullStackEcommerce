import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axiosInstance from '../api/axiosInstance';
import ModalComponent from '../components/ModalComponent';
import { FaGoogle, FaFacebook, FaApple } from "react-icons/fa"; // react-icons paketi
import { Eye, EyeOff, Mail } from "lucide-react";
import { Loader } from "lucide-react";
const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);  // Default checked
  const [loading, setLoading] = useState(false); // Yeni state
  const [modalData, setModalData] = useState({
    isOpen: false,
    message: '',
    type: '', // 'success', 'error', 'warning'
  });
  useEffect(() => {
    // localStorage'dan bilgileri al
    const storedEmail = localStorage.getItem("rememberedEmail");
    const storedPassword = localStorage.getItem("rememberedPassword");
    const storedRememberMe = localStorage.getItem("rememberMe") === "true";

    if (storedRememberMe && storedEmail && storedPassword) {
      setEmail(storedEmail);
      setPassword(storedPassword);
      setRememberMe(true); // Checkbox işaretli gelsin
      setFormData({ email: storedEmail, password: storedPassword });
    }
  }, []);
  const closeModal = () => {
    setModalData({ isOpen: false, message: "", type: "" });
    // Modal kapandıktan sonra yönlendirme işlemi
    if (modalData.type === "warning") {
      navigate("/email-verify"); // E-mail doğrulaması sayfasına yönlendir
    }
    if (modalData.type === "error") {
      navigate("/login"); // Hata durumu için login sayfasına yönlendir
    }
    if (modalData.type === "success") {
      navigate("/"); // Giriş başarılıysa dashboard'a yönlendir
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    if (name === "email") setEmail(value);
    if (name === "password") setPassword(value);
  };

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const handleRememberMeChange = (e) => {
    setRememberMe(e.target.checked);

  };

  const handleSubmit = async (e) => {

    e.preventDefault(); // Sayfa yenilenmesini engelle
    setLoading(true); // İşlem başladı, butonu devre dışı bırak
    if (rememberMe) {
      localStorage.setItem("rememberedEmail", email);
      localStorage.setItem("rememberedPassword", password);
      localStorage.setItem("rememberMe", "true");
    } else {
      localStorage.removeItem("rememberedEmail");
      localStorage.removeItem("rememberedPassword");
      localStorage.removeItem("rememberMe");
    }
    try {
      console.log("Base URL:", axiosInstance.defaults.baseURL);
      const response = await axiosInstance.post('/auth/login', formData);
    
      const userData = response.data;

      if (userData?.accessToken) {
        localStorage.setItem('accessToken', userData.accessToken);
        login(userData.accessToken);
        setModalData({
          isOpen: true,
          message: userData.active ? 'Giriş başarılı!' : 'Hesabınız aktif değil, e-mail doğrulaması yapınız.',
          type: userData.active ? 'success' : 'warning',
        });
      } else {
        setModalData({ isOpen: true, message: 'Giriş başarısız!', type: 'error' });
      }
    } catch (error) {
      console.log(error.response);
      if (error.response?.data === "Hesap Aktif Degil") {
        setModalData({
          isOpen: true,
          message: "Hesap Aktif Değil! E-mail doğrulaması gerekli.",
          type: 'warning',
        });
      } else {
        setModalData({
          isOpen: true,
          message: error.response?.data?.message +'\n'+error.response?.data?.data || 'Bir hata oluştu. Lütfen tekrar deneyin.',
          type: 'error',
        });
      }
    } finally {
      setLoading(false); // İşlem tamamlandı, butonu tekrar aktif hale getir
    }
  };
  return (
    <main className="login-container">
      <div className="login-left">
        <div className="logo-cart"></div>

        {/* Giriş Formu */}
        <form className="login-form" onSubmit={handleSubmit}>
          <h2 className="form-title-text">Zaten bir hesabınız var mı ?</h2>
          {/* Sosyal Medya Butonları */}
          <div className="social-login">
            <button className="social-btn google"><FaGoogle size={20} /> </button>
            <button className="social-btn facebook"><FaFacebook size={20} /> </button>
            <button className="social-btn apple"><FaApple size={20} /> </button>
          </div>
          {/* Ayırıcı Çizgi */}
          <div className="divider">
            <span>veya</span>
          </div>

          <div className="input-wrapper">
            <input
              type="email"
              name="email"
              className="input-field"
              placeholder="Email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
            <Mail className="mail-toggle" />
          </div>

          <div className="input-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              className="input-field"
              placeholder="Password"
              value={formData.password}
              onChange={handleInputChange}
              required
            />
            <button className="password-toggle" tabIndex="-1" type="button" onClick={togglePasswordVisibility} >
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>

          <div className="cointainer">
            <a className="forget-password" href="/forget-password">Şifremi unuttum</a>

            <div className="remember-me-container">
              <input
                type="checkbox"
                className="remember-me-checkbox"
                id="rememberMe"
                checked={rememberMe}
                onChange={handleRememberMeChange}
              />
              <label htmlFor="rememberMe" className="remember-me-label">Beni Hatırla</label>

            </div>
          </div>
          <button type="submit" className="login-button" disabled={loading}>
            {loading ? <Loader className="spinner" size={20} /> : "Giriş Yap"}
          </button>

          <div className="signup-container">
            <div className="signup-text">
              Bir hesabınız yok mu ?<a href="/register" className="signup-link">Hesap Oluştur</a>
            </div>
          </div>
        </form>
      </div>

      <ModalComponent
        isOpen={modalData.isOpen}
        onRequestClose={closeModal}
        message={modalData.message}
        type={modalData.type}
      />
    </main>
  );
};

export default LoginPage;
