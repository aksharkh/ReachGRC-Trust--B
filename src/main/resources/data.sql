-- ===============================
-- COMPANY
-- ===============================
INSERT INTO companies (
    company_name,
    statement,
    is_active,
    created_at,
    updated_at
) VALUES (
    'Reach GRC',
    'Risk & Compliance Management Platform',
    TRUE,
    NOW(),
    NOW()
);

-- ===============================
-- DOMAINS
-- ===============================
INSERT INTO domain (
    name,
    company_id,
    created_at,
    updated_at
) VALUES
(
    'Information Security',
    1,
    NOW(),
    NOW()
),
(
    'Compliance',
    1,
    NOW(),
    NOW()
);

-- ===============================
-- CONTROLS
-- ===============================
INSERT INTO controls (
    name,
    status,
    domain_id,
    remarks,
    created_at,
    updated_at
) VALUES
(
    'Access Control',
    'PENDING',
    1,
    'Ensure role-based access',
    NOW(),
    NOW()
),
(
    'Encryption',
    'OK',
    1,
    'Data encryption at rest',
    NOW(),
    NOW()
),
(
    'Audit Logging',
    'NOT_OK',
    2,
    'Track all system activities',
    NOW(),
    NOW()
);
