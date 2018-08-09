package com.nompor.app.game.enemy;

import com.nompor.app.game.Enemy;
import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.scene.Node;

//敵オブジェクト、棘が生えたジャンピング植物を表すクラス
public class NeedleLeaf extends Enemy{

	double fallSpeed;
	int groundCounter = 0;

	public NeedleLeaf(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}

	@Override
	public void move() {

		if ( isGround ) {
			groundCounter=++groundCounter%30;

			//地上だったら落下速度0
			fallSpeed = 0;

			//壁に当たったらジャンプ
			if ( groundCounter == 0 ) {
				//ジャンプしたなら落下速度を逆にする
				fallSpeed = -20;
			}

			//表示画像切り替え
			ImageAnimationView img = (ImageAnimationView) getViewNode();
			img.setIndex(0);
		} else if ( isAir ) {
			//空中だったら落下速度アップ
			fallSpeed+=0.5;

			//表示画像切り替え
			ImageAnimationView img = (ImageAnimationView) getViewNode();
			img.setIndex(1);
		}

		//上下移動処理
		moveY(fallSpeed);
	}

	@Override
	public void mouseAction() {
		//クリックされたら倒される
		isAlive = false;
	}
}
