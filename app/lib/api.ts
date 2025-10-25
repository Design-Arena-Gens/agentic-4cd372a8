import axios from 'axios'
import { Product, CartItem, Order } from '../types'

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

const axiosInstance = axios.create({
  baseURL: `${API_URL}/api`,
  headers: {
    'Content-Type': 'application/json',
  },
})

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const api = {
  // Auth
  register: async (email: string, password: string, name: string) => {
    const { data } = await axiosInstance.post('/auth/register', { email, password, name })
    return data
  },

  login: async (email: string, password: string) => {
    const { data } = await axiosInstance.post('/auth/login', { email, password })
    return data
  },

  // Products
  getProducts: async (): Promise<Product[]> => {
    const { data } = await axiosInstance.get('/products')
    return data
  },

  getProduct: async (id: number): Promise<Product> => {
    const { data } = await axiosInstance.get(`/products/${id}`)
    return data
  },

  // Cart
  getCart: async (): Promise<CartItem[]> => {
    const { data } = await axiosInstance.get('/cart')
    return data
  },

  addToCart: async (productId: number, quantity: number) => {
    const { data } = await axiosInstance.post('/cart', { productId, quantity })
    return data
  },

  updateCartItem: async (id: number, quantity: number) => {
    const { data } = await axiosInstance.put(`/cart/${id}`, { quantity })
    return data
  },

  removeFromCart: async (id: number) => {
    await axiosInstance.delete(`/cart/${id}`)
  },

  clearCart: async () => {
    await axiosInstance.delete('/cart')
  },

  // Payment
  createPaymentIntent: async (amount: number) => {
    const { data } = await axiosInstance.post('/payment/create-intent', { amount })
    return data
  },

  confirmPayment: async (paymentIntentId: string) => {
    const { data } = await axiosInstance.post('/payment/confirm', { paymentIntentId })
    return data
  },

  getOrders: async (): Promise<Order[]> => {
    const { data } = await axiosInstance.get('/payment/orders')
    return data
  },
}
