INSERT INTO identity.users (
    id,
    username,
    password_hash,
    display_name,
    email,
    status,
    created_at,
    updated_at,
    version
)
VALUES
(
    '00000000-0000-0000-0000-000000000001',
    'admin',
    '$2a$10$XZreOR34tju522j4n7Vrb.ujNxAqQ7kicPZvMGk3m.fujIzNSliQK',
    'Platform Administrator',
    'admin@smartops.local',
    'ACTIVE',
    NOW(),
    NOW(),
    0
),
(
    '00000000-0000-0000-0000-000000000002',
    'support',
    '$2a$10$S.WQre1Oite0LTj/GeYdT.W1o9DbuxriRJ5FyDCsywanEDYB8tl66',
    'Support Agent',
    'support@smartops.local',
    'ACTIVE',
    NOW(),
    NOW(),
    0
),
(
    '00000000-0000-0000-0000-000000000003',
    'dispatcher',
    '$2a$10$QdEy7pkuzWqn2jGEIrMTGew4..HT3g7t9pY.ZChN1Au2162RrBs/.',
    'Dispatcher',
    'dispatcher@smartops.local',
    'ACTIVE',
    NOW(),
    NOW(),
    0
),
(
    '00000000-0000-0000-0000-000000000004',
    'technician',
    '$2a$10$K4uub7VICehRdve/0JgqfeeyU2C32lJ2qeyrGUs4flvcrAw8UYNV.',
    'Technician',
    'technician@smartops.local',
    'ACTIVE',
    NOW(),
    NOW(),
    0
),
(
    '00000000-0000-0000-0000-000000000005',
    'billing',
    '$2a$10$Jk9UH.k/AdXxTb9AQZgHV.b5DzxTgbeSBUcI2vz3fodShoa8bODzu',
    'Billing Manager',
    'billing@smartops.local',
    'ACTIVE',
    NOW(),
    NOW(),
    0
);

INSERT INTO identity.user_roles (user_id, role)
VALUES
('00000000-0000-0000-0000-000000000001', 'ADMIN'),
('00000000-0000-0000-0000-000000000002', 'SUPPORT_AGENT'),
('00000000-0000-0000-0000-000000000003', 'DISPATCHER'),
('00000000-0000-0000-0000-000000000004', 'TECHNICIAN'),
('00000000-0000-0000-0000-000000000005', 'BILLING_MANAGER');