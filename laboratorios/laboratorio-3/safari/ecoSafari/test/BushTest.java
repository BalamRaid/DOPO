package test;
import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;

/**
 * Pruebas de la clase Bush: color por edad, retoño único con prioridad
 * de dirección, y desaparición al quedar vecino a un elefante.
 */
public class BushTest {

    private EcoSafari habitat;
    private Bush arbusto;

    @BeforeEach
    public void setUp(){
        habitat = new EcoSafari();
        arbusto = new Bush(habitat, 10, 10);
    }

    @Test
    public void colorVerdeAlNacer(){
        assertEquals(Color.GREEN, arbusto.getColor());
    }

    @Test
    public void colorAmarilloAlosCuatroTics(){
        for (int i=0; i<4; i++){
            arbusto.tic();
        }
        assertEquals(Color.YELLOW, arbusto.getColor());
    }

    @Test
    public void retonaAlNorteEnSegundoTic(){
        arbusto.tic();
        arbusto.tic();
        assertNotNull(habitat.get(9,10));
        assertTrue(habitat.get(9,10) instanceof Bush);
    }

    @Test
    public void retonaAlSurSiNorteOcupado(){
        new Bush(habitat, 9, 10);
        arbusto.tic();
        arbusto.tic();
        assertNotNull(habitat.get(11,10));
        assertTrue(habitat.get(11,10) instanceof Bush);
    }

    @Test
    public void noRetonaDosVeces(){
        arbusto.tic();
        arbusto.tic();
        habitat.set(null, 9, 10);
        arbusto.tic();
        assertNull(habitat.get(9,10));
    }

    @Test
    public void desapareceConElefanteVecino(){
        new Elephant(habitat, 10, 11);
        arbusto.tic();
        assertNull(habitat.find(arbusto));
    }
}