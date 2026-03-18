<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useProductStore } from '../stores/product';
import { useAuthStore } from '../stores/auth';
import api from '../services/api';

const route = useRoute();
const productStore = useProductStore();
const authStore = useAuthStore();

const product = ref(null);
const quantity = ref(1);
const addingToCart = ref(false);
const showSuccess = ref(false);

onMounted(async () => {
  product.value = await productStore.fetchProductById(route.params.id);
});

async function addToCart() {
  if (!authStore.isAuthenticated) {
    window.location.href = '/login';
    return;
  }
  
  if (quantity.value < 1) return;

  addingToCart.value = true;
  try {
    await api.post('/api/cart/items', {
      productId: product.value.id,
      quantity: quantity.value
    });
    showSuccess.value = true;
    setTimeout(() => {
      showSuccess.value = false;
    }, 2500);
  } catch (err) {
    alert(err.response?.data?.message || 'Failed to add item to cart.');
  } finally {
    addingToCart.value = false;
  }
}
</script>

<template>
  <div class="container">
    <div v-if="productStore.loading" class="loading">Loading product...</div>
    <div v-else-if="product" class="product-detail">
      <div class="image-section">
        <img :src="product.imageUrl || 'https://via.placeholder.com/600x400?text=No+Image'" :alt="product.name" />
      </div>
      <div class="info-section">
        <p class="category">{{ product.category?.name }}</p>
        <h1>{{ product.name }}</h1>
        <p class="brand">Brand: {{ product.brand?.name }}</p>
        <div class="price">${{ product.price }}</div>
        
        <p class="description">{{ product.description }}</p>

        <div class="purchase-actions">
          <div class="quantity-input">
            <button @click="quantity > 1 && quantity--">−</button>
            <span>{{ quantity }}</span>
            <button @click="quantity++">+</button>
          </div>
          <button @click="addToCart" class="btn-primary flex-1" :class="{ 'btn-success': showSuccess }" :disabled="addingToCart || !product.isActive">
            <span v-if="showSuccess">✓ Added to Cart</span>
            <span v-else-if="addingToCart">Adding...</span>
            <span v-else>Add to Cart</span>
          </button>
        </div>

        <div class="stock-status" :class="{ 'in-stock': product.stockQuantity > 0, 'out-of-stock': product.stockQuantity <= 0 }">
          {{ product.stockQuantity > 0 ? 'In Stock' : 'Out of Stock' }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail { display: grid; grid-template-columns: 1fr 1fr; gap: 4rem; padding-top: 2rem; }
.image-section img { width: 100%; border-radius: 24px; border: 1px solid var(--border); }
.category { color: var(--primary); font-weight: 700; text-transform: uppercase; font-size: 0.8rem; letter-spacing: 1px; }
h1 { font-size: 3rem; margin: 0.5rem 0 1rem; }
.brand { color: var(--text-muted); margin-bottom: 2rem; }
.price { font-size: 2.5rem; font-weight: 800; color: var(--accent); margin-bottom: 2rem; }
.description { color: var(--text-muted); line-height: 1.8; margin-bottom: 3rem; }
.purchase-actions { display: flex; gap: 1.5rem; margin-bottom: 2rem; }
.quantity-input { display: flex; align-items: center; background: var(--surface); border-radius: 8px; border: 1px solid var(--border); }
.quantity-input button { padding: 0.75rem 1rem; background: transparent; color: white; }
.quantity-input span { padding: 0 1rem; min-width: 40px; text-align: center; font-weight: 600; }
.flex-1 { flex: 1; transition: background-color 0.2s; }
.btn-success { background: #10b981 !important; border-color: #10b981 !important; }
.stock-status { font-size: 0.9rem; font-weight: 600; padding: 0.5rem 1rem; border-radius: 99px; display: inline-block; }
.in-stock { background: rgba(34, 197, 94, 0.1); color: #22c55e; }
.out-of-stock { background: rgba(239, 68, 68, 0.1); color: #ef4444; border: 1px solid rgba(239, 68, 68, 0.3); }
.loading { text-align: center; padding: 5rem; color: var(--text-muted); font-size: 1.1rem; }
</style>
