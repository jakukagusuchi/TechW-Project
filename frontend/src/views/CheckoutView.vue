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

const shippingInfo = ref({
  fullName: '',
  address: '',
  city: '',
  zipCode: ''
});

const selectedPayment = ref('CASH_ON_DELIVERY');
const submitting = ref(false);

const cartItems = computed(() => cart.value?.items || []);
const subtotal = computed(() =>
  cartItems.value.reduce((acc, item) => acc + item.productPrice * item.quantity, 0)
);

const paymentMethods = [
  {
    id: 'BANK_TRANSFER',
    name: 'Bank Transfer',
    icon: '🏦',
    description: 'Transfer via bank account. Order will be processed after payment confirmation.',
    color: '#3b82f6'
  },
  {
    id: 'QR_CODE',
    name: 'QR Code Payment',
    icon: '📱',
    description: 'Scan QR code to pay instantly with your mobile banking app.',
    color: '#8b5cf6'
  },
  {
    id: 'CASH_ON_DELIVERY',
    name: 'Cash on Delivery',
    icon: '💵',
    description: 'Pay in cash when you receive your order. No advance payment needed.',
    color: '#10b981'
  }
];

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
    error.value = 'Failed to load cart data.';
  } finally {
    loading.value = false;
  }
}

async function handleCheckout() {
  if (!shippingInfo.value.fullName || !shippingInfo.value.address || !shippingInfo.value.city) {
    error.value = 'Please fill in all required shipping fields.';
    return;
  }

  submitting.value = true;
  error.value = null;

  try {
    await api.post('/api/cart/checkout', {
      fullName: shippingInfo.value.fullName,
      address: shippingInfo.value.address,
      city: shippingInfo.value.city,
      zipCode: shippingInfo.value.zipCode,
      paymentMethod: selectedPayment.value
    });
    router.push('/orders');
  } catch (err) {
    error.value = err.response?.data?.message || 'Checkout failed. Please try again.';
    submitting.value = false;
  }
}

onMounted(() => fetchCart());
</script>

<template>
  <div class="container checkout-container">
    <h1 class="checkout-title">Checkout</h1>

    <div v-if="loading" class="state-box">
      <div class="spinner"></div>
      <p>Loading checkout...</p>
    </div>

    <div v-else-if="cartItems.length === 0" class="state-box">
      <p>Your cart is empty. Add some items first!</p>
      <RouterLink to="/products" class="btn-primary mt-4">Browse Products</RouterLink>
    </div>

    <div v-else class="checkout-grid">
      <!-- LEFT: Shipping + Payment -->
      <div class="checkout-form-area">
        <div v-if="error" class="error-banner">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          {{ error }}
        </div>

        <!-- Shipping Section -->
        <div class="section-card">
          <div class="section-header">
            <span class="section-number">1</span>
            <h2>Shipping Information</h2>
          </div>
          <form @submit.prevent="handleCheckout" id="checkout-form">
            <div class="form-group">
              <label for="fullName">Full Name <span class="req">*</span></label>
              <input type="text" id="fullName" class="form-input" v-model="shippingInfo.fullName" required placeholder="Enter your full name" />
            </div>
            <div class="form-group">
              <label for="address">Address <span class="req">*</span></label>
              <input type="text" id="address" class="form-input" v-model="shippingInfo.address" required placeholder="Street address, building, apt..." />
            </div>
            <div class="form-row">
              <div class="form-group">
                <label for="city">City <span class="req">*</span></label>
                <input type="text" id="city" class="form-input" v-model="shippingInfo.city" required placeholder="City" />
              </div>
              <div class="form-group">
                <label for="zipCode">Zip Code</label>
                <input type="text" id="zipCode" class="form-input" v-model="shippingInfo.zipCode" placeholder="Zip / Postal code" />
              </div>
            </div>
          </form>
        </div>

        <!-- Payment Section -->
        <div class="section-card">
          <div class="section-header">
            <span class="section-number">2</span>
            <h2>Payment Method</h2>
          </div>
          <div class="payment-options">
            <label
              v-for="method in paymentMethods"
              :key="method.id"
              :class="['payment-option', { selected: selectedPayment === method.id }]"
              :style="selectedPayment === method.id ? `border-color: ${method.color}; box-shadow: 0 0 0 1px ${method.color}` : ''"
            >
              <input type="radio" v-model="selectedPayment" :value="method.id" name="payment" class="sr-only" />
              <div class="payment-radio" :style="selectedPayment === method.id ? `border-color: ${method.color}` : ''">
                <div class="payment-radio-dot" :style="selectedPayment === method.id ? `background: ${method.color}` : ''"></div>
              </div>
              <div class="payment-icon">{{ method.icon }}</div>
              <div class="payment-info">
                <span class="payment-name">{{ method.name }}</span>
                <span class="payment-desc">{{ method.description }}</span>
              </div>
            </label>
          </div>
        </div>
      </div>

      <!-- RIGHT: Order Summary -->
      <div class="checkout-sidebar">
        <div class="summary-card">
          <h3 class="summary-title">Order Summary</h3>

          <div class="summary-items">
            <div v-for="item in cartItems" :key="item.id" class="summary-item">
              <img :src="item.productImageUrl || 'https://via.placeholder.com/48x48?text=N/A'" :alt="item.productName" class="summary-item-img" />
              <div class="summary-item-info">
                <span class="summary-item-name">{{ item.productName }}</span>
                <span class="summary-item-qty">Qty: {{ item.quantity }}</span>
              </div>
              <span class="summary-item-price">${{ (item.productPrice * item.quantity).toFixed(2) }}</span>
            </div>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-rows">
            <div class="summary-row">
              <span class="text-muted">Subtotal</span>
              <span class="font-medium">${{ subtotal.toFixed(2) }}</span>
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

          <button
            form="checkout-form"
            type="submit"
            class="btn-primary w-full place-order-btn"
            :disabled="submitting"
          >
            <span v-if="submitting" class="flex-center gap-2">
              <div class="spinner-sm"></div> Processing...
            </span>
            <span v-else class="flex-center gap-2">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              Place Order
            </span>
          </button>

          <RouterLink to="/cart" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" /></svg>
            Back to Cart
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.checkout-container { padding-top: 2rem; padding-bottom: 5rem; }
.checkout-title { font-size: 2.25rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 2rem; }

.state-box {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 6rem 0; text-align: center;
  background: var(--surface); border-radius: var(--radius-lg); border: 1px dashed var(--border-strong);
  color: var(--text-muted);
}
.spinner { width: 40px; height: 40px; border: 3px solid var(--border-strong); border-top-color: var(--primary); border-radius: 50%; animation: spin 0.8s linear infinite; margin-bottom: 1rem; }
.spinner-sm { width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.mt-4 { margin-top: 1rem; }
.w-full { width: 100%; }
.flex-center { display: flex; justify-content: center; align-items: center; }
.gap-2 { gap: 0.5rem; }
.req { color: var(--danger); }

.checkout-grid {
  display: grid; grid-template-columns: 1fr 400px; gap: 2.5rem; align-items: start;
}

/* Error */
.error-banner {
  display: flex; align-items: center; gap: 0.6rem;
  background: rgba(239, 68, 68, 0.1); color: #fca5a5;
  padding: 0.85rem 1.15rem; border-radius: var(--radius-md);
  margin-bottom: 1.5rem; border: 1px solid rgba(239, 68, 68, 0.25); font-weight: 500; font-size: 0.92rem;
}

/* Sections */
.section-card {
  background: var(--surface); padding: 2rem; border-radius: var(--radius-lg);
  border: 1px solid var(--border); margin-bottom: 1.5rem;
}
.section-header { display: flex; align-items: center; gap: 1rem; margin-bottom: 1.75rem; }
.section-number {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-hover));
  color: white; font-weight: 800; font-size: 0.85rem;
  display: flex; justify-content: center; align-items: center;
}
.section-header h2 { font-size: 1.2rem; font-weight: 700; color: var(--text-light); }

.form-group { margin-bottom: 1.25rem; }
.form-group label { display: block; margin-bottom: 0.5rem; color: var(--text-muted); font-size: 0.88rem; font-weight: 600; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }

/* Payment Options */
.payment-options { display: flex; flex-direction: column; gap: 0.85rem; }
.payment-option {
  display: flex; align-items: center; gap: 1rem; padding: 1.15rem 1.25rem;
  background: var(--bg-card); border: 2px solid var(--border-strong);
  border-radius: var(--radius-md); cursor: pointer; transition: all 0.2s;
}
.payment-option:hover { border-color: var(--text-dim); }
.sr-only { position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(0,0,0,0); }

.payment-radio {
  width: 22px; height: 22px; border-radius: 50%;
  border: 2px solid var(--border-strong); display: flex; justify-content: center; align-items: center;
  flex-shrink: 0; transition: all 0.2s;
}
.payment-radio-dot { width: 10px; height: 10px; border-radius: 50%; background: transparent; transition: all 0.2s; }

.payment-icon { font-size: 1.5rem; flex-shrink: 0; }
.payment-info { display: flex; flex-direction: column; gap: 0.2rem; }
.payment-name { font-weight: 700; font-size: 0.95rem; color: var(--text-light); }
.payment-desc { font-size: 0.82rem; color: var(--text-muted); line-height: 1.4; }

/* Summary Sidebar */
.checkout-sidebar { position: sticky; top: 90px; }
.summary-card {
  background: var(--surface); padding: 2rem; border-radius: var(--radius-lg);
  border: 1px solid var(--border); box-shadow: var(--shadow-sm);
}
.summary-title { font-size: 1.25rem; font-weight: 700; margin-bottom: 1.5rem; letter-spacing: -0.01em; }

.summary-items { max-height: 280px; overflow-y: auto; margin-bottom: 1rem; display: flex; flex-direction: column; gap: 0.75rem; }
.summary-item { display: flex; align-items: center; gap: 0.75rem; }
.summary-item-img { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; border: 1px solid var(--border); background: var(--bg-dark); flex-shrink: 0; }
.summary-item-info { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.summary-item-name { font-size: 0.88rem; font-weight: 600; color: var(--text-light); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.summary-item-qty { font-size: 0.78rem; color: var(--text-dim); }
.summary-item-price { font-weight: 700; font-size: 0.9rem; color: var(--text-light); white-space: nowrap; }

.summary-divider { height: 1px; background: var(--border-strong); margin: 1rem 0; }

.summary-rows { display: flex; flex-direction: column; gap: 0.85rem; margin-bottom: 1rem; }
.summary-row { display: flex; justify-content: space-between; align-items: center; font-size: 0.95rem; }
.text-muted { color: var(--text-muted); }
.font-medium { font-weight: 500; }
.free-text { color: var(--success); font-weight: 700; font-size: 0.85rem; letter-spacing: 0.05em; padding: 0.15rem 0.5rem; background: rgba(16,185,129,0.1); border-radius: var(--radius-sm); }

.summary-total {
  display: flex; justify-content: space-between; align-items: center;
  font-size: 1.05rem; color: var(--text-light); font-weight: 600;
  padding-top: 1rem; border-top: 1px solid var(--border-strong); margin-bottom: 1.5rem;
}
.total-amount { font-size: 1.75rem; font-weight: 800; color: var(--primary); letter-spacing: -0.03em; }

.place-order-btn { padding: 1rem; font-size: 1.05rem; justify-content: center; margin-bottom: 1rem; }

.back-link {
  display: flex; align-items: center; justify-content: center; gap: 0.5rem;
  color: var(--text-muted); font-size: 0.9rem; font-weight: 500; transition: color 0.2s;
}
.back-link:hover { color: var(--text-light); }

@media (max-width: 960px) {
  .checkout-grid { grid-template-columns: 1fr; }
  .checkout-sidebar { position: static; }
}
@media (max-width: 500px) {
  .form-row { grid-template-columns: 1fr; }
}
</style>
