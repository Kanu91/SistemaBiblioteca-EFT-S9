package org.example.model;

import java.time.LocalDate;

/** fechaDevolucion representa el VENCIMIENTO pactado del préstamo. */
public record Prestamo(int id, int estudianteId, int libroId, String estudiante,
                       String libro, LocalDate fechaPrestamo, LocalDate fechaDevolucion,
                       boolean devuelto, LocalDate fechaReal, int diasAtraso) {
    public boolean estaVencido() {
        return !devuelto && fechaDevolucion != null && fechaDevolucion.isBefore(LocalDate.now());
    }
    public String estado() {
        if (devuelto) return diasAtraso > 0 ? "Devuelto con atraso" : "Devuelto";
        return estaVencido() ? "ATRASADO" : "En préstamo";
    }
}
