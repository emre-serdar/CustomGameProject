package cs.binghamton.edu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Ship {

    //ship features
    float movementSpeed; //world units per sec
    int shield;

    //position
    float xPosition, yPosition; //lower left corner
    float width, height;

    //graphics
    TextureRegion shipTexture, shieldTexture;

    public Ship(float movementSpeed, int shield,  float width, float height, float xCenter, float yCenter, TextureRegion shipTexture, TextureRegion shieldTexture) {
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.xPosition = xCenter - width/2;
        this.yPosition = yCenter - height/2;
        this.width = width;
        this.height = height;
        this.shipTexture = shipTexture;
        this.shieldTexture = shieldTexture;
    }

    public void draw(Batch batch) {
        //to draw ship
        batch.draw(shipTexture, xPosition, yPosition, width, height);

        //to draw shield
        if (shield > 0) { // if ship has at least 1 shield
            batch.draw(shieldTexture,xPosition,yPosition,width,height);
        }
    }
}
