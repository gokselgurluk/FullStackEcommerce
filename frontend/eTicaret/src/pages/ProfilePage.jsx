import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance'; // axios instance kullanımı
import { Table, Container } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // AuthContext kullanımı
const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // Yeni access token almak için fonksiyon
  const refreshAccessToken = async () => {
    try {
      const response = await axiosInstance.post('/auth/refresh-token', { email: userEmail }); // Giriş yapan kullanıcının email'i
      const newAccessToken = response.data.accessToken || response.headers['new-access-token'];
  
      if (newAccessToken) {
        localStorage.setItem('accessToken', newAccessToken);
        return newAccessToken;
      } else {
        throw new Error('Yeni access token alınamadı.');
      }
    } catch (error) {
      console.error('Access token yenileme hatası:', error);
      throw error;
    }
  };

  const fetchProfile = async () => {
    const token = localStorage.getItem('accessToken'); // Access token'ı al

    if (!token) {
      setError('Access token is missing. Please log in.');
      setLoading(false);
      return;
    }

    try {
      const response = await axiosInstance.get('/api/users/profile', {
        headers: {
          Authorization: `Bearer ${token}`, // Access token'ı header'a ekle
        },
      });

      if (response.status === 200) {
        setProfile(response.data.data); // Profil verilerini güncelle
      } else {
        throw new Error('Failed to fetch profile.');
      }
    } catch (error) {
      if (error.response?.status === 403) {
        // Access token süresi dolmuş
        try {
          const newAccessToken = await refreshAccessToken(); // Yeni access token al
          // Yeni token ile tekrar dene
          const retryResponse = await axiosInstance.get('/api/users/profile', {
            headers: {
              Authorization: `Bearer ${newAccessToken}`,
            },
          });

          if (retryResponse.status === 200) {
            setProfile(retryResponse.data.data);
          } else {
            throw new Error('Failed to fetch profile after token refresh.');
          }
        } catch (refreshError) {
          setError('Session expired. Please log in again.');
          navigate('/login'); // Kullanıcıyı giriş ekranına yönlendir
        }
      } else {
        setError('An error occurred while fetching the profile.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <Container className="mt-4">
      <h3>User Profile</h3>
      {profile ? (
        <Table striped bordered hover>
          <thead>
            <tr>
              <th>Field</th>
              <th>Value</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>ID</td>
              <td>{profile.id}</td>
            </tr>
            <tr>
              <td>Username</td>
              <td>{profile.username}</td>
            </tr>
            <tr>
              <td>Surname</td>
              <td>{profile.surname}</td>
            </tr>
            <tr>
              <td>Email</td>
              <td>{profile.email}</td>
            </tr>
            <tr>
              <td>Role</td>
              <td>{profile.roleEnum}</td>
            </tr>
            <tr>
              <td>Created At</td>
              <td>{new Date(profile.createdAt).toLocaleString()}</td>
            </tr>
            <tr>
              <td>Last Login</td>
              <td>{new Date(profile.lastLogin).toLocaleString()}</td>
            </tr>
            <tr>
              <td>Status</td>
              <td>{profile.active ? 'Active' : 'Inactive'}</td>
            </tr>
          </tbody>
        </Table>
      ) : (
        <div>No profile data available.</div>
      )}
    </Container>
  );
};

export default ProfilePage;
