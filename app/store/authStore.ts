import { create } from 'zustand'
import { User } from '../types'

interface AuthState {
  user: User | null
  setUser: (user: User | null) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  setUser: (user) => {
    if (user) {
      localStorage.setItem('token', user.token)
      localStorage.setItem('user', JSON.stringify(user))
    }
    set({ user })
  },
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    set({ user: null })
  },
}))

// Initialize from localStorage
if (typeof window !== 'undefined') {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    useAuthStore.setState({ user: JSON.parse(storedUser) })
  }
}
