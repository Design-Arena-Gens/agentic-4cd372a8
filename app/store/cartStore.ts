import { create } from 'zustand'
import { CartItem } from '../types'
import { api } from '../lib/api'

interface CartState {
  items: CartItem[]
  loading: boolean
  fetchCart: () => Promise<void>
  addItem: (productId: number, quantity: number) => Promise<void>
  updateItem: (id: number, quantity: number) => Promise<void>
  removeItem: (id: number) => Promise<void>
  clearCart: () => Promise<void>
  getTotal: () => number
}

export const useCartStore = create<CartState>((set, get) => ({
  items: [],
  loading: false,

  fetchCart: async () => {
    try {
      set({ loading: true })
      const items = await api.getCart()
      set({ items })
    } catch (error) {
      console.error('Failed to fetch cart:', error)
    } finally {
      set({ loading: false })
    }
  },

  addItem: async (productId: number, quantity: number) => {
    try {
      await api.addToCart(productId, quantity)
      await get().fetchCart()
    } catch (error) {
      console.error('Failed to add item:', error)
      throw error
    }
  },

  updateItem: async (id: number, quantity: number) => {
    try {
      await api.updateCartItem(id, quantity)
      await get().fetchCart()
    } catch (error) {
      console.error('Failed to update item:', error)
      throw error
    }
  },

  removeItem: async (id: number) => {
    try {
      await api.removeFromCart(id)
      await get().fetchCart()
    } catch (error) {
      console.error('Failed to remove item:', error)
      throw error
    }
  },

  clearCart: async () => {
    try {
      await api.clearCart()
      set({ items: [] })
    } catch (error) {
      console.error('Failed to clear cart:', error)
      throw error
    }
  },

  getTotal: () => {
    const { items } = get()
    return items.reduce((total, item) => total + item.product.price * item.quantity, 0)
  },
}))
