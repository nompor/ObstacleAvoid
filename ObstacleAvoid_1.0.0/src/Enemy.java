

import javafx.scene.Node;

public abstract class Enemy extends CharaObject {

	public Enemy(Node viewNode, Node hitNode) {
		super(viewNode, hitNode);
	}


	//ダメージを受けたら生存フラグOFF
	public void onDamage() {
		isAlive = false;
	}
}
