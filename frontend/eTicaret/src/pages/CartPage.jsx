import React from 'react';
import { useCart } from '../context/CartContext'; // Sepet verilerini almak için context'i import et
import CartItem from '../components/CartItem'; // Sepet öğesi bileşeni

const CartPage = () => {
  const { cart, getTotal } = useCart(); // CartContext'ten sepet verilerini al

  return (
    <div>
      <h1>Your Cart</h1>
      {cart.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        cart.map(item => <CartItem key={item.id} item={item} />) // Sepetteki öğeleri listele
      )}
      <h3>Total: ${getTotal()}</h3> {/* Sepet toplamını göster */}
    </div>
  );
};

export default CartPage;
