-- Insertar un usuario de prueba para las pruebas E2E
-- Contraseña es '123456' codificada con BCrypt
INSERT INTO users (username, password, email, first_name, last_name, nif, enabled) VALUES 
('testinfinia', '$2a$10$unAI7BxZdAeo1a0/tTEL9uIchrVBmlNpxZW7aXyRFla9KAqEqg3Uu', 'testinfinia@infinia.com', 'Test', 'User', '12345678Z', true);

-- Asignar rol 'USER' al usuario de prueba
-- El id del usuario será 1 porque es la primera inserción
INSERT INTO user_roles (user_id, roles) VALUES 
(1, 'USER');

-- Insertar productos de prueba
INSERT INTO products (id, sku_id, type, description, price, size, image_url) VALUES
(RANDOM_UUID(), '852542-001', 'SNEAKERS', 'Zapatillas Air Jordan 1 Mid', 119.99, '42', 'https://static.nike.com/a/images/t_PDP_864_v1/f_auto,b_rgb:f5f5f5/i1-8a2a4e6f-b5e5-4a5d-8b7c-3c6e7f8a9a0c/air-jordan-1-mid-zapatillas-1Z2kZ3.png'),
(RANDOM_UUID(), '921826-101', 'SNEAKERS', 'Zapatillas Nike Air Max 97', 179.99, '43', 'https://static.nike.com/a/images/t_PDP_864_v1/f_auto,b_rgb:f5f5f5/i1-8a2a4e6f-b5e5-4a5d-8b7c-3c6e7f8a9a0c/air-max-97-zapatillas-1Z2kZ3.png');
