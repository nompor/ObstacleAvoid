package com.nompor.app.game;

import javafx.geometry.Bounds;
import javafx.scene.Node;

//全てのキャラクターが継承すべきスーパークラス
public abstract class CharaObject extends GameObject {

	//キャラの無敵時間
	int invisibleCount;

	double prel;
	double pret;
	double prer;
	double preb;

	//空中にいるかどうか
	protected boolean isAir;

	//地上にいるかどうか
	protected boolean isGround;

	//壁に当たったかどうか
	protected boolean isWall;

	public CharaObject(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
		positionInit();
	}

	@Override
	public void update() {
		if ( isInvisible() ) {
			invisibleCount--;
			getViewNode().setOpacity(0.5);
		} else {
			getViewNode().setOpacity(1);
		}

		//移動前の座標を保持しておく
		positionInit();
		move();
	}

	//現在の座標を記録しておく
	protected void positionInit() {
		Bounds b = getHitNode().getBoundsInParent();
		prel = b.getMinX();
		pret = b.getMinY();
		prer = b.getMaxX();
		preb = b.getMaxY();
	}

	//無敵状態かを返す
	public boolean isInvisible() {
		return invisibleCount > 0;
	}

	//キャラがダメージを負った時に呼び出すメソッド
	public void onDamage() {}

	//移動処理を行う抽象メソッド
	public abstract void move();
}
