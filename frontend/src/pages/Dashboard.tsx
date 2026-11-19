import { useEffect, useState } from 'react'
import {
  Users, Stethoscope, Clock3,
  CheckCircle2, RefreshCw
} from 'lucide-react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { Token, Doctor } from '../types'

export default function Dashboard() {
  const { user } = useAuth()

  const [queue, setQueue] = useState<Token[]>([])
  const [doctors, setDoctors] = useState<Doctor[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function load() {
    setLoading(true)
    setError('')

    try {
      const requests = [
        api.get('/tokens/clinic-queue', {
          params: { clinicId: user?.clinicId }
        })
      ]

      if (user?.role !== 'DOCTOR') {
        requests.push(
          api.get(`/doctors/clinic/${user?.clinicId}`)
        )
      }

      const results = await Promise.all(requests)

      setQueue(results[0].data || [])
      setDoctors(results[1]?.data || [])
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
        'Unable to load dashboard data.'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [user?.clinicId])

  const waiting = queue.filter(
    t => t.status === 'WAITING'
  ).length

  const active = queue.filter(
    t => ['CALLED', 'IN_PROGRESS'].includes(t.status)
  ).length

  const completed = queue.filter(
    t => t.status === 'COMPLETED'
  ).length

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Overview</h1>
          <p>Here's what's happening at your clinic today.</p>
        </div>

        <button className="button secondary" onClick={load}>
          <RefreshCw size={16} /> Refresh
        </button>
      </div>

      {error && <div className="alert error">{error}</div>}

      {loading ? (
        <div className="loading">Loading clinic data...</div>
      ) : (
        <>
          <div className="stat-grid">
            <Stat
              icon={<Users />}
              label="Today's tokens"
              value={queue.length}
              tone="blue"
            />

            <Stat
              icon={<Clock3 />}
              label="Waiting patients"
              value={waiting}
              tone="amber"
            />

            <Stat
              icon={<Stethoscope />}
              label="In consultation / called"
              value={active}
              tone="purple"
            />

            <Stat
              icon={<CheckCircle2 />}
              label="Completed"
              value={completed}
              tone="green"
            />
          </div>

          <div className="content-grid">
            <div className="panel">
              <div className="panel-head">
                <div>
                  <h3>Today's queue</h3>
                  <p>Live patient token activity</p>
                </div>
              </div>

              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Token</th>
                      <th>Patient ID</th>
                      <th>Doctor ID</th>
                      <th>Status</th>
                    </tr>
                  </thead>

                  <tbody>
                    {queue.slice(0, 8).map(token => (
                      <tr key={token.id}>
                        <td>#{token.tokenNumber}</td>
                        <td>{token.patientId}</td>
                        <td>{token.doctorId}</td>
                        <td>
                          <span className={`badge ${token.status.toLowerCase()}`}>
                            {token.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                {queue.length === 0 && (
                  <div className="empty-state">
                    No tokens generated today.
                  </div>
                )}
              </div>
            </div>

            <div className="panel">
              <div className="panel-head">
                <div>
                  <h3>Clinic team</h3>
                  <p>Registered doctors</p>
                </div>
              </div>

              {doctors.map(doctor => (
                <div className="doctor-row" key={doctor.id}>
                  <span className="doctor-avatar">
                    {doctor.name.charAt(0)}
                  </span>

                  <div>
                    <b>{doctor.name}</b>
                    <small>{doctor.specialization}</small>
                  </div>

                  <span className={`badge ${doctor.active ? 'completed' : 'cancelled'}`}>
                    {doctor.active ? 'Active' : 'Inactive'}
                  </span>
                </div>
              ))}

              {doctors.length === 0 && (
                <div className="empty-state">
                  No doctors available.
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </>
  )
}

function Stat({
  icon,
  label,
  value,
  tone
}: {
  icon: React.ReactNode
  label: string
  value: number
  tone: string
}) {
  return (
    <div className="stat-card">
      <span className={`stat-icon ${tone}`}>
        {icon}
      </span>

      <span className="stat-label">{label}</span>
      <strong>{value}</strong>
      <small>Today's activity</small>
    </div>
  )
}