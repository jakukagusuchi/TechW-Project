<script setup>
import { ref } from 'vue';

const orders = ref([
  {
    id: 'ORD-12345',
    date: '2026-02-25',
    total: 2298.00,
    status: 'Delivered',
    items: [
      { name: 'MacBook Pro M3', quantity: 1, price: 1999 },
      { name: 'Wireless Headphones', quantity: 1, price: 299 }
    ]
  },
  {
    id: 'ORD-12346',
    date: '2026-02-20',
    total: 129.00,
    status: 'Shipped',
    items: [
      { name: 'Mechanical Keyboard', quantity: 1, price: 129 }
    ]
  }
]);

function getStatusClass(status) {
  switch (status.toLowerCase()) {
    case 'delivered': return 'status-delivered';
    case 'shipped': return 'status-shipped';
    case 'pending': return 'status-pending';
    default: return '';
  }
}
</script>

<template>
  <div class="container orders-container">
    <h1>Your Orders</h1>
    
    <div v-if="orders.length === 0" class="empty-orders">
      <p>You haven't placed any orders yet.</p>
      <RouterLink to="/products" class="btn-primary">Start Shopping</RouterLink>
    </div>
    
    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card card">
        <div class="order-header">
          <div class="order-meta">
            <span class="order-id">#{{ order.id }}</span>
            <span class="order-date">{{ order.date }}</span>
          </div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ order.status }}
          </div>
        </div>
        
        <div class="order-items">
          <div v-for="item in order.items" :key="item.name" class="order-item">
            <span>{{ item.name }} x{{ item.quantity }}</span>
            <span>${{ item.price }}</span>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            <span>Total:</span>
            <span class="total-amount">${{ order.total }}</span>
          </div>
          <button class="btn-text">View Details</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.orders-container {
  padding-top: 2rem;
  padding-bottom: 4rem;
}

h1 { margin-bottom: 2rem; }

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
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border);
}

.order-meta {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.order-id {
  font-weight: 700;
  font-size: 1.1rem;
}

.order-date {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.order-status {
  padding: 0.25rem 0.75rem;
  border-radius: 99px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status-delivered { background: rgba(34, 197, 94, 0.15); color: #22c55e; }
.status-shipped { background: rgba(37, 99, 235, 0.15); color: #3b82f6; }
.status-pending { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }

.order-items {
  margin-bottom: 1.5rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  color: var(--text-light);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-total {
  font-size: 1.1rem;
}

.total-amount {
  font-weight: 700;
  margin-left: 0.5rem;
  color: var(--accent);
}

.btn-text {
  background: transparent;
  color: var(--primary);
  padding: 0.5rem 0;
  font-weight: 600;
}

.btn-text:hover {
  text-decoration: underline;
}

.empty-orders {
  text-align: center;
  padding: 4rem 0;
  color: var(--text-muted);
}
</style>
