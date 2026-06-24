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

-- ------------------------------------------------------------
-- PUBLICACION
-- prop 1 (DISPONIBLE): publicacion ACTIVA
-- prop 2 (ALQUILADA):  publicacion FINALIZADA (fue publicada, consiguio inquilino, se cerro)
-- prop 3 (DISPONIBLE): publicacion ACTIVA
-- prop 4 (DISPONIBLE): publicacion PAUSADA
-- ------------------------------------------------------------
INSERT IGNORE INTO publicacion (id, propiedad_id, precio_mensual, condiciones, descripcion, fecha_publicacion, estado, eliminada)
VALUES
  (1, 1, 85000.00, 'Depósito 2 meses, garantía propietario.',           'Departamento luminoso en pleno centro, piso 3 con cochera incluida.',     '2026-01-10', 'ACTIVA',    0),
  (2, 2, 120000.00,'Solo familias, no mascotas.',                        'Casa amplia con jardín y garage doble en Rosario.',                        '2025-10-01', 'FINALIZADA',0),
  (3, 3, 95000.00, 'Uso comercial exclusivo, contrato mínimo 12 meses.','Local en zona céntrica con gran visibilidad y acceso vehicular.',           '2026-02-15', 'ACTIVA',    0),
  (4, 4, 75000.00, 'Depósito 1 mes, se aceptan mascotas pequeñas.',     'Departamento planta baja con patio exclusivo, muy tranquilo.',             '2026-03-01', 'PAUSADA',   0);

-- ------------------------------------------------------------
-- HISTORIAL_ESTADO_PUBLICACION
-- ------------------------------------------------------------
INSERT IGNORE INTO historial_estado_publicacion (id, estado, fecha_hora, publicacion_id)
VALUES
  (1, 'ACTIVA',    '2026-01-10 09:00:00', 1),
  (2, 'ACTIVA',    '2025-10-01 10:00:00', 2),
  (3, 'FINALIZADA','2026-01-20 15:30:00', 2),
  (4, 'ACTIVA',    '2026-02-15 11:00:00', 3),
  (5, 'ACTIVA',    '2026-03-01 08:00:00', 4),
  (6, 'PAUSADA',   '2026-03-15 14:00:00', 4);
