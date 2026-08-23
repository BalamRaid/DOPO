import java.awt.Shape;
import java.awt.geom.*;

/**
 * Maps symbol colors to a hand-designed Shape when a curated design
 * exists, falling back to a generic circle for any other valid CSS
 * color. Pure geometry only — knows nothing about Canvas or drawing.
 */
public class SymbolShapeCatalog {

    /**
     * Returns the Shape associated with the given color, centered at (0,0).
     */
    public static Shape shapeFor(String color) {
        switch (color) {
            case "red":    return square();
            case "orange": return crescentMoon();
            case "yellow": return triangle();
            case "green":  return hexagram();
            case "blue":   return circle();
            case "pink":   return lotus(2, 20, 9);
            case "violet": return lotus(12, 16, 5);
            default:       return circle();
        }
    }

    private static Shape square() {
        return new Rectangle2D.Double(-15, -15, 30, 30);
    }

    private static Shape circle() {
        return new Ellipse2D.Double(-15, -15, 30, 30);
    }

    private static Shape triangle() {
        Path2D p = new Path2D.Double();
        p.moveTo(0, -18);
        p.lineTo(16, 12);
        p.lineTo(-16, 12);
        p.closePath();
        return p;
    }

    private static Shape crescentMoon() {
        Area moon = new Area(new Ellipse2D.Double(-15, -15, 30, 30));
        moon.subtract(new Area(new Ellipse2D.Double(-5, -17, 30, 30)));
        return moon;
    }

    private static Shape hexagram() {
        Path2D star = new Path2D.Double();
        star.moveTo(0, -18);
        star.lineTo(16, 12);
        star.lineTo(-16, 12);
        star.closePath();
        star.moveTo(0, 18);
        star.lineTo(16, -12);
        star.lineTo(-16, -12);
        star.closePath();
        return star;
    }

    /**
     * A single petal shape pointing "up" from the origin, used as the
     * building block for lotus symbols.
     */
    private static Path2D petal(double length, double width) {
        Path2D p = new Path2D.Double();
        p.moveTo(0, 0);
        p.curveTo(width, -length * 0.5, width, -length * 0.8, 0, -length);
        p.curveTo(-width, -length * 0.8, -width, -length * 0.5, 0, 0);
        p.closePath();
        return p;
    }

    /**
     * Builds a stylized lotus by repeating one petal, rotated evenly
     * around the center. "count" petals is a simplification — used for
     * both the 2-petal and the (stylized) "thousand-petal" lotus.
     */
    private static Shape lotus(int count, double length, double width) {
        Path2D combined = new Path2D.Double();
        Path2D basePetal = petal(length, width);
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            AffineTransform rotation = AffineTransform.getRotateInstance(angle);
            combined.append(rotation.createTransformedShape(basePetal), false);
        }
        return combined;
    }
}