-- ===============================
-- COMPANY
-- ===============================
INSERT INTO companies (
    company_name,
    statement,
    is_active,
    api_key,
    api_key_status,
    api_key_issued_at,
    api_key_expires_at,
    subscription_plan,
    subscription_status,
    subscription_expires_at,
    created_at,
    updated_at
) VALUES (
    'Reach GRC',
    'Risk & Compliance Management Platform',
    TRUE,
    'rgc_a3f9c2e1b74d6085fa91dc23e0b58764',
    'ACTIVE',
    NOW(),
    NOW() + INTERVAL '1 year',
    'FREE',
    'ACTIVE',
    NOW() + INTERVAL '1 year',
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
