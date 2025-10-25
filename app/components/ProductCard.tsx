'use client'

import { useState } from 'react'
import Image from 'next/image'
import { Product } from '../types'
import { useAuthStore } from '../store/authStore'
import { useCartStore } from '../store/cartStore'
import { useRouter } from 'next/navigation'

interface Props {
  product: Product
}

export default function ProductCard({ product }: Props) {
  const { user } = useAuthStore()
  const { addItem } = useCartStore()
  const router = useRouter()
  const [adding, setAdding] = useState(false)

  const handleAddToCart = async () => {
    if (!user) {
      router.push('/login')
      return
    }

    try {
      setAdding(true)
      await addItem(product.id, 1)
      alert('Added to cart!')
    } catch (error) {
      alert('Failed to add to cart')
    } finally {
      setAdding(false)
    }
  }

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition">
      <div className="relative h-48 w-full">
        <Image
          src={product.image}
          alt={product.name}
          fill
          className="object-cover"
        />
      </div>
      <div className="p-4">
        <h3 className="text-lg font-semibold text-gray-900 mb-2">{product.name}</h3>
        <p className="text-gray-600 text-sm mb-3 line-clamp-2">{product.description}</p>
        <div className="flex items-center justify-between">
          <span className="text-2xl font-bold text-pink-600">${product.price}</span>
          <button
            onClick={handleAddToCart}
            disabled={adding || product.stock === 0}
            className="bg-pink-600 hover:bg-pink-700 text-white px-4 py-2 rounded disabled:bg-gray-400 disabled:cursor-not-allowed transition"
          >
            {adding ? 'Adding...' : product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
          </button>
        </div>
      </div>
    </div>
  )
}
