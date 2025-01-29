import React from "react";
import { useAuth } from "../context/AuthContext"; // AuthContext'ten isAuth'i alıyoruz
import { Link } from "react-router-dom";
import { Navbar, Nav, Container, Button } from "react-bootstrap";
import { BsPersonCircle } from "react-icons/bs";
import { FaSignOutAlt } from 'react-icons/fa';
import { FaSignInAlt } from 'react-icons/fa';

const Navbars = () => {
  const { isAuth, logout } = useAuth(); // isAuth'i ve logout fonksiyonunu alıyoruz

  return (
    <Navbar bg="dark" variant="dark">
      <Container>
        <Navbar.Brand href="/">E-Ticaret</Navbar.Brand>
        <Nav className="me-auto">
          <Nav.Link href="/">Home</Nav.Link>
        </Nav>
        <Nav className="ms-auto" style={{ gap: "10px" }}>
          {isAuth ? (
            <>
              {/* Giriş yaptıysa Profile linki görünür */}
              <Nav.Link as={Link} to="/profile" className="profile-link">
                <BsPersonCircle style={{ fontSize: "24px" }} />
              </Nav.Link>
              <Nav.Link as={Link} to="/Info" className="/Info-link">
                <BsPersonCircle style={{ fontSize: "24px" }} />
              </Nav.Link>
              <Button
                variant="outline-light"
                style={{ display: 'flex', alignItems: 'center', gap: '5px' }}
                onClick={logout}
              >
                <FaSignOutAlt />
                Logout
              </Button>
            </>
          ) : (
            <Nav.Link as={Link} to="/login" className="login-link">
              <Button
                variant="outline-light"
                style={{ display: 'flex', alignItems: 'center', gap: '5px' }}
              >
                <FaSignInAlt />
                Login
              </Button>
            </Nav.Link>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Navbars;
