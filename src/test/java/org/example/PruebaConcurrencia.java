package org.example;

import org.example.config.DatabaseConnection;
import org.example.service.PrestamoService;
import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Prueba manual con base de datos real. Solo úsala con un libro de stock exactamente 1.
 * Ejecutar con argumentos: ID_ESTUDIANTE ID_LIBRO NUMERO_HILOS
 * Modifica datos: deja registrado 1 préstamo si se cumplen las precondiciones.
 */
public final class PruebaConcurrencia {
    public static void main(String[] args) throws Exception {
        if(args.length!=3) {
            System.out.println("Uso: PruebaConcurrencia <ID_ESTUDIANTE> <ID_LIBRO> <HILOS>");return;
        }
        int student=Integer.parseInt(args[0]),book=Integer.parseInt(args[1]),workers=Integer.parseInt(args[2]);
        if(workers<2 || workers>20)throw new IllegalArgumentException("Usa entre 2 y 20 hilos.");
        try(Connection c=DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps=c.prepareStatement("SELECT stock FROM libros WHERE id=?")) {
            ps.setInt(1,book);
            try(ResultSet rs=ps.executeQuery()) {
                if(!rs.next() || rs.getInt(1)!=1)throw new IllegalStateException("El libro debe tener stock=1 ANTES de ejecutar.");
            }
        }
        ExecutorService pool=Executors.newFixedThreadPool(workers);
        CountDownLatch ready=new CountDownLatch(workers),start=new CountDownLatch(1);
        AtomicInteger success=new AtomicInteger(),rejected=new AtomicInteger();
        for(int i=0;i<workers;i++)pool.submit(()->{
            ready.countDown();
            try{start.await();new PrestamoService().registrarPrestamo(student,book);success.incrementAndGet();}
            catch(Exception e){rejected.incrementAndGet();}
        });
        ready.await();start.countDown();pool.shutdown();
        if(!pool.awaitTermination(20,TimeUnit.SECONDS))throw new IllegalStateException("Tiempo de espera excedido.");
        if(success.get()!=1 || rejected.get()!=workers-1)throw new AssertionError("Inconsistencia en operaciones simultáneas.");
        try(Connection c=DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps=c.prepareStatement("SELECT stock FROM libros WHERE id=?")) {
            ps.setInt(1,book);
            try(ResultSet rs=ps.executeQuery()){
                rs.next();if(rs.getInt(1)!=0)throw new AssertionError("El stock debería ser 0.");
            }
        }
        System.out.println("OK concurrencia: "+success+" préstamo aprobado, "+rejected+" rechazados, stock final 0.");
    }
}
