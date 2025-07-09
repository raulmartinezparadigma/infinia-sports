-- Insertar usuario de prueba para E2E
-- Contraseña es '123456' codificada con BCrypt
INSERT INTO users (username, password, email, first_name, last_name, nif, enabled) VALUES 
('testinfinia', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'testinfinia@infinia.com', 'Test', 'User', '12345678Z', true);

-- Asignar rol 'USER' al usuario de prueba
-- El id del usuario será 1 porque es la primera inserción
INSERT INTO user_roles (user_id, roles) VALUES 
(1, 'USER');
