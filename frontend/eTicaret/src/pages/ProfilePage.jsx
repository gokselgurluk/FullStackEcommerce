

import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance"; // axios instance
import { Table, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext"; // AuthContext kullanımı

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [sessions, setSessions] = useState([]); // Yeni state: Kullanıcının aktif oturumları
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // Yeni access token almak için fonksiyon
  const refreshAccessToken = async () => {
    try {
      const response = await axiosInstance.post("/auth/refresh-token", {
        email: profile?.email, // Giriş yapan kullanıcının email'i
      });
      const newAccessToken =
        response.data.accessToken || response.headers["new-access-token"];

      if (newAccessToken) {
        localStorage.setItem("accessToken", newAccessToken);
        return newAccessToken;
      } else {
        throw new Error("Yeni access token alınamadı.");
      }
    } catch (error) {
      console.error("Access token yenileme hatası:", error);
      throw error;
    }
  };

  // Profil bilgisini çek
  const fetchProfile = async () => {
    const token = localStorage.getItem("accessToken");

    if (!token) {
      setError("Access token is missing. Please log in.");
      setLoading(false);
      return;
    }

    try {
      const response = await axiosInstance.get("/api/users/profile", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 200) {
        setProfile(response.data.data);
        fetchSessionInfo(); // Kullanıcı oturum bilgilerini getir
      } else {
        throw new Error("Failed to fetch profile.");
      }
    } catch (error) {
      if (error.response?.status === 403) {
        try {
          const newAccessToken = await refreshAccessToken();
          const retryResponse = await axiosInstance.get("/api/users/profile", {
            headers: {
              Authorization: `Bearer ${newAccessToken}`,
            },
          });

          if (retryResponse.status === 200) {
            setProfile(retryResponse.data.data);
            fetchSessionInfo(); // Yeni token ile sessionları getir
          } else {
            throw new Error("Failed to fetch profile after token refresh.");
          }
        } catch (refreshError) {
          setError("Session expired. Please log in again.");
          navigate("/login");
        }
      } else {
        setError("An error occurred while fetching the profile.");
        navigate("/email-verify");
      }
    } finally {
      setLoading(false);
    }
  };

  // Kullanıcının aktif oturumlarını çek
  const fetchSessionInfo = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      const response = await axiosInstance.get("/api/users/sessionInfo", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 200 && response.data.status) {
        setSessions(response.data.data);
      }
    } catch (error) {
      console.error("Oturum bilgileri alınırken hata:", error);
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
        <>
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
                <td>{profile.active ? "Active" : "Inactive"}</td>
              </tr>
              
            </tbody>
          </Table>

          <h3 className="mt-4">Active Sessions</h3>
          {sessions.length > 0 ? (
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>IP Address</th>
                  <th>Browser</th>
                  <th>OS</th>
                  <th>Device</th>
                  <th>Created At</th>
                  <th>Expires At</th>
                  <th>Oturumu Yönet</th>
                </tr>
              </thead>
              <tbody>
                {sessions.map((session) => (
                  <tr key={session.id}>
                    <td>{session.id}</td>
                    <td>{session.ipAddress}</td>
                    <td>{session.browser}</td>
                    <td>{session.os}</td>
                    <td>{session.device}</td>
                    <td>{new Date(session.createdAt).toLocaleString()}</td>
                    <td>{new Date(session.expiresAt).toLocaleString()}</td>
                <td><button style={{borderRadius:"10px"}}>Kaldır</button></td>
             
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div>No active sessions available.</div>
          )}
        </>
      ) : (
        <div>No profile data available.</div>
      )}
    </Container>
  );
};

export default ProfilePage;
