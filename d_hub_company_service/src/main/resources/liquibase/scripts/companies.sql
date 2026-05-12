CREATE TABLE companies (
    company_id BIGSERIAL PRIMARY KEY,
    name_company VARCHAR(255) NOT NULL,
    budget NUMERIC
);

CREATE TABLE company_employee_ids (
    company_id BIGINT REFERENCES companies (company_id) ON DELETE CASCADE,
    employee_id BIGINT
);


