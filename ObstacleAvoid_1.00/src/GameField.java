

import javafx.geometry.Bounds;
import javafx.scene.Node;

//ゲームのステージ表すクラス
//足場として利用されるBlockオブジェクトと装飾用のFieldオブジェクトの集合体で構成される
public class GameField {
	Field[][] fields;
	int minX;
	int maxX;
	int minY;
	int maxY;

	public GameField(Field[][] fields) {
		this.fields = fields;
		minX = 0;
		minY = 0;
		maxX = fields[0].length * Block.W;
		maxY = fields.length * Block.H;
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

		//地上、空中フラグを無衝突状態に初期化
		chara.isAir = true;
		chara.isGround = false;

		//画面端である場合は壁に当たったものとする
		if ( b.getMaxX() > maxX ) {
			chara.moveX(maxX - b.getMaxX());
		}
		if ( b.getMinX() < minX ) {
			chara.moveX(minX - b.getMinX());
		}

		for ( int i = sy;i <= ey;i++ ) {
			for ( int j = sx;j <= ex;j++ ) {
				Field block = fields[i][j];
				if ( block != null && block.isHit(chara) ) {
					//ブロックにめり込んだらめり込んだ分戻す
					block.sinkingRevise(chara);
				}
			}
		}
	}
}
