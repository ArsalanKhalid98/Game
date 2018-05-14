import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * This class has all the information about the player and enemy.
 *
 * @author s25919, s325894
 */

public class Player extends GameObjects {

    private int hp;
    private double speedMultiplier;
    private int score;
    private boolean exploded;

    /**
     *
     * @param sprite String that contains the sprite/image.
     * @param hp int variable that contains the number of hp a player has.
     * @param root
     */
    Player(String sprite, int hp, Pane root) {
        super(new ImageView(String.valueOf(Bullet.class.getResource(sprite))));
        this.hp = hp;
        root.getChildren().add(getView());
    }
    int getHp() {
        return hp;
    }
    void setHp(int hp){
        this.hp = hp;
    }
    double getX() {
        return getView().getTranslateX();
    }
    double getY() {
        return getView().getTranslateY();
    }
    void setSpeedMultiplier(double speed) {
        this.speedMultiplier = speed;}
    double getSpeedMultiplier() {
        return speedMultiplier;
    }
    int getScore() {
        return score;
    }
    void setScore(int score){
        this.score = score;
    }
    boolean getExploded(){
        return exploded;
    }
    void setExploded(boolean b){
        exploded = b;
    }
}