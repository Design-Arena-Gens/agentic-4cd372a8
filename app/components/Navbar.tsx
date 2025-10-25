'use client'

import Link from 'next/link'
import { useEffect, useState } from 'react'
import { useAuthStore } from '../store/authStore'
import { useCartStore } from '../store/cartStore'

export default function Navbar() {
  const { user, logout } = useAuthStore()
  const { items, fetchCart } = useCartStore()
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
    if (user) {
      fetchCart()
    }
  }, [user])

  const handleLogout = () => {
    logout()
    window.location.href = '/'
  }

  if (!mounted) return null

  return (
    <nav className="bg-pink-600 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link href="/" className="text-2xl font-bold">
            🎂 Cake Shop
          </Link>

          <div className="flex items-center space-x-6">
            <Link href="/" className="hover:text-pink-200 transition">
              Home
            </Link>

            {user ? (
              <>
                <Link href="/cart" className="hover:text-pink-200 transition relative">
                  Cart
                  {items.length > 0 && (
                    <span className="absolute -top-2 -right-2 bg-yellow-400 text-pink-900 text-xs rounded-full h-5 w-5 flex items-center justify-center font-bold">
                      {items.length}
                    </span>
                  )}
                </Link>
                <Link href="/orders" className="hover:text-pink-200 transition">
                  Orders
                </Link>
                <span className="text-pink-200">Hi, {user.name}</span>
                <button
                  onClick={handleLogout}
                  className="bg-pink-700 hover:bg-pink-800 px-4 py-2 rounded transition"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  href="/login"
                  className="bg-pink-700 hover:bg-pink-800 px-4 py-2 rounded transition"
                >
                  Login
                </Link>
                <Link
                  href="/register"
                  className="bg-yellow-400 text-pink-900 hover:bg-yellow-300 px-4 py-2 rounded font-semibold transition"
                >
                  Register
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}
