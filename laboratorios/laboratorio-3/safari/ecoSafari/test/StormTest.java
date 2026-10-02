package test;
import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;

/**
 * Pruebas de la clase Storm: desplazamiento circular en diagonal noreste,
 * destrucción de lo que encuentra en su centro, color, control de turno y área.
 */
public class StormTest {

    private EcoSafari habitat;

    @BeforeEach
    public void setUp(){
        habitat = new EcoSafari();
    }

    @Test
    public void seMueveEnDiagonalNoreste(){
        Storm tormenta = new Storm(habitat, 10, 10);
        tormenta.tic();
        assertNull(habitat.get(10,10));
        assertSame(tormenta, habitat.get(9,11));
    }

    @Test
    public void reapareceAlSalirPorElNorte(){
        Storm tormenta = new Storm(habitat, 0, 10);
        tormenta.tic();
        assertSame(tormenta, habitat.get(24,11));
    }

    @Test
    public void reapareceAlSalirPorElEste(){
        Storm tormenta = new Storm(habitat, 10, 24);
        tormenta.tic();
        assertSame(tormenta, habitat.get(9,0));
    }

    @Test
    public void destruyeLoQueEncuentraEnSuCentro(){
        Elephant dumbo = new Elephant(habitat, 9, 11);
        Storm tormenta = new Storm(habitat, 10, 10);
        tormenta.tic();
        assertNull(habitat.find(dumbo));
        assertSame(tormenta, habitat.get(9,11));
    }

    @Test
    public void esNegraYNoEsOrganismo(){
        Storm tormenta = new Storm(habitat, 10, 10);
        assertEquals(Color.BLACK, tormenta.getColor());
        assertFalse(tormenta.isOrganism());
    }

    @Test
    public void noSeMueveDosVecesPorTurno(){
        Storm tormenta = new Storm(habitat, 10, 10);
        tormenta.tic();
        tormenta.tic();
        assertSame(tormenta, habitat.get(9,11));
        tormenta.tac();
        tormenta.tic();
        assertSame(tormenta, habitat.get(8,12));
    }

    @Test
    public void afectaUnAreaDeRadioUno(){
        Storm tormenta = new Storm(habitat, 10, 10);
        assertEquals(1, tormenta.getAffectedRadius());
    }
}