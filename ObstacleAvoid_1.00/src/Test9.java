
import java.util.Iterator;

import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameSceneFX;
import com.nompor.gtk.fx.GameViewGroupFX;
import com.nompor.gtk.fx.animation.PagingTextAnimationView;
import com.nompor.gtk.fx.animation.TextAnimationView;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Test9 extends Application {
	final int WIDTH = 800, HEIGHT = 600;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//画面の表示
		GTKManagerFX.changeView(new TestTitleView());
	}

	//タイトル画面
	@SuppressWarnings("deprecation")
	class TestTitleView extends GameViewGroupFX{

		TestGameView gameView = new TestGameView();
		{
			getChildren().add(gameView);

			Rectangle rect = new Rectangle(800,600);
			rect.setFill(Color.rgb(0, 0, 0, 0.5));
			getChildren().add(rect);

			//ゲーム画面へ遷移
			Text game = new Text("ゲーム画面へ");
			game.setFont(new Font(50));
			game.setOnMouseClicked(e->{
				GTKManagerFX.animationClear();
				GTKManagerFX.changeViewDefaultAnimation(new TestGameUIView());
			});
			game.setCursor(Cursor.HAND);
			game.setOnMouseEntered(e->game.setFill(Color.ORANGE));
			game.setOnMouseExited(e->game.setFill(Color.WHITE));
			game.setWrappingWidth(AppManager.getW());
			game.setTextAlignment(TextAlignment.CENTER);
			game.setTranslateY(100);
			game.setFill(Color.WHITE);
			getChildren().add(game);

			//説明画面へ遷移
			Text description = new Text("説明画面へ");
			description.setFont(new Font(50));
			description.setOnMouseClicked(e->{
				GTKManagerFX.animationClear();
				GTKManagerFX.changeViewDefaultAnimation(new TestDescriptionView());
			});
			description.setCursor(Cursor.HAND);
			description.setOnMouseEntered(e->description.setFill(Color.ORANGE));
			description.setOnMouseExited(e->description.setFill(Color.WHITE));
			description.setWrappingWidth(AppManager.getW());
			description.setTextAlignment(TextAlignment.CENTER);
			description.setTranslateY(300);
			description.setFill(Color.WHITE);
			getChildren().add(description);

			//フルスクリーンへ移行
			Text fullsc = new Text("フルスクリーン");
			fullsc.setFont(new Font(50));
			fullsc.setOnMouseClicked(e->{
				GTKManagerFX.setFullScreenWithResolution(!GTKManagerFX.isFullScreen());
			});
			fullsc.setCursor(Cursor.HAND);
			fullsc.setOnMouseEntered(e->fullsc.setFill(Color.ORANGE));
			fullsc.setOnMouseExited(e->fullsc.setFill(Color.WHITE));
			fullsc.setWrappingWidth(AppManager.getW());
			fullsc.setTextAlignment(TextAlignment.CENTER);
			fullsc.setTranslateY(500);
			fullsc.setFill(Color.WHITE);
			getChildren().add(fullsc);
		}
	}

	//ゲーム説明画面
	class TestDescriptionView extends GameViewGroupFX{
		TestGameView gameView = new TestGameView();
		{

			//ゲーム画面の追加
			getChildren().add(gameView);

			Font f = new Font(20);
			Color clr = new Color(1, 1, 1, 1);

			//テキスト枠
			Rectangle rect = new Rectangle(70, 60, 400, 200);
			rect.setArcWidth(10);
			rect.setArcHeight(10);
			rect.setFill(Color.rgb(0,0,80));
			rect.setStroke(Color.LIME);
			getChildren().add(rect);

			//ボタンを追加
			Button btn = new Button();
			btn.setText("タイトルへ");
			btn.setTranslateX(650);
			btn.setTranslateY(20);
			btn.setCursor(Cursor.HAND);
			btn.setOnAction(e -> {
				GTKManagerFX.animationClear();
				GTKManagerFX.changeViewDefaultAnimation(new TestTitleView());
			});
			btn.setFocusTraversable(false);//ボタンにフォーカスが当たるとキーイベントが正常に動かない
			getChildren().add(btn);

			//1ページずつメッセージの追加
			PagingTextAnimationView pager = new PagingTextAnimationView(90,95);
			int dur = 80;
			pager.add(new TextAnimationView(dur, "ゲームの説明をテストだよ。\n矢印をクリックしてページを進めてね。", clr, f));
			pager.add(new TextAnimationView(dur, "あいうえお。\n", clr, f));
			pager.add(new TextAnimationView(dur, "てすとてすと。\n", clr, f));
			pager.add(new TextAnimationView(dur, "ふんがー。\n", clr, f));
			pager.add(new TextAnimationView(dur, "働きたくないでござる。", clr, f));
			pager.doPlayNowPage();
			getChildren().add(pager);

			//三角形の作成(次のページと前のページへのボタン)
			Polygon nextTri = new Polygon(
					510,160,
					480,120,
					480,200
			);
			Polygon prevTri = new Polygon(
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
			nextTri.setOnMouseClicked(e->pager.nextPage());
			prevTri.setOnMouseClicked(e->{
				pager.prevPage();
				pager.doFinalNowPage();
			});
			getChildren().add(prevTri);
			getChildren().add(nextTri);
		}
	}

	//ゲーム画面(UI部分表示)
	class TestGameUIView extends GameViewGroupFX{
		boolean isEndOfView=false;
		TestGameView gameView = new TestGameView();
		{
			getChildren().add(gameView);
			setOnProcess(e ->{
				//ゲームループ

				//ゲームオーバーかゲームクリアを表示したら以降何もしないようにする
				if (isEndOfView) return;

				if ( gameView.isGameClear ) {

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

					//各種フェードアニメーションのセッティング
					FadeTransition fade1 = new FadeTransition(Duration.millis(2000),rect);
					fade1.setToValue(0.8);

					FadeTransition fade2 = new FadeTransition(Duration.millis(100), clearText);
					fade2.setToValue(1);

					Animation anime = new SequentialTransition(fade1, fade2);
					anime.play();
					isEndOfView = true;
				} else if ( gameView.isGameOver ) {

					//画面を白くする
					Rectangle rect = new Rectangle(0,0,AppManager.getW(),AppManager.getH());
					rect.setFill(Color.BLACK);
					rect.setOpacity(0);
					getChildren().add(rect);

					//ゲームオーバーテキストを表示
					Text clearText = new Text("Game Over");
					clearText.setTranslateX(80);
					clearText.setTranslateY(150);
					clearText.setFont(new Font(30));
					clearText.setFill(Color.RED);
					clearText.setOpacity(0);
					getChildren().add(clearText);

					//各種フェードアニメーションのセッティング
					FadeTransition fade1 = new FadeTransition(Duration.millis(2000),rect);
					fade1.setToValue(0.8);

					FadeTransition fade2 = new FadeTransition(Duration.millis(100), clearText);
					fade2.setToValue(1);

					Animation anime = new SequentialTransition(fade1, fade2);
					anime.play();
					isEndOfView = true;
				}
			});

			//画面上部の黒い領域を描画
			LinearGradient grad = new LinearGradient(0, 0, 0, 40, false, CycleMethod.NO_CYCLE, new Stop(0, Color.BLACK), new Stop(1, new Color(0, 0, 0, 0.5)));
			Rectangle rect = new Rectangle(0,0,AppManager.getW(),40);
			rect.setFill(grad);
			getChildren().add(rect);

			//タイトルに戻るはゲーム開始後に表示
			Text titleRet = new Text("タイトルに戻る");
			titleRet.setFont(new Font(18));
			titleRet.setFill(Color.WHITE);
			titleRet.setTranslateY(30);
			titleRet.setTranslateX(50);
			titleRet.setOnMouseClicked(e->{
				GTKManagerFX.animationClear();
				GTKManagerFX.changeViewDefaultAnimation(new TestTitleView());
			});
			titleRet.setCursor(Cursor.HAND);
			titleRet.setOnMouseEntered(e->titleRet.setFill(Color.ORANGE));
			titleRet.setOnMouseExited(e->titleRet.setFill(Color.WHITE));
			getChildren().add(titleRet);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if ( isEndOfView ) {
				GTKManagerFX.animationClear();
				GTKManagerFX.changeViewDefaultAnimation(new TestTitleView());
			}
		}
	}

	//ゲーム画面
	class TestGameView extends GameSceneFX{
		Player p;
		FieldObject[][] data;
		FixedTargetCamera2DFX camera;
		GameField field;
		FieldObjectAppearanceObserverFactory factory;
		FieldObjectManager objMng;
		boolean isGameClear=false;
		boolean isGameOver=false;
		TestGameView(){
			super(WIDTH,HEIGHT);
		}

		@Override
		public void start() {

			//ワールド領域1200*600に合わせて二次元配列構築
			final int MAX_W=1200,MAX_H=HEIGHT;
			final int ROW=MAX_H/50,COL=MAX_W/50;
			data = new FieldObject[ROW][COL];

			//画面一番下に地面ブロック配置
			final int UNDER_INDEX = (ROW-1);
			for ( int i = 0;i < COL;i++ ) {
				data[UNDER_INDEX][i] = FieldObject.GROUND;
			}
			data[UNDER_INDEX][10] = FieldObject.NONE;

			//オブジェクト出現ファクトリ
			factory = FieldObjectAppearanceObserverFactory.createFactory(this, data);
			factory.init();

			//表示リスト
			objMng = factory.getFieldObjectManager();

			//カメラ
			camera = factory.camera;

			//ステージデータ取得
			field = objMng.getField();

			//プレイヤーの作成
			p = objMng.getPlayer();
		}

		@Override
		public void process() {
			//ゲームループ

			//出現チェック
			factory.execute();

			//プレイヤー移動
			p.update();

			//プレイヤーとフィールド判定
			field.fieldCheck(p);

			//背景の動き
			Iterator<BackgroundScrollObject> backs = objMng.getBackgrounds();
			while(backs.hasNext()) {
				BackgroundScrollObject back = backs.next();
				back.update(camera);
			}

			//ゴール判定
			//プレイヤーがフィールドの右端まで辿り着いたかを判定する
			Bounds b = p.getViewNode().getBoundsInParent();
			if ( b.getMaxX() > objMng.getField().maxX - 50 ) {
				isGameClear = true;
			} else if ( b.getMinY() > objMng.getField().maxY ) {
				isGameOver = true;
			}
		}
	}
}
