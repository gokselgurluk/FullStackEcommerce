import React, { useEffect, useState } from 'react';
import axiosInstance from '../api/axiosInstance'; // axios instance kullanımı
import { Table, Container } from 'react-bootstrap'; // Bootstrap Table bileşeni

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
      const token = localStorage.getItem('accessToken'); // Get token from localStorage

      if (!token) {
        setError('Token is missing. Please log in.');
        setLoading(false);
        return;
      }

      try {
        const response = await axiosInstance.get('/api/users/profile', {
          headers: {
            Authorization: `Bearer ${token}`, // Add token to Authorization header
          },
        });

        // Log the response to check if data is coming
        console.log(response.data);

        if (response.status === 200) {
          // The profile data is inside response.data.data
          setProfile(response.data.data);
        } else {
          setError('Failed to fetch profile');
        }
      } catch (error) {
        console.error('Error fetching profile:', error);
        setError('An error occurred while fetching the profile.');
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []); // Run only once when the component is mounted

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
