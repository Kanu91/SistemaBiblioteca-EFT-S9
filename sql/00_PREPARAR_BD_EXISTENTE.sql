-- SISTEMA DE GESTIÓN DE BIBLIOTECA ESCOLAR
-- Compatible con MySQL 8.x. La base de datos utiliza utf8mb4.
-- fecha_devolucion de la tabla prestamos = FECHA DE VENCIMIENTO programada.
-- devoluciones registra la fecha REAL y los días de atraso.
CREATE DATABASE IF NOT EXISTS biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca;
-- Ejecuta este script si ya creaste las CINCO tablas con el archivo del docente.
-- No elimina ni reemplaza datos existentes.
CREATE TABLE IF NOT EXISTS devoluciones (
  id INT PRIMARY KEY AUTO_INCREMENT,
  id_prestamo INT NOT NULL UNIQUE,
  fecha_real DATETIME NOT NULL,
  dias_atraso INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_devolucion_prestamo FOREIGN KEY(id_prestamo) REFERENCES prestamos(id)
) ENGINE=InnoDB;
-- Para agregar usuarios y libros de demostración, ejecuta luego 03_datos_demo.sql.
