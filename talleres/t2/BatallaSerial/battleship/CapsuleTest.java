package battleship;

import static battleship.TestFixtures.HERE;
import static battleship.TestFixtures.shipWithCrew;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Capsule}: inmunidad, ausencia de debilidad, cadena de
 * nodrizas y regla de autodestrucción por destrucción de su nodriza.
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("Capsule")
class CapsuleTest {

    // ------------------------------------------------------------------
    // Características propias
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Nunca es débil")
    void neverWeak() {
        Capsule capsule = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        assertFalse(capsule.isWeak());
    }

    @Test
    @DisplayName("Es inmune a las explosiones")
    void isImmune() {
        Capsule capsule = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        assertFalse(capsule.canBeDestroyed());
    }

    @Test
    @DisplayName("No tiene tripulantes: no necesita marinos ni tiene pilotos")
    void hasNoCrew() {
        Capsule capsule = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        assertFalse(capsule.needsSailors());
        assertTrue(capsule.assignedPilots().isEmpty());
    }

    @Test
    @DisplayName("No se puede crear una cápsula sin nodriza ni sin posición")
    void requiresMotherAndLocation() {
        assertThrows(NullPointerException.class, () -> new Capsule(HERE, null));
        assertThrows(NullPointerException.class,
                () -> new Capsule(null, shipWithCrew(1, HERE, 1)));
    }

    @Test
    @DisplayName("Conserva a su nodriza")
    void keepsMother() {
        Ship ship = shipWithCrew(1, HERE, 1);
        assertSame(ship, new Capsule(HERE, ship).getMother());
    }

    // ------------------------------------------------------------------
    // Movimiento
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Siempre puede moverse al norte")
    void alwaysCanMove() throws BattleShipException {
        Capsule capsule = new Capsule(new Position(4, 4), shipWithCrew(1, HERE, 1));
        assertTrue(capsule.canMove());
        capsule.moveNorth();
        assertEquals(new Position(4, 5), capsule.getLocation());
    }

    // ------------------------------------------------------------------
    // Instrucciones y cadena de nodrizas
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Pide instrucción a su nodriza barco")
    void requestsInstructionFromShip() {
        Ship ship = shipWithCrew(900, HERE, 1);
        Capsule capsule = new Capsule(HERE, ship);
        assertEquals(ship.giveInstruction(), capsule.requestInstruction());
    }

    @Test
    @DisplayName("La instrucción se propaga por una cadena de cápsulas hasta el barco")
    void instructionPropagatesThroughCapsuleChain() {
        Ship ship = shipWithCrew(900, HERE, 1);
        Capsule first = new Capsule(HERE, ship);
        Capsule second = new Capsule(HERE, first);
        Capsule third = new Capsule(HERE, second);
        assertEquals(ship.giveInstruction(), third.requestInstruction());
        assertEquals(ship.giveInstruction(), second.giveInstruction());
    }

    @Test
    @DisplayName("Una cápsula puede ser nodriza de otra (es un Mothership)")
    void capsuleCanBeMother() {
        Capsule mother = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        Mothership asMother = mother;
        Capsule child = new Capsule(HERE, asMother);
        assertSame(mother, child.getMother());
    }

    // ------------------------------------------------------------------
    // Autodestrucción
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Mientras su nodriza exista no decide autodestruirse")
    void doesNotSelfDestructWhileMotherLives() {
        Capsule capsule = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        assertFalse(capsule.mustSelfDestruct());
        assertEquals(SelfDestructible.NO_DECISION, capsule.getSelfDestructCause());
    }

    @Test
    @DisplayName("Decide autodestruirse cuando su nodriza es destruida y explica la causa")
    void selfDestructsWhenMotherIsDestroyed() {
        Ship ship = shipWithCrew(1, HERE, 1);
        Capsule capsule = new Capsule(HERE, ship);
        ship.selfDestruct();
        assertTrue(capsule.mustSelfDestruct());
        assertEquals(Capsule.MOTHER_DESTROYED_CAUSE, capsule.getSelfDestructCause());
    }

    @Test
    @DisplayName("Ignora la instrucción general de autodestrucción: solo depende de su nodriza")
    void ignoresGeneralInstruction() {
        Capsule capsule = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        capsule.receiveSelfDestructInstruction();
        assertFalse(capsule.mustSelfDestruct());
    }

    @Test
    @DisplayName("Si la nodriza es otra cápsula, la destrucción de esta la arrastra")
    void capsuleMotherDestructionPropagates() {
        Capsule mother = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        Capsule child = new Capsule(HERE, mother);
        assertFalse(child.mustSelfDestruct());
        mother.selfDestruct();
        assertTrue(child.mustSelfDestruct());
    }

    @Test
    @DisplayName("Puede autodestruirse aunque sea inmune a los ataques")
    void canSelfDestructDespiteImmunity() {
        Capsule capsule = new Capsule(HERE, shipWithCrew(1, HERE, 1));
        assertFalse(capsule.isDestroyed());
        capsule.selfDestruct();
        assertTrue(capsule.isDestroyed());
    }
}
