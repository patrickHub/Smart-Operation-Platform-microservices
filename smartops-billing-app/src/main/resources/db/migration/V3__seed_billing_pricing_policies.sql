INSERT INTO billing.pricing_policies (
    id,
    code,
    name,
    work_type,
    labor_rate,
    default_currency,
    active,
    valid_from,
    valid_to,
    rules_json,
    created_at,
    created_by,
    updated_at,
    updated_by
)
VALUES (
    '11111111-aaaa-4aaa-8aaa-111111111111',
    'CORRECTIVE-CHF-STD',
    'Standard corrective maintenance pricing',
    'CORRECTIVE',
    120.00,
    'CHF',
    TRUE,
    DATE '2026-01-01',
    NULL,
    '{"description":"Standard labor pricing for corrective work orders"}',
    NOW(),
    'system',
    NOW(),
    'system'
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO billing.pricing_policies (
    id,
    code,
    name,
    work_type,
    labor_rate,
    default_currency,
    active,
    valid_from,
    valid_to,
    rules_json,
    created_at,
    created_by,
    updated_at,
    updated_by
)
VALUES (
    '22222222-bbbb-4bbb-8bbb-222222222222',
    'PREVENTIVE-CHF-STD',
    'Standard preventive maintenance pricing',
    'PREVENTIVE',
    100.00,
    'CHF',
    TRUE,
    DATE '2026-01-01',
    NULL,
    '{"description":"Standard labor pricing for preventive work orders"}',
    NOW(),
    'system',
    NOW(),
    'system'
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO billing.pricing_policies (
    id,
    code,
    name,
    work_type,
    labor_rate,
    default_currency,
    active,
    valid_from,
    valid_to,
    rules_json,
    created_at,
    created_by,
    updated_at,
    updated_by
)
VALUES (
    '33333333-cccc-4ccc-8ccc-333333333333',
    'INSPECTION-CHF-STD',
    'Standard inspection pricing',
    'INSPECTION',
    90.00,
    'CHF',
    TRUE,
    DATE '2026-01-01',
    NULL,
    '{"description":"Standard labor pricing for inspection work orders"}',
    NOW(),
    'system',
    NOW(),
    'system'
)
ON CONFLICT (code) DO NOTHING;