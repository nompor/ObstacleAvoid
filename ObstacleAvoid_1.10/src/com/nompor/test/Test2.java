package com.nompor.test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import com.nompor.app.game.CharaObject;
import com.nompor.app.game.FieldObject;
import com.nompor.app.game.FieldObjectAppearanceObserverFactory;
import com.nompor.app.game.FieldObjectManager;
import com.nompor.app.game.Player;
import com.nompor.gtk.GTKException;
import com.nompor.gtk.file.GTKFileUtil;
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameSceneFX;
import com.nompor.gtk.fx.GameViewGroupFX;

import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Test2 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	static Properties prop;

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		File file = new File("test_data/keyconf.txt");
		try {
			if ( !file.exists() ) {
				//デフォルトキーコード保存
				prop = new Properties();
				prop.setProperty("左移動",KeyCode.LEFT.getName());
				prop.setProperty("右移動",KeyCode.RIGHT.getName());
				prop.setProperty("ジャンプ",KeyCode.Z.getName());
				prop.setProperty("キーコンフィグ",KeyCode.ESCAPE.getName());
				GTKFileUtil.writeProperty(file, prop);
				prop.forEach((k,v)->{
					GTKManagerFX.registKey((String)k,KeyCode.getKeyCode((String)v));
				});
			} else {
				prop = GTKFileUtil.readProperty(file);
				prop.forEach((k,v)->{
					GTKManagerFX.registKey((String)k,KeyCode.getKeyCode((String)v));
				});
			}
		}catch (GTKException e) {
		}

		//キーコンフィグ画面
		GameViewGroupFX view = new GameViewGroupFX() {
			boolean isConfigMode;
			Canvas canvas;
			HashMap<String, String> changeMap = new HashMap<>();
			HashSet<String> conflictSet = new HashSet<>();
			Object[] checkList;
			Font f = new Font(40);
			int idx=0;
			public void process() {
				if ( isConfigMode ) {
					if ( canvas == null ) {
						checkList = prop.keySet().toArray();

						//canvasがない場合は初期化処理
						canvas = new Canvas(WIDTH, HEIGHT);
						GTKManagerFX.setOnKeyPressed(e -> {
							String k = (String) checkList[idx];
							String v = e.getCode().getName();
							if ( !conflictSet.contains(v) ) {
								idx++;
								conflictSet.add(v);
								changeMap.put(k, v);
								if ( idx >= checkList.length ) {
									isConfigMode = false;
									prop = new Properties();
									changeMap.forEach(prop::setProperty);

									//登録キー全削除
									GTKManagerFX.clearKey();

									//再登録
									prop.forEach((k2,v2)->{
										GTKManagerFX.registKey((String)k2,KeyCode.getKeyCode((String)v2));
									});

									//ファイルを書き出し
									GTKFileUtil.writeProperty(file, prop);

									//不要データを削除
									getChildren().remove(canvas);
									canvas = null;
									GTKManagerFX.setOnKeyPressed(null);
									changeMap.clear();
									conflictSet.clear();
									idx=0;
								}
							}
						});
						getChildren().add(canvas);
					}
					GraphicsContext g = canvas.getGraphicsContext2D();
					int i = 0;
					g.clearRect(0, 0, WIDTH, HEIGHT);
					g.setFill(Color.rgb(0, 0, 0, 0.8));
					g.fillRect(0, 0, WIDTH, HEIGHT);
					g.setFont(f);
					for ( Object k : checkList ) {
						Object v = prop.get(k);
						g.setFill(idx != i ? Color.rgb(155, 0, 100, 0.7) : Color.rgb(255, 100, 0, 0.7));
						g.fillRect(0, 100+i*100, WIDTH, 50);
						g.setFill(Color.WHITE);
						g.fillText((String)k, 100, 140+i*100);
						g.fillText((String)v, 350, 140+i*100);
						if ( changeMap.containsKey(k) ) g.fillText(changeMap.get(k), 600, 140+i*100);
						i++;
					};
				} else {
					super.process();
					if(GTKManagerFX.isKeyPress("キーコンフィグ")){
						isConfigMode = true;
					}
				}
			}
		};

		//ゲーム画面
		GameSceneFX scene = new GameSceneFX(WIDTH, HEIGHT) {
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
				if (GTKManagerFX.isKeyDown("右移動")) {
					o.moveX(3);
				} else if (GTKManagerFX.isKeyDown("左移動")) {
					o.moveX(-3);
				}
				if ( GTKManagerFX.isKeyPress("ジャンプ") ) {
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
		};
		view.getChildren().add(scene);

		//画面表示
		GTKManagerFX.changeView(view);

	}
}
