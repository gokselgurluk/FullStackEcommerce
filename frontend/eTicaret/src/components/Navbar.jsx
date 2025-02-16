import React from "react";
import { useAuth } from "../context/AuthContext"; // AuthContext'ten isAuth'i alıyoruz
import { Link } from "react-router-dom";
import { Navbar, Nav, Container, Button, NavDropdown } from "react-bootstrap"; // NavDropdown eklendi
import { BsPersonCircle, BsCart } from "react-icons/bs";
import { FaSignOutAlt, FaSignInAlt } from "react-icons/fa";

const Navbars = () => {
  const { isAuth, logout } = useAuth(); // isAuth'i ve logout fonksiyonunu alıyoruz

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        {/* Navbar */}
        <Navbar.Brand as={Link} to="/">E-Shop</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">Home</Nav.Link>
            <Nav.Link as={Link} to="/shop">Shop</Nav.Link>
            <NavDropdown title="Categories" id="basic-nav-dropdown">
              <NavDropdown.Item as={Link} to="/electronics">Electronics</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/clothing">Clothing</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/accessories">Accessories</NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </Navbar.Collapse>

        <Nav className="ms-auto" style={{ gap: "10px" }}>
          {isAuth ? (
            <>
              {/* Giriş yaptıysa Profile linki görünür */}
              <Nav.Link as={Link} to="/profile" className="profile-link">
                <BsPersonCircle style={{ fontSize: "24px" }} />
              </Nav.Link>
              <Nav.Link as={Link} to="/cart" className="info-link">
                <BsCart style={{ fontSize: "24px" }} />
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
            <Button as={Link} to="/login" variant="outline-light" style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
              <FaSignInAlt />
              Login
            </Button>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Navbars;
