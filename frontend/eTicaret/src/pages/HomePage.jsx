import React, { useState, useEffect } from 'react';

const HomePage = () => {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const fetchUsers = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                console.error('Token bulunamadı!');
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/api/users', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (response.status === 403) {
                    console.error('Erişim reddedildi!');
                    return;
                }

                const data = await response.json();
                if (data.status === 'success') {
                    setUsers(data.users); // Kullanıcıları state'e set ediyoruz
                }
            } catch (error) {
                console.error('Veri çekme hatası:', error);
            }
        };

        fetchUsers();
    }, []);

    return (
        <div>
            <h1>Kullanıcılar</h1>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Ad</th>
                        <th>Soyad</th>
                        <th>Email</th>
                        <th>Rol</th>
                    </tr>
                </thead>
                <tbody>
                    {users.length > 0 ? (
                        users.map(user => (
                            <tr key={user.id}>
                                <td>{user.id}</td>
                                <td>{user.username}</td>
                                <td>{user.surname}</td>
                                <td>{user.email}</td>
                                <td>{user.roleEnum}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="5">Kullanıcı bulunamadı.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default HomePage;
