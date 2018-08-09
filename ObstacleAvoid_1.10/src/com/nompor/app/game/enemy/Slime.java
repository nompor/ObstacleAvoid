package com.nompor.app.game.enemy;

import com.nompor.app.game.Enemy;
import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.animation.Animation.Status;
import javafx.scene.Node;

//敵オブジェクト、青色スライム
public class Slime extends Enemy{

	int fallSpeed;
	int speed=-1;

	public Slime(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);

		//アニメーションの開始
		ImageAnimationView img = (ImageAnimationView) getViewNode();
		img.setDefaultAnimationRange();
		img.setScaleX(speed > 0 ? -1 : 1);
		if ( img.getStatus() != Status.RUNNING ) {
			img.play();
		}
	}

	@Override
	public void move() {
		if ( isGround ) {
			//地上だったら落下速度0
			fallSpeed = 0;

			//壁に当たったらジャンプ
			if ( isWall ) {
				//ジャンプしたなら落下速度を逆にする
				fallSpeed = -12;
			}
		} else if ( isAir ) {
			//空中だったら落下速度アップ
			fallSpeed++;
		}

		//上下移動処理
		moveY(fallSpeed);

		//左右移動
		moveX(speed);
	}

	@Override
	public void mouseAction() {
		//クリックされたら倒される
		isAlive = false;
	}
}
