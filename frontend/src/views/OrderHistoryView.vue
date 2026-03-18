<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const orders = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    const { data } = await api.get('/api/orders/my-orders');
    orders.value = data;
  } catch (err) {
    console.error('Failed to fetch orders', err);
    error.value = 'Failed to load your orders.';
  } finally {
    loading.value = false;
  }
});

function getStatusClass(status) {
  if (!status) return '';
  switch (status.toUpperCase()) {
    case 'DELIVERED': return 'status-delivered';
    case 'SHIPPED': return 'status-shipped';
    case 'PENDING': return 'status-pending';
    case 'CANCELLED': return 'status-cancelled';
    default: return '';
  }
}

function formatDate(dateString) {
  if (!dateString) return 'Parsing error';
  const date = new Date(dateString);
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
}
</script>

<template>
  <div class="container orders-container">
    <h1>Your Orders</h1>
    
    <div v-if="loading" class="state-message">
      Loading your order history...
    </div>

    <div v-else-if="error" class="state-message error">
      {{ error }}
    </div>

    <div v-else-if="orders.length === 0" class="empty-orders">
      <p>You haven't placed any orders yet.</p>
      <RouterLink to="/products" class="btn-primary">Start Shopping</RouterLink>
    </div>
    
    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card card">
        <div class="order-header">
          <div class="order-meta">
            <span class="order-id">Order #{{ order.id.substring(0,8) }}...</span>
            <span class="order-date">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ order.status }}
          </div>
        </div>
        
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <span>{{ item.productName }} x{{ item.quantity }}</span>
            <span>${{ (item.unitPrice * item.quantity).toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            <span>Total:</span>
            <span class="total-amount">${{ order.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.orders-container {
  padding-top: 2rem;
  padding-bottom: 4rem;
  max-width: 800px;
  margin: 0 auto;
}

h1 { margin-bottom: 2rem; font-weight: 800; font-size: 2.2rem; }

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.card {
  background: var(--surface);
  padding: 1.5rem;
  border-radius: 16px;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
  transition: transform 0.2s, border-color 0.2s;
}
.card:hover { border-color: var(--border-strong); transform: translateY(-2px); }

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  padding-bottom: 1.25rem;
  border-bottom: 1px solid var(--border);
}

.order-meta {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.order-id {
  font-weight: 700;
  font-size: 1.05rem;
  color: var(--text-light);
}

.order-date {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.order-status {
  padding: 0.35rem 0.85rem;
  border-radius: 99px;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.status-delivered { background: rgba(34, 197, 94, 0.15); color: #34d399; }
.status-shipped { background: rgba(59, 130, 246, 0.15); color: #60a5fa; }
.status-pending { background: rgba(245, 158, 11, 0.15); color: #fbbf24; border: 1px solid rgba(245, 158, 11, 0.3); }
.status-cancelled { background: rgba(239, 68, 68, 0.15); color: #f87171; }

.order-items {
  margin-bottom: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  color: var(--text-muted);
  font-size: 0.95rem;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px dashed var(--border-strong);
}

.order-total {
  font-size: 1.15rem;
  font-weight: 600;
  color: var(--text-light);
}

.total-amount {
  font-weight: 800;
  font-size: 1.5rem;
  margin-left: 0.5rem;
  color: var(--primary);
  letter-spacing: -0.02em;
}

.state-message, .empty-orders {
  text-align: center;
  padding: 5rem 0;
  color: var(--text-muted);
  background: var(--surface);
  border-radius: var(--radius-lg);
  border: 1px dashed var(--border-strong);
}
.state-message.error { color: #f87171; border-color: rgba(239, 68, 68, 0.3); }
.empty-orders p { margin-bottom: 2rem; font-size: 1.1rem; }
</style>
