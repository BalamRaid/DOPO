import java.awt.*;

/**
 * A triangle that can be manipulated and that draws itself on a canvas.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0  (15 July 2000)
 */

public class Triangle{
    
    /**
     * Número de vértices que tiene un triángulo. Es una constante común
     * a todas las instancias de la clase.
     */
    public static final int VERTICES=3;
    
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;

    /**
     * Create a new triangle at default position with default color.
     */
    public Triangle(){
        height = 30;
        width = 40;
        xPosition = 140;
        yPosition = 15;
        color = "green";
        isVisible = false;
    }
    
    /**
     * Crea un nuevo triángulo con el color, ancho y alto indicados,
     * ubicado en la posición por defecto.
     *
     * @param color color inicial del triángulo
     * @param width ancho inicial en píxeles
     * @param height alto inicial en píxeles
     */
    public Triangle(String color, int width, int height){
        this.color = color;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Calcula el perímetro actual del triángulo, asumiendo que es isósceles
     * @return el perímetro del triángulo en píxeles
     */
    public double perimeter(){
        double lado = Math.sqrt(Math.pow(width / 2.0, 2) + Math.pow(height, 2));
        return width + 2 * lado;
}
    
    /**
     * Calcula el área actual del triángulo, a partir de su alto y ancho.
     *
     * @return el área del triángulo en píxeles cuadrados
     */
    public int area(){
        return (height * width)/2;
    }

    /**
     * Convierte el triángulo en uno equilátero, conservando de forma
     * aproximada la misma área que tenía antes de la conversión.
     */
    public void equilateral(){
        int areaActual = area();
        double nuevoWidth = Math.sqrt(4 * areaActual/ Math.sqrt(3));
        double nuevoHeight = (nuevoWidth * Math.sqrt(3))/ 2; 
        
        erase();
        
        width = (int) Math.round(nuevoWidth);
        height = (int) Math.round(nuevoHeight);
        
        draw();
    }
    
    /**
     * Mueve el triángulo horizontalmente, un paso de 20 píxeles a la vez,
     * la cantidad de veces indicada. Un valor positivo lo mueve hacia la
     * derecha, un valor negativo lo mueve hacia la izquierda.
     *
     * @param times número de pasos a mover (puede ser negativo)
     */
    public void walk(int times){
        if(times == 0){
            System.out.println("Sin movimiento");
        }else{
            for(int i = 0; i < Math.abs(times); i++){
                if(times > 0){
                  moveRight();  
                }else{
                    moveLeft();
                }
            }
        }
    }
    
    /**
     * Make this triangle visible. If it was already visible, do nothing.
     */
    public void makeVisible(){
        isVisible = true;
        draw();
    }
    
    /**
     * Make this triangle invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible(){
        erase();
        isVisible = false;
    }
    
    /**
     * Move the triangle a few pixels to the right.
     */
    public void moveRight(){
        moveHorizontal(20);
    }

    /**
     * Move the triangle a few pixels to the left.
     */
    public void moveLeft(){
        moveHorizontal(-20);
    }

    /**
     * Move the triangle a few pixels up.
     */
    public void moveUp(){
        moveVertical(-20);
    }

    /**
     * Move the triangle a few pixels down.
     */
    public void moveDown(){
        moveVertical(20);
    }

    /**
     * Move the triangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance){
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the triangle vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance){
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Slowly move the triangle horizontally.
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            xPosition += delta;
            draw();
        }
    }

    /**
     * Slowly move the triangle vertically.
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            yPosition += delta;
            draw();
        }
    }

    /**
     * Change the size to the new size
     * @param newHeight the new height in pixels. newHeight must be >=0.
     * @param newWidht the new width in pixels. newWidht must be >=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }
    
    /**
     * Change the color. 
     * @param color the new color. Valid colors are "red", "yellow", "blue", "green",
     * "magenta" and "black".
     */
    public void changeColor(String newColor){
        color = newColor;
        draw();
    }

    /*
     * Draw the triangle with current specifications on screen.
     */
    private void draw(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            int[] xpoints = { xPosition, xPosition + (width/2), xPosition - (width/2) };
            int[] ypoints = { yPosition, yPosition + height, yPosition + height };
            canvas.draw(this, color, new Polygon(xpoints, ypoints, 3));
            canvas.wait(10);
        }
    }

    /*
     * Erase the triangle on screen.
     */
    private void erase(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}