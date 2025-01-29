// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext'; // Import AuthProvider
import PrivateRoute from './components/PrivateRoute'; // Import PrivateRoute
import HomePage from './pages/HomePage'; // Your HomePage component
import LoginPage from './pages/LoginPage'; // Your LoginPage component
import ProfilePage from './pages/ProfilePage'; // Your ProfilePage component
import RegisterPage from './pages/RegisterPage'; 
import EmailVerifyPage from './pages/EmailVerifyPage';

import NavbarComponent from './components/Navbar'
// src/index.js or src/main.js
import 'bootstrap/dist/css/bootstrap.min.css';
import UserInfoPage from './pages/UserInfoPage';

const App = () => {
  return (
    <AuthProvider> {/* AuthProvider wraps the app */}
      <Router> {/* Router wraps the routes */}
        <NavbarComponent/>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/Register" element={<RegisterPage />} />
        
          {/* Protect ProfilePage with PrivateRoute */}
          <Route path="/profile" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
          <Route path="/Info" element={<PrivateRoute><UserInfoPage /></PrivateRoute>} />
          <Route path="/email-verify" element={<EmailVerifyPage />} />

        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
