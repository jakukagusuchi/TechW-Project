<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const orders = ref([]);
const loading = ref(true);
const error = ref(null);
const editingOrderId = ref(null);
const editForm = ref({ shippingAddress: '', paymentMethod: '' });
const cancellingId = ref(null);

const paymentOptions = [
  { value: 'BANK_TRANSFER', label: '🏦 Bank Transfer' },
  { value: 'QR_CODE', label: '📱 QR Code' },
  { value: 'CASH_ON_DELIVERY', label: '💵 Cash on Delivery' },
];

onMounted(async () => {
  await fetchOrders();
});

async function fetchOrders() {
  loading.value = true;
  try {
    const { data } = await api.get('/api/orders/my-orders');
    orders.value = data;
  } catch (err) {
    console.error('Failed to fetch orders', err);
    error.value = 'Failed to load your orders.';
  } finally {
    loading.value = false;
  }
}

function getStatusClass(status) {
  if (!status) return '';
  switch (status.toUpperCase()) {
    case 'DELIVERED': return 'status-delivered';
    case 'SHIPPED': return 'status-shipped';
    case 'PROCESSING': return 'status-processing';
    case 'PENDING': return 'status-pending';
    case 'CANCELLED': return 'status-cancelled';
    default: return '';
  }
}

function getPaymentLabel(method) {
  const opt = paymentOptions.find(p => p.value === method);
  return opt ? opt.label : method || 'N/A';
}

function formatDate(dateString) {
  if (!dateString) return 'N/A';
  const date = new Date(dateString);
  return date.toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' });
}

function startEdit(order) {
  editingOrderId.value = order.id;
  editForm.value = {
    shippingAddress: order.shippingAddress || '',
    paymentMethod: order.paymentMethod || 'CASH_ON_DELIVERY',
  };
}

function cancelEdit() {
  editingOrderId.value = null;
}

async function saveEdit(orderId) {
  try {
    await api.put(`/api/orders/${orderId}/update`, {
      shippingAddress: editForm.value.shippingAddress,
      paymentMethod: editForm.value.paymentMethod,
    });
    editingOrderId.value = null;
    await fetchOrders();
  } catch (err) {
    alert(err.response?.data?.message || 'Failed to update order.');
  }
}

async function cancelOrder(orderId) {
  if (!confirm('Are you sure you want to cancel this order?')) return;
  cancellingId.value = orderId;
  try {
    await api.put(`/api/orders/${orderId}/cancel`);
    await fetchOrders();
  } catch (err) {
    alert(err.response?.data?.message || 'Failed to cancel order.');
  } finally {
    cancellingId.value = null;
  }
}
</script>

<template>
  <div class="container orders-container">
    <h1>Your Orders</h1>

    <div v-if="loading" class="state-message">Loading your order history...</div>
    <div v-else-if="error" class="state-message error">{{ error }}</div>
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
          <div class="order-header-right">
            <span class="payment-tag">{{ getPaymentLabel(order.paymentMethod) }}</span>
            <div class="order-status" :class="getStatusClass(order.status)">{{ order.status }}</div>
          </div>
        </div>

        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <span>{{ item.productName }} x{{ item.quantity }}</span>
            <span>${{ (item.unitPrice * item.quantity).toFixed(2) }}</span>
          </div>
        </div>

        <!-- Edit Form (only for PENDING orders) -->
        <div v-if="editingOrderId === order.id" class="edit-form">
          <div class="edit-form-title">Edit Order</div>
          <div class="form-group">
            <label>Shipping Address</label>
            <input type="text" class="form-input" v-model="editForm.shippingAddress" placeholder="Update address" />
          </div>
          <div class="form-group">
            <label>Payment Method</label>
            <select class="form-input" v-model="editForm.paymentMethod">
              <option v-for="opt in paymentOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </div>
          <div class="edit-actions">
            <button class="btn-ghost" @click="cancelEdit">Cancel</button>
            <button class="btn-primary" @click="saveEdit(order.id)">Save Changes</button>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-actions" v-if="order.status === 'PENDING' && editingOrderId !== order.id">
            <button class="btn-edit-order" @click="startEdit(order)">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
              Edit
            </button>
            <button class="btn-cancel-order" @click="cancelOrder(order.id)" :disabled="cancellingId === order.id">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
              {{ cancellingId === order.id ? 'Cancelling...' : 'Cancel Order' }}
            </button>
          </div>
          <div class="order-total">
            <span>Total:</span>
            <span class="total-amount">${{ order.totalAmount?.toFixed ? order.totalAmount.toFixed(2) : order.totalAmount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.orders-container { padding-top: 2rem; padding-bottom: 4rem; max-width: 800px; margin: 0 auto; }
h1 { margin-bottom: 2rem; font-weight: 800; font-size: 2.2rem; }

.orders-list { display: flex; flex-direction: column; gap: 1.5rem; }

.card { background: var(--surface); padding: 1.5rem; border-radius: 16px; border: 1px solid var(--border); box-shadow: var(--shadow-sm); transition: transform 0.2s, border-color 0.2s; }
.card:hover { border-color: var(--border-strong); transform: translateY(-2px); }

.order-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; padding-bottom: 1.25rem; border-bottom: 1px solid var(--border); }
.order-meta { display: flex; flex-direction: column; gap: 0.35rem; }
.order-id { font-weight: 700; font-size: 1.05rem; color: var(--text-light); }
.order-date { color: var(--text-muted); font-size: 0.9rem; }

.order-header-right { display: flex; align-items: center; gap: 0.75rem; }
.payment-tag { padding: 0.25rem 0.65rem; border-radius: 6px; font-size: 0.78rem; font-weight: 600; background: var(--surface-2); border: 1px solid var(--border); color: var(--text-muted); }

.order-status { padding: 0.35rem 0.85rem; border-radius: 99px; font-size: 0.8rem; font-weight: 700; letter-spacing: 0.05em; text-transform: uppercase; }
.status-delivered { background: rgba(34, 197, 94, 0.15); color: #34d399; }
.status-shipped { background: rgba(59, 130, 246, 0.15); color: #60a5fa; }
.status-processing { background: rgba(139, 92, 246, 0.15); color: #a78bfa; }
.status-pending { background: rgba(245, 158, 11, 0.15); color: #fbbf24; border: 1px solid rgba(245, 158, 11, 0.3); }
.status-cancelled { background: rgba(239, 68, 68, 0.15); color: #f87171; }

.order-items { margin-bottom: 1.5rem; display: flex; flex-direction: column; gap: 0.75rem; }
.order-item { display: flex; justify-content: space-between; color: var(--text-muted); font-size: 0.95rem; }

/* Edit Form */
.edit-form { background: var(--bg-card); border: 1px solid var(--border-strong); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.25rem; }
.edit-form-title { font-weight: 700; font-size: 0.95rem; color: var(--text-light); margin-bottom: 1rem; }
.form-group { margin-bottom: 0.85rem; }
.form-group label { display: block; margin-bottom: 0.35rem; color: var(--text-muted); font-size: 0.82rem; font-weight: 600; }
.edit-actions { display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1rem; }

.order-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 1rem; border-top: 1px dashed var(--border-strong); }

.order-actions { display: flex; gap: 0.5rem; }
.btn-edit-order, .btn-cancel-order {
  display: flex; align-items: center; gap: 0.35rem;
  padding: 0.4rem 0.75rem; border-radius: 8px; font-size: 0.82rem; font-weight: 600;
  background: transparent; border: 1px solid var(--border-strong); color: var(--text-muted);
  cursor: pointer; transition: all 0.2s;
}
.btn-edit-order:hover { background: var(--primary-light); color: var(--primary); border-color: var(--primary); }
.btn-cancel-order:hover { background: rgba(239, 68, 68, 0.1); color: #f87171; border-color: rgba(239, 68, 68, 0.3); }
.btn-cancel-order:disabled { opacity: 0.5; cursor: not-allowed; }

.order-total { font-size: 1.15rem; font-weight: 600; color: var(--text-light); margin-left: auto; }
.total-amount { font-weight: 800; font-size: 1.5rem; margin-left: 0.5rem; color: var(--primary); letter-spacing: -0.02em; }

.state-message, .empty-orders { text-align: center; padding: 5rem 0; color: var(--text-muted); background: var(--surface); border-radius: var(--radius-lg); border: 1px dashed var(--border-strong); }
.state-message.error { color: #f87171; border-color: rgba(239, 68, 68, 0.3); }
.empty-orders p { margin-bottom: 2rem; font-size: 1.1rem; }
</style>
