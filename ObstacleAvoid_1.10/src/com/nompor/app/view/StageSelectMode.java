package com.nompor.app.view;

import com.nompor.app.game.FieldObject;
import com.nompor.app.game.FieldObjectAppearanceObserverFactory;
import com.nompor.app.game.GameManager;
import com.nompor.app.game.Player;
import com.nompor.app.manager.AppManager;
import com.nompor.app.manager.ConfigManager;
import com.nompor.app.manager.PlayerControllerManager;
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GameViewGroupFX;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StageSelectMode extends GameViewGroupFX {
	//ゲームモード画面オブジェクト
	GameManager gm;
	GameMode gameView;

	public StageSelectMode() {
		AppManager.titleBGMStart();

		//キーの無効化
		PlayerControllerManager.setActive(false);

		gm = createStageSelectGameManager();

		//ゲーム画面の追加
		getChildren().add(gm);

		//黒領域追加
		Rectangle rect = new Rectangle(100,0,600,AppManager.getH());
		RadialGradient grad = new RadialGradient(0, 0, 400, 300, 300, false, CycleMethod.NO_CYCLE
				, new Stop(0, new Color(0, 0, 0, 1))
				, new Stop(0.8, new Color(0, 0, 0, 0.8))
				, new Stop(1, new Color(0, 0, 0, 0))
		);
		rect.setFill(grad);
		getChildren().add(rect);

		//テキスト作成
		Text st1 = new Text(0,200,"ステージ1");
		Text st2 = new Text(0,250,"ステージ2");
		Text st3 = new Text(0,300,"ステージ3");
		Text difficulty = new Text(0,360,"難易度");
		Text ret = new Text(0,430,"タイトルに戻る");

		//文字の色
		st1.setFill(Color.WHITE);
		st2.setFill(Color.WHITE);
		st3.setFill(Color.WHITE);
		difficulty.setFill(Color.WHITE);
		ret.setFill(Color.WHITE);

		//横幅
		st1.setWrappingWidth(AppManager.getW());
		st2.setWrappingWidth(AppManager.getW());
		st3.setWrappingWidth(AppManager.getW());
		difficulty.setWrappingWidth(AppManager.getW());
		ret.setWrappingWidth(AppManager.getW());

		//中央寄せ
		st1.setTextAlignment(TextAlignment.CENTER);
		st2.setTextAlignment(TextAlignment.CENTER);
		st3.setTextAlignment(TextAlignment.CENTER);
		difficulty.setTextAlignment(TextAlignment.CENTER);
		ret.setTextAlignment(TextAlignment.CENTER);

		//文字の大きさ
		st1.setFont(new Font(30));
		st2.setFont(new Font(30));
		st3.setFont(new Font(30));
		difficulty.setFont(new Font(30));
		ret.setFont(new Font(30));

		//マウスが領域に入った時
		st1.setOnMouseEntered(this::onOver);
		st2.setOnMouseEntered(this::onOver);
		st3.setOnMouseEntered(this::onOver);
		ret.setOnMouseEntered(this::onOver);

		//マウスが領域から出たとき
		st1.setOnMouseExited(this::onExited);
		st2.setOnMouseExited(this::onExited);
		st3.setOnMouseExited(this::onExited);
		ret.setOnMouseExited(this::onExited);

		//カーソル
		st1.setCursor(Cursor.HAND);
		st2.setCursor(Cursor.HAND);
		st3.setCursor(Cursor.HAND);
		ret.setCursor(Cursor.HAND);

		//クリックされた時
		st1.setOnMouseClicked(e -> prepareGame("stage1",1));
		st2.setOnMouseClicked(e -> prepareGame("stage2",2));
		st3.setOnMouseClicked(e -> prepareGame("stage3",3));
		ret.setOnMouseClicked(e -> AppManager.change(ViewType.TITLE));

		getChildren().add(st1);
		getChildren().add(st2);
		getChildren().add(st3);
		getChildren().add(difficulty);
		getChildren().add(ret);

		//移動自動化ボタン
		Node autoBtn = ConfigManager.getAutoMoveButton();
		autoBtn.setTranslateX(680);
		autoBtn.setTranslateY(20);
		getChildren().add(autoBtn);
	}

	public void process() {
		//ゲームの進行
		if ( gm != null ) {
			gm.process();
		}
		if (gameView != null) {
			gameView.process();
		}
	}

	//タイトル画面用ゲーム画面のセッティング
	GameManager createStageSelectGameManager() {

		//タイトルで表示するステージを構築
		FieldObject[][] stageData = new FieldObject[12][40];
		for ( int i = 0;i < stageData[11].length;i++ ) {
			stageData[11][i] = FieldObject.GROUND;
		}
		stageData[9][0] = FieldObject.BLOCK;
		stageData[9][1] = FieldObject.BLOCK;
		stageData[9][2] = FieldObject.BLOCK;
		stageData[9][3] = FieldObject.BLOCK;
		stageData[9][4] = FieldObject.BLOCK;
		stageData[9][5] = FieldObject.BLOCK;
		stageData[9][6] = FieldObject.BLOCK;

		stageData[10][8] = FieldObject.BLOCK;
		stageData[10][16] = FieldObject.BLOCK;

		stageData[7][8] = FieldObject.BLOCK;
		stageData[7][9] = FieldObject.BLOCK;
		stageData[7][10] = FieldObject.BLOCK;
		stageData[7][11] = FieldObject.BLOCK;
		stageData[7][12] = FieldObject.BLOCK;
		stageData[7][13] = FieldObject.BLOCK;
		stageData[7][14] = FieldObject.BLOCK;
		stageData[7][15] = FieldObject.BLOCK;
		stageData[7][16] = FieldObject.BLOCK;
		stageData[6][8] = FieldObject.BLOCK;
		stageData[6][16] = FieldObject.BLOCK;

		stageData[3][8] = FieldObject.BLOCK;
		stageData[3][9] = FieldObject.BLOCK;
		stageData[3][10] = FieldObject.BLOCK;
		stageData[3][11] = FieldObject.BLOCK;
		stageData[3][12] = FieldObject.BLOCK;
		stageData[3][13] = FieldObject.BLOCK;
		stageData[3][14] = FieldObject.BLOCK;
		stageData[3][15] = FieldObject.BLOCK;
		stageData[3][16] = FieldObject.BLOCK;
		stageData[2][8] = FieldObject.BLOCK;
		stageData[2][16] = FieldObject.BLOCK;

		stageData[2][9] = FieldObject.RED_SLIME;
		stageData[2][10] = FieldObject.RED_SLIME;
		stageData[2][11] = FieldObject.RED_SLIME;
		stageData[2][12] = FieldObject.RED_SLIME;
		stageData[2][13] = FieldObject.RED_SLIME;
		stageData[2][14] = FieldObject.RED_SLIME;
		stageData[2][15] = FieldObject.RED_SLIME;

		stageData[6][9] = FieldObject.RED_SLIME;
		stageData[6][10] = FieldObject.RED_SLIME;
		stageData[6][11] = FieldObject.RED_SLIME;
		stageData[6][12] = FieldObject.RED_SLIME;
		stageData[6][13] = FieldObject.RED_SLIME;
		stageData[6][14] = FieldObject.RED_SLIME;
		stageData[6][15] = FieldObject.RED_SLIME;

		stageData[10][9] = FieldObject.RED_SLIME;
		stageData[10][10] = FieldObject.RED_SLIME;
		stageData[10][11] = FieldObject.RED_SLIME;
		stageData[10][12] = FieldObject.RED_SLIME;
		stageData[10][13] = FieldObject.RED_SLIME;
		stageData[10][14] = FieldObject.RED_SLIME;
		stageData[10][15] = FieldObject.RED_SLIME;

		stageData[0][12] = FieldObject.BIRD;
		stageData[1][12] = FieldObject.BIRD;
		stageData[5][13] = FieldObject.BIRD;

		stageData[9][8] = FieldObject.NEGI;
		stageData[9][16] = FieldObject.NEGI;

		GameManager gm = new GameManager(stageData);
		FieldObjectAppearanceObserverFactory factory = gm.getFactory();

		//プレイヤーを強制座標移動
		Player player = factory.getFieldObjectManager().getPlayer();
		player.moveX(300);
		player.moveY(-100);

		//プレイヤー座標を更新したのでカメラも更新する
		FixedTargetCamera2DFX camera = (FixedTargetCamera2DFX) gm.getGameCamera();
		camera.update();

		//初期出現処理
		factory.init();

		return gm;
	}

	private void onOver(MouseEvent e) {
		Text obj = (Text)e.getSource();
		obj.setFill(Color.ORANGE);
	}

	private void onExited(MouseEvent e) {
		Text obj = (Text)e.getSource();
		obj.setFill(Color.WHITE);
	}

	private void prepareGame(String name, int key) {
		getChildren().clear();
		gm = null;
		gameView = GameMode.createFileGameMode(name);
		gameView.setRecordKey(key);
		gameView.start();

		getChildren().add(gameView);
	}
}
