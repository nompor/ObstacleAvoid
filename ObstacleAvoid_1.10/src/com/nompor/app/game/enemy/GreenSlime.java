package com.nompor.app.game.enemy;

import com.nompor.app.game.Enemy;
import com.nompor.app.manager.PlayerControllerManager;
import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.animation.Animation.Status;
import javafx.scene.Node;

//敵オブジェクト、緑色スライム
public class GreenSlime extends Enemy{

	int fallSpeed;
	int speed=-2;

	public GreenSlime(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);

		//アニメーションの開始
		ImageAnimationView img = (ImageAnimationView) getViewNode();
		img.setDefaultAnimationRange();
		if ( img.getStatus() != Status.RUNNING ) {
			img.play();
		}
	}

	@Override
	public void move() {
		if ( isGround ) {
			//地上だったら落下速度0
			fallSpeed = 0;

			//ジャンプボタンを押されたらジャンプ
			if ( PlayerControllerManager.isJump() ) {
				//ジャンプしたなら落下速度を逆にする
				fallSpeed = -13;
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
