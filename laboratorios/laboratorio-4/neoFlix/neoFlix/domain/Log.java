package domain;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

/**
 * Bitácora de errores del sistema: escribe los incidentes NO esperados en el
 * archivo "NeoSeries.log" (en el directorio de trabajo) para que el equipo
 * técnico pueda investigarlos después. El usuario final nunca ve este archivo.
 */
public class Log{

    /** Nombre base del archivo de bitácora (se le agrega la extensión ".log"). */
    public static String name="NeoSeries";

    /**
     * Registra una excepción en la bitácora: agrega una entrada con marca de
     * tiempo (fecha y hora), la clase y el método que la registran, el nivel
     * SEVERE, el mensaje de la excepción y su pila completa; si el registro no
     * se puede escribir, informa el problema y termina la aplicación.
     *
     * @param e excepción que se desea registrar
     */
    public static void record(Exception e){
        try{
            Logger logger=Logger.getLogger(name);
            logger.setUseParentHandlers(false);
            FileHandler file=new FileHandler(name+".log",true);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.log(Level.SEVERE,e.toString(),e);
            file.close();
        }catch (Exception oe){
            oe.printStackTrace();
            System.exit(0);
        }
    }
}
