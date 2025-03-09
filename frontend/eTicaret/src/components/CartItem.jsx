import React from 'react';
import { BsTrash } from 'react-icons/bs';
import { useCart } from '../context/CartContext';

const CartItem = ({ item }) => {
  const { removeFromCart } = useCart();

  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px' }}>
      <div>
        <h4>{item.name}</h4>
        <p>${item.price}</p>
      </div>
      <button onClick={() => removeFromCart(item.id)}>
        <BsTrash />
      </button>
    </div>
  );
};

export default CartItem;
