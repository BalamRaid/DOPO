package domain;
import java.awt.Color;


//Include the documentation
public class Elephant extends Organism implements Entity{
    private final EcoSafari habitat;
    private boolean hasActed;
    
    public Elephant(EcoSafari habitat,int row, int column){
        this.habitat=habitat;
        habitat.set((Entity)this, row, column);  
        hasActed=false;
    }

    public EcoSafari getHabitat(){
        return habitat;
    }
    
    public final Color getColor(){
        return(getEnergy()>=80? Color.DARK_GRAY: Color.LIGHT_GRAY);
    }

    public final int shape(){
        return Entity.ROUND;
    }

    public void tic(){
        if ((! hasActed) && (move(1, 1))) {
            changeEnergy(-10);
            if (getEnergy()==0){
                disappear();
            }
        }
        hasActed=true;
    }
    
    public void tac(){
        hasActed=false;
    }    
}
