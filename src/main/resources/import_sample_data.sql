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

INSERT INTO product (barcode, nombre, precio, foto, company_id) VALUES
('7711000789002', 'Arequipe', 14.0, 'assets/img/demo/arequipe.jpg', 1),
('7712000890123', 'Yogur Natural', 8.5, 'assets/img/demo/yogur.jpg', 2),
('7713000901234', 'Galletas Festival', 9.0, 'assets/img/demo/galletas.jpg', 3),
('7714001012345', 'Chocolate de Mesa', 16.0, 'assets/img/demo/chocolate.jpg', 4),
('7715001123456', 'Avena Líquida', 7.5, 'assets/img/demo/avena.jpg', 5),
('7716001234567', 'Mantequilla', 13.0, 'assets/img/demo/mantequilla.jpg', 1),
('7717001345678', 'Tostadas Integrales', 11.0, 'assets/img/demo/tostadas.jpg', 2),
('7718001456789', 'Maíz Pira', 6.0, 'assets/img/demo/maiz.jpg', 3),
('7719001567890', 'Lentejas', 17.0, 'assets/img/demo/lentejas.jpg', 4),
('7720001678901', 'Harina de Trigo', 12.0, 'assets/img/demo/harina.jpg', 5),
('7721001789012', 'Cereal de Maíz', 19.0, 'assets/img/demo/cereal.jpg', 1),
('7722001890123', 'Milo', 22.0, 'assets/img/demo/milo.jpg', 2),
('7723001901234', 'Panela', 8.0, 'assets/img/demo/panela.jpg', 3),
('7724002012345', 'Quesito Antioqueño', 25.0, 'assets/img/demo/quesito.jpg', 4),
('7725002123456', 'Jugo de Naranja', 10.0, 'assets/img/demo/jugo.jpg', 5),
('7726002234567', 'Tamal', 18.0, 'assets/img/demo/tamal.jpg', 1),
('7727002345678', 'Empanadas', 15.0, 'assets/img/demo/empanadas.jpg', 2),
('7728002456789', 'Chorizo Santarrosano', 20.0, 'assets/img/demo/chorizo.jpg', 3),
('7729002567890', 'Arepas', 9.5, 'assets/img/demo/arepas.jpg', 4),
('7730002678901', 'Natilla', 13.5, 'assets/img/demo/natilla.jpg', 5);

-- NoSQL section for MongoDB

db.companies.insertMany([
  {
    _id: 1,
    name: "Alpina",
    description: "Lácteos y alimentos procesados",
    email: "contacto@alpina.com",
    telefono: "+57 1 8000 518 900",
    contact_name: "Juan Pérez"
  },
  {
    _id: 2,
    name: "Grupo Éxito",
    description: "Supermercados y retail",
    email: "info@grupoexito.com",
    telefono: "+57 4 604 1919",
    contact_name: "María Gómez"
  },
  {
    _id: 3,
    name: "Postobón",
    description: "Bebidas y refrescos",
    email: "servicio@postobon.com.co",
    telefono: "+57 1 404 9000",
    contact_name: "Carlos Rodríguez"
  },
  {
    _id: 4,
    name: "Nutresa",
    description: "Alimentos procesados y chocolates",
    email: "contacto@nutresa.com",
    telefono: "+57 4 444 8600",
    contact_name: "Ana Martínez"
  },
  {
    _id: 5,
    name: "Colanta",
    description: "Lácteos y productos agrícolas",
    email: "info@colanta.com",
    telefono: "+57 4 445 9000",
    contact_name: "Luis Ramírez"
  }
])

db.products.insertMany([
  {
    "_id": "7501000123456",
    "nombre": "Leche Entera de 1000 mililitros ml descremada",
    "tokens": ["leche", "entera", "1000", "mililitros", "ml", "descremada"],
    "features": ["lácteo", "descremada", "bebida"],
    "reference": { "barcode": "7501000123456", "company_id": "1" }
  },
  {
    "_id": "7702000456789",
    "nombre": "Arroz Blanco premium grano largo 1000 gramos",
    "tokens": ["arroz", "blanco", "premium", "grano largo", "1000", "gramos"],
    "features": ["cereal", "arroz", "grano largo"],
    "reference": { "barcode": "7702000456789", "company_id": "2" }
  }])

db.products.insertMany([
  {
    "_id": "7501000123456",
    "nombre": "Leche Entera de 1000 mililitros ml descremada",
    "tokens": ["leche", "entera", "1000", "mililitros", "ml", "descremada"],
    "features": ["lácteo", "descremada", "bebida"],
    "reference": { "barcode": "7501000123456", "company_id": "1" }
  },
  {
    "_id": "7702000456789",
    "nombre": "Arroz Blanco premium grano largo 1000 gramos",
    "tokens": ["arroz", "blanco", "premium", "grano largo", "1000", "gramos"],
    "features": ["cereal", "arroz", "grano largo"],
    "reference": { "barcode": "7702000456789", "company_id": "2" }
  },
  {
    "_id": "7703000789123",
    "nombre": "Azúcar Refinada blanca 1000 gramos",
    "tokens": ["azúcar", "refinada", "blanca", "1000", "gramos"],
    "features": ["endulzante", "azúcar", "refinada"],
    "reference": { "barcode": "7703000789123", "company_id": "3" }
  },
  {
    "_id": "7704000123987",
    "nombre": "Aceite de Girasol vegetal 1000 mililitros",
    "tokens": ["aceite", "girasol", "vegetal", "1000", "mililitros"],
    "features": ["aceite", "vegetal", "girasol"],
    "reference": { "barcode": "7704000123987", "company_id": "4" }
  },
  {
    "_id": "7705000678912",
    "nombre": "Sal de Mesa refinada 500 gramos",
    "tokens": ["sal", "mesa", "refinada", "500", "gramos"],
    "features": ["condimento", "sal", "refinada"],
    "reference": { "barcode": "7705000678912", "company_id": "5" }
  },
  {
    "_id": "7706000234567",
    "nombre": "Huevos AA frescos docena",
    "tokens": ["huevos", "aa", "frescos", "docena"],
    "features": ["huevo", "fresco", "proteína"],
    "reference": { "barcode": "7706000234567", "company_id": "1" }
  },
  {
    "_id": "7707000345678",
    "nombre": "Pan Tajado integral paquete 500 gramos",
    "tokens": ["pan", "tajado", "integral", "paquete", "500", "gramos"],
    "features": ["pan", "integral", "horneado"],
    "reference": { "barcode": "7707000345678", "company_id": "2" }
  },
  {
    "_id": "7708000456789",
    "nombre": "Queso Campesino fresco 500 gramos",
    "tokens": ["queso", "campesino", "fresco", "500", "gramos"],
    "features": ["queso", "lácteo", "fresco"],
    "reference": { "barcode": "7708000456789", "company_id": "3" }
  },
  {
    "_id": "7709000567890",
    "nombre": "Café Molido tradicional 250 gramos",
    "tokens": ["café", "molido", "tradicional", "250", "gramos"],
    "features": ["café", "molido", "bebida"],
    "reference": { "barcode": "7709000567890", "company_id": "4" }
  },
  {
    "_id": "7710000678901",
    "nombre": "Frijol Bola Roja seleccionado 500 gramos",
    "tokens": ["frijol", "bola", "roja", "seleccionado", "500", "gramos"],
    "features": ["legumbre", "frijol", "rojo"],
    "reference": { "barcode": "7710000678901", "company_id": "5" }
  },
  {
    "_id": "7711000789002",
    "nombre": "Arequipe tradicional cremoso 250 gramos",
    "tokens": ["arequipe", "tradicional", "cremoso", "250", "gramos"],
    "features": ["dulce", "arequipe", "postre"],
    "reference": { "barcode": "7711000789002", "company_id": "1" }
  },
  {
    "_id": "7712000890123",
    "nombre": "Yogur Natural descremado 200 gramos",
    "tokens": ["yogur", "natural", "descremado", "200", "gramos"],
    "features": ["lácteo", "yogur", "natural"],
    "reference": { "barcode": "7712000890123", "company_id": "2" }
  },
  {
    "_id": "7713000901234",
    "nombre": "Galletas Festival surtidas paquete 300 gramos",
    "tokens": ["galletas", "festival", "surtidas", "paquete", "300", "gramos"],
    "features": ["galleta", "dulce", "snack"],
    "reference": { "barcode": "7713000901234", "company_id": "3" }
  },
  {
    "_id": "7714001012345",
    "nombre": "Chocolate de Mesa tradicional tableta 250 gramos",
    "tokens": ["chocolate", "mesa", "tradicional", "tableta", "250", "gramos"],
    "features": ["chocolate", "bebida", "mesa"],
    "reference": { "barcode": "7714001012345", "company_id": "4" }
  },
  {
    "_id": "7715001123456",
    "nombre": "Avena Líquida tradicional 500 mililitros",
    "tokens": ["avena", "líquida", "tradicional", "500", "mililitros"],
    "features": ["bebida", "avena", "cereal"],
    "reference": { "barcode": "7715001123456", "company_id": "5" }
  },
  {
    "_id": "7716001234567",
    "nombre": "Mantequilla natural 250 gramos",
    "tokens": ["mantequilla", "natural", "250", "gramos"],
    "features": ["lácteo", "mantequilla", "grasa"],
    "reference": { "barcode": "7716001234567", "company_id": "1" }
  },
  {
    "_id": "7717001345678",
    "nombre": "Tostadas Integrales paquete 200 gramos",
    "tokens": ["tostadas", "integrales", "paquete", "200", "gramos"],
    "features": ["pan", "integral", "tostada"],
    "reference": { "barcode": "7717001345678", "company_id": "2" }
  },
  {
    "_id": "7718001456789",
    "nombre": "Maíz Pira para crispetas 500 gramos",
    "tokens": ["maíz", "pira", "crispetas", "500", "gramos"],
    "features": ["cereal", "maíz", "pira"],
    "reference": { "barcode": "7718001456789", "company_id": "3" }
  },
  {
    "_id": "7719001567890",
    "nombre": "Lentejas seleccionadas 500 gramos",
    "tokens": ["lentejas", "seleccionadas", "500", "gramos"],
    "features": ["legumbre", "lenteja", "proteína"],
    "reference": { "barcode": "7719001567890", "company_id": "4" }
  },
  {
    "_id": "7720001678901",
    "nombre": "Harina de Trigo fortificada 1000 gramos",
    "tokens": ["harina", "trigo", "fortificada", "1000", "gramos"],
    "features": ["harina", "trigo", "cereal"],
    "reference": { "barcode": "7720001678901", "company_id": "5" }
  },
  {
    "_id": "7721001789012",
    "nombre": "Cereal de Maíz crocante 300 gramos",
    "tokens": ["cereal", "maíz", "crocante", "300", "gramos"],
    "features": ["cereal", "maíz", "desayuno"],
    "reference": { "barcode": "7721001789012", "company_id": "1" }
  },
  {
    "_id": "7722001890123",
    "nombre": "Milo bebida achocolatada 400 gramos",
    "tokens": ["milo", "bebida", "achocolatada", "400", "gramos"],
    "features": ["bebida", "chocolate", "milo"],
    "reference": { "barcode": "7722001890123", "company_id": "2" }
  },
  {
    "_id": "7723001901234",
    "nombre": "Panela orgánica bloque 500 gramos",
    "tokens": ["panela", "orgánica", "bloque", "500", "gramos"],
    "features": ["endulzante", "panela", "orgánica"],
    "reference": { "barcode": "7723001901234", "company_id": "3" }
  },
  {
    "_id": "7724002012345",
    "nombre": "Quesito Antioqueño fresco 250 gramos",
    "tokens": ["quesito", "antioqueño", "fresco", "250", "gramos"],
    "features": ["queso", "lácteo", "antioqueño"],
    "reference": { "barcode": "7724002012345", "company_id": "4" }
  },
  {
    "_id": "7725002123456",
    "nombre": "Jugo de Naranja natural 1000 mililitros",
    "tokens": ["jugo", "naranja", "natural", "1000", "mililitros"],
    "features": ["bebida", "jugo", "naranja"],
    "reference": { "barcode": "7725002123456", "company_id": "5" }
  },
  {
    "_id": "7726002234567",
    "nombre": "Tamal tradicional 400 gramos",
    "tokens": ["tamal", "tradicional", "400", "gramos"],
    "features": ["tamal", "tradicional", "comida"],
    "reference": { "barcode": "7726002234567", "company_id": "1" }
  },
  {
    "_id": "7727002345678",
    "nombre": "Empanadas de carne paquete 6 unidades",
    "tokens": ["empanadas", "carne", "paquete", "6", "unidades"],
    "features": ["empanada", "carne", "frito"],
    "reference": { "barcode": "7727002345678", "company_id": "2" }
  },
  {
    "_id": "7728002456789",
    "nombre": "Chorizo Santarrosano tradicional 500 gramos",
    "tokens": ["chorizo", "santarrosano", "tradicional", "500", "gramos"],
    "features": ["chorizo", "carne", "embutido"],
    "reference": { "barcode": "7728002456789", "company_id": "3" }
  },
  {
    "_id": "7729002567890",
    "nombre": "Arepas blancas paquete 10 unidades",
    "tokens": ["arepas", "blancas", "paquete", "10", "unidades"],
    "features": ["arepa", "maíz", "tradicional"],
    "reference": { "barcode": "7729002567890", "company_id": "4" }
  },
  {
    "_id": "7730002678901",
    "nombre": "Natilla tradicional 500 gramos",
    "tokens": ["natilla", "tradicional", "500", "gramos"],
    "features": ["postre", "natilla", "tradicional"],
    "reference": { "barcode": "7730002678901", "company_id": "5" }
  }
])

-- Nuevos productos de leche (Alqueria y Alpina, company_id corregido)
db.products.insertMany([
  {
    _id: "8001000123001",
    nombre: "Leche Deslactosada 1000 miligramos",
    tokens: ["leche", "deslactosada", "1000", "miligramos"],
    features: ["lácteo", "deslactosada", "bebida"],
    reference: { barcode: "8001000123001", company_id: "5", marca: "Alqueria" }
  },
  {
    _id: "8001000123002",
    nombre: "Leche Semidescremada 500 miligramos",
    tokens: ["leche", "semidescremada", "500", "miligramos"],
    features: ["lácteo", "semidescremada", "bebida"],
    reference: { barcode: "8001000123002", company_id: "1", marca: "Alpina" }
  },
  {
    _id: "8001000123003",
    nombre: "Leche Entera 250 miligramos",
    tokens: ["leche", "entera", "250", "miligramos"],
    features: ["lácteo", "entera", "bebida"],
    reference: { barcode: "8001000123003", company_id: "1", marca: "Alpina" }
  },
  {
    _id: "8001000123004",
    nombre: "Leche Deslactosada 500 miligramos",
    tokens: ["leche", "deslactosada", "500", "miligramos"],
    features: ["lácteo", "deslactosada", "bebida"],
    reference: { barcode: "8001000123004", company_id: "5", marca: "Alqueria" }
  },
  {
    _id: "8001000123005",
    nombre: "Leche Semidescremada 1000 miligramos",
    tokens: ["leche", "semidescremada", "1000", "miligramos"],
    features: ["lácteo", "semidescremada", "bebida"],
    reference: { barcode: "8001000123005", company_id: "5", marca: "Alqueria" }
  },
  {
    _id: "8001000123006",
    nombre: "Leche Entera 250 miligramos",
    tokens: ["leche", "entera", "250", "miligramos"],
    features: ["lácteo", "entera", "bebida"],
    reference: { barcode: "8001000123006", company_id: "5", marca: "Alqueria" }
  }
])
