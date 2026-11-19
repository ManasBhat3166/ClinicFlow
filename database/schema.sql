
-- ==========================================
-- CLINIC QUEUE MANAGEMENT SYSTEM
-- Initial Database Schema
-- ==========================================

-- 1. CLINICS
CREATE TABLE clinics (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address TEXT,
    phone VARCHAR(15),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. USERS
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinics(id),
    name VARCHAR(120) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('OWNER', 'DOCTOR', 'RECEPTION')),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (clinic_id, mobile)
);

-- 3. DOCTORS
CREATE TABLE doctors (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinics(id),
    user_id BIGINT UNIQUE REFERENCES users(id),
    name VARCHAR(120) NOT NULL,
    specialization VARCHAR(120),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 4. PATIENTS
CREATE TABLE patients (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinics(id),
    name VARCHAR(120) NOT NULL,
    mobile VARCHAR(15),
    date_of_birth DATE,
    gender VARCHAR(20),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 5. CLINIC SETTINGS
CREATE TABLE clinic_settings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id BIGINT NOT NULL UNIQUE REFERENCES clinics(id),
    online_booking BOOLEAN DEFAULT TRUE,
    daily_token_limit INTEGER NOT NULL DEFAULT 60,
    opd_start TIME,
    opd_end TIME,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 6. TOKENS
CREATE TABLE tokens (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinics(id),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),

    token_number INTEGER NOT NULL,
    token_date DATE NOT NULL,

    source VARCHAR(20) NOT NULL
        CHECK (source IN ('ONLINE', 'WALK_IN')),

    status VARCHAR(20) NOT NULL DEFAULT 'WAITING'
        CHECK (status IN (
            'WAITING',
            'CALLED',
            'IN_PROGRESS',
            'COMPLETED',
            'SKIPPED',
            'NO_SHOW',
            'CANCELLED'
        )),

    booked_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (clinic_id, doctor_id, token_date, token_number)
);

-- 7. VISITS
CREATE TABLE visits (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinics(id),
    token_id BIGINT NOT NULL UNIQUE REFERENCES tokens(id),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),

    visit_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ
);

-- 8. VISIT NOTES
CREATE TABLE visit_notes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    visit_id BIGINT NOT NULL UNIQUE REFERENCES visits(id),
    symptoms TEXT,
    diagnosis TEXT,
    prescription TEXT,
    advice TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- INDEXES
-- ==========================================

CREATE INDEX idx_patients_mobile
ON patients(clinic_id, mobile);

CREATE INDEX idx_tokens_queue
ON tokens(clinic_id, doctor_id, token_date, status, token_number);

CREATE INDEX idx_tokens_patient
ON tokens(patient_id, token_date DESC);

CREATE INDEX idx_visits_patient
ON visits(patient_id, visit_date DESC);

CREATE INDEX idx_doctors_clinic
ON doctors(clinic_id);

-- ==========================================
-- END OF INITIAL SCHEMA
-- ==========================================
