import React from "react";
import { Container, Navbar, Nav, Form, FormControl, Button, Row, Col, Card } from "react-bootstrap";

const HomePage = () => {
  return (
    <>


      {/* Ana Banner */}
      <Container className="mt-4">
        <Row>
          <Col>
            <Card className="bg-dark text-white">
              <Card.Img src="https://via.placeholder.com/1200x400" alt="Kampanya" />
              <Card.ImgOverlay>
                <Card.Title>Büyük İndirimler!</Card.Title>
                <Card.Text>Sezonun en iyi fırsatlarını kaçırmayın.</Card.Text>
                <Button variant="light">Alışverişe Başla</Button>
              </Card.ImgOverlay>
            </Card>
          </Col>
        </Row>
      </Container>

      {/* Öne Çıkan Ürünler */}
      <Container className="mt-5">
        <h2 className="text-center">Öne Çıkan Ürünler</h2>
        <Row>
          {[1, 2, 3, 4].map((item) => (
            <Col key={item} md={3} className="mb-4">
              <Card>
                <Card.Img variant="top" src="https://via.placeholder.com/200" />
                <Card.Body>
                  <Card.Title>Ürün {item}</Card.Title>
                  <Card.Text>Bu harika ürünü şimdi satın alın.</Card.Text>
                  <Button variant="primary">Sepete Ekle</Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>

      {/* Footer */}
      <footer className="bg-dark text-white text-center p-4 mt-5">
        <p>&copy; 2025 E-Ticaret. Tüm hakları saklıdır.</p>
      </footer>
    </>
  );
};

export default HomePage;
