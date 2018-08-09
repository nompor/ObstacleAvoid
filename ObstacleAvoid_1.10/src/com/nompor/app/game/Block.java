package com.nompor.app.game;

import com.nompor.app.game.GameField.AdjoinBlockInfo;

import javafx.geometry.Bounds;
import javafx.scene.Node;

//当たり判定のあるブロック型オブジェクトを表すクラス
public class Block extends Field {

	public Block(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}

	//ブロックにめり込んだキャラクタを押し出すメソッド
	@Override
	public void sinkingRevise(CharaObject mv, AdjoinBlockInfo info) {

		//当たり判定オブジェクトの取得
		Node node = getHitNode();
		Node mvNode = mv.getHitNode();
		Bounds b = node.getBoundsInParent();
		Bounds mvb = mvNode.getBoundsInParent();

		if ( mv.preb <= b.getMinY() && !info.up ) {
			//キャラがブロックの上側から突っ込んだ場合めり込んだ分キャラを上にずらす
			//ただし、その上に別のブロックがある場合は押し上げない
			mv.moveY(-(mvb.getMaxY() - b.getMinY()));
			mv.isGround = true;
		} else if ( mv.pret >= b.getMaxY() && !info.bottom ) {
			//キャラがブロックの下側から突っ込んだ場合めり込んだ分キャラを下にずらす
			//ただし、その下に別のブロックがある場合は押し下げない
			mv.moveY(b.getMaxY() - mvb.getMinY());
		} else if ( mv.prer <= b.getMinX() ) {
			//キャラがブロックの左側から突っ込んだ場合めり込んだ分キャラを左にずらす
			mv.moveX(-(mvb.getMaxX() - b.getMinX()));
			mv.isWall = true;
		} else if ( mv.prel >= b.getMaxX() ) {
			//キャラがブロックの右側から突っ込んだ場合めり込んだ分キャラを右にずらす
			mv.moveX(b.getMaxX() - mvb.getMinX());
			mv.isWall = true;
		}

		//地上ではない場合は空中
		mv.isAir=!mv.isGround;
	}
}
