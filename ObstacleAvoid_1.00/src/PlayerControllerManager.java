

import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.input.MouseManagerFX;

import javafx.scene.input.MouseButton;

//プレイヤーが操作した内容を取得できるクラス
public class PlayerControllerManager {

	//マウスマネージャー
	static MouseManagerFX mMng = GTKManagerFX.getMouseManager();

	//このフラグがオフの場合は常にキー入力を無効化する
	static boolean isActive=true;

	static {

		//検出マウスボタンの登録
		mMng.regist(MouseButton.PRIMARY);
		mMng.regist(MouseButton.SECONDARY);
	}

	//このフラグがオフの場合は常にアクションフラグを無効化する
	public static void setActive(boolean isActive) {
		PlayerControllerManager.isActive =isActive;
	}

	//ジャンプすべきか（）
	public static boolean isJump() {
		return isActive && mMng.isPress(MouseButton.SECONDARY);
	}

	//ジャンプボタンを押下し続けているか
	public static boolean isJumping() {
		return mMng.isDown(MouseButton.SECONDARY);
	}

	//右移動すべきか
	public static boolean isRight() {
		return isActive;
	}
	//マウスの左クリック
	public static boolean isMPressLeft() {
		return isActive && mMng.isPress(MouseButton.PRIMARY);
	}
	//マウスのX座標を取得します
	public static double getMX() {
		return mMng.getPoint().x;
	}
	//マウスのY座標を取得します
	public static double getMY() {
		return mMng.getPoint().y;
	}
}
