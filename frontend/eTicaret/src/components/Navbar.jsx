import React from 'react';
import { useAuth } from '../context/AuthContext'; // AuthContext'ten isAuth'i alıyoruz
import { Link } from 'react-router-dom';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';

const Navbars = () => {
  const { isAuth, logout } = useAuth(); // isAuth'i ve logout fonksiyonunu alıyoruz

  return (
    <Navbar bg="dark" variant="dark">
      <Container>
        <Navbar.Brand href="/">Navbar</Navbar.Brand>
        <Nav className="me-auto">
          <Nav.Link href="/">Home</Nav.Link>
        </Nav>
        <Nav className="ms-auto">
          {isAuth ? (
            <>
              {/* Giriş yaptıysa Profile linki görünür */}
              <Nav.Link as={Link} to="/profile" className="profile-link">
                Profile
              </Nav.Link>
              <Button variant="outline-light" onClick={logout}>
                Logout
              </Button>
            </>
          ) : (
            <Nav.Link as={Link} to="/login" className="login-link">
              Login
            </Nav.Link>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Navbars;
