export type Role = 'OWNER' | 'DOCTOR' | 'RECEPTION'

export interface AuthUser {
  token: string
  tokenType?: string
  userId: number
  clinicId: number
  name: string
  mobile: string
  role: Role
}

export interface Patient {
  id: number
  clinicId: number
  name: string
  mobile: string
  dob?: string
  gender?: string
  address?: string
}

export interface Doctor {
  id: number
  clinicId: number
  userId?: number
  name: string
  specialization: string
  active: boolean
}

export interface Token {
  id: number
  clinicId: number
  patientId: number
  doctorId: number
  tokenNumber: number
  tokenDate: string
  source: string
  status: string
  bookedAt?: string
}

export interface Visit {
  id: number
  clinicId: number
  tokenId: number
  patientId: number
  doctorId: number
  visitDate: string
  startedAt?: string
  completedAt?: string
}

export interface ClinicSetting {
  id?: number
  clinicId: number
  onlineBooking: boolean
  dailyTokenLimit: number
  opdStart: string
  opdEnd: string
}