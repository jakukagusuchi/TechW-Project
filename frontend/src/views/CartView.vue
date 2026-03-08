<script setup>
import { ref } from 'vue';

// Mock cart items for initial view
const cartItems = ref([
  { id: 1, name: 'MacBook Pro M3', price: 1999, quantity: 1, image: 'https://via.placeholder.com/100' },
  { id: 2, name: 'Wireless Headphones', price: 299, quantity: 2, image: 'https://via.placeholder.com/100' }
]);

const subtotal = ref(cartItems.value.reduce((acc, item) => acc + (item.price * item.quantity), 0));

function removeItem(id) {
  cartItems.value = cartItems.value.filter(item => item.id !== id);
}
</script>

<template>
  <div class="container">
    <h1>Your Shopping Cart</h1>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <p>Your cart is empty.</p>
      <RouterLink to="/products" class="btn-primary text-center">Start Shopping</RouterLink>
    </div>

    <div v-else class="cart-layout">
      <div class="cart-items">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <img :src="item.image" alt="Product" />
          <div class="item-info">
            <h3>{{ item.name }}</h3>
            <p class="unit-price">${{ item.price }}</p>
          </div>
          <div class="item-quantity">
            <button @click="item.quantity > 1 && item.quantity--">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="item.quantity++">+</button>
          </div>
          <div class="item-total">${{ item.price * item.quantity }}</div>
          <button @click="removeItem(item.id)" class="btn-remove">×</button>
        </div>
      </div>

      <div class="cart-summary">
        <h3>Order Summary</h3>
        <div class="summary-row">
          <span>Subtotal</span>
          <span>${{ subtotal }}</span>
        </div>
        <div class="summary-row">
          <span>Shipping</span>
          <span class="free">FREE</span>
        </div>
        <div class="summary-row total">
          <span>Total</span>
          <span>${{ subtotal }}</span>
        </div>
        <RouterLink to="/checkout" class="btn-primary w-full text-center block">Proceed to Checkout</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
h1 { margin-bottom: 3rem; }
.cart-layout { display: grid; grid-template-columns: 1fr 350px; gap: 3rem; align-items: start; }
.cart-items { display: flex; flex-direction: column; gap: 1.5rem; }
.cart-item { display: grid; grid-template-columns: 100px 1fr auto auto auto; align-items: center; gap: 2rem; background: var(--surface); padding: 1.5rem; border-radius: 16px; border: 1px solid var(--border); }
.cart-item img { width: 100px; height: 100px; border-radius: 8px; border: 1px solid var(--border); }
.item-info h3 { font-size: 1.1rem; margin-bottom: 0.25rem; }
.unit-price { color: var(--text-muted); font-size: 0.9rem; }
.item-quantity { display: flex; align-items: center; background: var(--bg-dark); border-radius: 8px; border: 1px solid var(--border); padding: 5px; }
.item-quantity button { background: transparent; color: white; padding: 5px 10px; font-size: 1.2rem; }
.item-quantity span { padding: 0 10px; min-width: 30px; text-align: center; }
.item-total { font-weight: 700; font-size: 1.1rem; color: var(--accent); min-width: 80px; text-align: right; }
.btn-remove { background: transparent; color: #ef4444; font-size: 1.5rem; padding: 0 15px; }

.cart-summary { background: var(--surface); padding: 2rem; border-radius: 16px; border: 1px solid var(--border); }
.summary-row { display: flex; justify-content: space-between; margin-bottom: 1rem; color: var(--text-muted); }
.total { border-top: 1px solid var(--border); padding-top: 1.5rem; margin-top: 1.5rem; color: var(--text-light); font-size: 1.25rem; font-weight: 800; }
.free { color: #22c55e; font-weight: 700; }
.block { display: block; }
.text-center { text-align: center; }
.w-full { width: 100%; }
.empty-cart { text-align: center; padding: 6rem 0; color: var(--text-muted); }
</style>
