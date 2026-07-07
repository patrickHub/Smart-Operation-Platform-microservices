ALTER TABLE identity.users
    ADD COLUMN first_name VARCHAR(100),
    ADD COLUMN last_name VARCHAR(100),
    ADD COLUMN job_function VARCHAR(150);

UPDATE identity.users
SET
    first_name = 'Patrick ',
    last_name = 'Djomo',
    job_function = 'Platform Administrator',
    display_name = 'Patrick Djomo'
WHERE username = 'admin';

UPDATE identity.users
SET
    first_name = 'Pristane',
    last_name = 'Djomo',
    job_function = 'Support Agent',
    display_name = 'Pristane Djomo'
WHERE username = 'support';

UPDATE identity.users
SET
    first_name = 'Sebastien',
    last_name = 'Richoz',
    job_function = 'Dispatcher',
    display_name = 'Sebastien Richoz'
WHERE username = 'dispatcher';

UPDATE identity.users
SET
    first_name = 'Damien',
    last_name = 'Vity',
    job_function = 'Technician',
    display_name = 'Damien Vity'
WHERE username = 'technician';

UPDATE identity.users
SET
    first_name = 'Patrick',
    last_name = 'Champion',
    job_function = 'Billing Manager',
    display_name = 'Patrick Champion'
WHERE username = 'billing';

ALTER TABLE identity.users
    ALTER COLUMN first_name SET NOT NULL,
    ALTER COLUMN last_name SET NOT NULL,
    ALTER COLUMN job_function SET NOT NULL;