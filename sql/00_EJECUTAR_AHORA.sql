-- EJECUTAR EN MYSQL WORKBENCH UNA SOLA VEZ (o repetir sin eliminar datos)
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

-- Datos de prueba de elaboración propia, distintos de los ejemplos referenciales.
-- Seguro para ejecutar dos veces en una base que tenga los ejemplos del docente.
INSERT INTO categorias(nombre)
  SELECT 'Aventura' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Aventura');
INSERT INTO categorias(nombre)
  SELECT 'Ciencias Naturales' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Ciencias Naturales');
INSERT INTO categorias(nombre)
  SELECT 'Tecnología' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Tecnología');
INSERT INTO categorias(nombre)
  SELECT 'Historia de Chile' WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Historia de Chile');

INSERT INTO estudiantes(nombre,rut,curso,correo)
  SELECT 'Valentina Rojas','21123456-7','2 Medio A','valentina.rojas@escuela.local'
  WHERE NOT EXISTS (SELECT 1 FROM estudiantes WHERE rut='21123456-7');
INSERT INTO estudiantes(nombre,rut,curso,correo)
  SELECT 'Diego Navarro','20987654-3','3 Medio B','diego.navarro@escuela.local'
  WHERE NOT EXISTS (SELECT 1 FROM estudiantes WHERE rut='20987654-3');

INSERT INTO usuarios(nombre,rut,correo,`contraseña`,rol)
  SELECT 'Camila Fuentes','15678432-1','admin@biblioteca.local','p2$150000$Sl8mdsv+j7r6kFj2aS5nZw$hJg4DZ1hg42SFA/M7m825d6Ti+5AS3/uCKzCffrM3Uk','bibliotecario'
  WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE rut='15678432-1' OR correo='admin@biblioteca.local');
INSERT INTO usuarios(nombre,rut,correo,`contraseña`,rol)
  SELECT 'Valentina Rojas','21123456-7','estudiante@biblioteca.local','p2$150000$RKtjM+RLY6z2+yav9vFzYQ$Cb8Byrr9czBIsbgNemn2qz9ia34x3CvMVlvf3uDjhU0','estudiante'
  WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE rut='21123456-7' OR correo='estudiante@biblioteca.local');

INSERT INTO libros(titulo,autor,isbn,editorial,stock,id_categoria)
  SELECT 'El faro de los vientos','Ignacio Leiva','9789560001001','Editorial Horizonte',4,c.id
  FROM categorias c WHERE c.nombre='Aventura'
  AND NOT EXISTS (SELECT 1 FROM libros WHERE isbn='9789560001001') LIMIT 1;
INSERT INTO libros(titulo,autor,isbn,editorial,stock,id_categoria)
  SELECT 'Exploradores del ecosistema','Paula Salinas','9789560001002','Aula Viva',5,c.id
  FROM categorias c WHERE c.nombre='Ciencias Naturales'
  AND NOT EXISTS (SELECT 1 FROM libros WHERE isbn='9789560001002') LIMIT 1;
INSERT INTO libros(titulo,autor,isbn,editorial,stock,id_categoria)
  SELECT 'Programación para imaginar','Tomás Espinoza','9789560001003','Nexo Digital',3,c.id
  FROM categorias c WHERE c.nombre='Tecnología'
  AND NOT EXISTS (SELECT 1 FROM libros WHERE isbn='9789560001003') LIMIT 1;
INSERT INTO libros(titulo,autor,isbn,editorial,stock,id_categoria)
  SELECT 'Relatos del valle central','Laura Mella','9789560001004','Sur Editorial',2,c.id
  FROM categorias c WHERE c.nombre='Historia de Chile'
  AND NOT EXISTS (SELECT 1 FROM libros WHERE isbn='9789560001004') LIMIT 1;

-- Comprobación
SELECT 'usuarios' AS tabla,COUNT(*) AS registros FROM usuarios
UNION ALL SELECT 'estudiantes',COUNT(*) FROM estudiantes
UNION ALL SELECT 'categorias',COUNT(*) FROM categorias
UNION ALL SELECT 'libros',COUNT(*) FROM libros
UNION ALL SELECT 'prestamos',COUNT(*) FROM prestamos;
