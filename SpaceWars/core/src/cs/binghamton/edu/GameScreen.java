package cs.binghamton.edu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
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

    //world parameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;

    //game objects
    private Ship playerShip;
    private Ship enemyShip;
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
                2,3,
                0.4f, 4, 45, 0.5f,
                playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShip = new EnemyShip(WORLD_WIDTH/2, WORLD_HEIGHT*3/4,
                10,10,
                2,1,
                0.4f, 4, 45, 0.8f,
                enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion
                );

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

        playerShip.update(delta);
        enemyShip.update(delta);

        //scrolling background
        renderBackground(delta);

        //enemy ships
        enemyShip.draw(batch);

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

    private void detectCollision(){
        //for each player fire, if it intersects an enemy ship
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while ( iterator.hasNext()) {
            Laser laser = iterator.next();
            if (enemyShip.intersects(laser.getBoundingBox())){
                //remove laser
                iterator.remove();
                //decrease number of shields
                if (enemyShip.shield > 0){
                    enemyShip.shield --;
                }

            }
        }

        //for enemy player fire, if it intersects a player ship
        iterator = enemyLaserList.listIterator();
        while ( iterator.hasNext()) {
            Laser laser = iterator.next();
            if (playerShip.intersects(laser.getBoundingBox())){
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
        if (enemyShip.canFireLaser()){
            Laser[] lasers = enemyShip.fireLasers();
            for (Laser laser: lasers){
                enemyLaserList.add(laser);
            }
        }
        //draw lasers


        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while ( iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.yPosition += laser.movementSpeed*delta; //delta = time has been passed
            //if lasers pass the boundary of screen according to y axis
            if (laser.yPosition > WORLD_HEIGHT){
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while ( iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.yPosition -= laser.movementSpeed*delta; //delta = time has been passed
            //if lasers pass the boundary of screen according to y axis
            if (laser.yPosition + laser.height <0 ){
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
