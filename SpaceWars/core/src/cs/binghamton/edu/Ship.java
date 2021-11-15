package cs.binghamton.edu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Ship {

    //ship features
    float movementSpeed; //world units per sec
    int shield;

    //position
    float xPosition, yPosition; //lower left corner
    float width, height;
    Rectangle boundingBox;

    //laser features
    float laserWidth, laserHeight;
    float laserMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot = 0; //start with zero

    //graphics
    TextureRegion shipTextureRegion, shieldTextureRegion, laserTextureRegion;


    public Ship(float xCenter, float yCenter,
                float width, float height,
                float movementSpeed, int shield,
                float laserWidth, float laserHeight,
                float laserMovementSpeed, float timeBetweenShots,
                TextureRegion shipTextureRegion,
                TextureRegion shieldTextureRegion,
                TextureRegion laserTextureRegion) {
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.xPosition = xCenter - width/2;
        this.yPosition = yCenter - height/2;
        this.width = width;
        this.height = height;
        this.laserWidth = laserWidth;
        this.laserHeight = laserHeight;
        this.laserMovementSpeed = laserMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
        this.boundingBox = new Rectangle(xPosition,yPosition,width,height);
    }

    // to update laser information of a ship
    public void update(float delta){
        //location of laser fire
        boundingBox.set(xPosition,yPosition,width,height);

        timeSinceLastShot += delta;

    }

    public abstract Laser[] fireLasers();

    //to find out if any laser intersects with fire lasers
    public boolean intersects(Rectangle rectangle1){
        return boundingBox.overlaps(rectangle1);
    }

    //to avoid ships to fire lasers infinitely at the same time
    public boolean canFireLaser(){
        return (timeSinceLastShot-timeBetweenShots >= 0);
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
