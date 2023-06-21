package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Info field.
 */
public class InfoField implements Cell{
    /**
     * The Info field.
     */
    String info;

    /**
     * Instantiates a new Info field.
     *
     * @param info the info stored
     */
    public InfoField(String info){this.info = info;}
    @Override
    public boolean passable() {
        return true;
    }
    public String toString(){return "InfoField";}

    /**
     * Get information string.
     *
     * @return information to display
     */
    public String getInformation(){return info;}

    /**
     * two info fields are the same iff their information is the same
     * @param o object to compare
     * @return whether they are the same
     */
    public boolean equals(Object o){
        if(o instanceof InfoField i){
            return i.getInformation().equals(this.info);
        }
        return false;
    }
    public int hashCode(){
        return this.getInformation().hashCode();
    }
}
