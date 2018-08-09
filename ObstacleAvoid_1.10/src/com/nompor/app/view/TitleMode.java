package com.nompor.app.view;

import com.nompor.app.game.FieldObject;
import com.nompor.app.game.FieldObjectAppearanceObserverFactory;
import com.nompor.app.game.GameManager;
import com.nompor.app.game.Player;
import com.nompor.app.manager.AppManager;
import com.nompor.app.manager.ConfigManager;
import com.nompor.app.manager.PlayerControllerManager;
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GameViewFX;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

//タイトル画面
public class TitleMode extends GameViewFX {

	GameManager gm;
	public void start() {
		gm = createTitleGameManager();

		//ゲーム画面の追加
		getChildren().add(gm);

		//黒領域追加
		Rectangle rect = new Rectangle(200,225,400,375);
		RadialGradient grad = new RadialGradient(0, 0, 400, 425, 200, false, CycleMethod.NO_CYCLE
				, new Stop(0, new Color(0, 0, 0, 1))
				, new Stop(0.8, new Color(0, 0, 0, 0.8))
				, new Stop(1, new Color(0, 0, 0, 0))
		);
		rect.setFill(grad);
		getChildren().add(rect);

		//キー入力の無効化
		PlayerControllerManager.setActive(false);
		ImageView title = new ImageView(AppManager.getImage("moglogo"));
		title.setTranslateY(50);
		title.setScaleX(0.95);

		//テキスト作成
		Text start = new Text(0,350,"ランダムモード");
		Text select = new Text(0,410,"ステージセレクト");
		Text desc = new Text(0,470,"チュートリアル");
		Text end = new Text(0,530,"終了");

		//文字の色
		start.setFill(Color.WHITE);
		select.setFill(Color.WHITE);
		desc.setFill(Color.WHITE);
		end.setFill(Color.WHITE);

		//横幅
		start.setWrappingWidth(AppManager.getW());
		select.setWrappingWidth(AppManager.getW());
		desc.setWrappingWidth(AppManager.getW());
		end.setWrappingWidth(AppManager.getW());

		//中央寄せ
		start.setTextAlignment(TextAlignment.CENTER);
		select.setTextAlignment(TextAlignment.CENTER);
		desc.setTextAlignment(TextAlignment.CENTER);
		end.setTextAlignment(TextAlignment.CENTER);

		//文字の大きさ
		start.setFont(new Font(40));
		select.setFont(new Font(40));
		desc.setFont(new Font(40));
		end.setFont(new Font(40));

		//カーソル
		start.setCursor(Cursor.HAND);
		select.setCursor(Cursor.HAND);
		desc.setCursor(Cursor.HAND);
		end.setCursor(Cursor.HAND);

		//マウスが領域に入った時
		start.setOnMouseEntered(this::onOver);
		select.setOnMouseEntered(this::onOver);
		desc.setOnMouseEntered(this::onOver);
		end.setOnMouseEntered(this::onOver);

		//マウスが領域から出たとき
		start.setOnMouseExited(this::onExited);
		select.setOnMouseExited(this::onExited);
		desc.setOnMouseExited(this::onExited);
		end.setOnMouseExited(this::onExited);

		//テキストがクリックされた時
		start.setOnMouseClicked(e->AppManager.change(ViewType.GAME));
		select.setOnMouseClicked(e->AppManager.change(ViewType.STAGE_SELECT));
		desc.setOnMouseClicked(e->AppManager.change(ViewType.DESCRIPTION));
		end.setOnMouseClicked(e->AppManager.end());

		//要素の追加
		getChildren().add(title);
		getChildren().add(start);
		getChildren().add(select);
		getChildren().add(desc);
		getChildren().add(end);

		//移動自動化ボタン
		Node autoBtn = ConfigManager.getAutoMoveButton();
		autoBtn.setTranslateX(680);
		autoBtn.setTranslateY(20);
		getChildren().add(autoBtn);

		AppManager.titleBGMStart();
	}



	//タイトル画面用ゲーム画面のセッティング
	GameManager createTitleGameManager() {

		//タイトルで表示するステージを構築
		FieldObject[][] stageData = new FieldObject[12][40];
		for ( int i = 0;i < stageData[11].length;i++ ) {
			stageData[11][i] = FieldObject.GROUND;
		}
		stageData[9][0] = FieldObject.GROUND;
		stageData[9][1] = FieldObject.GROUND;
		stageData[9][2] = FieldObject.GROUND;
		stageData[9][3] = FieldObject.GROUND;
		stageData[9][4] = FieldObject.GROUND;
		stageData[9][5] = FieldObject.GROUND;
		stageData[9][6] = FieldObject.GROUND;

		stageData[10][8] = FieldObject.BLOCK;
		stageData[10][16] = FieldObject.BLOCK;

		stageData[10][9] = FieldObject.RED_SLIME;
		stageData[10][10] = FieldObject.SLIME;

		stageData[3][12] = FieldObject.BIRD;
		stageData[5][13] = FieldObject.BIRD;

		stageData[2][19] = FieldObject.NEEDLE_LEAF;

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

	//ゲームの処理
	public void process() {
		gm.process();
	}

	private void onOver(MouseEvent e) {
		Text obj = (Text)e.getSource();
		obj.setFill(Color.ORANGE);
	}

	private void onExited(MouseEvent e) {
		Text obj = (Text)e.getSource();
		obj.setFill(Color.WHITE);
	}
}