package com.nompor.app.view;


import static com.nompor.gtk.fx.GTKManagerFX.*;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.nompor.app.game.FieldObject;
import com.nompor.app.game.GameManager;
import com.nompor.app.game.ObjectType;
import com.nompor.app.manager.AppManager;
import com.nompor.app.manager.ConfigManager;
import com.nompor.app.manager.PlayerControllerManager;
import com.nompor.app.manager.RecordManager;
import com.nompor.gtk.file.GTKFileUtil;
import com.nompor.gtk.fx.GameViewFX;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

//ゲーム画面
public class GameMode extends GameViewFX {

	GameManager gm;
	boolean isResultProc = false;
	boolean isEnd = false;
	int saveDataKey = 0;
	CountDownLatch countDown = new CountDownLatch(3);
	Text countDownText = new Text();
	Text negiCountText = new Text();
	Text nowTime = new Text();
	Text titleRet = new Text("タイトルに戻る");

	private GameMode(GameManager gm) {
		this.gm = gm;

		//ゲーム画面の追加
		getChildren().add(gm);

		//キー入力の無効化
		PlayerControllerManager.setActive(false);

		//画面上部の黒い領域を描画
		LinearGradient grad = new LinearGradient(0, 0, 0, 40, false, CycleMethod.NO_CYCLE, new Stop(0, Color.BLACK), new Stop(1, new Color(0, 0, 0, 0.5)));
		Rectangle rect = new Rectangle(0,0,AppManager.getW(),40);
		rect.setFill(grad);
		getChildren().add(rect);

		//カウントダウン
		countDownText.setWrappingWidth(AppManager.getW());
		countDownText.setTextAlignment(TextAlignment.CENTER);
		countDownText.setFont(new Font(80));
		countDownText.setFill(Color.WHITE);
		countDownText.setTranslateY(300);
		getChildren().add(countDownText);

		//ネギのゲット数
		ImageView negi = new ImageView(AppManager.getImage("negi"));
		negi.setRotate(45);
		negi.setTranslateY(0);
		getChildren().add(negi);
		negiCountText = new Text("×0");
		negiCountText.setFont(new Font(30));
		negiCountText.setFill(Color.WHITE);
		negiCountText.setTranslateX(50);
		negiCountText.setTranslateY(30);
		getChildren().add(negiCountText);

		//経過時間
		nowTime.setFont(new Font(15));
		nowTime.setFill(Color.WHITE);
		nowTime.setTranslateX(270);
		nowTime.setTranslateY(30);
		getChildren().add(nowTime);

		//移動自動化ボタン
		Node autoBtn = ConfigManager.getAutoMoveButton();
		autoBtn.setTranslateX(700);
		autoBtn.setTranslateY(10);
		getChildren().add(autoBtn);
	}

	public GameMode() {
		this(createMainGameManager());
	}

	//ファイルからステージを読み込みゲーム画面を構築する
	public static GameMode createFileGameMode(String name) {
		return new GameMode(createFileGameManager(name));
	}

	//画面が表示された時に呼び出されるメソッド
	public void start() {
		AppManager.gameBGMStop();

		//カウントダウン
		Thread th = new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while(countDown != null && countDown.getCount() > 0) {
					AppManager.playSE("se5");
					countDownText.setText(String.valueOf(countDown.getCount()));
					try {
						countDown.await(1, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					countDown.countDown();
				}
				countDownText.setText("START");
				AppManager.playSE("se6");

				//START文字のアニメーション
				//アニメーションの加速減速を抑止するためにInterpolator.LINEARで線形補完を適用させる
				ScaleTransition sc = new ScaleTransition(Duration.millis(1000), countDownText);
				sc.setInterpolator(Interpolator.LINEAR);
				FadeTransition fd = new FadeTransition(Duration.millis(1000), countDownText);
				fd.setInterpolator(Interpolator.LINEAR);
				fd.setToValue(0);
				sc.setToX(2);
				sc.setToY(2);
				ParallelTransition animation = new ParallelTransition(sc,fd);
				animation.setOnFinished(e -> getChildren().remove(countDownText));
				animation.play();
				PlayerControllerManager.setActive(true);
				gm.setStart(true);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				AppManager.gameBGMStart();

				//タイトルに戻るはゲーム開始後に表示
				titleRet.setFont(new Font(18));
				titleRet.setFill(Color.WHITE);
				titleRet.setTranslateY(30);
				titleRet.setTranslateX(130);
				titleRet.setOnMouseClicked(e->AppManager.change(ViewType.TITLE));
				titleRet.setCursor(Cursor.HAND);
				titleRet.setOnMouseEntered(e->titleRet.setFill(Color.ORANGE));
				titleRet.setOnMouseExited(e->titleRet.setFill(Color.WHITE));

				//UIの変更はJavaFXスレッドで実行しなければならない
				Platform.runLater(()->getChildren().add(titleRet));
				return null;
			}
		});
		//デーモンスレッドにしておくと、アプリケーション終了時にこのスレッドも即時終了できる
		th.setDaemon(true);
		th.start();


		//最速タイムの表示
		String time = getTimeString(RecordManager.loadFile()[saveDataKey]);
		Text recRext = new Text("最速タイム:"+time);
		recRext.setFont(new Font(15));
		recRext.setFill(Color.WHITE);
		recRext.setTranslateX(480);
		recRext.setTranslateY(30);
		getChildren().add(recRext);
	}

	public void process() {

		gm.process();
		int frameCount = gm.getFrameCount();
		nowTime.setText("経過時間:"+getTimeString(frameCount));
		negiCountText.setText("x"+gm.getNegiCount());
		if ( !isResultProc && gm.isGameClear() ) {
			isResultProc = true;

			//データを保存
			int data[] = RecordManager.loadFile();
			int pastFrameCount = data[saveDataKey];
			if ( pastFrameCount > gm.getFrameCount() ) {
				data[saveDataKey] = frameCount;
				RecordManager.saveFile(data);
			}

			//キー操作無効化
			PlayerControllerManager.setActive(false);

			//画面を暗くする
			Rectangle rect = new Rectangle(0,0,AppManager.getW(),AppManager.getH());
			rect.setFill(Color.BLACK);
			rect.setOpacity(0);
			getChildren().add(rect);

			//ステージクリアテキストを表示
			Text clearText = new Text("Stage Clear!!");
			clearText.setTranslateX(80);
			clearText.setTranslateY(150);
			clearText.setFont(new Font(30));
			clearText.setFill(Color.YELLOW);
			clearText.setOpacity(0);
			getChildren().add(clearText);

			//クリアタイムの表示
			Text timeText = new Text(getTimeString(gm.getFrameCount()));
			timeText.setTranslateX(80);
			timeText.setTranslateY(250);
			timeText.setFont(new Font(30));
			timeText.setFill(Color.WHITE);
			timeText.setOpacity(0);
			getChildren().add(timeText);

			//タイトルへ戻る為のテキストを構築
			Text clickEnd = new Text("クリックでタイトルに戻ります。");
			clickEnd.setWrappingWidth(AppManager.getW());
			clickEnd.setTextAlignment(TextAlignment.CENTER);
			clickEnd.setTranslateY(500);
			clickEnd.setFont(new Font(20));
			clickEnd.setFill(Color.WHITE);
			clickEnd.setOpacity(0);
			getChildren().add(clickEnd);

			//各種フェードアニメーションのセッティング
			FadeTransition fade1 = createFadeTransition(Duration.millis(2000),rect);
			fade1.setToValue(0.8);

			FadeTransition fade2 = createFadeTransition(Duration.millis(100), clearText);
			fade2.setToValue(1);

			FadeTransition fade3 = createFadeTransition(Duration.millis(100),timeText);
			fade3.setToValue(1);

			Animation anime = createSequentialTransition(fade1, fade2, createPauseTransition(Duration.seconds(1)), fade3, createPauseTransition(Duration.seconds(1)));
			anime.play();

			AppManager.playSE("se7");

			//アニメーション終了後に終了処理を可能にする
			anime.setOnFinished(e -> {

				//クリックでタイトルに戻ります。を表示
				FadeTransition fade4 = createFadeTransition(Duration.millis(1000),clickEnd);
				fade4.setToValue(1);
				fade4.setAutoReverse(true);
				fade4.setCycleCount(Animation.INDEFINITE);
				fade4.play();

				//終了フラグを立てる
				isEnd = true;
			});
		} else if ( !isResultProc && gm.isGameOver() ) {
			isResultProc = true;

			//キー操作無効化
			PlayerControllerManager.setActive(false);

			//画面を白くする
			Rectangle rect = new Rectangle(0,0,AppManager.getW(),AppManager.getH());
			rect.setFill(Color.BLACK);
			rect.setOpacity(0);
			getChildren().add(rect);

			//ステージクリアテキストを表示
			Text clearText = new Text("Game Over");
			clearText.setTranslateX(80);
			clearText.setTranslateY(150);
			clearText.setFont(new Font(30));
			clearText.setFill(Color.RED);
			clearText.setOpacity(0);
			getChildren().add(clearText);

			//タイトルへ戻る為のテキストを構築
			Text clickEnd = new Text("クリックでタイトルに戻ります。");
			clickEnd.setWrappingWidth(AppManager.getW());
			clickEnd.setTextAlignment(TextAlignment.CENTER);
			clickEnd.setTranslateY(500);
			clickEnd.setFont(new Font(20));
			clickEnd.setFill(Color.WHITE);
			clickEnd.setOpacity(0);
			getChildren().add(clickEnd);

			//各種フェードアニメーションのセッティング
			FadeTransition fade1 = createFadeTransition(Duration.millis(2000),rect);
			fade1.setToValue(0.8);

			FadeTransition fade2 = createFadeTransition(Duration.millis(100), clearText);
			fade2.setToValue(1);

			Animation anime = createSequentialTransition(fade1, fade2, createPauseTransition(Duration.seconds(1)));
			anime.play();

			//アニメーション終了後に終了処理を可能にする
			anime.setOnFinished(e -> {

				//クリックでタイトルに戻ります。を表示
				FadeTransition fade4 = createFadeTransition(Duration.millis(1000),clickEnd);
				fade4.setToValue(1);
				fade4.setAutoReverse(true);
				fade4.setCycleCount(Animation.INDEFINITE);
				fade4.play();

				//終了フラグを立てる
				isEnd = true;
			});
		}
	}

	//終了時にクリックでタイトルへ戻る処理
	public void mouseClicked(MouseEvent e) {
		if ( isEnd ) AppManager.change(ViewType.TITLE);
	}

	//終了時にボタン押されたらタイトルへ戻る処理(ENTER、ESCAPEは何もしない)
	public void keyPressed(KeyEvent e) {
		if ( isEnd ) {
			switch(e.getCode()) {
			case ESCAPE:
				break;
			case ENTER:
				break;
			default:
				if ( isEnd ) AppManager.change(ViewType.TITLE);
			}
		}
	}

	//メインゲーム画面のセッティング
	private static GameManager createMainGameManager() {

		//ファイルから読み取った数値からFieldObjectに変換(デバッグ用)
		/*
		String[][] stageDataStr = GTKFileUtil.readCSV(new File("data/stage.txt"));
		FieldObject[][] stageData = new FieldObject[stageDataStr.length][stageDataStr[0].length];
		for ( int i = 0;i < stageData.length;i++ ) {
			for ( int j = 0;j < stageData[i].length;j++ ) {
				int index = Integer.parseInt(stageDataStr[i][j]);
				stageData[i][j] = FieldObject.getFieldObjectForIndex(index);
			}
		}
		*/

		//ランダムステージ構築
		FieldObject[][] data = new FieldObject[12][360];
		for ( int x = 0;x < data[data.length-1].length;x++ ) {
			data[data.length-1][x] = FieldObject.GROUND;
		}
		Random rand = new Random();
		FieldObject[] enemys = FieldObject.getFieldObjects(ObjectType.ENEMY);
		for ( int i = 15;i < 360;i+=10 ) {
			FieldObject e = enemys[rand.nextInt(enemys.length)];
			switch(e) {
			case BIRD:data[8][i]=e;break;
			default:data[10][i]=e;break;
			}
		}
		for ( int i = 14;i < 360;i+=20 ) {
			switch ( rand.nextInt(3) ) {
			case 1:
				data[8][i]=FieldObject.BLOCK;
				data[9][i]=FieldObject.BLOCK;
				break;
			case 2:
				data[9][i]=FieldObject.BLOCK;
				data[10][i]=FieldObject.BLOCK;
				break;
			default:
				data[10][i]=FieldObject.BLOCK;
				break;
			}
		}
		for ( int i = 8;i < 360;i+=20 ) {
			int basho = rand.nextInt(2);
			if ( basho == 0 ) {
				data[8][i] = FieldObject.NEGI;
			} else {
				data[10][i] = FieldObject.NEGI;
			}
		}
		for ( int i = 10;i < 360;i+=50 ) {
			data[11][i] = FieldObject.NONE;
		}
		FieldObject[] flds = FieldObject.getFieldObjects(ObjectType.FIELD);
		for ( int i = 12;i < 360;i+=5 ) {
			if ( data[10][i] == null && data[11][i] == FieldObject.GROUND ) {
				data[10][i] = flds[rand.nextInt(flds.length)];
			}
		}

		GameManager gm = new GameManager(data);

		gm.init();

		return gm;
	}

	//ファイルからゲーム画面のセッティング
	private static GameManager createFileGameManager(String name) {

		//ファイルから読み取った数値からFieldObjectに変換
		String[][] stageDataStr = GTKFileUtil.readCSV(new File("data/"+name+".txt"));
		FieldObject[][] stageData = new FieldObject[stageDataStr.length][stageDataStr[0].length];
		for ( int i = 0;i < stageData.length;i++ ) {
			for ( int j = 0;j < stageData[i].length;j++ ) {
				int index = Integer.parseInt(stageDataStr[i][j]);
				stageData[i][j] = FieldObject.getFieldObjectForIndex(index);
			}
		}

		GameManager gm = new GameManager(stageData);

		gm.init();

		return gm;
	}

	public String getTimeString(int frameCounter) {
		try {
			//合計フレーム数をミリ秒に変換
			long allMs = (long)(frameCounter * (1000.0 / 60.0));

			//ナノ秒を元にLocalTimeを構築
			LocalTime localTime = LocalTime.ofNanoOfDay(allMs * 1000 * 1000);

			//時間形式の文字列に変換して返す
			return localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
		} catch (Exception e) {
		}

		return "遅すぎます。寝てたんじゃないでしょうね・・・";
	}

	public void setRecordKey(int key) {
		saveDataKey = key;
	}
}
