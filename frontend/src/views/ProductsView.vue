<script setup>
import { onMounted, ref } from 'vue';
import { useProductStore } from '../stores/product';

const productStore = useProductStore();
const searchQuery = ref('');

onMounted(() => {
  productStore.fetchProducts();
});

function handleSearch() {
  productStore.fetchProducts({ name: searchQuery.value });
}
</script>

<template>
  <div class="container">
    <div class="catalog-header">
      <h1>Our Products</h1>
      <div class="search-bar">
        <input v-model="searchQuery" @input="handleSearch" type="text" placeholder="Search for gadgets..." />
      </div>
    </div>

    <div v-if="productStore.loading" class="loading">Loading products...</div>
    <div v-else-if="productStore.products.length === 0" class="empty">No products found.</div>
    
    <div v-else class="product-grid">
      <div v-for="product in productStore.products" :key="product.id" class="product-card">
        <div class="product-image">
          <img :src="product.imageUrl || 'https://via.placeholder.com/300x200?text=No+Image'" :alt="product.name" />
        </div>
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <p class="brand">{{ product.brand?.name }}</p>
          <div class="price-row">
            <span class="price">${{ product.price }}</span>
            <RouterLink :to="'/products/' + product.id" class="btn-detail">View Details</RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.catalog-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 3rem; }
.search-bar input { padding: 0.75rem 1.5rem; border-radius: 99px; background: var(--surface); border: 1px solid var(--border); color: white; width: 300px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 2rem; }
.product-card { background: var(--surface); border-radius: 16px; overflow: hidden; border: 1px solid var(--border); transition: 0.3s; }
.product-card:hover { transform: translateY(-5px); border-color: var(--primary); }
.product-image img { width: 100%; height: 200px; object-fit: cover; }
.product-info { padding: 1.5rem; }
h3 { font-size: 1.25rem; margin-bottom: 0.25rem; }
.brand { color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1rem; }
.price-row { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 1.5rem; font-weight: 700; color: var(--accent); }
.btn-detail { color: var(--primary); font-weight: 600; font-size: 0.9rem; }
.loading, .empty { text-align: center; padding: 4rem; color: var(--text-muted); }
</style>
