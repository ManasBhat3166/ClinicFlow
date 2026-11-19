import { useEffect, useState } from 'react'

import api from '../services/api'
import { useAuth } from '../context/AuthContext'
import type { Token } from '../types'

export default function WaitingRoom() {
  const { user } = useAuth()

  const [called, setCalled] = useState<Token[]>([])
  const [queue, setQueue] = useState<Token[]>([])

  async function load() {
    try {
      const [calledResponse, queueResponse] =
        await Promise.all([
          api.get('/tokens/clinic-called', {
            params: { clinicId: user?.clinicId }
          }),
          api.get('/tokens/clinic-queue', {
            params: { clinicId: user?.clinicId }
          })
        ])

      setCalled(calledResponse.data || [])

      setQueue(
        (queueResponse.data || [])
          .filter((token: Token) => token.status === 'WAITING')
          .slice(0, 8)
      )
    } catch (error) {
      console.error('Waiting room update failed:', error)
    }
  }

  useEffect(() => {
    load()

    const timer = window.setInterval(load, 10000)

    return () => window.clearInterval(timer)
  }, [user?.clinicId])

  return (
    <div className="waiting-screen">

      <div className="waiting-header">
        <span className="login-brand">
          MediQueue
        </span>

        <span>PLEASE WAIT FOR YOUR TOKEN</span>
      </div>

      <div className="waiting-main">

        <div>
          <span className="eyebrow">NOW CALLING</span>
          <h1>Current tokens</h1>

          <div className="called-grid">
            {called.length > 0 ? (
              called.map(token => (
                <div className="called-card" key={token.id}>
                  <small>DOCTOR {token.doctorId}</small>
                  <strong>{token.tokenNumber}</strong>
                  <span>Proceed to consultation</span>
                </div>
              ))
            ) : (
              <div className="waiting-empty">
                No patient is being called right now.
              </div>
            )}
          </div>
        </div>

        <div className="waiting-list">
          <h2>Up next</h2>

          {queue.map(token => (
            <div className="up-next" key={token.id}>
              <b>#{token.tokenNumber}</b>
              <span>Doctor {token.doctorId}</span>
            </div>
          ))}

          {queue.length === 0 && (
            <p>Queue is currently clear.</p>
          )}
        </div>

      </div>

      <footer>
        Thank you for choosing our clinic.
        Please keep your token ready.
      </footer>

    </div>
  )
}