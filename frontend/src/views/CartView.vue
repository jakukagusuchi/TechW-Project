<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import api from '../services/api';

const router = useRouter();
const authStore = useAuthStore();

const cart = ref(null);
const loading = ref(true);
const error = ref(null);
const checkingOut = ref(false);

const cartItems = computed(() => cart.value?.items || []);
const subtotal = computed(() =>
  cartItems.value.reduce((acc, item) => acc + item.productPrice * item.quantity, 0)
);

async function fetchCart() {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  loading.value = true;
  error.value = null;
  try {
    const { data } = await api.get('/api/cart');
    cart.value = data;
  } catch (err) {
    error.value = 'Failed to load cart. Please try again.';
  } finally {
    loading.value = false;
  }
}

async function updateQuantity(itemId, quantity) {
  if (quantity < 1) return;
  try {
    const { data } = await api.put(`/api/cart/items/${itemId}?quantity=${quantity}`);
    cart.value = data;
  } catch (err) {
    error.value = 'Failed to update quantity.';
  }
}

async function removeItem(itemId) {
  try {
    const { data } = await api.delete(`/api/cart/items/${itemId}`);
    cart.value = data;
  } catch (err) {
    error.value = 'Failed to remove item.';
  }
}

async function clearCart() {
  try {
    await api.delete('/api/cart');
    cart.value = null;
    await fetchCart();
  } catch (err) {
    error.value = 'Failed to clear cart.';
  }
}

async function checkout() {
  router.push('/checkout');
}

onMounted(() => fetchCart());
</script>

<template>
  <div class="container cart-page">
    <div class="page-header">
      <h1 class="page-title">Shopping Cart</h1>
      <p class="page-subtitle" v-if="cartItems.length > 0">You have {{ cartItems.length }} item{{ cartItems.length !== 1 ? 's' : '' }} in your cart</p>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Loading your cart...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
      <p>{{ error }}</p>
      <button @click="fetchCart" class="btn-primary mt-4">Retry</button>
    </div>

    <div v-else-if="cartItems.length === 0" class="empty-state">
      <div class="empty-icon-box">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 9m13-9l2 9M9 21a1 1 0 100-2 1 1 0 000 2zm10 0a1 1 0 100-2 1 1 0 000 2z"/></svg>
      </div>
      <h2>Your cart is empty</h2>
      <p>Looks like you haven't added anything to your cart yet.</p>
      <RouterLink to="/products" class="btn-primary mt-4">Explore Products</RouterLink>
    </div>

    <div v-else class="cart-layout">
      <div class="cart-items-container">
        <div class="cart-toolbar">
          <h2 class="section-title">Cart Items</h2>
          <button @click="clearCart" class="btn-ghost text-danger">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" class="mr-1"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
            Clear All
          </button>
        </div>

        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="item-visuals">
              <img :src="item.productImageUrl || 'https://via.placeholder.com/120x120?text=No+Image'" :alt="item.productName" class="item-img" />
            </div>
            
            <div class="item-details">
              <div class="item-header">
                <h3 class="item-name">{{ item.productName }}</h3>
                <button @click="removeItem(item.id)" class="btn-remove" title="Remove item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                </button>
              </div>
              <p class="unit-price">${{ item.productPrice?.toFixed(2) }} <span class="text-dim">each</span></p>
              
              <div class="item-actions">
                <div class="quantity-selector">
                  <button @click="updateQuantity(item.id, item.quantity - 1)" :disabled="item.quantity <= 1">−</button>
                  <span class="qty-value">{{ item.quantity }}</span>
                  <button @click="updateQuantity(item.id, item.quantity + 1)">+</button>
                </div>
                <div class="item-total-price">${{ (item.productPrice * item.quantity).toFixed(2) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="cart-sidebar">
        <div class="summary-card">
          <h3 class="summary-title">Order Summary</h3>
          
          <div class="summary-rows">
            <div class="summary-row">
              <span class="text-muted">Items Subtotal</span>
              <span class="font-medium">${{ subtotal.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span class="text-muted">Estimated Tax</span>
              <span class="font-medium">$0.00</span>
            </div>
            <div class="summary-row">
              <span class="text-muted">Shipping</span>
              <span class="free-text">FREE</span>
            </div>
          </div>
          
          <div class="summary-total">
            <span>Total</span>
            <span class="total-amount">${{ subtotal.toFixed(2) }}</span>
          </div>
          
          <button @click="checkout" class="btn-primary w-full checkout-btn" :disabled="checkingOut">
            <span v-if="checkingOut" class="flex-center gap-2">
              <div class="spinner-sm"></div> Processing...
            </span>
            <span v-else class="flex-center gap-2">
              Secure Checkout
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M14 5l7 7m0 0l-7 7m7-7H3" /></svg>
            </span>
          </button>
          
          <RouterLink to="/products" class="continue-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" /></svg>
            Continue Shopping
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-page { padding-top: 2rem; padding-bottom: 5rem; }

.page-header { margin-bottom: 2.5rem; }
.page-title { font-size: 2.25rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 0.25rem; }
.page-subtitle { color: var(--text-muted); font-size: 1.05rem; }

.mr-1 { margin-right: 0.25rem; }
.mt-4 { margin-top: 1rem; }
.text-danger { color: #f87171 !important; }
.text-danger:hover { color: #ef4444 !important; }
.text-dim { color: var(--text-dim); }
.font-medium { font-weight: 500; }
.w-full { width: 100%; }
.flex-center { display: flex; justify-content: center; align-items: center; }
.gap-2 { gap: 0.5rem; }

/* States */
.loading-state, .error-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6rem 0;
  text-align: center;
  background: var(--surface);
  border-radius: var(--radius-lg);
  border: 1px dashed var(--border-strong);
}
.spinner {
  width: 40px; height: 40px;
  border: 3px solid var(--border-strong);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 1rem;
}
.spinner-sm {
  width: 18px; height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.error-icon { width: 48px; height: 48px; color: #ef4444; margin-bottom: 1rem; }
.empty-icon-box {
  width: 96px; height: 96px;
  border-radius: 50%;
  background: var(--surface-2);
  display: flex; justify-content: center; align-items: center;
  color: var(--text-muted);
  margin-bottom: 1.5rem;
}
.empty-state h2 { font-size: 1.5rem; margin-bottom: 0.5rem; color: var(--text-light); }
.empty-state p { color: var(--text-muted); }

/* Layout */
.cart-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 2.5rem;
  align-items: start;
}

/* Toolbar */
.cart-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.section-title { font-size: 1.25rem; font-weight: 700; }
.btn-ghost { display: flex; align-items: center; font-weight: 600; font-size: 0.85rem; padding: 0.5rem; }

/* Items */
.cart-list { display: flex; flex-direction: column; gap: 1rem; }
.cart-item {
  display: flex;
  gap: 1.5rem;
  background: var(--surface);
  padding: 1.25rem;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  transition: all 0.2s;
}
.cart-item:hover { border-color: var(--border-strong); background: var(--surface-2); }

.item-visuals { flex-shrink: 0; }
.item-img {
  width: 110px;
  height: 110px;
  object-fit: cover;
  border-radius: var(--radius-md);
  background: var(--bg-dark);
  border: 1px solid var(--border);
}

.item-details { flex: 1; display: flex; flex-direction: column; }
.item-header { display: flex; justify-content: space-between; align-items: flex-start; }
.item-name { font-size: 1.1rem; font-weight: 600; color: var(--text-light); margin-bottom: 0.25rem; line-height: 1.4; }
.unit-price { color: var(--text-muted); font-size: 0.95rem; margin-bottom: 1rem; }

.item-actions {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.quantity-selector {
  display: inline-flex;
  align-items: center;
  background: var(--bg-dark);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-strong);
  overflow: hidden;
}
.quantity-selector button {
  background: transparent;
  color: var(--text-light);
  width: 36px; height: 36px;
  display: flex; justify-content: center; align-items: center;
  font-size: 1.2rem;
}
.quantity-selector button:hover:not(:disabled) { background: var(--surface-2); }
.quantity-selector button:disabled { opacity: 0.3; cursor: not-allowed; }
.qty-value { min-width: 40px; text-align: center; font-weight: 600; font-size: 0.95rem; }

.item-total-price { font-weight: 800; font-size: 1.2rem; color: var(--text-light); letter-spacing: -0.01em; }

.btn-remove {
  background: transparent;
  color: var(--text-muted);
  padding: 0.4rem;
  border-radius: var(--radius-sm);
}
.btn-remove:hover { background: rgba(239, 68, 68, 0.1); color: #ef4444; }

/* Sidebar Summary */
.cart-sidebar { position: sticky; top: 90px; }
.summary-card {
  background: var(--surface);
  padding: 2rem;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
}
.summary-title { font-size: 1.25rem; font-weight: 700; margin-bottom: 1.5rem; letter-spacing: -0.01em; }

.summary-rows { display: flex; flex-direction: column; gap: 1rem; margin-bottom: 1.5rem; padding-bottom: 1.5rem; border-bottom: 1px solid var(--border-strong); }
.summary-row { display: flex; justify-content: space-between; align-items: center; font-size: 0.95rem; }
.free-text { color: var(--success); font-weight: 700; font-size: 0.85rem; letter-spacing: 0.05em; padding: 0.15rem 0.5rem; background: rgba(16,185,129,0.1); border-radius: var(--radius-sm); }

.summary-total {
  display: flex; justify-content: space-between; align-items: center;
  font-size: 1.1rem; color: var(--text-light); font-weight: 600; margin-bottom: 1.5rem;
}
.total-amount { font-size: 1.75rem; font-weight: 800; color: var(--primary); letter-spacing: -0.03em; }

.checkout-btn { padding: 1rem; font-size: 1.05rem; justify-content: center; margin-bottom: 1.25rem; }

.continue-link {
  display: flex; align-items: center; justify-content: center; gap: 0.5rem;
  color: var(--text-muted); font-size: 0.9rem; font-weight: 500; transition: color 0.2s;
}
.continue-link:hover { color: var(--text-light); }

@media (max-width: 960px) {
  .cart-layout { grid-template-columns: 1fr; }
  .cart-sidebar { position: static; }
}
@media (max-width: 500px) {
  .cart-item { flex-direction: column; }
  .item-img { width: 100%; height: 200px; }
}
</style>
