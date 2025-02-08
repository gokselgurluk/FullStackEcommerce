import React, { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance"; // axios instance kullanımı

const UserSessionInfo = () => {
  const [userInfo, setUserInfo] = useState(null); // Kullanıcı bilgilerini tutacak state
  const accessToken = localStorage.getItem("accessToken"); // accessToken'ı localStorage'dan al

  // Kullanıcı bilgilerini almak için useEffect
  useEffect(() => {
    if (accessToken) {
      const fetchUserInfo = async () => {
        try {
          const response = await axiosInstance.get('/api/users/sessionInfo', {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          });

          if (response.status === 200) {
            setUserInfo(response.data.data); // Kullanıcı bilgilerini state'e kaydet
            console.log('Fetched User Info:', response.data.data);
          } else {
            console.error("Failed to fetch user info.");
          }
        } catch (error) {
          console.error("Error fetching user info:", error);
        }
      };

      fetchUserInfo();
    } else {
      console.log("No access token found.");
    }
  }, [accessToken]);

  return (
    <div style={{ maxWidth: "400px", margin: "auto", padding: "20px" }}>
      <h2>User Info</h2>
      {userInfo ? (
        <div>
          <p>Email: {userInfo.email}</p>
          <p>Active:{userInfo.active ? 'Active' : 'Inactive'}</p>
          {/* Diğer kullanıcı bilgileri buraya eklenebilir */}
        </div>
      ) : (
        <p>Kullanıcı bilgileri yükleniyor...</p>
      )}
    </div>
  );
};

export default UserSessionInfo;
