package cs.binghamton.edu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Laser {

    //position
    float xPosition, yPosition; //bottom center of the laser
    float width, height;

    //physical features
    float movementSpeed; //world units per sec

    //graphics
    TextureRegion textureRegion;

    public Laser(float xPosition, float yPosition, float width, float height, float movementSpeed, TextureRegion textureRegion) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch){
        batch.draw(textureRegion,xPosition,yPosition,width,height);

    }

    public Rectangle getBoundingBox(){
        return new Rectangle(xPosition,yPosition,width,height);

    }
}
