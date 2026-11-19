import { useEffect, useState, type FormEvent } from 'react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { Visit, Patient } from '../types'

export default function Visits() {
  const { user } = useAuth()

  const [items, setItems] = useState<Visit[]>([])
  const [patients, setPatients] = useState<Patient[]>([])

  const [patientId, setPatientId] = useState('')
  const [tokenId, setTokenId] = useState('')
  const [visitId, setVisitId] = useState('')

  const [notes, setNotes] = useState({
    symptoms: '',
    diagnosis: '',
    prescription: '',
    advice: ''
  })

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function loadPatients() {
    try {
      const response = await api.get(
        `/patients/clinic/${user?.clinicId}`
      )

      setPatients(response.data || [])
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to load patients.')
    }
  }

  useEffect(() => {
    loadPatients()
  }, [user?.clinicId])

  async function history(e: FormEvent) {
    e.preventDefault()

    setLoading(true)

    try {
      const response = await api.get(
        `/visits/patient/${patientId}`
      )

      setItems(response.data || [])
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to load visit history.')
    } finally {
      setLoading(false)
    }
  }

  async function start(e: FormEvent) {
    e.preventDefault()

    try {
      const response = await api.post('/visits/start', {
        tokenId: Number(tokenId)
      })

      setVisitId(String(response.data.id))
      setError('')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to start visit.')
    }
  }

  async function saveNotes(e: FormEvent) {
    e.preventDefault()

    try {
      await api.post('/visit-notes', {
        visitId: Number(visitId),
        ...notes
      })

      setError('Consultation notes saved successfully.')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to save notes.')
    }
  }

  async function complete(id: number) {
    try {
      await api.patch(`/visits/${id}/complete`)
      setError('Visit completed successfully.')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to complete visit.')
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Consultations</h1>
          <p>Start visits, record notes and review patient history.</p>
        </div>
      </div>

      {error && <div className="alert info">{error}</div>}

      <div className="content-grid">
        <div className="panel">
          <h3>Start consultation</h3>
          <p className="panel-subtitle">
            Enter the database ID of a token in CALLED status.
          </p>

          <form className="form-grid" onSubmit={start}>
            <Field label="Token database ID">
              <input
                type="number"
                min="1"
                required
                value={tokenId}
                onChange={e => setTokenId(e.target.value)}
              />
            </Field>

            <button className="button primary full">
              Start visit
            </button>
          </form>

          {visitId && (
            <div className="success-block">
              Visit created. Visit ID: <b>{visitId}</b>
            </div>
          )}
        </div>

        <div className="panel">
          <h3>Clinical notes</h3>

          <form className="form-grid" onSubmit={saveNotes}>
            <Field label="Visit ID">
              <input
                type="number"
                required
                value={visitId}
                onChange={e => setVisitId(e.target.value)}
              />
            </Field>

            {(
              ['symptoms', 'diagnosis', 'prescription', 'advice'] as const
            ).map(key => (
              <Field key={key} label={key.toUpperCase()}>
                <textarea
                  rows={2}
                  value={notes[key]}
                  onChange={e =>
                    setNotes({ ...notes, [key]: e.target.value })
                  }
                />
              </Field>
            ))}

            <button className="button primary full">
              Save consultation notes
            </button>
          </form>
        </div>
      </div>

      <div className="panel">
        <h3>Patient visit history</h3>

        <form className="toolbar" onSubmit={history}>
          <select
            required
            value={patientId}
            onChange={e => setPatientId(e.target.value)}
          >
            <option value="">Select patient</option>

            {patients.map(patient => (
              <option key={patient.id} value={patient.id}>
                {patient.name} · #{patient.id}
              </option>
            ))}
          </select>

          <button className="button secondary">
            View history
          </button>
        </form>

        {loading ? (
          <div className="loading">Loading visits...</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Visit ID</th>
                  <th>Token ID</th>
                  <th>Visit date</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {items.map(visit => (
                  <tr key={visit.id}>
                    <td>#{visit.id}</td>
                    <td>{visit.tokenId}</td>
                    <td>{visit.visitDate}</td>
                    <td>
                      {visit.completedAt ? 'Completed' : 'In progress'}
                    </td>
                    <td>
                      {!visit.completedAt && user?.role === 'DOCTOR' && (
                        <button
                          className="text-button"
                          onClick={() => complete(visit.id)}
                        >
                          Complete visit
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {items.length === 0 && (
              <div className="empty-state">
                No visit history to display.
              </div>
            )}
          </div>
        )}
      </div>
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