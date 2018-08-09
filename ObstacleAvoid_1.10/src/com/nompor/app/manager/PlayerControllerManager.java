package com.nompor.app.manager;

import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.input.KeyCodeManagerFX;
import com.nompor.gtk.fx.input.MouseManagerFX;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

//プレイヤーが操作した内容を取得できるクラス
public class PlayerControllerManager {

	private enum KeyAction{
		LEFT,RIGHT,JUMP;
	}

	//キーコードマネージャー
	static KeyCodeManagerFX keyMng = GTKManagerFX.getKeyCodeManager();

	//マウスマネージャー
	static MouseManagerFX mMng = GTKManagerFX.getMouseManager();

	//このフラグがオフの場合は常にキー入力を無効化する
	static boolean isActive=true;

	static {
		//検出キーの登録
		keyMng.orRegist(KeyAction.JUMP, KeyCode.UP, KeyCode.S);
		keyMng.orRegist(KeyAction.LEFT, KeyCode.LEFT, KeyCode.Z);
		keyMng.orRegist(KeyAction.RIGHT, KeyCode.RIGHT, KeyCode.X);

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
		return isActive && (keyMng.isPress(KeyAction.JUMP)||mMng.isPress(MouseButton.SECONDARY));
	}

	//ジャンプボタンを押下し続けているか
	public static boolean isJumping() {
		return (keyMng.isDown(KeyAction.JUMP)||mMng.isDown(MouseButton.SECONDARY));
	}

	//左移動すべきか
	public static boolean isLeft() {
		return isActive && (keyMng.isDown(KeyAction.LEFT));
	}

	//右移動すべきか
	public static boolean isRight() {
		return isActive && (ConfigManager.isAutoRightMode && !isLeft() || keyMng.isDown(KeyAction.RIGHT));
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
