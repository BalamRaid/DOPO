package test;
import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;

/**
 * Pruebas de la clase Elephant: ciclo de energía, movimiento, desaparición y color.
 */
public class ElephantTest {

    private EcoSafari habitat;
    private Elephant dumbo;

    @BeforeEach
    public void setUp(){
        habitat = new EcoSafari();
        dumbo = new Elephant(habitat, 5, 5);
    }

    @Test
    public void energiaInicialEs100(){
        assertEquals(100, dumbo.getEnergy());
    }

    @Test
    public void ticMueveYDescuentaEnergia(){
        dumbo.tic();
        assertEquals(90, dumbo.getEnergy());
        assertNull(habitat.get(5,5));
        assertNotNull(habitat.get(6,6));
    }

    @Test
    public void desaparecerAlLlegarACero(){
        dumbo.changeEnergy(-90);
        dumbo.tic();
        assertEquals(0, dumbo.getEnergy());
        assertNull(habitat.find(dumbo));
    }

    @Test
    public void colorGrisOscuroConEnergiaAlta(){
        assertEquals(Color.DARK_GRAY, dumbo.getColor());
    }

    @Test
    public void colorGrisClaroConEnergiaBaja(){
        dumbo.changeEnergy(-30);
        assertEquals(Color.LIGHT_GRAY, dumbo.getColor());
    }

    @Test
    public void noActuaDosVecesEnElMismoTurno(){
        dumbo.tic();
        int[] posicionTrasPrimerTic = habitat.find(dumbo);
        dumbo.tic();
        assertEquals(90, dumbo.getEnergy());
        assertArrayEquals(posicionTrasPrimerTic, habitat.find(dumbo));
    }
}