import { useState, type FormEvent } from 'react'
import { Navigate } from 'react-router-dom'
import { HeartPulse, LockKeyhole, Smartphone, Building2 } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { user, login } = useAuth()

  const [clinicId, setClinicId] = useState('1')
  const [mobile, setMobile] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) return <Navigate to="/" replace />

  async function submit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setBusy(true)

    try {
      await login(Number(clinicId), mobile.trim(), password)
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
        'Invalid clinic ID, mobile number or password.'
      )
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="login-screen">
      <div className="login-art">
        <div className="login-brand">
          <HeartPulse size={25} /> MediQueue
        </div>

        <div className="art-copy">
          <span className="eyebrow">SMARTER CLINIC OPERATIONS</span>
          <h1>Better care starts with a <em>better queue.</em></h1>
          <p>
            One connected workspace for your clinic team,
            patient flow and daily operations.
          </p>
        </div>

        <div className="art-bottom">
          Simple. Secure. Patient-first.
        </div>
      </div>

      <div className="login-panel">
        <form className="login-card" onSubmit={submit}>
          <span className="mobile-brand">
            <HeartPulse /> MediQueue
          </span>

          <span className="eyebrow">WELCOME BACK</span>
          <h2>Sign in to your clinic</h2>
          <p className="muted">
            Enter your account details to continue.
          </p>

          <label>Clinic ID</label>
          <div className="input-icon">
            <Building2 size={17} />
            <input
              type="number"
              min="1"
              value={clinicId}
              onChange={e => setClinicId(e.target.value)}
              required
            />
          </div>

          <label>Mobile number</label>
          <div className="input-icon">
            <Smartphone size={17} />
            <input
              value={mobile}
              onChange={e => setMobile(e.target.value)}
              placeholder="Registered mobile number"
              required
            />
          </div>

          <label>Password</label>
          <div className="input-icon">
            <LockKeyhole size={17} />
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="Enter password"
              required
            />
          </div>

          {error && <div className="alert error">{error}</div>}

          <button className="button primary full" disabled={busy}>
            {busy ? 'Signing in...' : 'Sign in securely'}
          </button>

          <p className="login-foot">
            Access is restricted to authorized clinic staff.
          </p>
        </form>
      </div>
    </div>
  )
}