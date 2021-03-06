package cs.binghamton.edu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerShip extends Ship {

    int lives;

    public PlayerShip(float xCenter, float yCenter,
                      float width, float height,
                      float movementSpeed, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCenter, yCenter, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
        lives=3;
    }



    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[1];
        //creating the laser
        laser[0] = new Laser(boundingBox.x+4.95f,boundingBox.y+ boundingBox.height*0.75f,laserWidth,laserHeight,laserMovementSpeed,laserTextureRegion);


        timeSinceLastShot = 0;

        return laser;
    }
}
