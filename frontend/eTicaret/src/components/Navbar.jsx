import React from 'react';
import { useAuth } from '../AuthContext'; // AuthContext'ten isAuth'i alıyoruz

import { Link } from 'react-router-dom';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';


const Navbars = () => {
  const { isAuth, logout } = useAuth(); // isAuth'i kullanıyoruz

  return (
    <Navbar bg="dark" variant="dark">
      <Container>
        <Navbar.Brand href="/">Navbar</Navbar.Brand>
        <Nav className="me-auto"> {/* Sol tarafta hizalamayı sağlamak için me-auto kullanıyoruz */}
          <Nav.Link href="/">Home</Nav.Link>
        </Nav>
        <Nav className="ms-auto"> {/* Sağ tarafta hizalamayı sağlamak için ms-auto kullanıyoruz */}
          {isAuth ? (
            <Button variant="outline-light" onClick={logout}>
              Logout
            </Button>
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
