import { useEffect, useState, type FormEvent } from 'react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { ClinicSetting } from '../types'

export default function Settings() {
  const { user } = useAuth()

  const [form, setForm] = useState<ClinicSetting>({
    clinicId: user?.clinicId || 1,
    onlineBooking: false,
    dailyTokenLimit: 50,
    opdStart: '09:00',
    opdEnd: '17:00'
  })

  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  useEffect(() => {
    api.get(`/clinic-settings/${user?.clinicId}`)
      .then(response => setForm(response.data))
      .catch(() => {
        setMessage('No settings found. Configure your clinic below.')
      })
      .finally(() => setLoading(false))
  }, [user?.clinicId])

  async function save(e: FormEvent) {
    e.preventDefault()
    setMessage('')

    try {
      await api.post('/clinic-settings', form)
      setMessage('Clinic settings saved successfully.')
    } catch (err: any) {
      setMessage(
        err.response?.data?.message || 'Unable to save clinic settings.'
      )
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Clinic settings</h1>
          <p>Configure online booking and daily OPD hours.</p>
        </div>
      </div>

      {loading ? (
        <div className="loading">Loading settings...</div>
      ) : (
        <div className="panel settings-panel">
          <form className="form-grid" onSubmit={save}>
            <Field label="Daily token limit">
              <input
                type="number"
                min="1"
                required
                value={form.dailyTokenLimit}
                onChange={e =>
                  setForm({
                    ...form,
                    dailyTokenLimit: Number(e.target.value)
                  })
                }
              />
            </Field>

            <Field label="OPD opening time">
              <input
                type="time"
                required
                value={form.opdStart}
                onChange={e =>
                  setForm({ ...form, opdStart: e.target.value })
                }
              />
            </Field>

            <Field label="OPD closing time">
              <input
                type="time"
                required
                value={form.opdEnd}
                onChange={e =>
                  setForm({ ...form, opdEnd: e.target.value })
                }
              />
            </Field>

            <label className="toggle-row">
              <span>
                <b>Online booking</b>
                <small>
                  Allow patients to book tokens online.
                </small>
              </span>

              <input
                type="checkbox"
                checked={form.onlineBooking}
                onChange={e =>
                  setForm({
                    ...form,
                    onlineBooking: e.target.checked
                  })
                }
              />
            </label>

            <button className="button primary">
              Save clinic settings
            </button>
          </form>

          {message && (
            <div className="alert info">{message}</div>
          )}
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