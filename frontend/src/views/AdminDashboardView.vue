<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const products = ref([]);
const categories = ref([]);
const brands = ref([]);
const loading = ref(true);
const error = ref(null);

const showModal = ref(false);
const isEditing = ref(false);

const initialProductState = {
    name: '',
    description: '',
    price: 0,
    stockQuantity: 0,
    imageUrl: '',
    isActive: true,
    category: { id: null },
    brand: { id: null }
};

const currentProduct = ref({ ...initialProductState });

// Assuming valid user token sets this
const stats = ref({
    totalSales: 0,
    totalOrders: 0,
    totalCustomers: 0
});

onMounted(() => {
    fetchData();
});

async function fetchData() {
    loading.value = true;
    error.value = null;
    try {
        const [productsRes, categoriesRes, brandsRes] = await Promise.all([
            api.get('/api/products?size=100'),
            api.get('/api/categories'),
            api.get('/api/brands')
        ]);

        products.value = productsRes.data.content || productsRes.data || [];
        categories.value = categoriesRes.data;
        brands.value = brandsRes.data;
    } catch (err) {
        console.error('Error fetching data:', err);
        error.value = 'Failed to load dashboard data. Please try again.';
    } finally {
        loading.value = false;
    }
}

function openAddModal() {
    isEditing.value = false;
    currentProduct.value = {
        ...initialProductState,
        category: { id: categories.value.length > 0 ? categories.value[0].id : null },
        brand: { id: brands.value.length > 0 ? brands.value[0].id : null }
    };
    showModal.value = true;
}

function openEditModal(product) {
    isEditing.value = true;
    currentProduct.value = {
        ...product,
        category: { id: product.category?.id || null },
        brand: { id: product.brand?.id || null }
    };
    showModal.value = true;
}

function closeModal() {
    showModal.value = false;
    currentProduct.value = { ...initialProductState };
}

async function saveProduct() {
    try {
        const payload = { ...currentProduct.value };
        if (!payload.category.id) payload.category = null;
        if (!payload.brand.id) payload.brand = null;

        if (isEditing.value) {
            await api.put(`/api/products/${payload.id}`, payload);
        } else {
            await api.post('/api/products', payload);
        }
        await fetchData();
        closeModal();
    } catch (err) {
        console.error('Error saving product:', err);
        alert('Failed to save product: ' + (err.response?.data?.message || err.message));
    }
}

async function deleteProduct(id) {
    // if (confirm('Are you sure you want to delete this product?')) {
    try {
        await api.delete(`/api/products/${id}`);
        await fetchData();
    } catch (err) {
        console.error('Error deleting product:', err);
        alert('Failed to delete product.');
    }
    // }
}
</script>

<template>
    <div class="container admin-container">
        <h1>Admin Dashboard</h1>

        <div v-if="error" class="error-message">
            {{ error }}
        </div>

        <div class="stats-grid">
            <div class="stat-card card">
                <span class="stat-label">Total Sales</span>
                <span class="stat-value">${{ stats.totalSales }}</span>
            </div>
            <div class="stat-card card">
                <span class="stat-label">Total Orders</span>
                <span class="stat-value">{{ stats.totalOrders }}</span>
            </div>
            <div class="stat-card card">
                <span class="stat-label">Total Customers</span>
                <span class="stat-value">{{ stats.totalCustomers }}</span>
            </div>
        </div>

        <div class="admin-content card">
            <div class="admin-header">
                <h2>Manage Products</h2>
                <button @click="openAddModal" class="btn-primary">Add New Product</button>
            </div>

            <div v-if="loading" class="loading-state">
                Loading data...
            </div>

            <table v-else class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Brand</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="product in products" :key="product.id">
                        <td>#{{ product.id }}</td>
                        <td>
                            <div class="product-info-cell">
                                <img :src="product.imageUrl || 'https://via.placeholder.com/48'" :alt="product.name"
                                    class="product-thumb">
                                <span>{{ product.name }}</span>
                            </div>
                        </td>
                        <td>{{ product.category?.name || 'N/A' }}</td>
                        <td>{{ product.brand?.name || 'N/A' }}</td>
                        <td>${{ product.price }}</td>
                        <td>{{ product.stockQuantity }}</td>
                        <td>
                            <span :class="['status-badge', product.isActive ? 'active' : 'inactive']">
                                {{ product.isActive ? 'Active' : 'Inactive' }}
                            </span>
                        </td>
                        <td class="actions">
                            <button @click="openEditModal(product)" class="btn-edit">Edit</button>
                            <button @click="deleteProduct(product.id)" class="btn-delete">Delete</button>
                        </td>
                    </tr>
                    <tr v-if="products.length === 0">
                        <td colspan="8" class="empty-state">No products found. Add one to get started!</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Modal Form -->
        <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
            <div class="modal-content">
                <h2>{{ isEditing ? 'Edit Product' : 'Add New Product' }}</h2>
                <form @submit.prevent="saveProduct" class="product-form">
                    <div class="form-group">
                        <label for="name">Product Name *</label>
                        <input type="text" id="name" v-model="currentProduct.name" required
                            placeholder="Enter product name">
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="category">Category</label>
                            <select id="category" v-model="currentProduct.category.id"
                                :required="categories.length > 0">
                                <option :value="null">Select a category</option>
                                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="brand">Brand</label>
                            <select id="brand" v-model="currentProduct.brand.id" :required="brands.length > 0">
                                <option :value="null">Select a brand</option>
                                <option v-for="b in brands" :key="b.id" :value="b.id">{{ b.name }}</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="price">Price ($) *</label>
                            <input type="number" id="price" v-model="currentProduct.price" step="0.01" min="0" required>
                        </div>

                        <div class="form-group">
                            <label for="stockQuantity">Stock Quantity *</label>
                            <input type="number" id="stockQuantity" v-model="currentProduct.stockQuantity" min="0"
                                required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="imageUrl">Image URL</label>
                        <input type="url" id="imageUrl" v-model="currentProduct.imageUrl"
                            placeholder="https://example.com/image.jpg">
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" v-model="currentProduct.description" rows="3"
                            placeholder="Enter product description"></textarea>
                    </div>

                    <div class="form-group checkbox-group">
                        <input type="checkbox" id="isActive" v-model="currentProduct.isActive">
                        <label for="isActive">Is Available (Active)</label>
                    </div>

                    <div class="form-actions">
                        <button type="button" @click="closeModal" class="btn-secondary">Cancel</button>
                        <button type="submit" class="btn-primary">Save Product</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<style scoped>
.admin-container {
    padding-top: 2rem;
    padding-bottom: 4rem;
}

h1 {
    margin-bottom: 2rem;
}

.error-message {
    background-color: rgba(239, 68, 68, 0.1);
    color: #ef4444;
    padding: 1rem;
    border-radius: 8px;
    margin-bottom: 1.5rem;
    border: 1px solid rgba(239, 68, 68, 0.2);
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1.5rem;
    margin-bottom: 2rem;
}

.card {
    background: var(--surface);
    padding: 1.5rem;
    border-radius: 16px;
    border: 1px solid var(--border);
}

.stat-card {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.stat-label {
    color: var(--text-muted);
    font-size: 0.9rem;
}

.stat-value {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--accent);
}

.admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.loading-state,
.empty-state {
    text-align: center;
    padding: 3rem;
    color: var(--text-muted);
    background: var(--surface);
    border-radius: 12px;
}

.admin-table {
    width: 100%;
    border-collapse: collapse;
}

.admin-table th {
    text-align: left;
    padding: 1rem;
    border-bottom: 2px solid var(--border);
    color: var(--text-muted);
    font-weight: 600;
}

.admin-table td {
    padding: 1rem;
    border-bottom: 1px solid var(--border);
    vertical-align: middle;
}

.product-info-cell {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.product-thumb {
    width: 48px;
    height: 48px;
    object-fit: cover;
    border-radius: 8px;
    border: 1px solid var(--border);
}

.status-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 99px;
    font-size: 0.8rem;
    font-weight: 600;
}

.status-badge.active {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
}

.status-badge.inactive {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
}

.actions {
    display: flex;
    gap: 1rem;
}

.btn-edit {
    background: transparent;
    color: var(--primary);
    font-weight: 600;
    border: none;
    cursor: pointer;
}

.btn-edit:hover {
    text-decoration: underline;
}

.btn-delete {
    background: transparent;
    color: #ef4444;
    font-weight: 600;
    border: none;
    cursor: pointer;
}

.btn-delete:hover {
    text-decoration: underline;
}

/* Modal Styles */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(4px);
}

.modal-content {
    background: var(--surface);
    padding: 2rem;
    border-radius: 16px;
    width: 100%;
    max-width: 600px;
    max-height: 90vh;
    overflow-y: auto;
    border: 1px solid var(--border);
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
}

.modal-content h2 {
    margin-bottom: 1.5rem;
}

.product-form {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.form-group label {
    font-weight: 500;
    font-size: 0.95rem;
    color: var(--text-color);
}

.form-group input,
.form-group select,
.form-group textarea {
    padding: 0.75rem 1rem;
    border-radius: 8px;
    border: 1px solid var(--border);
    background: var(--bg-color);
    color: var(--text-color);
    font-family: inherit;
    transition: all 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

.checkbox-group {
    flex-direction: row;
    align-items: center;
    gap: 0.75rem;
}

.checkbox-group input {
    width: 1.25rem;
    height: 1.25rem;
    accent-color: var(--primary);
    cursor: pointer;
}

.checkbox-group label {
    cursor: pointer;
    margin: 0;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1rem;
    padding-top: 1.5rem;
    border-top: 1px solid var(--border);
}

.btn-secondary {
    background: transparent;
    color: var(--text-color);
    border: 1px solid var(--border);
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
}

.btn-secondary:hover {
    background: var(--border);
}

@media (max-width: 768px) {
    .stats-grid {
        grid-template-columns: 1fr;
    }

    .form-row {
        grid-template-columns: 1fr;
    }

    .modal-content {
        margin: 1rem;
        padding: 1.5rem;
    }
}
</style>
