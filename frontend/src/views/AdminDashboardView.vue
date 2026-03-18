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
const imageMode = ref('url'); // 'url' or 'upload'
const isDragging = ref(false);
const imagePreview = ref(null);


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
    imageMode.value = 'url';
    imagePreview.value = null;
    currentProduct.value = {
        ...initialProductState,
        category: { id: categories.value.length > 0 ? categories.value[0].id : null },
        brand: { id: brands.value.length > 0 ? brands.value[0].id : null }
    };
    showModal.value = true;
}

function openEditModal(product) {
    isEditing.value = true;
    imageMode.value = 'url';
    imagePreview.value = product.imageUrl?.startsWith('data:') ? product.imageUrl : null;
    currentProduct.value = {
        ...product,
        category: { id: product.category?.id || null },
        brand: { id: product.brand?.id || null }
    };
    showModal.value = true;
}

function closeModal() {
    showModal.value = false;
    imagePreview.value = null;
    currentProduct.value = { ...initialProductState };
}

function onFileDrop(event) {
    isDragging.value = false;
    const file = event.dataTransfer?.files[0] || event.target?.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
        alert('Please upload an image file.');
        return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
        currentProduct.value.imageUrl = e.target.result;
        imagePreview.value = e.target.result;
    };
    reader.readAsDataURL(file);
}

function clearImage() {
    currentProduct.value.imageUrl = '';
    imagePreview.value = null;
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
        <div class="admin-header-main">
            <div>
                <h1 class="page-title">Admin Dashboard</h1>
                <p class="page-subtitle">Manage your store operations and inventory.</p>
            </div>
            <button @click="openAddModal" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"/></svg>
                New Product
            </button>
        </div>

        <div v-if="error" class="error-alert">
            <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
            {{ error }}
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon sales">💲</div>
                <div class="stat-info">
                    <span class="stat-label">Total Sales</span>
                    <span class="stat-value">${{ stats.totalSales }}</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon orders">📦</div>
                <div class="stat-info">
                    <span class="stat-label">Total Orders</span>
                    <span class="stat-value">{{ stats.totalOrders }}</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon customers">👥</div>
                <div class="stat-info">
                    <span class="stat-label">Total Customers</span>
                    <span class="stat-value">{{ stats.totalCustomers }}</span>
                </div>
            </div>
        </div>

        <div class="card inventory-card">
            <div class="card-header">
                <h2>Product Inventory</h2>
            </div>

            <div v-if="loading" class="loading-state">
                <div class="spinner"></div>
                Loading inventory...
            </div>

            <div v-else class="table-responsive">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Category</th>
                            <th>Brand</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th class="text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="product in products" :key="product.id">
                            <td>
                                <div class="product-info-cell">
                                    <img :src="product.imageUrl || 'https://via.placeholder.com/48?text=N/A'" :alt="product.name" class="product-thumb">
                                    <div class="product-name-block">
                                        <span class="p-name">{{ product.name }}</span>
                                        <span class="p-id">ID: {{ product.id.substring(0,8) }}...</span>
                                    </div>
                                </div>
                            </td>
                            <td class="text-muted">{{ product.category?.name || 'N/A' }}</td>
                            <td class="text-muted">{{ product.brand?.name || 'N/A' }}</td>
                            <td class="font-medium">${{ product.price }}</td>
                            <td>
                                <span :class="['stock-count', product.stockQuantity <= 5 ? 'critical' : 'good']">{{ product.stockQuantity }}</span>
                            </td>
                            <td>
                                <span :class="['status-badge', product.isActive ? 'active' : 'inactive']">
                                    {{ product.isActive ? 'Active' : 'Hidden' }}
                                </span>
                            </td>
                            <td class="actions text-right">
                                <button @click="openEditModal(product)" class="btn-icon btn-edit" title="Edit">✏️</button>
                                <button @click="deleteProduct(product.id)" class="btn-icon btn-delete" title="Delete">🗑️</button>
                            </td>
                        </tr>
                        <tr v-if="products.length === 0">
                            <td colspan="7" class="empty-state">No products found. Add one to get started!</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Modal Form -->
        <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>{{ isEditing ? 'Edit Product' : 'Add New Product' }}</h2>
                    <button @click="closeModal" class="btn-close">×</button>
                </div>
                
                <form @submit.prevent="saveProduct" class="product-form">
                    <div class="form-group">
                        <label for="name">Product Name <span class="req">*</span></label>
                        <input type="text" id="name" class="form-input" v-model="currentProduct.name" required placeholder="Enter product name">
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="category">Category</label>
                            <select id="category" class="form-input" v-model="currentProduct.category.id" :required="categories.length > 0">
                                <option :value="null">Select a category</option>
                                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="brand">Brand</label>
                            <select id="brand" class="form-input" v-model="currentProduct.brand.id" :required="brands.length > 0">
                                <option :value="null">Select a brand</option>
                                <option v-for="b in brands" :key="b.id" :value="b.id">{{ b.name }}</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="price">Price ($) <span class="req">*</span></label>
                            <input type="number" id="price" class="form-input" v-model="currentProduct.price" step="0.01" min="0" required>
                        </div>

                        <div class="form-group">
                            <label for="stockQuantity">Stock Quantity <span class="req">*</span></label>
                            <input type="number" id="stockQuantity" class="form-input" v-model="currentProduct.stockQuantity" min="0" required>
                        </div>
                    </div>

                    <div class="form-group image-upload-group">
                        <div class="flex-between">
                            <label>Product Image <span class="optional-tag">(Optional)</span></label>
                            <div class="image-mode-tabs">
                                <button type="button" :class="['mode-tab', { active: imageMode === 'url' }]" @click="imageMode = 'url'">🔗 URL Link</button>
                                <button type="button" :class="['mode-tab', { active: imageMode === 'upload' }]" @click="imageMode = 'upload'">⬆ Upload File</button>
                            </div>
                        </div>

                        <div v-if="imageMode === 'url'">
                            <input type="text" id="imageUrl" class="form-input" v-model="currentProduct.imageUrl" placeholder="https://example.com/image.jpg">
                        </div>

                        <div v-else
                            class="drop-zone"
                            :class="{ dragging: isDragging, 'has-image': imagePreview }"
                            @dragenter.prevent="isDragging = true"
                            @dragleave.prevent="isDragging = false"
                            @dragover.prevent
                            @drop.prevent="onFileDrop"
                            @click="$refs.fileInput.click()"
                        >
                            <div v-if="!imagePreview" class="drop-zone-placeholder">
                                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"/></svg>
                                <p>Drag & drop an image here</p>
                                <p class="drop-hint">or click to browse from your computer</p>
                            </div>
                            <div v-else class="drop-zone-preview">
                                <img :src="imagePreview" alt="Preview">
                                <button type="button" class="clear-image" @click.stop="clearImage">×</button>
                            </div>
                            <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="onFileDrop">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" class="form-input" v-model="currentProduct.description" rows="3" placeholder="Enter product description"></textarea>
                    </div>

                    <div class="form-group checkbox-group">
                        <label class="toggle-switch">
                            <input type="checkbox" id="isActive" v-model="currentProduct.isActive">
                            <span class="slider"></span>
                        </label>
                        <label for="isActive">Is Available (Active)</label>
                    </div>

                    <div class="form-actions">
                        <button type="button" @click="closeModal" class="btn-ghost">Cancel</button>
                        <button type="submit" class="btn-primary">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
                            Save Product
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<style scoped>
.admin-container { padding-top: 2.5rem; padding-bottom: 5rem; }

.admin-header-main {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2.5rem;
}
.page-title { font-size: 2.25rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 0.25rem; }
.page-subtitle { color: var(--text-muted); font-size: 1.05rem; }

.text-muted { color: var(--text-muted); }
.text-right { text-align: right; }
.font-medium { font-weight: 500; font-size: 1.05rem; }
.req { color: var(--danger); }

/* Error state */
.error-alert {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background-color: rgba(239, 68, 68, 0.1);
    color: #fca5a5;
    padding: 1rem 1.25rem;
    border-radius: var(--radius-md);
    margin-bottom: 2rem;
    border: 1px solid rgba(239, 68, 68, 0.25);
    font-weight: 500;
}
.error-icon { width: 24px; height: 24px; color: var(--danger); }

/* Stats Grid */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2.5rem;
}
.stat-card {
    display: flex;
    align-items: center;
    gap: 1.25rem;
    background: var(--surface);
    padding: 1.5rem;
    border-radius: var(--radius-lg);
    border: 1px solid var(--border);
    box-shadow: var(--shadow-sm);
}
.stat-icon {
    width: 52px; height: 52px;
    border-radius: 14px;
    display: flex; justify-content: center; align-items: center;
    font-size: 1.5rem;
}
.stat-icon.sales { background: rgba(16, 185, 129, 0.15); color: #10b981; }
.stat-icon.orders { background: rgba(99, 102, 241, 0.15); color: #818cf8; }
.stat-icon.customers { background: rgba(245, 158, 11, 0.15); color: #fbbf24; }

.stat-info { display: flex; flex-direction: column; }
.stat-label { color: var(--text-muted); font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.2rem; }
.stat-value { font-size: 1.7rem; font-weight: 800; color: var(--text-light); line-height: 1; }

/* Inventory Card */
.inventory-card { padding: 0; overflow: hidden; }
.card-header {
    padding: 1.5rem 1.5rem 1rem;
    border-bottom: 1px solid var(--border);
}
.card-header h2 { font-size: 1.25rem; font-weight: 700; color: var(--text-light); }

.table-responsive { overflow-x: auto; }
.admin-table { width: 100%; border-collapse: collapse; text-align: left; }
.admin-table th {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid var(--border-strong);
    background: rgba(255,255,255,0.02);
    color: var(--text-muted);
    font-weight: 600;
    font-size: 0.85rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    white-space: nowrap;
}
.admin-table td {
    padding: 1.25rem 1.5rem;
    border-bottom: 1px solid var(--border);
    vertical-align: middle;
}
.admin-table tr:hover { background: rgba(255,255,255,0.015); }

/* Product Row Specifics */
.product-info-cell { display: flex; align-items: center; gap: 1rem; }
.product-thumb { width: 44px; height: 44px; object-fit: cover; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-dark); }
.product-name-block { display: flex; flex-direction: column; gap: 0.2rem; }
.p-name { font-weight: 600; color: var(--text-light); font-size: 0.95rem; }
.p-id { font-size: 0.75rem; color: var(--text-dim); }

.stock-count { font-weight: 700; }
.stock-count.critical { color: #fca5a5; }
.stock-count.good { color: #34d399; }

.status-badge {
    padding: 0.25rem 0.65rem;
    border-radius: 99px;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}
.status-badge.active { background: rgba(16, 185, 129, 0.15); color: #34d399; }
.status-badge.inactive { background: rgba(100, 116, 139, 0.2); color: var(--text-muted); }

.actions { display: flex; gap: 0.5rem; justify-content: flex-end; }
.btn-icon {
    width: 32px; height: 32px;
    border-radius: 8px;
    display: flex; justify-content: center; align-items: center;
    background: var(--surface-2);
    border: 1px solid var(--border);
    transition: all 0.2s;
    font-size: 0.9rem;
}
.btn-icon:hover { border-color: var(--border-strong); transform: translateY(-1px); }
.btn-delete:hover { background: rgba(239, 68, 68, 0.15); border-color: rgba(239, 68, 68, 0.3); }

.loading-state, .empty-state { text-align: center; padding: 4rem; color: var(--text-muted); display: flex; flex-direction: column; align-items: center; gap: 1rem; }
.spinner { width: 30px; height: 30px; border: 3px solid var(--border); border-top-color: var(--primary); border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* Modal Styles */
.modal-overlay {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(8, 11, 20, 0.85);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    display: flex; align-items: center; justify-content: center;
    z-index: 1000;
    padding: 1.5rem;
}
.modal-content {
    background: var(--surface);
    border-radius: var(--radius-xl);
    width: 100%; max-width: 640px;
    max-height: 90vh;
    overflow-y: auto;
    border: 1px solid var(--border-strong);
    box-shadow: var(--shadow-lg);
    padding: 2.5rem;
}
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
.modal-header h2 { font-size: 1.5rem; font-weight: 800; letter-spacing: -0.01em; }
.btn-close { font-size: 1.75rem; background: transparent; color: var(--text-muted); line-height: 1; border: none; transition: color 0.2s; }
.btn-close:hover { color: var(--text-light); }

.product-form { display: flex; flex-direction: column; gap: 1.25rem; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; }
.form-group { display: flex; flex-direction: column; gap: 0.5rem; }
.form-group label { font-weight: 600; font-size: 0.9rem; color: var(--text-muted); }

/* Switch Toggle */
.checkbox-group { flex-direction: row; align-items: center; gap: 0.75rem; user-select: none; margin-top: 0.5rem; }
.toggle-switch { position: relative; width: 44px; height: 24px; display: inline-block; cursor: pointer; }
.toggle-switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background-color: var(--surface-2); border: 1px solid var(--border-strong); border-radius: 99px; transition: .3s; }
.slider:before { position: absolute; content: ""; height: 16px; width: 16px; left: 3px; bottom: 3px; background-color: var(--text-muted); transition: .3s; border-radius: 50%; }
.toggle-switch input:checked + .slider { background-color: var(--primary); border-color: var(--primary); }
.toggle-switch input:checked + .slider:before { transform: translateX(20px); background-color: white; }

.form-actions {
    display: flex; justify-content: flex-end; gap: 1rem;
    margin-top: 1rem; padding-top: 1.5rem; border-top: 1px solid var(--border);
}

/* Image Upload UI */
.flex-between { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.25rem; }
.flex-between label { margin-bottom: 0; }
.optional-tag { font-size: 0.75rem; font-weight: 400; color: var(--text-dim); }

.image-mode-tabs { display: flex; gap: 0.25rem; background: var(--bg-dark); padding: 0.2rem; border-radius: var(--radius-sm); border: 1px solid var(--border); }
.mode-tab { padding: 0.25rem 0.75rem; border-radius: 6px; background: transparent; color: var(--text-muted); font-size: 0.8rem; font-weight: 500; border: none; }
.mode-tab.active { background: var(--surface-2); color: var(--text-light); box-shadow: 0 1px 3px rgba(0,0,0,0.3); }

.drop-zone {
    border: 2px dashed var(--border-strong);
    border-radius: var(--radius-md);
    background: var(--bg-dark);
    padding: 2.5rem; text-align: center;
    cursor: pointer; transition: all 0.2s;
    min-height: 160px;
    display: flex; align-items: center; justify-content: center;
}
.drop-zone:hover, .drop-zone.dragging { border-color: var(--primary); background: rgba(99, 102, 241, 0.05); }
.drop-zone-placeholder svg { width: 40px; height: 40px; color: var(--primary); margin-bottom: 0.75rem; opacity: 0.8; }
.drop-zone-placeholder p { color: var(--text-light); font-weight: 500; font-size: 0.95rem; margin: 0; }
.drop-hint { font-size: 0.8rem !important; color: var(--text-muted) !important; margin-top: 0.25rem !important; }

.drop-zone-preview { position: relative; display: inline-block; }
.drop-zone-preview img { max-height: 140px; max-width: 100%; border-radius: var(--radius-sm); object-fit: contain; }
.clear-image {
    position: absolute; top: -10px; right: -10px;
    background: var(--danger); color: white; border: none;
    width: 24px; height: 24px; border-radius: 50%;
    display: flex; justify-content: center; align-items: center;
    font-size: 1.1rem; cursor: pointer;
    box-shadow: var(--shadow-sm);
}
.clear-image:hover { transform: scale(1.1); }

@media (max-width: 768px) {
    .modal-content { padding: 1.5rem; margin: 0; }
    .form-row { grid-template-columns: 1fr; gap: 1.25rem; }
    .admin-header-main { flex-direction: column; align-items: flex-start; gap: 1.25rem; }
}
</style>
