import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from 'react-router-dom'

import {
  AuthProvider,
  useAuth
} from './context/AuthContext'

import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'

// Pages
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Patients from './pages/Patients'
import Doctors from './pages/Doctors'
import Queue from './pages/Queue'
import Visits from './pages/Visits'
import Settings from './pages/Settings'
import WaitingRoom from './pages/WaitingRoom'

function HomeRedirect() {
  const { user } = useAuth()

  return (
    <Navigate
      to={user ? '/' : '/login'}
      replace
    />
  )
}

function AppRoutes() {

  return (
    <Routes>

      {/* Public route */}
      <Route
        path="/login"
        element={<Login />}
      />

      {/* Authenticated routes */}
      <Route element={<ProtectedRoute />}>

        <Route element={<Layout />}>

          <Route
            path="/"
            element={<Dashboard />}
          />

          <Route
            path="/patients"
            element={<Patients />}
          />

          <Route
            path="/doctors"
            element={<Doctors />}
          />

          <Route
            path="/queue"
            element={<Queue />}
          />

          <Route element={
            <ProtectedRoute roles={['OWNER', 'DOCTOR']} />
          }>
            <Route
              path="/visits"
              element={<Visits />}
            />
          </Route>

          <Route element={
            <ProtectedRoute roles={['OWNER']} />
          }>
            <Route
              path="/settings"
              element={<Settings />}
            />
          </Route>

          <Route
            path="/waiting-room"
            element={<WaitingRoom />}
          />

        </Route>

      </Route>

      <Route
        path="*"
        element={<HomeRedirect />}
      />

    </Routes>
  )
}

export default function App() {

  return (
    <BrowserRouter>

      <AuthProvider>
        <AppRoutes />
      </AuthProvider>

    </BrowserRouter>
  )
}