// src/components/HomePage.js
import React from 'react';
import { Container, Row, Col, Card, Button, Navbar, Nav, NavDropdown } from 'react-bootstrap';

const products = [
  {
    id: 1,
    name: 'Product 1',
    description: 'Description of Product 1',
    price: '$10.00',
    imageUrl: 'https://via.placeholder.com/150'
  },
  {
    id: 2,
    name: 'Product 2',
    description: 'Description of Product 2',
    price: '$20.00',
    imageUrl: 'https://via.placeholder.com/150'
  },
  {
    id: 3,
    name: 'Product 3',
    description: 'Description of Product 3',
    price: '$30.00',
    imageUrl: 'https://via.placeholder.com/150'
  }
];

const HomePage = () => {
  return (
    <div>
    

      {/* Product List */}
      <Container className="my-4">
        <Row>
          {products.map((product) => (
            <Col key={product.id} sm={12} md={6} lg={4}>
              <Card className="mb-4">
                <Card.Img variant="top" src={product.imageUrl} />
                <Card.Body>
                  <Card.Title>{product.name}</Card.Title>
                  <Card.Text>{product.description}</Card.Text>
                  <Card.Text>
                    <strong>{product.price}</strong>
                  </Card.Text>
                  <Button variant="primary">Add to Cart</Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>

      {/* Footer */}
      <footer className="bg-dark text-white text-center py-3">
        <p>&copy; 2025 E-Shop. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default HomePage;
