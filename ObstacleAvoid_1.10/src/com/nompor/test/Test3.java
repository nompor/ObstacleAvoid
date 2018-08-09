package com.nompor.test;

import java.io.File;
import java.util.Iterator;

import com.nompor.app.game.Enemy;
import com.nompor.app.game.FieldObject;
import com.nompor.app.game.FieldObjectAppearanceObserverFactory;
import com.nompor.app.game.FieldObjectManager;
import com.nompor.app.game.GameObject;
import com.nompor.app.game.Player;
import com.nompor.app.manager.ConfigManager;
import com.nompor.gtk.file.GTKFileUtil;
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameSceneFX;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.stage.Stage;

public class Test3 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//ゲーム画面
		GameSceneFX scene = new GameSceneFX(WIDTH, HEIGHT) {
			Player p;
			FieldObject[][] data;
			FieldObjectAppearanceObserverFactory factory;
			FieldObjectManager objMng;
			FixedTargetCamera2DFX camera;

			@Override
			public void start() {

				//CSVファイルを読み込んで数値を別オブジェクトに変換
				String[][] str = GTKFileUtil.readCSV(new File("test_data/stage.txt"));
				data = new FieldObject[str.length][str[0].length];
				for ( int i = 0;i < str.length;i++ ) {
					for ( int j = 0;j < str[i].length;j++ ) {
						switch(str[i][j]) {
						case "0":data[i][j]=FieldObject.NONE;break;
						case "1":data[i][j]=FieldObject.GROUND;break;
						case "2":data[i][j]=FieldObject.BLOCK;break;
						case "3":data[i][j]=FieldObject.SLIME;break;
						case "4":data[i][j]=FieldObject.NEGI;break;
						}
					}
				}

				//オブジェクト出現ファクトリ
				factory = FieldObjectAppearanceObserverFactory.createFactory(this, data);
				factory.init();
				camera = (FixedTargetCamera2DFX)getGameCamera();

				//キャラを中心にカメラを設定(ファクトリはキャラの右側を中心にするため強制で変更)
				camera.setOffsetX(0);
				camera.setOffsetY(0);

				//表示リスト
				objMng = factory.getFieldObjectManager();

				//プレイヤーの作成
				p = objMng.getPlayer();
				p.moveY(500);

				//自動右移動無効化
				ConfigManager.setAutoRightMode(false);
			}

			@Override
			public void process() {
				//ゲームループ

				factory.execute();

				p.update();

				//敵の動き
				Iterator<Enemy> enemys = objMng.getEnemys();
				while(enemys.hasNext()) {
					Enemy enemy = enemys.next();
					enemy.update();

					if ( !isObjectInWindow(enemy) ) {
						enemys.remove();
					}
				}
			}

			//ゲームオブジェクトが画面内かどうか
			public boolean isObjectInWindow(GameObject go) {
				double l = camera.getLeft() - 200;
				double t = camera.getTop() - 200;
				double r = camera.getRight() + 200;
				double b = camera.getBottom() + 200;
				Bounds bounds = go.getNode().getBoundsInParent();
				return bounds.getMaxX() < r
				&& bounds.getMinY() > t
				&& bounds.getMinX() > l
				&& bounds.getMaxY() < b;
			}
		};

		//画面表示
		GTKManagerFX.changeView(scene);

	}
}
