export interface Product {
  id: number
  name: string
  description: string
  price: number
  image: string
  category: string
  stock: number
}

export interface CartItem {
  id: number
  product: Product
  quantity: number
}

export interface User {
  email: string
  name: string
  token: string
}

export interface Order {
  id: number
  items: OrderItem[]
  totalAmount: number
  status: string
  createdAt: string
}

export interface OrderItem {
  id: number
  product: Product
  quantity: number
  price: number
}
