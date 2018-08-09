package com.nompor.app.game;

import com.nompor.gtk.fx.CameraTargetNode2D;

import javafx.scene.Node;

//全てのゲーム内オブジェクトが継承すべきスーパークラス
public class GameObject extends CameraTargetNode2D {

	//生存状態フラグ
	protected boolean isAlive;

	//当たり判定用Node
	Node hitNode;

	//このオブジェクトの列挙型
	FieldObject type;

	//マウスアクションを実行するか
	boolean isMouseAction;

	public GameObject(Node viewNode, Node hitNode) {
		super(viewNode);
		this.hitNode = hitNode;
	}
	public GameObject(Node viewNode) {
		this(viewNode, null);
	}

	//引数のオブジェクトと衝突しているかどうか
	public boolean isHit(GameObject object) {
		Node node = object.getHitNode();
		return node != null && hitNode != null && object.getHitNode().getBoundsInParent().intersects(hitNode.getBoundsInParent());
	}

	//引数のオブジェクトと衝突しているかどうか
	public boolean isHit(Node node) {
		return node != null && hitNode != null && node.getBoundsInParent().intersects(hitNode.getBoundsInParent());
	}

	//このオブジェクトの生存状態
	public boolean isAlive() {
		return isAlive;
	}

	//Window内にオブジェクトが存在しているかどうか
	/*
	public boolean isObjectInWindow() {
		FixedTargetCamera2DFX camera = (FixedTargetCamera2DFX)AppManager.getCamera();
		double l = camera.getLeft() - 200;
		double t = camera.getTop() - 200;
		double r = camera.getRight() + 200;
		double b = camera.getBottom() + 200;
		Bounds bounds = getNode().getBoundsInParent();
		return bounds.getMaxX() < r
		&& bounds.getMinY() > t
		&& bounds.getMinX() > l
		&& bounds.getMaxY() < b;
	}
*/
	//画面表示用Node
	public Node getViewNode() {
		//スーパークラスのgetNode呼び出し
		return super.getNode();
	}

	//当たり判定Node
	public Node getHitNode() {
		return hitNode;
	}

	//毎フレーム呼び出されるので、サブクラスで個々の動きを記述する
	public void update() {}

	//横移動用メソッド
	public void moveX(double x) {
		getNode().setTranslateX(getNode().getTranslateX()+x);
		hitNode.setTranslateX(hitNode.getTranslateX()+x);
	}

	//縦移動用メソッド
	public void moveY(double y) {
		getNode().setTranslateY(getNode().getTranslateY()+y);
		hitNode.setTranslateY(hitNode.getTranslateY()+y);
	}

	//マウスアクション
	public final void onMouseAction(){
		if ( isMouseAction ) {
			mouseAction();
		}
	}

	//マウスクリックしたときの特定のアクション
	public void mouseAction() {}

	//オブジェクトのタイプ
	public void setType(FieldObject type) {
		this.type = type;
	}

	//オブジェクトタイプを返します
	public FieldObject getFieldObject() {
		return type;
	}
}
