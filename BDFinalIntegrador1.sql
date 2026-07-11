drop database if exists carzone_bdt;
CREATE DATABASE IF NOT EXISTS carzone_bdt;
USE carzone_bdt;

CREATE TABLE usuario (
    Id_usuario VARCHAR(6) PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL,
    contrasena VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_rol CHECK (rol IN ('administrador', 'vendedor'))
);

CREATE TABLE cliente (
    Id_cliente VARCHAR(6) PRIMARY KEY,
    nombres VARCHAR(60) NOT NULL,
    apellidos VARCHAR(60) NOT NULL,
    dni VARCHAR(8) NOT NULL UNIQUE,
    telefono VARCHAR(9),
    correo VARCHAR(60),
    direccion VARCHAR(100)
);

CREATE TABLE auto (
    Id_auto VARCHAR(6) PRIMARY KEY,
    marca VARCHAR(30) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    anio INT(4) NOT NULL,
    color VARCHAR(20) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'disponible',
    codigo_4_cifras VARCHAR(4) NOT NULL,
    CONSTRAINT chk_estado_auto CHECK (estado IN ('disponible', 'reservado', 'vendido', 'eliminado'))
);

CREATE TABLE venta (
    Id_venta VARCHAR(6) PRIMARY KEY,
    Id_cliente VARCHAR(6) NOT NULL,
    Id_usuario VARCHAR(6) NOT NULL,
    Id_auto VARCHAR(6) NOT NULL,
    fecha_venta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    monto_total DECIMAL(10,2) NOT NULL,
    estado_venta VARCHAR(20) NOT NULL DEFAULT 'completada',
    CONSTRAINT fk_venta_cliente FOREIGN KEY (Id_cliente) REFERENCES cliente(Id_cliente),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (Id_usuario) REFERENCES usuario(Id_usuario),
    CONSTRAINT fk_venta_auto FOREIGN KEY (Id_auto) REFERENCES auto(Id_auto),
    CONSTRAINT chk_estado_venta CHECK (estado_venta IN ('completada', 'anulada', 'en proceso'))
);

CREATE TABLE inventario (
    id_inventario VARCHAR(6) PRIMARY KEY,
    id_auto VARCHAR(6) NOT NULL,
    stock INT(3) NOT NULL DEFAULT 1,
    disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_auto FOREIGN KEY (id_auto) REFERENCES auto(Id_auto)
);
CREATE TABLE comprobante (
    id_comprobante VARCHAR(6) PRIMARY KEY,
    id_venta VARCHAR(6) NOT NULL,
    tipo_comprobante VARCHAR(20) NOT NULL DEFAULT 'boleta',
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_identificador VARCHAR(10) NOT NULL,
    CONSTRAINT fk_comp_venta FOREIGN KEY (id_venta) REFERENCES venta(Id_venta),
    CONSTRAINT chk_tipo_comp CHECK (tipo_comprobante IN ('boleta', 'factura'))
);

INSERT INTO usuario (Id_usuario, nombre_usuario, contrasena, rol, estado) VALUES
('USR001', 'Admin CarZone', 'XXXXXXXX', 'administrador', TRUE),
('USR002', 'Vendedor1', 'XXXXXXXX', 'vendedor', TRUE);

INSERT INTO cliente (Id_cliente, nombres, apellidos, dni, telefono, correo, direccion) VALUES
('CLI001', 'Carlos Alberto',    'Quispe Mamani',       '45678901', '987654321', 'carlos.quispe@gmail.com',       'Av. Arequipa 1245, Miraflores'),
('CLI002', 'María Elena',       'Torres Huanca',       '32145678', '956781234', 'maria.torres@hotmail.com',      'Jr. Ucayali 320, Lima'),
('CLI003', 'Javier Andrés',     'Sánchez Flores',      '78901234', '912345678', 'javier.sanchez@outlook.com',    'Calle Los Pinos 88, San Isidro'),
('CLI004', 'Lucía Fernanda',    'Rojas Ccari',         '56789012', '923456789', 'lucia.rojas@gmail.com',         'Av. Brasil 450, Jesús María'),
('CLI005', 'Roberto Miguel',    'Vargas Condori',      '89012345', '934567890', 'roberto.vargas@yahoo.com',      'Psje. Santa Rosa 12, Surco'),
('CLI006', 'Diana Patricia',    'Mendoza Paredes',     '41234567', '945678901', 'diana.mendoza@gmail.com',       'Av. Angamos 780, Surquillo'),
('CLI007', 'Fernando José',     'Castillo Rivas',      '67890123', '956123478', 'fernando.castillo@hotmail.com', 'Jr. Cusco 210, Breña'),
('CLI008', 'Patricia Isabel',   'Huamán Aguilar',      '23456789', '967234589', 'patricia.huaman@gmail.com',     'Av. La Marina 1500, San Miguel'),
('CLI009', 'Alejandro Gabriel', 'Salazar Ponce',       '90123456', '978345690', 'alejandro.salazar@outlook.com', 'Calle Las Begonias 300, San Borja'),
('CLI010', 'Gabriela Nicole',   'Delgado Ríos',        '34567890', '989456701', 'gabriela.delgado@gmail.com',    'Av. Salaverry 950, Jesús María'),
('CLI011', 'Ricardo Andrés',    'Chávez Loayza',       '12345670', '990567812', 'ricardo.chavez@hotmail.com',    'Jr. Junín 145, Lima'),
('CLI012', 'Sofía Alejandra',   'Nina Apaza',          '65432109', '901678923', 'sofia.nina@gmail.com',          'Av. Benavides 2200, Miraflores'),
('CLI013', 'Manuel Eduardo',    'Cárdenas Soto',       '87654320', '912789034', 'manuel.cardenas@outlook.com',   'Calle Los Álamos 55, La Molina'),
('CLI014', 'Valentina Isabel',  'Espinoza Vega',       '43210987', '923890145', 'valentina.espinoza@gmail.com',  'Av. Primavera 400, San Borja'),
('CLI015', 'Jorge Luis',        'Guerrero Palomino',   '65098712', '934901256', 'jorge.guerrero@hotmail.com',    'Jr. Puno 88, Rímac'),
('CLI016', 'Camila Antonella',  'Ramos Yupanqui',      '21098765', '945012367', 'camila.ramos@gmail.com',        'Av. Colonial 1300, Callao'),
('CLI017', 'Diego Alonso',      'Alvarado Cruz',       '43987650', '956123470', 'diego.alvarado@outlook.com',    'Calle Las Camelias 200, San Isidro'),
('CLI018', 'Andrea Carolina',   'Meza Contreras',      '65874320', '967234581', 'andrea.meza@gmail.com',         'Av. Larco 890, Miraflores'),
('CLI019', 'Miguel Ángel',      'Cabrera Ortiz',       '87965430', '978345692', 'miguel.cabrera@hotmail.com',    'Jr. Ayacucho 320, Lima'),
('CLI020', 'Daniela Alexandra', 'Fuentes Zúñiga',      '32198760', '989456703', 'daniela.fuentes@gmail.com',     'Av. Aviación 1800, San Borja'),
('CLI021', 'Luis Fernando',     'Paredes Villanueva',  '54321098', '990567814', 'luis.paredes@outlook.com',      'Calle Los Sauces 78, Surco'),
('CLI022', 'Carmen Rosa',       'Bustamante León',     '76543219', '901678925', 'carmen.bustamante@gmail.com',   'Av. Universitaria 2500, Los Olivos'),
('CLI023', 'Pedro Pablo',       'Cornejo Salas',       '98765431', '912789036', 'pedro.cornejo@hotmail.com',     'Jr. Huánuco 150, Breña'),
('CLI024', 'Rosa María',        'Rivera Choque',       '13579246', '923890147', 'rosa.rivera@gmail.com',         'Av. Pershing 600, Magdalena del Mar'),
('CLI025', 'Juan Carlos',       'Villegas Ninahuanca', '24681357', '934901258', 'juan.villegas@outlook.com',     'Calle Las Orquídeas 90, San Miguel'),
('CLI026', 'Elena Sofía',       'Bravo Sotelo',        '97531864', '945012369', 'elena.bravo@gmail.com',         'Av. Grau 450, Barranco'),
('CLI027', 'Antonio José',      'Peña Escobar',        '86420975', '956123472', 'antonio.pena@hotmail.com',      'Jr. Tacna 210, Lima'),
('CLI028', 'Paola Fernanda',    'Zevallos Miranda',    '75319864', '967234583', 'paola.zevallos@gmail.com',      'Av. Javier Prado 3200, San Borja');

INSERT INTO auto (Id_auto, marca, modelo, anio, color, precio, estado, codigo_4_cifras) VALUES
('A00001', 'Toyota',    'Corolla',   2022, 'Blanco',   45000.00, 'disponible', '1120'),
('A00002', 'Hyundai',   'Tucson',    2023, 'Gris',     68000.00, 'disponible', '2231'),
('A00003', 'Kia',       'Sportage',  2023, 'Negro',    72000.00, 'disponible', '3341'),
('A00004', 'Nissan',    'Sentra',    2022, 'Rojo',     38000.00, 'disponible', '4450'),
('A00005', 'Chevrolet', 'Captiva',   2021, 'Azul',     55000.00, 'disponible', '5562'),
('A00006', 'Ford',      'Explorer',  2022, 'Blanco',   89000.00, 'disponible', '6673'),
('A00007', 'Honda',     'Civic',     2023, 'Plateado', 52000.00, 'disponible', '7784');

INSERT INTO inventario (id_inventario, id_auto, stock, disponibilidad, fecha_ingreso) VALUES
('INV001', 'A00001', 7, TRUE, '2026-02-14 09:00:00'),
('INV002', 'A00002', 7, TRUE, '2026-02-14 09:15:00'),
('INV003', 'A00003', 7, TRUE, '2026-02-20 10:30:00'),
('INV004', 'A00004', 7, TRUE, '2026-03-18 11:00:00'),
('INV005', 'A00005', 7, TRUE, '2026-04-22 08:45:00'),
('INV006', 'A00006', 7, TRUE, '2026-05-27 14:10:00'),
('INV007', 'A00007', 7, TRUE, '2026-06-05 09:30:00');

INSERT INTO venta (Id_venta, Id_cliente, Id_usuario, Id_auto, fecha_venta, monto_total, estado_venta) VALUES
('V00001', 'CLI001', 'USR002', 'A00003', '2025-03-10 10:30:00', 72000.00, 'completada'),
('V00002', 'CLI002', 'USR002', 'A00004', '2025-04-05 14:15:00', 38000.00, 'completada'),
('V00003', 'CLI003', 'USR001', 'A00005', '2025-05-20 09:00:00', 55000.00, 'anulada'),
('V00004', 'CLI004', 'USR002', 'A00006', '2025-06-01 16:45:00', 89000.00, 'completada'),
('V00005', 'CLI005', 'USR001', 'A00007', '2025-06-15 11:20:00', 52000.00, 'en proceso');

INSERT INTO venta (Id_venta, Id_cliente, Id_usuario, Id_auto, fecha_venta, monto_total, estado_venta) VALUES
('V00006', 'CLI006', 'USR002', 'A00001', '2025-07-08 09:45:00', 45000.00, 'completada'),
('V00007', 'CLI007', 'USR001', 'A00002', '2025-07-22 15:10:00', 68000.00, 'completada'),
('V00008', 'CLI008', 'USR002', 'A00004', '2025-08-04 10:20:00', 38000.00, 'anulada'),
('V00009', 'CLI009', 'USR002', 'A00006', '2025-08-19 13:35:00', 89000.00, 'completada'),
('V00010', 'CLI010', 'USR001', 'A00007', '2025-09-02 11:00:00', 52000.00, 'completada'),
('V00011', 'CLI011', 'USR002', 'A00003', '2025-09-17 16:25:00', 72000.00, 'completada'),
('V00012', 'CLI012', 'USR001', 'A00005', '2025-10-05 09:15:00', 55000.00, 'en proceso'),
('V00013', 'CLI001', 'USR002', 'A00001', '2025-10-21 14:50:00', 45000.00, 'completada'),
('V00014', 'CLI014', 'USR001', 'A00002', '2025-11-03 10:40:00', 68000.00, 'completada'),
('V00015', 'CLI015', 'USR002', 'A00006', '2025-11-18 15:05:00', 89000.00, 'anulada'),
('V00016', 'CLI016', 'USR002', 'A00007', '2025-12-06 12:30:00', 52000.00, 'completada'),
('V00017', 'CLI017', 'USR001', 'A00004', '2025-12-22 09:55:00', 38000.00, 'completada'),
('V00018', 'CLI002', 'USR002', 'A00003', '2026-01-10 11:15:00', 72000.00, 'completada'),
('V00019', 'CLI019', 'USR002', 'A00005', '2026-01-27 16:40:00', 55000.00, 'completada'),
('V00020', 'CLI020', 'USR001', 'A00001', '2026-02-09 10:05:00', 45000.00, 'en proceso'),
('V00021', 'CLI021', 'USR002', 'A00002', '2026-02-24 13:20:00', 68000.00, 'completada'),
('V00022', 'CLI004', 'USR001', 'A00006', '2026-03-12 15:45:00', 89000.00, 'completada'),
('V00023', 'CLI023', 'USR002', 'A00007', '2026-04-03 09:30:00', 52000.00, 'anulada'),
('V00024', 'CLI024', 'USR002', 'A00003', '2026-04-20 14:10:00', 72000.00, 'completada'),
('V00025', 'CLI025', 'USR001', 'A00004', '2026-05-08 11:50:00', 38000.00, 'completada'),
('V00026', 'CLI006', 'USR002', 'A00005', '2026-05-25 16:15:00', 55000.00, 'completada'),
('V00027', 'CLI027', 'USR001', 'A00001', '2026-06-11 10:30:00', 45000.00, 'completada'),
('V00028', 'CLI028', 'USR002', 'A00002', '2026-06-26 12:45:00', 68000.00, 'en proceso');

SELECT * FROM usuario;

SELECT * FROM cliente;

SELECT * FROM auto;

SELECT * FROM venta;

-- Comprobantes generados para las ventas con estado 'completada'
-- (las ventas 'anulada' y 'en proceso' no generan comprobante)
INSERT INTO comprobante (id_comprobante, id_venta, tipo_comprobante, fecha_emision, codigo_identificador) VALUES
    ('C001', 'V00001', 'factura', '2025-03-10 10:40:00', 'CMP000001'),
    ('C002', 'V00002', 'boleta', '2025-04-05 14:25:00', 'CMP000002'),
    ('C003', 'V00004', 'factura', '2025-06-01 16:55:00', 'CMP000003'),
    ('C004', 'V00006', 'boleta', '2025-07-08 09:55:00', 'CMP000004'),
    ('C005', 'V00007', 'boleta', '2025-07-22 15:20:00', 'CMP000005'),
    ('C006', 'V00009', 'factura', '2025-08-19 13:45:00', 'CMP000006'),
    ('C007', 'V00010', 'boleta', '2025-09-02 11:10:00', 'CMP000007'),
    ('C008', 'V00011', 'factura', '2025-09-17 16:35:00', 'CMP000008'),
    ('C009', 'V00013', 'boleta', '2025-10-21 15:00:00', 'CMP000009'),
    ('C010', 'V00014', 'boleta', '2025-11-03 10:50:00', 'CMP000010'),
    ('C011', 'V00016', 'boleta', '2025-12-06 12:40:00', 'CMP000011'),
    ('C012', 'V00017', 'boleta', '2025-12-22 10:05:00', 'CMP000012'),
    ('C013', 'V00018', 'factura', '2026-01-10 11:25:00', 'CMP000013'),
    ('C014', 'V00019', 'boleta', '2026-01-27 16:50:00', 'CMP000014'),
    ('C015', 'V00021', 'boleta', '2026-02-24 13:30:00', 'CMP000015'),
    ('C016', 'V00022', 'factura', '2026-03-12 15:55:00', 'CMP000016'),
    ('C017', 'V00024', 'factura', '2026-04-20 14:20:00', 'CMP000017'),
    ('C018', 'V00025', 'boleta', '2026-05-08 12:00:00', 'CMP000018'),
    ('C019', 'V00026', 'boleta', '2026-05-25 16:25:00', 'CMP000019');

SELECT * FROM inventario;

SELECT * FROM comprobante;

ALTER TABLE auto DROP CONSTRAINT chk_estado_auto;
ALTER TABLE auto ADD CONSTRAINT chk_estado_auto CHECK (estado IN ('disponible', 'reservado', 'vendido', 'eliminado'));