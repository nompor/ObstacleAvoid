package com.nompor.app.game.enemy;

import com.nompor.app.game.Enemy;
import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.animation.Animation.Status;
import javafx.scene.Node;

//敵オブジェクト、鳥を表すクラス
public class Bird extends Enemy{


	int speed = -1;
	public Bird(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);

		//アニメーションの開始
		ImageAnimationView img = (ImageAnimationView) getViewNode();
		img.setDefaultAnimationRange();
		img.setScaleX(-1);
		if ( img.getStatus() != Status.RUNNING ) {
			img.play();
		}
	}

	@Override
	public void move() {
		//壁に当たったら下に移動
		if ( isWall ) {
			moveY(1);
		} else {
			moveX(speed);
		}
	}

	@Override
	public void mouseAction() {
		//クリックされたら倒される
		isAlive = false;
	}
}
