import { NavLink, Outlet } from 'react-router-dom'

import {
  Activity,
  LayoutDashboard,
  Users,
  Stethoscope,
  ListOrdered,
  ClipboardList,
  Settings,
  Monitor,
  LogOut,
  HeartPulse
} from 'lucide-react'

import { useAuth } from '../context/AuthContext'

export default function Layout() {

  const { user, logout } = useAuth()

  const links = [
    {
      to: '/',
      label: 'Overview',
      icon: LayoutDashboard,
      roles: ['OWNER', 'DOCTOR', 'RECEPTION']
    },
    {
      to: '/patients',
      label: 'Patients',
      icon: Users,
      roles: ['OWNER', 'DOCTOR', 'RECEPTION']
    },
    {
      to: '/doctors',
      label: 'Doctors',
      icon: Stethoscope,
      roles: ['OWNER', 'DOCTOR', 'RECEPTION']
    },
    {
      to: '/queue',
      label: 'Queue Management',
      icon: ListOrdered,
      roles: ['OWNER', 'DOCTOR', 'RECEPTION']
    },
    {
      to: '/visits',
      label: 'Consultations',
      icon: ClipboardList,
      roles: ['OWNER', 'DOCTOR']
    },
    {
      to: '/waiting-room',
      label: 'Waiting Room',
      icon: Monitor,
      roles: ['OWNER', 'DOCTOR', 'RECEPTION']
    },
    {
      to: '/settings',
      label: 'Clinic Settings',
      icon: Settings,
      roles: ['OWNER']
    }
  ]

  return (
    <div className="app-shell">

      <aside className="sidebar">

        <div className="brand">
          <span className="brand-icon">
            <HeartPulse size={22} />
          </span>

          <span>
            MediQueue
            <small>CLINIC MANAGEMENT</small>
          </span>
        </div>

        <div className="nav-caption">
          WORKSPACE
        </div>

        <nav>
          {links
            .filter(link =>
              link.roles.includes(user?.role || '')
            )
            .map(({ to, label, icon: Icon }) => (

              <NavLink
                key={to}
                to={to}
                end={to === '/'}
                className={({ isActive }) =>
                  `nav-link ${isActive ? 'active' : ''}`
                }
              >
                <Icon size={18} />
                <span>{label}</span>
              </NavLink>

            ))}
        </nav>

        <div className="sidebar-bottom">

          <div className="clinic-mini">
            <span className="avatar">
              {user?.name?.charAt(0).toUpperCase()}
            </span>

            <div>
              <b>{user?.name}</b>
              <small>{user?.role}</small>
            </div>
          </div>

          <button
            className="logout-button"
            onClick={logout}
          >
            <LogOut size={17} />
            Sign out
          </button>

        </div>

      </aside>

      <main className="main-area">

        <header className="topbar">

          <div>
            <span className="topbar-label">
              CLINIC WORKSPACE
            </span>

            <h2>
              Welcome back, {user?.name?.split(' ')[0]}
            </h2>
          </div>

          <div className="topbar-right">
            <span className="status-dot" />
            <span>System connected</span>

            <span className="top-avatar">
              {user?.name?.charAt(0).toUpperCase()}
            </span>
          </div>

        </header>

        <section className="page-content">
          <Outlet />
        </section>

        <footer className="footer">
          <span>
            <Activity size={14} />
            MediQueue Clinic Management
          </span>

          <span>Secure clinic workspace</span>
        </footer>

      </main>

    </div>
  )
}