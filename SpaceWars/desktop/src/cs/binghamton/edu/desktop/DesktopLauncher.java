package cs.binghamton.edu.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import cs.binghamton.edu.SpaceWars;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 640; //changing the application size to make it similar as phone screens
		config.width = 360;
		new LwjglApplication(new SpaceWars(), config);
	}
}
