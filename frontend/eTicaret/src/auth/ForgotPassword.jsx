import React, { useState } from 'react';
import axiosInstance from "../api/axiosInstance";
import { XSquare, Loader } from "lucide-react";

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };
  const clearEmail = () => {
    setFormData({ email: "" });
  };
  const handleSubmit = async (e) => {
    e.preventDefault(); // Önce engelle
    setLoading(true);

    try {
      const response = await axiosInstance.post('/api/forgot-password', { email });
      console.log(response);
      if (response.data.status === true) {
        setMessage("✅ Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.");
        setError("");
      } else {
        setError("❌" + (response?.data?.message || "Şifre sıfırlama bağlantısı gönderilemedi."));
        setMessage("");
      }
    } catch (error) {
      setError("❌ " + (error.response?.data?.message || "Bir hata oluştu, lütfen tekrar deneyin."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="main">
      <div className="form-container">
        <form className="form" onSubmit={handleSubmit}>
          <h4 className="form-title">Şifremi Unuttum</h4>
          <div>
            <div className="input-wrapper">
              <input className='input-field'
                type="email"
                value={email}
                onChange={handleEmailChange}
                placeholder="E-posta adresinizi girin"
                required
              />
              {email && (
        <XSquare className="mail-XSquare" onClick={clearEmail} />
      )}
            </div>
          </div>
          <button type="submit" className="login-button" disabled={loading} style={{ maxWidth: "200px" }}>
            {loading ? <Loader className="spinner" size={20} /> : "Kod Gönder"}
          </button>

          {message && <p style={{ color: "green" }}>{message}</p>}
          {error && <p style={{ color: "red" }}>{error}</p>}
        </form>
      </div>
    </main>
  );
};

export default ForgotPassword;