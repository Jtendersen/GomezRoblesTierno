-- ============================================================
-- Script de datos iniciales para HogarYA
-- Ejecutar ANTES de levantar la aplicación
-- Base de datos: GomezRoblesTierno
-- ============================================================

USE GomezRoblesTierno;

-- ------------------------------------------------------------
-- PROVINCIA
-- ------------------------------------------------------------
INSERT IGNORE INTO provincia (id, nombre) VALUES (1, 'Santa Fe');

-- ------------------------------------------------------------
-- CIUDAD
-- ------------------------------------------------------------
INSERT IGNORE INTO ciudad (id, nombre, provincia_id) VALUES (1, 'Santa Fe', 1);
INSERT IGNORE INTO ciudad (id, nombre, provincia_id) VALUES (2, 'Rosario', 1);
INSERT IGNORE INTO ciudad (id, nombre, provincia_id) VALUES (3, 'Córdoba', 1);
INSERT IGNORE INTO ciudad (id, nombre, provincia_id) VALUES (4, 'Buenos Aires', 1);

-- Asegurar que todas las ciudades apunten a una provincia válida
UPDATE ciudad SET provincia_id = 1 WHERE provincia_id = 0 OR provincia_id IS NULL;

-- ------------------------------------------------------------
-- PERSONA (propietarios e inquilinos)
-- ------------------------------------------------------------
INSERT IGNORE INTO persona (id, dni_cuit, domicilio, eliminada, email, nombre, telefono, apellido)
VALUES
  (1, '20-11111111-1', 'Av. San Martín 100, Santa Fe',  0, 'cperez@mail.com',    'Carlos',   '342-4001111', 'Pérez'),
  (2, '27-22222222-2', 'Bv. Oroño 200, Rosario',        0, 'agarcia@mail.com',   'Ana',      '341-4002222', 'García'),
  (3, '20-33333333-3', 'Av. Colón 300, Córdoba',        0, 'lmartinez@mail.com', 'Luis',     '351-4003333', 'Martínez'),
  (4, '27-44444444-4', 'Corrientes 400, Buenos Aires',  0, 'mlopez@mail.com',    'María',    '011-40044444','López'),
  (5, '20-55555555-5', 'San Jerónimo 500, Santa Fe',    0, 'rfernandez@mail.com','Roberto',  '342-4005555', 'Fernández');

-- ------------------------------------------------------------
-- PROPIEDAD
-- ------------------------------------------------------------
INSERT IGNORE INTO propiedad (id, cantidad_ambientes, descripcion, direccion, eliminada, estado_disponibilidad, metros_cuadrados, tipo, ciudad_id, propietario_id)
VALUES
  (1, 2, 'Departamento luminoso, piso 3, con cochera.',              'Rivadavia 1234',        0, 'DISPONIBLE', 55,  'DEPARTAMENTO', 1, 1),
  (2, 4, 'Casa con jardín y garage doble.',                          'Pellegrini 456',         0, 'ALQUILADA',  120, 'CASA',         2, 2),
  (3, 1, 'Local comercial en zona céntrica, frente al mercado.',     'Av. Vélez Sársfield 789',0, 'DISPONIBLE', 80,  'LOCAL',        3, 3),
  (4, 3, 'Departamento en planta baja con patio exclusivo.',         'Urquiza 321',            0, 'DISPONIBLE', 70,  'DEPARTAMENTO', 1, 5);
