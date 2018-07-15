

import com.nompor.gtk.fx.GameViewFX;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameMode extends GameViewFX {
	
	int stage[][] = new int[][] {
		
	};
	
	public GameMode() {
		Rectangle  rect = new Rectangle(0,0,500,500);
		rect.setFill(Color.RED);
		getChildren().add(rect);
	}
}
