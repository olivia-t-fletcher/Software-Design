package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The interface Cell. all blocks drawn on screen implement this interface.
 */
public interface Cell {
    /**
     * Cell has methods for:
     * whether it can be walked on
     *
     * @return the boolean
     */
    boolean passable();

}
