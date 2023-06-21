package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Free tile.
 */
public class FreeTile implements Cell {
    /**
     * free tile can be walked on & that's it
     * @return free tiles can be walked on
     */
    @Override
    public boolean passable() {
        return true;
    }
    public String toString(){return "FreeTile";}
}
