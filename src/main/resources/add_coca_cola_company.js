// Script to add a new company 'Coca Cola' to db.companies

db.companies.insertOne({
  _id: 4,
  name: "Coca Cola",
  description: "Bebidas y refrescos",
  email: "contacto@cocacola.com",
  telefono: "+57 1 8000 123 456",
  contact_name: "Carlos Coca"
});

