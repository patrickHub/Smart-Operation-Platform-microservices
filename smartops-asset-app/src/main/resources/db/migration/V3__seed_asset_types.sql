INSERT INTO asset.asset_types (
    id,
    code,
    name,
    manufacturer,
    model,
    description,
    created_at,
    updated_at
)
VALUES
(   
    '272a7b44-218f-4a70-b0cc-93863af8c729',
    'Vent-C6-0000001',
    'C6 Familly',
    'Hamilton Medical',
    'C6',
    'Type of C6 familly ventillators',
    NOW(),
    NOW()
)
ON CONFLICT (code) DO NOTHING;