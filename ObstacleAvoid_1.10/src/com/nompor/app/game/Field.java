package com.nompor.app.game;

import com.nompor.app.game.GameField.AdjoinBlockInfo;

import javafx.scene.Node;

//フィール内の装飾オブジェクトを表すクラス
public class Field extends GameObject {

	//フィールドオブジェクトのサイズ
	public static int W = FieldObject.W;
	public static int H = FieldObject.H;

	boolean isDelete;

	public Field(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}
	public Field(Node viewNode) {
		this(viewNode, null);
	}

	//キャラクタを押し出すオーバーライド用メソッド
	public void sinkingRevise(CharaObject mv, AdjoinBlockInfo info) {}

	//クリックされたら削除する
	@Override
	public void mouseAction() {
		isAlive = false;

		isDelete = true;

		//当たり判定を削除
		hitNode = null;
	}
}
