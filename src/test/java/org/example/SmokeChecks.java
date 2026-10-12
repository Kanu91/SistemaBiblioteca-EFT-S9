package org.example;

import org.example.model.Prestamo;
import org.example.service.PasswordUtil;
import org.example.view.*;
import java.time.LocalDate;

/** Prueba autónoma sin servidor, sin bibliotecas de test externas. */
public final class SmokeChecks {
    private static void check(boolean condition,String message) {
        if (!condition) throw new IllegalStateException("FALLÓ: "+message);
    }
    public static void main(String[] args) {
        String hash=PasswordUtil.hash("PruebaSegura2026!");
        check(hash.length()<=100,"el hash debe caber en VARCHAR(100)");
        check(PasswordUtil.verify("PruebaSegura2026!",hash),"PBKDF2 verifica credencial válida");
        check(!PasswordUtil.verify("Incorrecta",hash),"PBKDF2 rechaza credencial inválida");
        check(PasswordUtil.verify("legado","legado"),"compatibilidad poblado original docente");
        check(!PasswordUtil.verify("no","legado"),"rechazo contraseña legado equivocada");
        Prestamo pendiente=new Prestamo(1,1,1,"Estudiante","Libro",LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(3),false,null,0);
        check(pendiente.estaVencido(),"detección de mora");
        check("ATRASADO".equals(pendiente.estado()),"estado de mora");
        new CategoriasPanel();new EstudiantesPanel();new LibrosPanel(true);
        new LibrosPanel(false);new UsuariosPanel(new org.example.model.Usuario(1,"Prueba","11","a@b", "bibliotecario"));
        new PrestamosPanel(new org.example.model.Usuario(1,"Prueba","11","a@b", "bibliotecario"));
        new ReportesPanel();
        System.out.println("OK: hash, autenticación, vencimiento y construcción de paneles.");
    }
}
