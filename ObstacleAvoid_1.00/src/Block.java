

import javafx.geometry.Bounds;
import javafx.scene.Node;

//当たり判定のあるブロック型オブジェクトを表すクラス
public class Block extends Field {

	public Block(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}

	//ブロックにめり込んだキャラクタを押し出すメソッド
	@Override
	public void sinkingRevise(CharaObject mv) {

		//当たり判定オブジェクトの取得
		Node node = getHitNode();
		Node mvNode = mv.getHitNode();
		Bounds b = node.getBoundsInParent();
		Bounds mvb = mvNode.getBoundsInParent();

		if ( mv.preb <= b.getMinY() ) {
			//キャラがブロックの上側から突っ込んだ場合めり込んだ分キャラを上にずらす
			mv.moveY(-(mvb.getMaxY() - b.getMinY()));
			mv.isGround = true;
		} else if ( mv.pret >= b.getMaxY() ) {
			//キャラがブロックの下側から突っ込んだ場合めり込んだ分キャラを下にずらす
			mv.moveY(b.getMaxY() - mvb.getMinY());
		} else if ( mv.prer <= b.getMinX() ) {
			//キャラがブロックの左側から突っ込んだ場合めり込んだ分キャラを左にずらす
			mv.moveX(-(mvb.getMaxX() - b.getMinX()));
		}

		//地上ではない場合は空中フラグオン
		mv.isAir=!mv.isGround;
	}
}
