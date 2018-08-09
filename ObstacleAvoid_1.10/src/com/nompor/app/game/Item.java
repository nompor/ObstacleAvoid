package com.nompor.app.game;

import javafx.scene.Node;

//アイテムを表すクラス
public class Item extends GameObject{

	boolean isClick;

	public Item(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}

	//マウスクリックしたときの特定のアクション
	@Override
	public void mouseAction() {
		isAlive = false;
		isClick = true;
	}

}
