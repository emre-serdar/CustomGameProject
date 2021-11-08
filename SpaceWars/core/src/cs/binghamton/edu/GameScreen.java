package cs.binghamton.edu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {


    //screen
    private Camera camera;
    private Viewport viewport;

    //graphics
    private SpriteBatch batch;
    // private Texture background;

    //instead of having a single background, an array of texture will contain multiple layers
    private Texture[] backgrounds;


    //timing
    private float[] backgroundOffsets = {0,0,0,0}; // to move background with offset timer
    private float backgroundMaxScrollingSpeed;

    //world parameters
    private final int WORLD_WIDTH = 72;
    private final int WORLD_HEIGHT = 128;

    //GameScreen constructor
    GameScreen(){
        // defining an Orthographic camera which works for only 2d graphs
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);
        //background = new Texture("spaceBackground3.png") ;
        //backgroundOffset = 0 ;

        //layers of background
        backgrounds = new Texture[4];
        backgrounds[0] = new Texture("spaceBackground00.png");
        backgrounds[1] = new Texture("spaceBackground01.png");
        backgrounds[2] = new Texture("spaceBackground02.png");
        backgrounds[3] = new Texture("spaceBackground03.png");

        //the fastest screen layer will roll the page in 4 seconds
        backgroundMaxScrollingSpeed = (float) WORLD_HEIGHT/4;

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


        batch.end();
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
