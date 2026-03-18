<script setup>
import { onMounted, ref } from 'vue';
import { useProductStore } from '../stores/product';
import { useAuthStore } from '../stores/auth';
import api from '../services/api';

const productStore = useProductStore();
const authStore = useAuthStore();
const searchQuery = ref('');
const addingToCart = ref({});
const cartFeedback = ref({});

onMounted(() => {
  productStore.fetchProducts();
});

function handleSearch() {
  productStore.fetchProducts({ name: searchQuery.value });
}

async function addToCart(product) {
  if (!authStore.isAuthenticated) {
    window.location.href = '/login';
    return;
  }
  addingToCart.value[product.id] = true;
  try {
    await api.post('/api/cart/items', {
      productId: product.id,
      quantity: 1
    });
    cartFeedback.value[product.id] = true;
    setTimeout(() => {
      cartFeedback.value[product.id] = false;
    }, 2000);
  } catch (err) {
    alert(err.response?.data?.message || 'Failed to add to cart.');
  } finally {
    addingToCart.value[product.id] = false;
  }
}
</script>

<template>
  <div class="container catalog-page">
    <div class="catalog-header">
      <div>
        <h1 class="page-title">Explore Catalog</h1>
        <p class="page-subtitle">Find your next favorite gadget</p>
      </div>
      <div class="search-bar">
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input v-model="searchQuery" @input="handleSearch" type="text" placeholder="Search products..." />
      </div>
    </div>

    <div v-if="productStore.loading" class="loading-state">
      <div class="spinner"></div>
      <p>Loading catalog...</p>
    </div>
    
    <div v-else-if="productStore.products.length === 0" class="empty-state">
      <div class="empty-icon">🔍</div>
      <h3>No products found</h3>
      <p>Try adjusting your search criteria</p>
    </div>
    
    <div v-else class="product-grid">
      <div v-for="product in productStore.products" :key="product.id" class="product-card group">
        <div class="product-image-wrapper">
          <img :src="product.imageUrl || 'https://via.placeholder.com/400x300?text=No+Image'" :alt="product.name" class="product-image" />
          
          <button
            @click.prevent="addToCart(product)"
            class="quick-add-btn"
            :class="{ 'added': cartFeedback[product.id], 'loading': addingToCart[product.id] }"
            :disabled="addingToCart[product.id] || !product.isActive"
            title="Add to Cart"
          >
            <svg v-if="cartFeedback[product.id]" xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
            <svg v-else-if="addingToCart[product.id]" class="spinner-sm" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2 9m13-9l2 9M9 21a1 1 0 100-2 1 1 0 000 2zm10 0a1 1 0 100-2 1 1 0 000 2z"/></svg>
          </button>

          <div v-if="!product.isActive" class="stock-badge out-of-stock">Out of Stock</div>
        </div>

        <div class="product-info">
          <div class="brand-name">{{ product.brand?.name || 'Unknown Brand' }}</div>
          <h3 class="product-name">{{ product.name }}</h3>
          
          <div class="price-footer">
            <span class="price">${{ product.price }}</span>
            <RouterLink :to="'/products/' + product.id" class="btn-ghost btn-sm">Details</RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.catalog-page { padding-top: 2rem; padding-bottom: 5rem; }

.catalog-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 3rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid var(--border);
}
.page-title { font-size: 2.25rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 0.25rem; }
.page-subtitle { color: var(--text-muted); font-size: 1.05rem; }

.search-bar {
  position: relative;
  width: 100%;
  max-width: 380px;
}
.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--text-dim);
}
.search-bar input {
  width: 100%;
  padding: 0.85rem 1rem 0.85rem 2.8rem;
  border-radius: 99px;
  background: var(--surface);
  border: 1px solid var(--border-strong);
  color: var(--text-light);
  font-family: inherit;
  font-size: 0.95rem;
  transition: all 0.2s;
}
.search-bar input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-light);
  background: var(--surface-2);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
}

.product-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.product-card:hover {
  transform: translateY(-6px);
  border-color: var(--primary);
  box-shadow: 0 12px 30px rgba(0,0,0,0.4), 0 0 0 1px var(--primary);
}

.product-image-wrapper {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: var(--bg-dark);
}
.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}
.product-card:hover .product-image {
  transform: scale(1.05);
}

/* Quick Add Button (Top Left) */
.quick-add-btn {
  position: absolute;
  top: 1rem;
  left: 1rem;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(8, 11, 20, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255,255,255,0.1);
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 10;
  box-shadow: var(--shadow-md);
}
.product-card:hover .quick-add-btn {
  opacity: 1;
  transform: translateY(0);
}
.quick-add-btn:hover:not(:disabled) {
  background: var(--primary);
  border-color: var(--primary);
  transform: translateY(0) scale(1.1);
}
.quick-add-btn.added {
  background: var(--success);
  border-color: var(--success);
  opacity: 1;
  transform: translateY(0);
}
.quick-add-btn:disabled { cursor: not-allowed; }

.stock-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  padding: 0.35rem 0.75rem;
  border-radius: 99px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  backdrop-filter: blur(8px);
}
.out-of-stock {
  background: rgba(239, 68, 68, 0.2);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.5);
}

.product-info {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.brand-name {
  color: var(--text-muted);
  font-size: 0.8rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 0.4rem;
}
.product-name {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--text-light);
  line-height: 1.4;
  margin-bottom: 1.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid var(--border);
}
.price {
  font-size: 1.35rem;
  font-weight: 800;
  color: var(--text-light);
  letter-spacing: -0.02em;
}

.btn-sm { padding: 0.4rem 0.85rem; font-size: 0.8rem; }

/* States */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6rem 0;
  text-align: center;
  color: var(--text-muted);
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
  width: 20px; height: 20px;
  animation: spin 1s linear infinite;
}
.opacity-25 { opacity: 0.25; }
.opacity-75 { opacity: 0.75; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-icon { font-size: 3rem; margin-bottom: 1rem; opacity: 0.5; }
.empty-state h3 { color: var(--text-light); font-size: 1.25rem; margin-bottom: 0.25rem; }

@media (max-width: 640px) {
  .catalog-header { flex-direction: column; align-items: stretch; gap: 1.5rem; }
  .search-bar { max-width: 100%; }
}
</style>
