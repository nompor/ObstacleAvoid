import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.animation.Animation;
import javafx.scene.Node;

//プレイヤーキャラを表すクラス
public class Player extends CharaObject {

	double fallSpeed = 0;
	int speed = 3;
	boolean isHighJump = false;

	public Player(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
		((ImageAnimationView)viewNode).setAnimationRange(1, 4);
	}

	@Override
	public void move() {
		boolean isRight = PlayerControllerManager.isRight();

		if ( isGround ) {
			//地上だったら落下速度0
			fallSpeed = 0;

			if ( isHighJump || PlayerControllerManager.isJump()) {
				//ジャンプしたなら落下速度を逆にする
				fallSpeed = isHighJump ? -20 : -15;
				AppManager.playSE("se4");
				isHighJump = false;
			}
		} else if ( isAir ) {
			if ( fallSpeed < 15 ) {
				//空中だったら落下速度アップ
				fallSpeed++;
			}
		}

		//移動処理と画像反転処理
		ImageAnimationView img = (ImageAnimationView) getViewNode();
		if ( isRight ) {
			//右へ移動
			moveX(speed);
			img.setScaleX(1);
		}

		//画像の切り替え処理
		if ( isAir ) {
			if ( img.getStatus() == Animation.Status.RUNNING ) {
				img.stop();
			}
			img.setIndex(img.getMaxIndex());
		} else if ( isRight ) {
			if ( img.getStatus() != Animation.Status.RUNNING ) {
				img.setIndex(1);
				img.play();
			}
		} else {
			if ( img.getStatus() == Animation.Status.RUNNING ) {
				img.stop();
			}
			img.setIndex(0);
		}

		//上下移動処理
		moveY(fallSpeed);
	}

	public void onDamage() {
		invisibleCount = 120;
		if ( speed > 5 ) {
			speed-=3;
		} else if ( speed > 4 ) {
			speed-=2;
		} else if ( speed > 1 ) {
			speed--;
		}
	}

	@Override
	public void mouseAction() {
		//クリックされたらハイジャンプ
		if ( isGround ) {
			isHighJump = true;
		}
	}
}
