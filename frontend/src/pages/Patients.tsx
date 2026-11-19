import { useEffect, useState, type FormEvent } from 'react'
import { Plus, Search, X } from 'lucide-react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { Patient } from '../types'

export default function Patients() {
  const { user } = useAuth()

  const [items, setItems] = useState<Patient[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)

  const [form, setForm] = useState({
    name: '',
    mobile: '',
    dob: '',
    gender: '',
    address: ''
  })

  async function load() {
    setLoading(true)
    setError('')

    try {
      const response = await api.get(
        `/patients/clinic/${user?.clinicId}`
      )

      setItems(response.data || [])
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to load patients.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [user?.clinicId])

  async function submit(e: FormEvent) {
    e.preventDefault()

    try {
      await api.post('/patients', {
        clinicId: user?.clinicId,
        ...form
      })

      setOpen(false)

      setForm({
        name: '',
        mobile: '',
        dob: '',
        gender: '',
        address: ''
      })

      await load()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Patient registration failed.')
    }
  }

  async function search() {
    if (!query.trim()) {
      load()
      return
    }

    setLoading(true)

    try {
      const response = await api.get('/patients/search', {
        params: {
          clinicId: user?.clinicId,
          mobile: query.trim()
        }
      })

      setItems(
        Array.isArray(response.data)
          ? response.data
          : response.data ? [response.data] : []
      )
    } catch (err: any) {
      setError(err.response?.data?.message || 'Patient search failed.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Patients</h1>
          <p>Register and manage patient records.</p>
        </div>

        {['OWNER', 'RECEPTION'].includes(user?.role || '') && (
          <button className="button primary" onClick={() => setOpen(true)}>
            <Plus size={17} /> Add patient
          </button>
        )}
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="panel">
        <div className="toolbar">
          <div className="search-box">
            <Search size={17} />

            <input
              value={query}
              onChange={e => setQuery(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && search()}
              placeholder="Search by mobile number"
            />
          </div>

          <button className="button secondary" onClick={search}>
            Search
          </button>

          <button
            className="button ghost"
            onClick={() => {
              setQuery('')
              load()
            }}
          >
            Clear
          </button>
        </div>

        {loading ? (
          <div className="loading">Loading patients...</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Patient</th>
                  <th>Mobile</th>
                  <th>Date of birth</th>
                  <th>Gender</th>
                  <th>Patient ID</th>
                </tr>
              </thead>

              <tbody>
                {items.map(patient => (
                  <tr key={patient.id}>
                    <td><b>{patient.name}</b></td>
                    <td>{patient.mobile}</td>
                    <td>{patient.dob || '—'}</td>
                    <td>{patient.gender || '—'}</td>
                    <td>#{patient.id}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {items.length === 0 && (
              <div className="empty-state">No patients found.</div>
            )}
          </div>
        )}
      </div>

      {open && (
        <div className="modal-backdrop">
          <div className="modal">
            <div className="modal-head">
              <h3>Register a patient</h3>

              <button
                className="icon-button"
                onClick={() => setOpen(false)}
              >
                <X />
              </button>
            </div>

            <form onSubmit={submit} className="form-grid">
              <Field label="Full name">
                <input
                  required
                  value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })}
                />
              </Field>

              <Field label="Mobile number">
                <input
                  required
                  value={form.mobile}
                  onChange={e => setForm({ ...form, mobile: e.target.value })}
                />
              </Field>

              <Field label="Date of birth">
                <input
                  type="date"
                  value={form.dob}
                  onChange={e => setForm({ ...form, dob: e.target.value })}
                />
              </Field>

              <Field label="Gender">
                <select
                  value={form.gender}
                  onChange={e => setForm({ ...form, gender: e.target.value })}
                >
                  <option value="">Select</option>
                  <option>Male</option>
                  <option>Female</option>
                  <option>Other</option>
                </select>
              </Field>

              <Field label="Address">
                <input
                  value={form.address}
                  onChange={e => setForm({ ...form, address: e.target.value })}
                />
              </Field>

              <button className="button primary full">
                Save patient
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
  return (
    <label className="field">
      {label}
      {children}
    </label>
  )
}