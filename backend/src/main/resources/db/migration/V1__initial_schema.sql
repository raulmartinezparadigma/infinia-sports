-- ===========================================
-- Flyway Migration V1: Initial Schema
-- ===========================================
-- Fecha: 2025-10-13
-- Descripción: Creación del esquema inicial de la base de datos
-- Tablas: users, addresses, products, user_roles

-- ===========================================
-- Tabla: users
-- ===========================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    nif VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE NOT NULL
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- ===========================================
-- Tabla: user_roles
-- ===========================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, roles)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);

-- ===========================================
-- Tabla: addresses
-- ===========================================
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(500) NOT NULL,
    address_line2 VARCHAR(500),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    main_address BOOLEAN DEFAULT TRUE NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_addresses_main ON addresses(main_address);

-- ===========================================
-- Tabla: products
-- ===========================================
CREATE TABLE products (
    id UUID PRIMARY KEY,
    sku_id VARCHAR(18) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    size VARCHAR(20) NOT NULL,
    image_url VARCHAR(500) NOT NULL
);

CREATE INDEX idx_products_sku_id ON products(sku_id);
CREATE INDEX idx_products_type ON products(type);

-- ===========================================
-- Comentarios de Documentación
-- ===========================================
COMMENT ON TABLE users IS 'Usuarios del sistema con autenticación';
COMMENT ON TABLE user_roles IS 'Roles asignados a los usuarios (USER, ADMIN)';
COMMENT ON TABLE addresses IS 'Direcciones de envío de los usuarios';
COMMENT ON TABLE products IS 'Catálogo de productos deportivos';
