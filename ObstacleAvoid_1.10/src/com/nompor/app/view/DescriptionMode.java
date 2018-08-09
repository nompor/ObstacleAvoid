package com.nompor.app.view;

import com.nompor.app.game.FieldObject;
import com.nompor.app.game.FieldObjectAppearanceObserverFactory;
import com.nompor.app.game.FieldObjectManager;
import com.nompor.app.game.GameManager;
import com.nompor.app.game.Player;
import com.nompor.app.manager.AppManager;
import com.nompor.app.manager.ConfigManager;
import com.nompor.app.manager.PlayerControllerManager;
import com.nompor.gtk.CameraTargetPoint2D;
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GameViewFX;
import com.nompor.gtk.fx.animation.PagingTextAnimationView;
import com.nompor.gtk.fx.animation.TextAnimationView;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

//説明画面
public class DescriptionMode extends GameViewFX{

	//ゲームロジック
	GameManager gm = createDescriptionGameManager();

	//ページ式メッセージアニメーションクラス
	PagingTextAnimationView pager = new PagingTextAnimationView(90,95);

	//オブジェクト生成ファクトリ
	FieldObjectAppearanceObserverFactory factory;

	//フィールド生成処理に介入するためにオブジェクトを保持
	FieldObjectManager gameObjects;

	//プレイヤーマーク
	Group page2Object = new Group();
	Text text1;
	Circle circle;

	//三角形
	Polygon nextTri;
	Polygon prevTri;

	public DescriptionMode() {

		factory = gm.getFactory();
		gameObjects = factory.getFieldObjectManager();

		//ゲーム画面の追加
		getChildren().add(gm);

		Font f = new Font(20);
		Color clr = new Color(1, 1, 1, 1);

		//1ページずつメッセージの追加
		int dur = 80;
		pager.add(new TextAnimationView(dur, "ゲームの説明を始めます。", clr, f));
		pager.add(new TextAnimationView(dur, "矢印をクリックしてページを進めてね。", clr, f));
		pager.add(new TextAnimationView(dur, "このゲームは、プレイヤーキャラとなる\n「もぐもる」を、ステージの一番右端へ\nネギを取りながら\n速く連れて行くことが目的です。\n", clr, f));
		pager.add(new TextAnimationView(dur, "ゲームモードは二つあり、\nランダムモードと、\nステージセレクトモードがあります。", clr, f));
		pager.add(new TextAnimationView(dur, "ランダムモードはランダムで\nステージが構築されます。", clr, f));
		pager.add(new TextAnimationView(dur, "ステージセレクトモードは、\nあらかじめ用意された3つのステージを\n選択してプレイできます。", clr, f));
		pager.add(new TextAnimationView(dur, "操作はキーボードとマウスを使用します。\n", clr, f));
		pager.add(new TextAnimationView(dur, "キーボード操作\nSキーまたは上キーでジャンプ\nZキーまたは左キーで左に移動\nXキーまたは右キーで右に移動\n右移動は設定で自動化できます。\n画面右上に設定ボタンが、\nあるのでいつでも変更できます。", clr, f));
		pager.add(new TextAnimationView(dur, "敵キャラはジャンプで踏みつけて\n倒すことができます。\n踏みつける時にジャンプボタンを\n押下していると、\n踏みつけハイジャンプができます。\n試しに踏んづけてみるといいでしょう。", clr, f));
		pager.add(new TextAnimationView(dur, "マウス操作\nマウス操作は、敵やブロック、\nプレイヤーキャラクター等を\n左クリックすることで、特定の\nアクションを実行させることができます。\n右クリックは強制で、\nプレイヤーキャラをジャンプさせます。", clr, f));
		pager.add(new TextAnimationView(dur, "アイテムのネギを取得すると\nプレイヤーの走行速度が上がります。\n反対に敵からダメージを受けると\n走行速度が下がります。", clr, f));
		pager.add(new TextAnimationView(dur, "すごい適当ですが、以上でゲーム説明は\n終了です。\n\n特殊コマンド\nESCキー・・・ゲーム終了\nENTER・・・フルスクリーンモード\nフルスクリーンは環境次第でバグりそう。", clr, f));
		pager.add(new TextAnimationView(dur, "どうでもいいひとりごと\n\nこのゲームのソースは公開しています。\n興味ある方は是非閲覧してみてください。\nJavaの初心者の方になら参考になるかも。", clr, f));
		pager.add(new TextAnimationView(dur, "汚いソースなので中級者以上の方は\nできるだけ見ないように。\n\n以上です。\n右上のボタンからタイトルに戻れるよ。", clr, f));
		pager.setAutoNextPageDuration(Duration.seconds(2));
		pager.doPlayNowPage();

		//ページ2のセッティング
		text1 = new Text("プレイヤー");
		text1.setFont(new Font(13));
		text1.setTranslateX(97);
		text1.setTranslateY(-125);
		text1.setFill(Color.WHITE);
		circle = new Circle(gameObjects.getPlayer().getCTX(), gameObjects.getPlayer().getCTY(), 40);
		circle.setFill(null);
		circle.setStroke(Color.RED);
		circle.setStrokeWidth(3);
		Rectangle pRect = new Rectangle(70,30);
		pRect.setTranslateX(90);
		pRect.setTranslateY(-145);
		pRect.setArcWidth(10);
		pRect.setArcHeight(10);
		page2Object.getChildren().add(pRect);
		page2Object.getChildren().add(text1);
		page2Object.getChildren().add(circle);
		getChildren().add(page2Object);
		page2Object.setVisible(false);

		//テキスト枠
		Rectangle rect = new Rectangle(70, 60, 400, 200);
		rect.setArcWidth(10);
		rect.setArcHeight(10);
		rect.setFill(Color.gray(0.1));
		rect.setStroke(Color.LIME);
		getChildren().add(rect);

		//プレイヤー非表示
		gameObjects.getPlayer().getViewNode().setVisible(false);

		//強制出現処理
		factory.init();

		getChildren().add(pager);

		//ボタンを追加
		Button btn = new Button();
		btn.setText("チュートリアル終了");
		btn.setTranslateX(650);
		btn.setTranslateY(20);
		btn.setBorder(new Border(new BorderStroke(Color.LIME, BorderStrokeStyle.DASHED, new CornerRadii(10), BorderWidths.DEFAULT)));
		btn.setTextFill(Color.WHITE);
		Background mainBack = new Background(
			new BackgroundFill(
					new LinearGradient(
						0, 0, 0, 1, true, CycleMethod.NO_CYCLE
						, new Stop(0, Color.gray(0.5))
						, new Stop(1, Color.BLACK)
					), new CornerRadii(10)
			, Insets.EMPTY
			)
		);
		btn.setBackground(mainBack);
		btn.setCursor(Cursor.HAND);
		btn.setOnAction(e -> AppManager.change(ViewType.TITLE));
		btn.setOnMouseEntered(e -> btn.setBackground(new Background(new BackgroundFill(Color.ORANGE, new CornerRadii(10), Insets.EMPTY))));
		btn.setOnMouseExited(e -> btn.setBackground(mainBack));
		btn.setFocusTraversable(false);//ボタンにフォーカスが当たるとキーイベントが正常に動かない
		getChildren().add(btn);

		//自動右移動ボタン
		ConfigManager.setAutoRightMode(false);
		Node autoBtn = ConfigManager.getAutoMoveButton();
		autoBtn.setTranslateX(650);
		autoBtn.setTranslateY(60);
		getChildren().add(autoBtn);

		//三角形の作成
		nextTri = new Polygon(
				510,160,
				480,120,
				480,200
		);
		prevTri = new Polygon(
				30,160,
				60,120,
				60,200
		);
		nextTri.setCursor(Cursor.HAND);
		prevTri.setCursor(Cursor.HAND);
		nextTri.setFill(Color.gray(0.2));
		prevTri.setFill(Color.gray(0.2));
		nextTri.setStroke(Color.LIME);
		prevTri.setStroke(Color.LIME);
		nextTri.setOnMouseEntered(e->nextTri.setFill(Color.ORANGE));
		nextTri.setOnMouseExited(e->nextTri.setFill(Color.gray(0.2)));
		prevTri.setOnMouseEntered(e->prevTri.setFill(Color.ORANGE));
		prevTri.setOnMouseExited(e->prevTri.setFill(Color.gray(0.2)));
		nextTri.setOnMouseClicked(e->pager.nextPage());//pager.doInitNowPage()等を呼び出すことによってページ移動時にメッセージアニメーションを最初から流すことも可能です。
		prevTri.setOnMouseClicked(e->pager.prevPage());
		getChildren().add(prevTri);
		getChildren().add(nextTri);
		nextTri.setVisible(false);
		prevTri.setVisible(false);
	}

	public void process() {
		//ゲームの進行
		gm.process();

		//ページごとの処理
		switch(pager.getPageNo()) {
		case 0:page0Process();break;
		case 1:page1Process();break;
		case 2:page2Process();break;
		case 3:page3Process();break;
		case 4:page4Process();break;
		case 5:page5Process();break;
		case 6:page6Process();break;
		case 7:page7Process();break;
		case 8:page8Process();break;
		case 9:page9Process();break;
		case 10:page10Process();break;
		case 11:page11Process();break;
		case 12:page12Process();break;
		case 13:page13Process();break;
		}
	}

	void page0Process() {
		//何もしない
	}

	boolean isPage1Logic=false;
	void page1Process() {
		if ( !isPage1Logic ) {
			//自動ページ切り替え機能オフ
			pager.autoPageNextOff();
			isPage1Logic = true;
		}
		nextTri.setVisible(true);
		prevTri.setVisible(false);
		page2Object.setVisible(false);
	}

	boolean isPage2Logic=false;
	void page2Process() {
		if ( !isPage2Logic ) {
			isPage2Logic = true;
			gameObjects.getPlayer().getViewNode().setVisible(true);
			gameObjects.getPlayer().moveY(-300);
			PlayerControllerManager.setActive(true);
		}
		page2Object.setVisible(true);
		page2Object.setTranslateX(gameObjects.getPlayer().getCTX() - 125);
		page2Object.setTranslateY(gameObjects.getPlayer().getCTY() + 80);
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page3Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
		page2Object.setVisible(false);
	}

	void page4Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
		page2Object.setVisible(false);
	}

	void page5Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page6Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page7Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	boolean isPage8Logic=false;
	void page8Process() {
		if ( !isPage8Logic ) {
			factory.setFieldObject(FieldObject.RED_SLIME, 10, 8);
			factory.init();
			isPage8Logic = true;
		}
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	boolean isPage9Logic=false;
	void page9Process() {
		if ( !isPage9Logic ) {
			factory.setFieldObject(FieldObject.NEEDLE_LEAF, 4, 12);
			factory.setFieldObject(FieldObject.NEGI, 10, 10);
			factory.setFieldObject(FieldObject.BLOCK, 5, 10);
			factory.setFieldObject(FieldObject.BLOCK, 6, 10);
			factory.setFieldObject(FieldObject.BLOCK, 7, 10);
			factory.setFieldObject(FieldObject.BLOCK, 7, 11);
			factory.setFieldObject(FieldObject.BLOCK, 7, 12);
			factory.setFieldObject(FieldObject.BLOCK, 7, 13);
			factory.setFieldObject(FieldObject.BLOCK, 7, 14);
			factory.setFieldObject(FieldObject.BLOCK, 6, 14);
			factory.setFieldObject(FieldObject.BLOCK, 5, 14);
			factory.setFieldObject(FieldObject.NEGI, 10, 14);
			factory.init();
			isPage9Logic = true;
		}
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page10Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page11Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page12Process() {
		nextTri.setVisible(true);
		prevTri.setVisible(true);
	}

	void page13Process() {
		nextTri.setVisible(false);
		prevTri.setVisible(true);
	}

	//メインゲーム画面のセッティング
	GameManager createDescriptionGameManager() {

		//チュートリアルで表示するステージを構築
		FieldObject[][] stageData = new FieldObject[12][18];
		for ( int i = 0;i < stageData[11].length;i++ ) {
			stageData[11][i] = FieldObject.GROUND;
		}

		//右側は通常特殊なカメラ位置になるようになっているため意図的に壁を作成しておく
		for ( int i = 0;i < stageData.length;i++ ) {
			stageData[i][16] = FieldObject.BLOCK;
		}
		stageData[9][0] = FieldObject.GROUND;
		stageData[9][1] = FieldObject.GROUND;
		stageData[9][2] = FieldObject.GROUND;

		stageData[10][4] = FieldObject.GROUND;
		stageData[10][12] = FieldObject.GROUND;

		GameManager gm = new GameManager(stageData);
		FieldObjectAppearanceObserverFactory factory = gm.getFactory();

		//プレイヤーを強制座標移動
		Player player = factory.getFieldObjectManager().getPlayer();
		player.moveX(100);
		player.moveY(-100);

		//カメラの座標は強制的に固定させる(ファクトリは強制でプレイヤーターゲットのFixedTargetCamera2DFXを生成するので固定のターゲットでセットしなおす)
		FixedTargetCamera2DFX camera = (FixedTargetCamera2DFX) gm.getGameCamera();
		camera.setTarget(new CameraTargetPoint2D(600,300));//カメラの中心点を600,300に移動
		camera.update();

		//初期出現処理
		factory.init();

		return gm;
	}
}
