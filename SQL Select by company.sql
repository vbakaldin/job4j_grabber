/*
CREATE TABLE companies
(
    id INTEGER NOT NULL,
    name CHARACTER VARYING,
    CONSTRAINT companies_pkey PRIMARY KEY (id)
);

CREATE TABLE people
(
    id INTEGER NOT NULL,
    name CHARACTER VARYING,
    company_id INTEGER REFERENCES companies(id),
    CONSTRAINT people_pkey PRIMARY KEY (id)
);


INSERT INTO companies (id, name) VALUES
    (1, 'Сбер'),
    (2, 'Тинькоф'),
    (5, 'ВТБ');

INSERT INTO people (id, name, company_id) VALUES
    (1, 'Анна', 1),
    (2, 'Иван', 1),
    (3, 'Ольга', 2),
    (4, 'Павел', 2),
    (5, 'Мария', 5)

*/

SELECT p.name, c.name AS company
FROM people p
JOIN companies c ON p.company_id = c.id
WHERE p.company_id <> 5;

SELECT c.name, COUNT(p.id) AS people_count
FROM companies c
LEFT JOIN people p ON c.id = p.company_id
GROUP BY c.id, c.name
ORDER BY people_count DESC
FETCH FIRST 1 ROW WITH TIES;