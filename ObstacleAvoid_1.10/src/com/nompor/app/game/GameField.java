package com.nompor.app.game;

import javafx.geometry.Bounds;
import javafx.scene.Node;

//ゲームのステージ表すクラス
//足場として利用されるBlockオブジェクトの集合体で構成される
public class GameField {
	Field[][] fields;
	int minX;
	int maxX;
	int minY;
	int maxY;

	//ブロックに衝突していた場合に隣接ブロックが上下左右にあるかどうかを保持する
	AdjoinBlockInfo info = new AdjoinBlockInfo();
	class AdjoinBlockInfo{
		boolean up;
		boolean bottom;
		boolean right;
		boolean left;
	}

	public GameField(Field[][] fields) {
		this.fields = fields;
		minX = 0;
		minY = 0;
		maxX = fields[0].length * Block.W;
		maxY = fields.length * Block.H;
	}

	//指定インデックスの列挙型を取得します
	private FieldObject getData(int yIdx, int xIdx) {
		return yIdx < 0 || yIdx >= fields.length || xIdx < 0 || xIdx >= fields[0].length || fields[yIdx][xIdx] == null || fields[yIdx][xIdx].isDelete ? FieldObject.NONE : fields[yIdx][xIdx].getFieldObject();
	}

	//指定インデックスのFieldオブジェクトを取得します
	public Field getField(int yIdx, int xIdx) {
		return yIdx < 0 || yIdx >= fields.length || xIdx < 0 || xIdx >= fields[0].length ? null : fields[yIdx][xIdx];
	}

	//ブロックリストを取得します
	public Field[][] getFieldList() {
		return fields;
	}

	//キャラクタのフィールド位置チェックを行い補正する
	public void fieldCheck(CharaObject chara) {
		//キャラクタがフィールドのブロックオブジェクトと当たっているか判定し、当たっていたら押し戻す処理
		Node node = chara.getHitNode();
		Bounds b = node.getBoundsInParent();

		//判定対象となるブロックインデックスを算出(開始地点から終了地点)
		int sx = (int)b.getMinX() / Block.W;
		int sy = (int)b.getMinY() / Block.H;
		int ex = (int)b.getMaxX() / Block.W;
		int ey = (int)b.getMaxY() / Block.H;

		//インデックスが配列を越えていた場合は端点に修正
		if ( sx < 0 ) sx = 0;
		if ( sy < 0 ) sy = 0;
		if ( ex < 0 ) ex = 0;
		if ( ey < 0 ) ey = 0;
		if ( sx >= fields[0].length ) sx = fields[0].length - 1;
		if ( sy >= fields.length ) sy = fields.length - 1;
		if ( ex >= fields[0].length ) ex = fields[0].length - 1;
		if ( ey >= fields.length ) ey = fields.length - 1;

		//地上、空中、壁判定フラグを無衝突状態に初期化
		chara.isAir = true;
		chara.isGround = false;
		chara.isWall = false;

		//画面端である場合は壁に当たったものとする
		if ( b.getMaxX() > maxX ) {
			chara.moveX(maxX - b.getMaxX());
			chara.isWall = true;
		}
		if ( b.getMinX() < minX ) {
			chara.moveX(minX - b.getMinX());
			chara.isWall = true;
		}

		for ( int i = sy;i <= ey;i++ ) {
			for ( int j = sx;j <= ex;j++ ) {
				Field block = fields[i][j];
				if ( block != null && block.isHit(chara) ) {
					//隣接ブロックの有無
					info.up = false;
					info.right = false;
					info.bottom = false;
					info.left = false;
					if( getData(i-1,j).TYPE == ObjectType.BLOCK ) info.up = true;
					if( getData(i,j+1).TYPE == ObjectType.BLOCK ) info.right = true;
					if( getData(i+1,j).TYPE == ObjectType.BLOCK ) info.bottom = true;
					if( getData(i,j-1).TYPE == ObjectType.BLOCK ) info.left = true;

					//ブロックにめり込んだらめり込んだ分戻す
					block.sinkingRevise(chara, info);
				}
			}
		}
	}
}
