import { useEffect, useState, type FormEvent } from 'react'
import { Plus, X, RefreshCw } from 'lucide-react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { Doctor } from '../types'

export default function Doctors() {
  const { user } = useAuth()

  const [items, setItems] = useState<Doctor[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [open, setOpen] = useState(false)

  const [form, setForm] = useState({
    name: '',
    mobile: '',
    password: '',
    specialization: ''
  })

  async function load() {
    setLoading(true)

    try {
      const response = await api.get(
        `/doctors/clinic/${user?.clinicId}`
      )

      setItems(response.data || [])
      setError('')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to load doctors.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [user?.clinicId])

  async function create(e: FormEvent) {
    e.preventDefault()

    try {
      await api.post('/doctors', {
        clinicId: user?.clinicId,
        ...form
      })

      setOpen(false)

      setForm({
        name: '',
        mobile: '',
        password: '',
        specialization: ''
      })

      load()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Doctor creation failed.')
    }
  }

  async function toggle(doctor: Doctor) {
    try {
      await api.patch(
        `/doctors/${doctor.id}/status`,
        null,
        { params: { active: !doctor.active } }
      )

      load()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Status update failed.')
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Doctors</h1>
          <p>Manage doctors registered with your clinic.</p>
        </div>

        <div className="toolbar">
          <button className="button secondary" onClick={load}>
            <RefreshCw size={16} /> Refresh
          </button>

          {user?.role === 'OWNER' && (
            <button className="button primary" onClick={() => setOpen(true)}>
              <Plus size={17} /> Add doctor
            </button>
          )}
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      {loading ? (
        <div className="loading">Loading doctors...</div>
      ) : (
        <div className="doctor-grid">
          {items.map(doctor => (
            <div className="panel doctor-card" key={doctor.id}>
              <div className="doctor-card-top">
                <span className="doctor-avatar large">
                  {doctor.name.charAt(0)}
                </span>

                <span className={`badge ${doctor.active ? 'completed' : 'cancelled'}`}>
                  {doctor.active ? 'Active' : 'Inactive'}
                </span>
              </div>

              <h3>{doctor.name}</h3>
              <p>{doctor.specialization}</p>

              <div className="doctor-meta">
                Doctor ID <b>#{doctor.id}</b>
              </div>

              {user?.role === 'OWNER' && (
                <button
                  className="button secondary full"
                  onClick={() => toggle(doctor)}
                >
                  {doctor.active ? 'Deactivate doctor' : 'Activate doctor'}
                </button>
              )}
            </div>
          ))}

          {items.length === 0 && (
            <div className="panel empty-state">
              No doctors registered yet.
            </div>
          )}
        </div>
      )}

      {open && (
        <div className="modal-backdrop">
          <div className="modal">
            <div className="modal-head">
              <h3>Add doctor</h3>

              <button
                className="icon-button"
                onClick={() => setOpen(false)}
              >
                <X />
              </button>
            </div>

            <form className="form-grid" onSubmit={create}>
              <Field label="Full name">
                <input
                  required
                  value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })}
                />
              </Field>

              <Field label="Mobile">
                <input
                  required
                  value={form.mobile}
                  onChange={e => setForm({ ...form, mobile: e.target.value })}
                />
              </Field>

              <Field label="Temporary password">
                <input
                  required
                  type="password"
                  value={form.password}
                  onChange={e => setForm({ ...form, password: e.target.value })}
                />
              </Field>

              <Field label="Specialization">
                <input
                  required
                  value={form.specialization}
                  onChange={e => setForm({ ...form, specialization: e.target.value })}
                />
              </Field>

              <button className="button primary full">
                Create doctor account
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  )
}

function Field({
  label,
  children
}: {
  label: string
  children: React.ReactNode
}) {
  return <label className="field">{label}{children}</label>
}