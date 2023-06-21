package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * Wall class. Can't be walked through or opened
 */
public class Wall implements Cell {
    public boolean passable() {
        return false;
    }

    public String toString() {
        return "Wall";
    }

}
