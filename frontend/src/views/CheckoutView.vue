<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const shippingInfo = ref({
  fullName: '',
  address: '',
  city: '',
  zipCode: '',
  country: ''
});

const paymentInfo = ref({
  cardNumber: '',
  expiryDate: '',
  cvv: ''
});

function handleCheckout() {
  // Placeholder for checkout logic
  alert('Order placed successfully!');
  router.push('/orders');
}
</script>

<template>
  <div class="container checkout-container">
    <h1>Checkout</h1>
    
    <div class="checkout-grid">
      <div class="checkout-form card">
        <h2>Shipping Information</h2>
        <form @submit.prevent="handleCheckout">
          <div class="form-group">
            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" v-model="shippingInfo.fullName" required />
          </div>
          
          <div class="form-group">
            <label for="address">Address</label>
            <input type="text" id="address" v-model="shippingInfo.address" required />
          </div>
          
          <div class="grid-2">
            <div class="form-group">
              <label for="city">City</label>
              <input type="text" id="city" v-model="shippingInfo.city" required />
            </div>
            <div class="form-group">
              <label for="zipCode">Zip Code</label>
              <input type="text" id="zipCode" v-model="shippingInfo.zipCode" required />
            </div>
          </div>
          
          <h2 class="mt-8">Payment Information</h2>
          <div class="form-group">
            <label for="cardNumber">Card Number</label>
            <input type="text" id="cardNumber" v-model="paymentInfo.cardNumber" placeholder="0000 0000 0000 0000" required />
          </div>
          
          <div class="grid-2">
            <div class="form-group">
              <label for="expiry">Expiry Date</label>
              <input type="text" id="expiry" v-model="paymentInfo.expiryDate" placeholder="MM/YY" required />
            </div>
            <div class="form-group">
              <label for="cvv">CVV</label>
              <input type="text" id="cvv" v-model="paymentInfo.cvv" placeholder="000" required />
            </div>
          </div>
          
          <button type="submit" class="btn-primary w-full mt-4">Place Order</button>
        </form>
      </div>
      
      <div class="order-summary card">
        <h2>Order Summary</h2>
        <div class="summary-row">
          <span>Items Subtotal</span>
          <span>$2298.00</span>
        </div>
        <div class="summary-row">
          <span>Shipping</span>
          <span class="free">FREE</span>
        </div>
        <div class="summary-row total">
          <span>Total</span>
          <span>$2298.00</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.checkout-container {
  padding-top: 2rem;
  padding-bottom: 4rem;
}

h1 { margin-bottom: 2rem; }
h2 { margin-bottom: 1.5rem; font-size: 1.25rem; }

.checkout-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 2rem;
}

.card {
  background: var(--surface);
  padding: 2rem;
  border-radius: 16px;
  border: 1px solid var(--border);
}

.form-group {
  margin-bottom: 1.25rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-muted);
  font-size: 0.9rem;
}

input {
  width: 100%;
  padding: 0.75rem;
  background: var(--bg-dark);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: white;
  outline: none;
}

input:focus {
  border-color: var(--primary);
}

.grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  color: var(--text-muted);
}

.total {
  border-top: 1px solid var(--border);
  padding-top: 1rem;
  margin-top: 1rem;
  color: var(--text-light);
  font-size: 1.25rem;
  font-weight: 700;
}

.free { color: #22c55e; }
.mt-4 { margin-top: 1rem; }
.mt-8 { margin-top: 2rem; }
.w-full { width: 100%; }

@media (max-width: 768px) {
  .checkout-grid {
    grid-template-columns: 1fr;
  }
}
</style>
