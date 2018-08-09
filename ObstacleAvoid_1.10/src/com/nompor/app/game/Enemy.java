package com.nompor.app.game;

import javafx.scene.Node;

//全ての敵オブジェクトが継承すべきスーパークラス
public abstract class Enemy extends CharaObject {

	public Enemy(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}


	//ダメージを受けたら生存フラグOFF
	public void onDamage() {
		isAlive = false;
	}
}
