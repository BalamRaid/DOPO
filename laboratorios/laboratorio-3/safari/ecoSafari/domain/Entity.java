package domain;
import java.awt.Color;

//Include the documentation
public interface Entity{
   public static final int SQUARE = 2;
   public static final int ROUND = 1;
    
   public void tic();
  
   public default void tac(){
   }

   public default int shape(){
      return SQUARE;
   }
  
   public abstract Color getColor();
  
    public default boolean isOrganism(){
      return false;
    }

   /**
     * Devuelve el radio del área que esta entidad afecta alrededor de su posición.
     * Un radio 0 indica que no afecta a las celdas vecinas.
     * @return el radio del área afectada
     */
    public default int getAffectedRadius(){
        return 0;
    }    
    
   public abstract EcoSafari getHabitat();
    
   public default boolean disappear(){
     boolean ok=false;
     int [] position=this.getHabitat().find(this);
        if (position!=null){
            getHabitat().set(null,position[0],position[1]);
            ok=true;
        }
        return ok;
    }
    
    public default  boolean move(int deltaRows, int deltaColumns){
        int [] position=getHabitat().find(this);
        EcoSafari habitat=getHabitat();
        boolean ok=false;
        if (position!=null){
            int r = position[0];
            int c = position[1];
            if (habitat.isInside(r+deltaRows,c+deltaColumns)){
                habitat.set(null,r,c);
                habitat.set(this,r+deltaRows,c+deltaColumns);
                ok=true;
            }
        }
        return ok;
    }
    
}
