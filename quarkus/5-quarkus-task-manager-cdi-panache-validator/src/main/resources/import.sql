INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at) VALUES (1, 'Découvrir Quarkus', 'Installer et lancer le premier projet', 'DONE', 'HIGH', '2025-01-15 09:00:00', '2025-01-15 09:00:00');
INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at) VALUES (2, 'Apprendre CDI', 'Injection de dépendances avec ArC', 'DONE', 'HIGH', '2025-01-16 10:00:00', '2025-01-16 10:00:00');
INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at) VALUES (3, 'Maîtriser Panache', 'ORM simplifié avec Active Record', 'IN_PROGRESS', 'HIGH', '2025-01-17 08:00:00', '2025-01-17 08:00:00');
INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at) VALUES (4, 'Ajouter la validation', 'Bean Validation + ExceptionMapper', 'TODO', 'MEDIUM', '2025-01-18 11:00:00', '2025-01-18 11:00:00');
INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at) VALUES (5, 'Écrire les tests', 'RestAssured + @QuarkusTest', 'TODO', 'MEDIUM', '2025-01-19 09:00:00', '2025-01-19 09:00:00');
ALTER SEQUENCE tasks_seq RESTART WITH 20;
