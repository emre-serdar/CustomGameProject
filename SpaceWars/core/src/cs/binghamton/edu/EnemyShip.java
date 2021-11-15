package cs.binghamton.edu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class EnemyShip extends Ship {

    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFrequency = 0.75f;


    public EnemyShip(float xCenter, float yCenter,
                      float width, float height,
                      float movementSpeed, int shield,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xCenter, yCenter, width, height, movementSpeed, shield, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);

        directionVector = new Vector2(0,-1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    //to move enemy players randomly
    private void randomDirectionVector(){
        double bearing = SpaceWars.random.nextDouble()*6.283; //using my own variable defined on SpaceWars.java file | 0 to 2*PI
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timeSinceLastDirectionChange += delta;
        if (timeSinceLastDirectionChange > directionChangeFrequency) {
            randomDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] laser = new Laser[1];
        //creating the laser
        laser[0] = new Laser(boundingBox.x+4.95f,boundingBox.y,laserWidth+0.5f,laserHeight,laserMovementSpeed,laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }

    @Override
    public void draw(Batch batch) {
        //to draw ship
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);

        //to draw shield
        if (shield > 0) { // if ship has at least 1 shield
            batch.draw(shieldTextureRegion,boundingBox.x,boundingBox.y-0.8f,boundingBox.width,boundingBox.height);
        }
    }
}
