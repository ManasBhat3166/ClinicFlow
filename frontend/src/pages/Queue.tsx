import { useEffect, useState, type FormEvent } from 'react'
import { Plus, RefreshCw, PhoneCall } from 'lucide-react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { Doctor, Patient, Token } from '../types'

export default function Queue() {
  const { user } = useAuth()

  const [tokens, setTokens] = useState<Token[]>([])
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [patients, setPatients] = useState<Patient[]>([])

  const [doctorId, setDoctorId] = useState('')
  const [patientId, setPatientId] = useState('')
  const [source, setSource] = useState('WALK_IN')

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function load() {
    setLoading(true)

    try {
      const [queueResponse, doctorResponse, patientResponse] =
        await Promise.all([
          api.get('/tokens/clinic-queue', {
            params: { clinicId: user?.clinicId }
          }),
          api.get(`/doctors/clinic/${user?.clinicId}`),
          api.get(`/patients/clinic/${user?.clinicId}`)
        ])

      setTokens(queueResponse.data || [])
      setDoctors(doctorResponse.data || [])
      setPatients(patientResponse.data || [])

      if (!doctorId && doctorResponse.data?.length) {
        setDoctorId(String(doctorResponse.data[0].id))
      }

      setError('')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to load queue.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [user?.clinicId])

  async function generate(e: FormEvent) {
    e.preventDefault()

    try {
      await api.post('/tokens', {
        clinicId: user?.clinicId,
        patientId: Number(patientId),
        doctorId: Number(doctorId),
        source
      })

      setPatientId('')
      setError('')
      await load()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Token generation failed.')
    }
  }

  async function callNext() {
    try {
      await api.post('/tokens/call-next', null, {
        params: {
          clinicId: user?.clinicId,
          doctorId: Number(doctorId)
        }
      })

      setError('')
      await load()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Unable to call next patient.')
    }
  }

  async function changeStatus(id: number, status: string) {
    try {
      await api.patch(`/tokens/${id}/status`, null, {
        params: { status }
      })

      await load()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Token update failed.')
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Queue management</h1>
          <p>Generate tokens and manage today's patient flow.</p>
        </div>

        <button className="button secondary" onClick={load}>
          <RefreshCw size={16} /> Refresh
        </button>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="content-grid queue-grid">
        <div className="panel">
          <div className="panel-head">
            <div>
              <h3>Generate a token</h3>
              <p>Create a walk-in or online booking token.</p>
            </div>
            <Plus />
          </div>

          <form className="form-grid" onSubmit={generate}>
            <Field label="Patient">
              <select
                required
                value={patientId}
                onChange={e => setPatientId(e.target.value)}
              >
                <option value="">Select patient</option>

                {patients.map(patient => (
                  <option key={patient.id} value={patient.id}>
                    {patient.name} · {patient.mobile}
                  </option>
                ))}
              </select>
            </Field>

            <Field label="Doctor">
              <select
                required
                value={doctorId}
                onChange={e => setDoctorId(e.target.value)}
              >
                <option value="">Select doctor</option>

                {doctors
                  .filter(d => d.active)
                  .map(doctor => (
                    <option key={doctor.id} value={doctor.id}>
                      {doctor.name}
                    </option>
                  ))}
              </select>
            </Field>

            <Field label="Booking source">
              <select
                value={source}
                onChange={e => setSource(e.target.value)}
              >
                <option value="WALK_IN">Walk-in</option>
                <option value="ONLINE">Online</option>
              </select>
            </Field>

            <button className="button primary full">
              Generate token
            </button>
          </form>
        </div>

        <div className="panel call-panel">
          <span className="stat-icon purple">
            <PhoneCall />
          </span>

          <h3>Call next patient</h3>

          <p>
            Select a doctor and call the next waiting patient.
          </p>

          <Field label="Doctor">
            <select
              value={doctorId}
              onChange={e => setDoctorId(e.target.value)}
            >
              {doctors.map(doctor => (
                <option key={doctor.id} value={doctor.id}>
                  {doctor.name}
                </option>
              ))}
            </select>
          </Field>

          <button
            className="button primary full"
            disabled={!doctorId || user?.role !== 'DOCTOR'}
            onClick={callNext}
          >
            Call next patient
          </button>

          {user?.role !== 'DOCTOR' && (
            <small className="muted">
              Only doctors can call the next patient.
            </small>
          )}
        </div>
      </div>

      <div className="panel">
        <div className="panel-head">
          <div>
            <h3>Today's queue</h3>
            <p>{tokens.length} tokens generated</p>
          </div>
        </div>

        {loading ? (
          <div className="loading">Loading queue...</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Token</th>
                  <th>Patient ID</th>
                  <th>Doctor ID</th>
                  <th>Source</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {tokens.map(token => (
                  <tr key={token.id}>
                    <td><b>#{token.tokenNumber}</b></td>
                    <td>{token.patientId}</td>
                    <td>{token.doctorId}</td>
                    <td>{token.source}</td>
                    <td>
                      <span className={`badge ${token.status.toLowerCase()}`}>
                        {token.status}
                      </span>
                    </td>

                    <td>
                      <div className="table-actions">
                        {token.status === 'CALLED' && user?.role === 'DOCTOR' && (
                          <button onClick={() => changeStatus(token.id, 'IN_PROGRESS')}>
                            Start
                          </button>
                        )}

                        {['WAITING', 'CALLED'].includes(token.status) &&
                          ['DOCTOR', 'RECEPTION'].includes(user?.role || '') && (
                            <button onClick={() => changeStatus(token.id, 'SKIPPED')}>
                              Skip
                            </button>
                        )}

                        {token.status === 'CALLED' && user?.role === 'DOCTOR' && (
                          <button onClick={() => changeStatus(token.id, 'NO_SHOW')}>
                            No show
                          </button>
                        )}

                        {token.status === 'WAITING' && user?.role === 'RECEPTION' && (
                          <button onClick={() => changeStatus(token.id, 'CANCELLED')}>
                            Cancel
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {tokens.length === 0 && (
              <div className="empty-state">
                No tokens generated today.
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