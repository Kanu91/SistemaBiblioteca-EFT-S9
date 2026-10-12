-- SISTEMA DE GESTIÓN DE BIBLIOTECA ESCOLAR
-- Compatible con MySQL 8.x. La base de datos utiliza utf8mb4.
-- fecha_devolucion de la tabla prestamos = FECHA DE VENCIMIENTO programada.
-- devoluciones registra la fecha REAL y los días de atraso.
CREATE DATABASE IF NOT EXISTS biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca;

CREATE TABLE IF NOT EXISTS usuarios (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  rut VARCHAR(12) UNIQUE,
  correo VARCHAR(100),
  `contraseña` VARCHAR(100),
  rol ENUM('bibliotecario','estudiante') NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS estudiantes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  rut VARCHAR(12) UNIQUE,
  curso VARCHAR(20),
  correo VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categorias (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS libros (
  id INT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(200),
  autor VARCHAR(100),
  isbn VARCHAR(20) UNIQUE,
  editorial VARCHAR(100),
  stock INT,
  id_categoria INT,
  CONSTRAINT fk_libros_categoria FOREIGN KEY(id_categoria) REFERENCES categorias(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prestamos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  id_estudiante INT,
  id_libro INT,
  fecha_prestamo DATE,
  fecha_devolucion DATE,
  devuelto BOOLEAN DEFAULT FALSE,
  CONSTRAINT fk_prestamos_estudiante FOREIGN KEY(id_estudiante) REFERENCES estudiantes(id),
  CONSTRAINT fk_prestamos_libro FOREIGN KEY(id_libro) REFERENCES libros(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS devoluciones (
  id INT PRIMARY KEY AUTO_INCREMENT,
  id_prestamo INT NOT NULL UNIQUE,
  fecha_real DATETIME NOT NULL,
  dias_atraso INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_devolucion_prestamo FOREIGN KEY(id_prestamo) REFERENCES prestamos(id)
) ENGINE=InnoDB;
