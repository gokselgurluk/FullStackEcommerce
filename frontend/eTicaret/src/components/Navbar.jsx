import React from "react";
import { useAuth } from "../context/AuthContext"; // AuthContext'ten isAuth'i alıyoruz
import { Link } from "react-router-dom";
import { Container, Navbar, Nav, Form, FormControl, Button, Row, Col, Card, NavDropdown } from "react-bootstrap";
import { BsPersonCircle, BsCart } from "react-icons/bs";
import { FaSignOutAlt, FaSignInAlt } from "react-icons/fa";
import { Search } from "lucide-react";

const Navbars = () => {
  const { isAuth, logout } = useAuth(); // isAuth'i ve logout fonksiyonunu alıyoruz

  return (
    <Navbar bg="dark" variant="dark" expand="lg">
      <Container>
        {/* Navbar */}
        <Navbar.Brand as={Link} to="/">E-Ticaret</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">Anasayfa</Nav.Link>
            <Nav.Link as={Link} to="/shop">Magaza</Nav.Link>
            <NavDropdown title="Kategoriler" id="basic-nav-dropdown">
              <NavDropdown.Item as={Link} to="/electronics">Elektronik</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/accessories">Ev & Yaşam</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/clothing">Moda & Giyim</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/accessories">Hobi & Eğlence</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/accessories">Süpermarket & Gıda</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/accessories">Kişisel Bakım & Kozmetik</NavDropdown.Item>
            </NavDropdown>
            <Nav.Link href="#deals">Kampanyalar</Nav.Link>
            <Form className="search-container">
              <Search className="search-icon"  />
              <FormControl
                type="search"
                placeholder=""
                className="search-input"
              />
            </Form>
          </Nav>
          <Nav>

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
                Çıkış
              </Button>
            </>
          ) : (
            <Button as={Link} to="/login" variant="outline-light" style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
              <FaSignInAlt />
              Giriş
            </Button>
          )}
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Navbars;
