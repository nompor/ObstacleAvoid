package com.nompor.test;

import com.nompor.app.game.CharaObject;
import com.nompor.app.game.FieldObject;
import com.nompor.app.game.FieldObjectAppearanceObserverFactory;
import com.nompor.app.game.FieldObjectManager;
import com.nompor.app.game.Player;
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameSceneFX;
import com.nompor.gtk.fx.input.KeyCodeManagerFX;

import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Test1 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//キーコード登録
		KeyCodeManagerFX kmng = GTKManagerFX.getKeyCodeManager();
		kmng.regist(KeyCode.LEFT);
		kmng.regist(KeyCode.RIGHT);
		kmng.regist(KeyCode.Z);

		//GameViewFXはGroupを継承したクラスで、processメソッドはゲームループの処理を実装する
		GTKManagerFX.changeView(new GameSceneFX(WIDTH, HEIGHT) {
			Player p;
			FieldObject[][] data;
			FieldObjectAppearanceObserverFactory factory;
			FieldObjectManager objMng;

			@Override
			public void start() {
				//ワールド領域1200*600に合わせて二次元配列構築
				final int MAX_W=1200,MAX_H=HEIGHT;
				final int ROW=MAX_H/50,COL=MAX_W/50;
				data = new FieldObject[ROW][COL];

				//オブジェクト出現ファクトリ
				factory = FieldObjectAppearanceObserverFactory.createFactory(this, data);
				factory.init();
				FixedTargetCamera2DFX camera = (FixedTargetCamera2DFX)getGameCamera();

				//キャラを中心にカメラを設定(ファクトリはキャラの右側を中心にするため強制で変更)
				camera.setOffsetX(0);
				camera.setOffsetY(0);

				//表示リスト
				objMng = factory.getFieldObjectManager();

				//プレイヤーの作成
				p = objMng.getPlayer();
			}

			@Override
			public void process() {
				//ゲームループ
				move(p);
			}

			int fallSpeed = 0;
			private void move(CharaObject o) {
				if (kmng.isDown(KeyCode.RIGHT)) {
					o.moveX(3);
				} else if (kmng.isDown(KeyCode.LEFT)) {
					o.moveX(-3);
				}
				if ( kmng.isPress(KeyCode.Z) ) {
					fallSpeed = -15;
				}
				fallSpeed++;
				o.moveY(fallSpeed);

				//ウィンドウ表示領域より下に行ったら止まるようにする
				double y = o.getHitNode().getBoundsInParent().getMaxY();
				if(y>=HEIGHT) {
					o.moveY(-(y-HEIGHT));
					fallSpeed = 0;
				} else {
				}
			}
		});
	}

}
