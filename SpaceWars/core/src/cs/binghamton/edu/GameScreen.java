package cs.binghamton.edu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GameScreen implements Screen {


    //screen
    private Camera camera;
    private Viewport viewport;


    //graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;


    //instead of having a single background, an array of texture will contain multiple layers
    private TextureRegion[] backgrounds;
    private float backgroundHeight; //height of background in World Units

    private TextureRegion playerShipTextureRegion,playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;

    //timing
    private float[] backgroundOffsets = {0,0,0,0}; // to move background with offset timer
    private float backgroundMaxScrollingSpeed;
    private float timeForEnemySpawn = 3f; //3 seconds
    private float enemySpawnTimer = 0f;

    //world parameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD =0.5f;

    //game objects
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipLists;
    private LinkedList<Laser> playerLaserList; // for lasers
    private LinkedList<Laser> enemyLaserList;

    //GameScreen constructor
    GameScreen(){
        // defining an Orthographic camera which works for only 2d graphs
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        //set up texture atlas
        textureAtlas = new TextureAtlas("images.atlas");


        //layers of background
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("spaceBackground00");
        backgrounds[1] = textureAtlas.findRegion("spaceBackground01");
        backgrounds[2] = textureAtlas.findRegion("spaceBackground02");
        backgrounds[3] = textureAtlas.findRegion("spaceBackground03");

        //the fastest screen layer will roll the page in 4 seconds
        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = (float) WORLD_HEIGHT/4;

        //initialize texture regions
        playerShipTextureRegion = textureAtlas.findRegion("playerShip1_green");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyRed5");
        playerShieldTextureRegion = textureAtlas.findRegion("shield3");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        //to flip shield
        enemyShieldTextureRegion.flip(false, true);
        playerLaserTextureRegion = textureAtlas.findRegion("laserGreen05");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed03");

        //set up game objects
        playerShip = new PlayerShip(WORLD_WIDTH/2, WORLD_HEIGHT/4,
                10,10,
                48,3,
                0.4f, 4, 40, 0.5f,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipLists = new LinkedList<>();



        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        batch = new SpriteBatch();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.begin();

        //scrolling background
        renderBackground(delta);

        //getting keyboard and touch input
        getInput(delta);
        playerShip.update(delta);
        if(enemyShipLists.size()<3){
            spawnEnemyShips(delta);
        }


        //enemy movement;
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipLists.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip,delta);
            enemyShip.update(delta);
            enemyShip.draw(batch);
        }


        //enemy ships


        //player ship

        playerShip.draw(batch);


        //create new lasers
        renderLasers(delta);

        //detect collisions between lasers and ships
        detectCollision();

        //explosions
        renderExplosions(delta);

        batch.end();

    }

    private void spawnEnemyShips(float delta){
        enemySpawnTimer+=delta;

        if (enemySpawnTimer > timeForEnemySpawn){
            enemyShipLists.add(new EnemyShip(WORLD_WIDTH/2, WORLD_HEIGHT*3/4,
                10,10,
                30,1,
                0.4f, 4, 40, 0.8f,
                enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));

            enemySpawnTimer -= timeForEnemySpawn;
        }


    }

    private void moveEnemy(EnemyShip enemyShip,float delta){
        //strategy: determine the max distance of enemy ships can move

        //top half of screen
        float leftBoundary, rightBoundary, upBoundary, downBoundary;
        leftBoundary = -enemyShip.boundingBox.x;
        downBoundary = (float)WORLD_HEIGHT/2-enemyShip.boundingBox.y; //half bottom
        rightBoundary = WORLD_WIDTH - enemyShip.boundingBox.x-enemyShip.boundingBox.width;
        upBoundary = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;


        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * delta;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * delta;


        //to avoid ship to exceed boundaries of screen
        if (xMove > 0) xMove = Math.min(xMove, rightBoundary);
        else xMove = Math.max(xMove, leftBoundary);

        if (yMove > 0) yMove = Math.min(yMove, upBoundary);
        else yMove = Math.max(yMove,downBoundary);

        //move the ship
        enemyShip.translate(xMove,yMove);
    }
    private void getInput(float delta){
        //keyboard input

        //strategy: determine the max distance the ship can move
        //check each key that matters and move accordingly

        float leftBoundary, rightBoundary, upBoundary, downBoundary;
        leftBoundary = -playerShip.boundingBox.x;
        downBoundary = -playerShip.boundingBox.y;
        rightBoundary = WORLD_WIDTH - playerShip.boundingBox.x-playerShip.boundingBox.width;
        upBoundary = (float) WORLD_HEIGHT/2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightBoundary > 0) {
            /*
            float xChange = playerShip.movementSpeed*delta;
            xChange = Math.min(xChange, rightBoundary); //to avoid object to disappear on screen
            playerShip.translate(xChange, 0f);
            */

            //same code with 1 line
            playerShip.translate(Math.min(playerShip.movementSpeed*delta, rightBoundary), 0f);

        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upBoundary > 0) {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed*delta, upBoundary));

        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftBoundary < 0) { //since left limit will be a negetavie number

            playerShip.translate(Math.max(-playerShip.movementSpeed*delta, leftBoundary), 0f);

        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downBoundary < 0) { //since left limit will be a negative number

            playerShip.translate(0f,Math.max(-playerShip.movementSpeed*delta, downBoundary));

        }

        //touch input or mouse click input
        if (Gdx.input.isTouched()) {
            //get the touching position
            float xTouchLocation = Gdx.input.getX();
            float yTouchLocation = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchLocation,yTouchLocation);
            touchPoint = viewport.unproject(touchPoint);

            //calculate x and y differences between the ship and user touch point
            Vector2 playerShipCenter = new Vector2(
                    playerShip.boundingBox.x+playerShip.boundingBox.width/2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCenter);
            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD){
                float xTouchDifference = touchPoint.x - playerShipCenter.x;
                float yTouchDifference = touchPoint.y - playerShipCenter.y;

                /*
                    distance = speed * time
                    distance = playerShip.movementSpeed * deltaTime
                */

                //scaling the maximum speed of ship
                float xMove = xTouchDifference/touchDistance * playerShip.movementSpeed * delta;
                float yMove = yTouchDifference/touchDistance * playerShip.movementSpeed * delta;

                //to avoid ship to exceed boundaries of screen
                if (xMove > 0) xMove = Math.min(xMove, rightBoundary);
                else xMove = Math.max(xMove, leftBoundary);

                if (yMove > 0) yMove = Math.min(yMove, upBoundary);
                else yMove = Math.max(yMove,downBoundary);
                //move the ship
                playerShip.translate(xMove,yMove);

            }

        }



    }

    private void detectCollision(){
        //for each player fire, if it intersects an enemy ship
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while ( iterator.hasNext()) {
            Laser laser = iterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipLists.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    //remove laser
                    iterator.remove();
                    //decrease number of shields
                    if (enemyShip.shield > 0) {
                        enemyShip.shield--;
                    }
                    break;

                }
            }

        }

        //for enemy player fire, if it intersects a player ship
        iterator = enemyLaserList.listIterator();
        while ( iterator.hasNext()) {
            Laser laser = iterator.next();
            if (playerShip.intersects(laser.boundingBox)){
                //remove laser
                iterator.remove();
                //decrease number of shields
                if (playerShip.shield>0){
                    playerShip.shield --;
                }
            }
        }


    }

    private void renderExplosions(float delta){

    }

    private void renderBackground(float delta) {

        //moving backgrounds with different speeds
        backgroundOffsets[0] += delta * backgroundMaxScrollingSpeed /8 ;
        backgroundOffsets[1] += delta * backgroundMaxScrollingSpeed /4 ;
        backgroundOffsets[2] += delta * backgroundMaxScrollingSpeed /2 ;
        backgroundOffsets[3] += delta * backgroundMaxScrollingSpeed;

        //to avoid printing stuff which cannot be seen from the user
        for (int layer=0; layer<backgroundOffsets.length; layer++){
            if(backgroundOffsets[layer] > WORLD_HEIGHT){
                backgroundOffsets[layer] = 0;
            }
            //drawing layer
            batch.draw(backgrounds[layer],0,
                    -backgroundOffsets[layer],
                    WORLD_WIDTH,WORLD_HEIGHT);
            batch.draw(backgrounds[layer],0,
                    -backgroundOffsets[layer] + WORLD_HEIGHT,
                    WORLD_WIDTH,WORLD_HEIGHT);

        }

    }
    private void renderLasers(float delta){
        //playership lasers
        if (playerShip.canFireLaser()){
            Laser[] lasers = playerShip.fireLasers();
            for (Laser laser: lasers){
                playerLaserList.add(laser);
            }
        }

        //enemyship lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipLists.listIterator();
        while(enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();

            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                for (Laser laser : lasers) {
                    enemyLaserList.add(laser);
                }
            }
        }

        //draw lasers

        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while ( iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed*delta; //delta = time has been passed
            //if lasers pass the boundary of screen according to y axis
            if (laser.boundingBox.y > WORLD_HEIGHT){
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while ( iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed*delta; //delta = time has been passed
            //if lasers pass the boundary of screen according to y axis
            if (laser.boundingBox.y + laser.boundingBox.height  <0 ){
                iterator.remove();
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        //to resize screen
        viewport.update(width,height,true);
        batch.setProjectionMatrix(camera.combined);
    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
