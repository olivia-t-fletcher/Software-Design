package nz.ac.vuw.ecs.swen225.gp24.domain;

/**
 * The type Chip. This is the treasure
 */
public class Chip implements Cell{

    /**
     * chip can be walked on
     * @return true
     */
    @Override
    public boolean passable() {
        return true;
    }

    /**
     * returns a replacement cell and increments the player's chip counter.
     *
     * @param g the game the chip is being picked up from
     * @return cell to replace the chip with once the player moves out of the way.
     */
    public Cell pickUp(Game g){
        g.player().pickUp();
        return new FreeTile();
    }
    public String toString(){return "Chip";}

}
