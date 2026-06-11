CREATE DATABASE IF NOT EXISTS carzone_bdd;
USE carzone_bdd;

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
    CONSTRAINT chk_estado_auto CHECK (estado IN ('disponible', 'reservado', 'vendido'))
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
('USR001', 'Admin CarZone', 'Admin123', 'administrador', TRUE),
('USR002', 'Vendedor1', 'Vend123a', 'vendedor', TRUE);

INSERT INTO auto (Id_auto, marca, modelo, anio, color, precio, estado, codigo_4_cifras) VALUES
('A00001', 'Toyota', 'Corolla', 2022, 'Blanco', 45000.00, 'disponible', '1120'),
('A00002', 'Hyundai', 'Tucson', 2023, 'Gris', 68000.00, 'disponible', '2231'),
('A00003', 'Kia', 'Sportage', 2023, 'Negro', 72000.00, 'disponible', '3341'),
('A00004', 'Nissan', 'Sentra', 2022, 'Rojo', 38000.00, 'disponible', '4450'),
('A00005', 'Chevrolet', 'Captiva', 2021, 'Azul', 55000.00, 'vendido', '5562');

INSERT INTO inventario (id_inventario, id_auto, stock, disponibilidad) VALUES
('INV001', 'A00001', 1, TRUE),
('INV002', 'A00002', 1, TRUE),
('INV003', 'A00003', 1, TRUE),
('INV004', 'A00004', 1, TRUE),
('INV005', 'A00005', 0, FALSE);
