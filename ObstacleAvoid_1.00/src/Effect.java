

import static com.nompor.gtk.fx.GTKManagerFX.*;

import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

//全てのゲーム内エフェクトを表すクラス
public class Effect extends GameObject {

	Animation a;
	boolean isStart;
	public Effect(Node viewNode) {
		super(viewNode);
		RotateTransition r = createRotateTransition(Duration.millis(100), viewNode);
		r.setToAngle(360);
		r.setCycleCount(Animation.INDEFINITE);
		a = r;
	}

	public void update() {
		if (a.getStatus() != Status.RUNNING) {
			a.play();
		}
		ImageAnimationView view = (ImageAnimationView) getViewNode();
		if (!isStart && view.getStatus() != Status.RUNNING) {
			view.play();
			isStart = true;
		}
		if ( isStart && view.getStatus() != Status.RUNNING ) {
			isAlive = false;
		}
		view.setVisible(!view.isVisible());
	}
}
