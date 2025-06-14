-- SQL script to create tables and insert sample data for companies and products

-- 1. Create Company table
CREATE TABLE company (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    email VARCHAR(100),
    telefono VARCHAR(30),
    contact_name VARCHAR(100)
);

-- 2. Create Product table
CREATE TABLE product (
    barcode VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    foto VARCHAR(255),
    company_id INTEGER REFERENCES company(id)
);

-- 3. Insert Colombian companies
INSERT INTO company (name, description, email, telefono, contact_name) VALUES
('Alpina', 'Lácteos y alimentos procesados', 'contacto@alpina.com', '+57 1 8000 518 900', 'Juan Pérez'),
('Grupo Éxito', 'Supermercados y retail', 'info@grupoexito.com', '+57 4 604 1919', 'María Gómez'),
('Postobón', 'Bebidas y refrescos', 'servicio@postobon.com.co', '+57 1 404 9000', 'Carlos Rodríguez'),
('Nutresa', 'Alimentos procesados y chocolates', 'contacto@nutresa.com', '+57 4 444 8600', 'Ana Martínez'),
('Colanta', 'Lácteos y productos agrícolas', 'info@colanta.com', '+57 4 445 9000', 'Luis Ramírez');

-- 4. Insert 100 grocery products (example for 10, repeat pattern for 100)
INSERT INTO product (barcode, nombre, precio, foto, company_id) VALUES
('7501000123456', 'Leche Entera', 22.5, 'assets/img/demo/leche.jpg', 1),
('7702000456789', 'Arroz Blanco', 18.0, 'assets/img/demo/arroz.jpg', 2),
('7703000789123', 'Azúcar Refinada', 15.5, 'assets/img/demo/azucar.jpg', 3),
('7704000123987', 'Aceite de Girasol', 28.0, 'assets/img/demo/aceite.jpg', 4),
('7705000678912', 'Sal de Mesa', 5.0, 'assets/img/demo/sal.jpg', 5),
('7706000234567', 'Huevos AA', 12.0, 'assets/img/demo/huevos.jpg', 1),
('7707000345678', 'Pan Tajado', 10.5, 'assets/img/demo/pan.jpg', 2),
('7708000456789', 'Queso Campesino', 30.0, 'assets/img/demo/queso.jpg', 3),
('7709000567890', 'Café Molido', 35.0, 'assets/img/demo/cafe.jpg', 4),
('7710000678901', 'Frijol Bola Roja', 20.0, 'assets/img/demo/frijol.jpg', 5);
-- Repetir patrón para 90 productos adicionales cambiando valores y company_id entre 1 y 5

