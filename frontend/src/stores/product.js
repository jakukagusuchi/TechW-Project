import { defineStore } from 'pinia';
import api from '../services/api';

export const useProductStore = defineStore('product', {
    state: () => ({
        products: [],
        loading: false,
        error: null,
        totalElements: 0,
        totalPages: 0,
    }),
    actions: {
        async fetchProducts(params = {}) {
            this.loading = true;
            try {
                const { data } = await api.get('/api/products', { params });
                this.products = data.content; // Spring Page object
                this.totalElements = data.totalElements;
                this.totalPages = data.totalPages;
            } catch (err) {
                this.error = 'Failed to load products';
            } finally {
                this.loading = false;
            }
        },
        async fetchProductById(id) {
            this.loading = true;
            try {
                const { data } = await api.get(`/api/products/${id}`);
                return data;
            } catch (err) {
                this.error = 'Product not found';
                return null;
            } finally {
                this.loading = false;
            }
        },
    },
});
