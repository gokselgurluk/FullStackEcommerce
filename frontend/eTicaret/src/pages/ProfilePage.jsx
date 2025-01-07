import React, { useEffect, useState } from 'react';

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

      if (response.ok) {
        const data = await response.json();  // JSON formatında dönen veriyi alıyoruz
        setProfile(data.data);  // 'data' alanını alıyoruz
      } else {
        console.log('Error fetching profile');
      }
    };

    fetchProfile();
  }, []);

  return (
    <div>
      <h1>Profil Sayfası</h1>
      <div>
        {/* Profile verilerini düzgün şekilde göstermek için */}
        {profile && (
          <div>
            <p><strong>Kullanıcı ID:</strong> {profile.split("\n")[0]}</p>
            <p><strong>Ad:</strong> {profile.split("\n")[1]}</p>
            <p><strong>Rol:</strong> {profile.split("\n")[2]}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
