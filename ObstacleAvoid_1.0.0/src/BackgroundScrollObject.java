

import com.nompor.gtk.fx.FixedTargetCamera2DFX;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

//背景のループスクロールを実現するクラス
//指定画像は必ずウィンドウサイズより大きい横幅の画像を取得する必要がある
public class BackgroundScrollObject extends GameObject{

	double scrollRatio;
	double x;
	double y;
	double autoMoveX;
	public BackgroundScrollObject(ImageView viewNode, double scrollRatio) {
		super(viewNode);
		Bounds b = viewNode.getBoundsInParent();
		x = b.getMinX();
		y = b.getMinY();
		this.scrollRatio = scrollRatio;

		//ウィンドウサイズより大きいオブジェクトでなければバグるので例外発生させとく
		if ( b.getWidth() <= AppManager.getW() ) throw new IllegalArgumentException();
	}

	//自動スクロール幅
	public void setAutoMoveX(double autoMoveX) {
		this.autoMoveX = autoMoveX;
	}

	public void update(FixedTargetCamera2DFX camera) {
		//自動スクロール指定
		x-=autoMoveX;

		//スクロールの割合を元にカメラの座標で調整し、遠近感のあるスクロールを実現する
		Node node = getViewNode();
		double cx = camera.getLeft();
		double cr = camera.getRight();
		double cy = camera.getTop();
		double rx = Math.round(x + cx * scrollRatio);
		double ry = Math.round(y + cy * scrollRatio);

		//カメラの領域に入らない場合は座標調整
		//ウィンドウ座標分右に同じ画像を用意しておくことを前提としているため、ウィンドウサイズ分ずらしても違和感のない座標変更が可能である
		while ( rx + AppManager.getW() <= cx ) rx += (int)AppManager.getW();
		while ( rx >= cr ) rx -= (int)AppManager.getW();

		//スクロール後の座標をセット
		node.setTranslateX(rx);
		node.setTranslateY(ry);
	}
}
