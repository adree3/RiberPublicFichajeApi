DROP DATABASE IF EXISTS riberRepublicFichaje;
CREATE DATABASE riberRepublicFichaje;
USE riberRepublicFichaje;

CREATE TABLE grupos (
    id int auto_increment primary key,
    nombre varchar(50) not null,
    faltas_totales int not null
);

CREATE TABLE usuarios (
	id int auto_increment primary key,
    nombre varchar(25) not null,
    apellido1 varchar(30) not null,
    apellido2 varchar(30),
    email varchar(40) unique not null,
    contrasena varchar(255) not null,
    rol ENUM('empleado', 'jefe') default 'empleado',
	grupo_id int, 
    estado ENUM('activo', 'inactivo') default 'activo',
	FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE
);

CREATE TABLE horarios (
    id int auto_increment primary key,
    grupo_id int not null,
    dia ENUM('lunes', 'martes', 'miercoles', 'jueves', 'viernes') not null,
    hora_entrada time not null,
    hora_salida time not null,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE
);

CREATE TABLE fichajes (
    id int auto_increment primary key,
    usuario_id int not null,
    fecha_hora_entrada DATETIME DEFAULT NULL,
    fecha_hora_salida DATETIME DEFAULT NULL,
    ubicacion varchar(255), 
    nfc_usado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE ausencias (
    id int auto_increment primary key,
    usuario_id int not null,
    fecha date not null,
    motivo ENUM('falta_injustificada', 'enfermedad', 'vacaciones', 'permiso', 'retraso') DEFAULT 'falta_injustificada',
    justificada boolean default false,
    detalles varchar(255),
    tiempo_registrado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO grupos (nombre, faltas_totales) VALUES 
('Desarrollo', 0),
('Marketing', 0),
('Administración', 0);

INSERT INTO usuarios (nombre, apellido1, apellido2, email, contrasena, rol, grupo_id, estado) VALUES
('Juan', 'Pérez', 'Gómez', 'juan.perez@example.com', 'hashedpassword1', 'jefe', 1, 'activo'),
('María', 'López', 'Fernández', 'maria.lopez@example.com', 'hashedpassword2', 'empleado', 1, 'activo'),
('Carlos', 'Ruiz', 'Martínez', 'carlos.ruiz@example.com', 'hashedpassword3', 'empleado', 2, 'activo'),
('Laura', 'Sánchez', NULL, 'laura.sanchez@example.com', 'hashedpassword4', 'empleado', 2, 'activo'),
('Pedro', 'García', 'Núñez', 'pedro.garcia@example.com', 'hashedpassword5', 'jefe', 3, 'activo'),
('Adrian', 'Alonso', 'Perez', 'adrian', 'a', 'jefe', 3, 'activo');

INSERT INTO horarios (grupo_id, dia, hora_entrada, hora_salida) VALUES 
(1, 'lunes', '09:00:00', '17:00:00'),
(1, 'martes', '09:00:00', '17:00:00'),
(2, 'miércoles', '10:00:00', '18:00:00'),
(3, 'jueves', '08:00:00', '16:00:00'),
(3, 'viernes', '08:00:00', '14:00:00');

INSERT INTO fichajes (usuario_id, fecha_hora_entrada, fecha_hora_salida, ubicacion, nfc_usado) VALUES 
(1, '2025-04-01 09:00:00', '2025-04-01 17:00:00', 'Oficina Principal', TRUE),
(1, '2025-04-01 09:00:00', '2025-04-01 19:00:00', 'Oficina Principal', TRUE),
(1, '2025-04-01 09:00:00', '2025-04-01 15:00:00', 'Oficina Principal', TRUE),
(2, '2025-04-01 09:05:00', '2025-04-01 17:10:00', 'Oficina Principal', FALSE),
(3, '2025-04-02 10:00:00', '2025-04-02 18:00:00', 'Sucursal Norte', TRUE),
(4, '2025-04-03 08:30:00', '2025-04-03 16:30:00', 'Sucursal Sur', FALSE);


INSERT INTO ausencias (usuario_id, fecha, motivo, justificada, detalles) VALUES 
(2, '2025-04-01', 'enfermedad', TRUE, 'Presentó certificado médico'),
(3, '2025-04-02', 'falta_injustificada', FALSE, 'No se presentó al trabajo'),
(4, '2025-04-03', 'vacaciones', TRUE, 'Vacaciones aprobadas'),
(5, '2025-04-04', 'permiso', TRUE, 'Permiso para asuntos personales');


