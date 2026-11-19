import {
  createContext,
  useContext,
  useMemo,
  useState,
  type ReactNode
} from 'react'

import api from '../services/api'
import type { AuthUser } from '../types'

interface AuthContextType {
  user: AuthUser | null
  loading: boolean
  login: (
    clinicId: number,
    mobile: string,
    password: string
  ) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({
  children
}: {
  children: ReactNode
}) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const savedUser = localStorage.getItem('clinic_user')

    return savedUser
      ? JSON.parse(savedUser) as AuthUser
      : null
  })

  async function login(
    clinicId: number,
    mobile: string,
    password: string
  ) {
    const response = await api.post('/auth/login', {
      clinicId,
      mobile,
      password
    })

    const userData: AuthUser = response.data

    localStorage.setItem('clinic_token', userData.token)
    localStorage.setItem(
      'clinic_user',
      JSON.stringify(userData)
    )

    setUser(userData)
  }

  function logout() {
    localStorage.removeItem('clinic_token')
    localStorage.removeItem('clinic_user')

    setUser(null)
    window.location.href = '/login'
  }

  const value = useMemo(
    () => ({
      user,
      loading: false,
      login,
      logout
    }),
    [user]
  )

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error(
      'useAuth must be used inside AuthProvider'
    )
  }

  return context
}