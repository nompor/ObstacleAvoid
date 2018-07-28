
import java.util.Iterator;

import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameSceneFX;
import com.nompor.gtk.fx.GameViewGroupFX;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Test8 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		//フラグ保持構造体
		class Flg {
			boolean isGameClear=false;
			boolean isGameOver=false;
			boolean isEndOfView=false;
		}
		Flg flg = new Flg();
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//ゲーム画面
		//GameSceneFXはJavaFXのSubSceneを継承したクラスで、processメソッドはゲームループの処理を実装する
		GameSceneFX gameView = new GameSceneFX(WIDTH,HEIGHT) {
			Player p;
			FieldObject[][] data;
			FixedTargetCamera2DFX camera;
			GameField field;
			FieldObjectAppearanceObserverFactory factory;
			FieldObjectManager objMng;

			@Override
			public void start() {

				//ワールド領域3000*600に合わせて二次元配列構築
				final int MAX_W=3000,MAX_H=HEIGHT;
				final int ROW=MAX_H/50,COL=MAX_W/50;
				data = new FieldObject[ROW][COL];

				//画面一番下に地面ブロック配置
				final int UNDER_INDEX = (ROW-1);
				for ( int i = 0;i < COL;i++ ) {
					data[UNDER_INDEX][i] = FieldObject.GROUND;
				}
				data[UNDER_INDEX][20] = FieldObject.NONE;

				//ブロックはテスト用に適当に追加
				for ( int i = 18;i < 25;i++ ) {
					data[ROW-3][i] = FieldObject.BLOCK;
				}

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
					flg.isGameClear = true;
				} else if ( b.getMinY() > objMng.getField().maxY ) {
					flg.isGameOver = true;
				}
			}
		};

		//ゲームオーバー、クリア画面
		//GameViewGroupFXはJavaFXのGroupを継承したクラスであり、子要素のGameView系オブジェクトに自動でイベント伝番するクラス
		GameViewGroupFX gameOverOrClearView = new GameViewGroupFX();
		gameOverOrClearView.setOnProcess(e -> {
			//ゲームループ

			//ゲームオーバーかゲームクリアを表示したら以降何もしないようにする
			if (flg.isEndOfView) return;

			if ( flg.isGameClear ) {

				//操作無効化
				PlayerControllerManager.setActive(false);

				//画面を暗くする
				Rectangle rect = new Rectangle(0,0,AppManager.getW(),AppManager.getH());
				rect.setFill(Color.BLACK);
				rect.setOpacity(0);
				gameOverOrClearView.getChildren().add(rect);

				//ステージクリアテキストを表示
				Text clearText = new Text("Stage Clear!!");
				clearText.setTranslateX(80);
				clearText.setTranslateY(150);
				clearText.setFont(new Font(30));
				clearText.setFill(Color.YELLOW);
				clearText.setOpacity(0);
				gameOverOrClearView.getChildren().add(clearText);

				//各種フェードアニメーションのセッティング
				FadeTransition fade1 = new FadeTransition(Duration.millis(2000),rect);
				fade1.setToValue(0.8);

				FadeTransition fade2 = new FadeTransition(Duration.millis(100), clearText);
				fade2.setToValue(1);

				Animation anime = new SequentialTransition(fade1, fade2);
				anime.play();
				flg.isEndOfView = true;
			} else if ( flg.isGameOver ) {

				//キー操作無効化
				PlayerControllerManager.setActive(false);

				//画面を白くする
				Rectangle rect = new Rectangle(0,0,AppManager.getW(),AppManager.getH());
				rect.setFill(Color.BLACK);
				rect.setOpacity(0);
				gameOverOrClearView.getChildren().add(rect);

				//ゲームオーバーテキストを表示
				Text clearText = new Text("Game Over");
				clearText.setTranslateX(80);
				clearText.setTranslateY(150);
				clearText.setFont(new Font(30));
				clearText.setFill(Color.RED);
				clearText.setOpacity(0);
				gameOverOrClearView.getChildren().add(clearText);

				//各種フェードアニメーションのセッティング
				FadeTransition fade1 = new FadeTransition(Duration.millis(2000),rect);
				fade1.setToValue(0.8);

				FadeTransition fade2 = new FadeTransition(Duration.millis(100), clearText);
				fade2.setToValue(1);

				Animation anime = new SequentialTransition(fade1, fade2);
				anime.play();
				flg.isEndOfView = true;
			}
		});

		//ゲーム画面をベースのゲームオーバーやクリアを表示する画面に追加
		gameOverOrClearView.getChildren().add(gameView);

		//画面の表示
		GTKManagerFX.changeView(gameOverOrClearView);
	}

}
