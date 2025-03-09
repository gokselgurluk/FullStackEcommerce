import React, { createContext, useContext, useState } from 'react';

const CartContext = createContext(); // CartContext'i oluştur

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);

  // Sepete ürün ekleme
  const addToCart = (item) => {
    setCart([...cart, item]);
  };

  // Sepetten ürün silme
  const removeFromCart = (id) => {
    setCart(cart.filter(item => item.id !== id));
  };

  // Sepet toplam fiyatı hesaplama
  const getTotal = () => {
    return cart.reduce((total, item) => total + item.price, 0);
  };

  return (
    <CartContext.Provider value={{ cart, addToCart, removeFromCart, getTotal }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext); // Context'i kullanmak için hook
