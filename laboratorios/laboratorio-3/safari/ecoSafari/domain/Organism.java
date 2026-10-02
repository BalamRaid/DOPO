package domain;

public abstract class Organism{
    
    private int energy; //Inv: 0<=energy<=100   
   
    /**Create a new Organism
     */
    public Organism(){
        energy=100;
    }


   /**Changes the organism's energy level by a given value.
     * @param value
     */
    public final void changeEnergy(int value){
        energy+=value;
        energy= (energy<0? 0 : (energy>100? 100 : energy));
    }  
    
    /**Changes the organism's energy level by a given percentage.
     * @param percentage 
     * @return 
     */
    public final void changeEnergy(float percentage){
        changeEnergy((int)Math.ceil(energy*percentage));
    }    
    
    
     /**Returns the energy
     *@return 
     */   
    public final int getEnergy(){
        return energy;
    }    
   
    /**Returns that it is an organism
     * @return
     */
    public final boolean isOrganism(){
        return true;
    } 
    
}
