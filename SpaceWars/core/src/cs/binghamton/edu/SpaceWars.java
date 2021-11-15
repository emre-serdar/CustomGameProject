package cs.binghamton.edu;

import com.badlogic.gdx.Game;

import java.util.Random;


public class SpaceWars extends Game {



	//creating game screen object
	GameScreen gameScreen;

	public static Random random = new Random();


	@Override
	public void create() {
		gameScreen = new GameScreen();

		//When the app begins, setScreen will make the game screen to display it
		setScreen(gameScreen);
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}
}
