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
    motivo ENUM('falta_injustificada', 'enfermedad', 'vacaciones', 'permiso', 'retraso', 'otro') DEFAULT 'falta_injustificada',
    estado ENUM('vacio','pendiente', 'aceptada', 'rechazada') default 'vacio',
    justificada boolean default false,
    detalles varchar(255),
    tiempo_registrado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO grupos (nombre, faltas_totales) VALUES
('Ventas', 0),
('Soporte', 0),
('Recursos Humanos', 0),
('IT', 0),
('Operaciones', 0),
('Calidad', 0),
('Logística', 0),
('Sin Asignar', 0);

INSERT INTO usuarios (nombre, apellido1, apellido2, email, contrasena, rol, grupo_id, estado) VALUES
('Ana', 'Martín', 'Lopez', 'ana.martin@educa.jcyl.es', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'empleado', 5, 'activo'),
('Luis', 'Fernández', 'Ortiz', 'luis.fernandez@educa.jcyl.es', '$2a$10$yyyyyyyyyyyyyyyyyyyyyyyyyyyyyy', 'empleado', 6, 'activo'),
('Elena', 'García', 'Suárez', 'elena.garcia@educa.jcyl.es', '$2a$10$zzzzzzzzzzzzzzzzzzzzzzzzzzzz', 'empleado', 7, 'activo'),
('Miguel', 'Santos', NULL, 'miguel.santos@educa.jcyl.es', '$2a$10$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'empleado', 4, 'activo'),
('Sonia', 'Ruiz', 'Pérez', 'sonia.ruiz@educa.jcyl.es', '$2a$10$bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 'empleado', 2, 'activo'),
('Jorge', 'Domínguez', 'Fernández', 'jorge.dominguez@educa.jcyl.es', '$2a$10$cccccccccccccccccccccccccccccc', 'jefe', 5, 'activo'),
('Patricia', 'Moreno', 'Nadal', 'patricia.moreno@educa.jcyl.es', '$2a$10$dddddddddddddddddddddddddddddd', 'empleado', 3, 'inactivo'),
('Raúl', 'Ibáñez', 'Gómez', 'raul.ibanez@educa.jcyl.es', '$2a$10$eeeeeeeeeeeeeeeeeeeeeeeeeeeeee', 'empleado', 1, 'activo'),
('Lorena', 'Domínguez', 'Sánchez',  'lorena.dominguez@educa.jcyl.es', '$2a$10$ffffffffffffffffffffffffffffff', 'empleado', 2, 'activo'),
('Óscar', 'Torres', 'Martín', 'oscar.torres@educa.jcyl.es', '$2a$10$gggggggggggggggggggggggggggggg', 'empleado', 4, 'activo'),
('Marcos', 'Gil', 'Vega', 'marcos.gil@educa.jcyl.es', '$2a$10$hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh', 'empleado', 6, 'activo'),
('Nuria', 'Castro', 'Ramos', 'nuria.castro@educa.jcyl.es', '$2a$10$iiiiiiiiiiiiiiiiiiiiiiiiiiiiii', 'empleado', 7, 'activo'),
('Héctor', 'Prieto', NULL, 'hector.prieto@educa.jcyl.es', '$2a$10$jjjjjjjjjjjjjjjjjjjjjjjjjjjjjj', 'jefe', 4, 'activo'),
('Isabel', 'Reyes', 'Ortiz', 'isabel.reyes@educa.jcyl.es', '$2a$10$kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk', 'empleado', 3, 'activo'),
('Pepe', 'García', 'Núñez', 'pepe@educa.jcyl.es', '$2a$10$VZKnGnSF9VP6wZBh847O5Oh/5Ki3GJUYoLvEIHuXQxuoWdcdp5edK', 'empleado', 3, 'activo'),
('Adrian', 'Alonso', 'Perez', 'adrian@educa.jcyl.es', '$2a$10$VZKnGnSF9VP6wZBh847O5Oh/5Ki3GJUYoLvEIHuXQxuoWdcdp5edK', 'jefe', 3, 'activo');

INSERT INTO horarios (grupo_id, dia, hora_entrada, hora_salida) VALUES
(5, 'lunes',    '08:30:00', '16:30:00'),
(5, 'martes',   '08:30:00', '16:30:00'),
(5, 'miercoles','08:30:00', '16:30:00'),
(5, 'jueves',   '08:30:00', '16:30:00'),
(5, 'viernes',  '08:30:00', '16:30:00'),
(6, 'lunes',    '10:00:00', '18:00:00'),
(6, 'martes',   '10:00:00', '18:00:00'),
(6, 'miercoles','10:00:00', '18:00:00'),
(6, 'jueves',   '10:00:00', '18:00:00'),
(6, 'viernes',  '10:00:00', '18:00:00'),
(7, 'lunes',    '09:00:00', '17:00:00'),
(7, 'martes',   '09:00:00', '17:00:00'),
(7, 'miercoles','09:00:00', '17:00:00'),
(7, 'jueves',   '09:00:00', '17:00:00'),
(7, 'viernes',  '09:00:00', '17:00:00');

INSERT INTO fichajes (usuario_id, fecha_hora_entrada, fecha_hora_salida, ubicacion, nfc_usado) VALUES 
(1, '2025-04-02 09:05:00','2025-04-02 17:15:00','Oficina Principal', TRUE),
(1, '2025-04-03 09:00:00','2025-04-03 16:45:00','Oficina Principal', FALSE),
(1, '2025-04-04 09:10:00','2025-04-04 17:00:00','Oficina Principal', TRUE),
(1, '2025-04-05 09:00:00','2025-04-05 12:00:00','Oficina Principal', FALSE),
(2, '2025-04-02 09:30:00','2025-04-02 17:30:00','Oficina Principal', TRUE),
(2, '2025-04-03 09:15:00','2025-04-03 17:00:00','Oficina Principal', TRUE),
(3, '2025-04-01 10:00:00','2025-04-01 18:00:00','Call Center', FALSE),
(3, '2025-04-02 10:05:00','2025-04-02 18:10:00','Call Center', TRUE),
(4, '2025-04-01 08:00:00','2025-04-01 16:00:00','Administración', FALSE),
(4, '2025-04-02 08:05:00','2025-04-02 16:00:00','Administración', TRUE),
(15, '2025-04-02 08:05:00','2025-04-02 16:00:00','Administración', TRUE),
(16, '2025-04-02 08:05:00','2025-04-02 16:00:00','Administración', TRUE);


INSERT INTO ausencias (usuario_id, fecha, motivo, estado, justificada, detalles) VALUES 
(2, '2025-04-01', 'enfermedad', 'aceptada', TRUE, 'Presentó certificado médico'),
(3, '2025-04-02', 'falta_injustificada', 'rechazada', FALSE, 'No se presentó al trabajo'),
(4, '2025-04-03', 'vacaciones', 'aceptada', TRUE, 'Vacaciones aprobadas'),
(5, '2025-04-04', 'permiso', 'aceptada', TRUE, 'Permiso para asuntos personales');

