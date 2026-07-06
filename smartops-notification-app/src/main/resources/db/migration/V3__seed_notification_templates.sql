INSERT INTO notification.notification_templates (
    id,
    code,
    event_type,
    channel,
    subject_template,
    body_template,
    active,
    created_at,
    updated_at
)
VALUES
(
    '11111111-1111-4111-8111-111111111111',
    'WORK_ORDER_CREATED_INTERNAL',
    'WORK_ORDER_CREATED',
    'INTERNAL',
    'New work order created: {{workOrderNumber}}',
    'A new work order {{workOrderNumber}} has been created for asset {{assetId}}.',
    TRUE,
    NOW(),
    NOW()
),
(
    '22222222-2222-4222-8222-222222222222',
    'WORK_ORDER_ASSIGNED_INTERNAL',
    'WORK_ORDER_ASSIGNED',
    'INTERNAL',
    'Work order assigned: {{workOrderNumber}}',
    'Work order {{workOrderNumber}} has been assigned to you.',
    TRUE,
    NOW(),
    NOW()
),
(
    '33333333-3333-4333-8333-333333333333',
    'WORK_ORDER_COMPLETED_INTERNAL',
    'WORK_ORDER_COMPLETED',
    'INTERNAL',
    'Work order completed: {{workOrderNumber}}',
    'Work order {{workOrderNumber}} has been completed.',
    TRUE,
    NOW(),
    NOW()
),
(
    '44444444-4444-4444-8444-444444444444',
    'INVOICE_GENERATED_INTERNAL',
    'INVOICE_GENERATED',
    'INTERNAL',
    'Invoice generated: {{invoiceNumber}}',
    'Invoice {{invoiceNumber}} has been generated for customer {{customerId}}. Total amount: {{totalAmount}} {{currency}}.',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (code) DO NOTHING;