//150120023 YUSUF DUMAN 150120074 �LKER KEKL�K   

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StarterTile extends ImageView implements Tile {

private boolean up;
private boolean left;
private boolean right;
private boolean down;


public StarterTile(Image tileImage,boolean up,boolean down,boolean right,boolean left){
    super(tileImage);
    setUp(up);
    setDown(down);
    setRight(right);
    setLeft(left);

}

    @Override
    public boolean getUp() {
        return up;
    }

    @Override
    public boolean getDown() {
        return down;
    }

    @Override
    public boolean getRight() {
        return right;
    }

    @Override
    public boolean getLeft() {
        return left;
    }

    @Override
    public void setUp(boolean up) {
        this.up = up;
    }

    @Override
    public void setDown(boolean down) {
        this.down=down;
    }

    @Override
    public void setRight(boolean right) {
        this.right=right;
    }

    @Override
    public void setLeft(boolean left) {
        this.left=left;
    }


}
