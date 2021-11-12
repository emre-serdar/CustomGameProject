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

    //laser features
    float laserWidth, laserHeight;
    float laserMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot = 0; //start with zero

    //graphics
    TextureRegion shipTextureRegion, shieldTextureRegion, laserTextureRegion;


    public Ship(float movementSpeed, int shield,
                float width, float height, float xCenter,
                float yCenter, float laserWidth, float laserHeight,
                float laserMovementSpeed,
                TextureRegion shipTextureRegion,
                TextureRegion shieldTextureRegion,
                TextureRegion laserTextureRegion) {
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.xPosition = xCenter - width/2;
        this.yPosition = yCenter - height/2;
        this.width = width;
        this.height = height;
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
    }

    public void draw(Batch batch) {
        //to draw ship
        batch.draw(shipTextureRegion, xPosition, yPosition, width, height);

        //to draw shield
        if (shield > 0) { // if ship has at least 1 shield
            batch.draw(shieldTextureRegion,xPosition,yPosition,width,height);
        }
    }
}
