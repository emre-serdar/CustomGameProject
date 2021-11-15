package cs.binghamton.edu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Laser {

    //position
    Rectangle boundingBox;

    //physical features
    float movementSpeed; //world units per sec

    //graphics
    TextureRegion textureRegion;

    public Laser(float xPosition, float yPosition, float width, float height, float movementSpeed, TextureRegion textureRegion) {
        this.boundingBox = new Rectangle(xPosition - width/2,yPosition - height/2,width,height);
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch){
        batch.draw(textureRegion, boundingBox.x,boundingBox.y,boundingBox.width,boundingBox.height);

    }

    public Rectangle getBoundingBox(){
        return boundingBox;

    }
}
