
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewFX;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Test6 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//GameViewFXはGroupを継承したクラスで、processメソッドはゲームループの処理を実装する
		GTKManagerFX.changeView(new GameViewFX() {
			Player p;
			FieldObject[][] data;
			FixedTargetCamera2DFX camera;
			Rectangle rect;
			GameField field;
			BackgroundScrollObject sora;
			BackgroundScrollObject ki;

			@Override
			public void start() {

				//ワールド領域2000*600に合わせて二次元配列構築
				final int MAX_W=3000,MAX_H=HEIGHT;
				final int ROW=MAX_H/50,COL=MAX_W/50;
				data = new FieldObject[ROW][COL];

				//画面一番下に地面ブロック配置
				final int UNDER_INDEX = (ROW-1);
				for ( int i = 0;i < COL;i++ ) {
					data[UNDER_INDEX][i] = FieldObject.GROUND;
				}

				//ブロックはテスト用に適当に追加
				for ( int i = 18;i < 25;i++ ) {
					data[ROW-3][i] = FieldObject.BLOCK;
				}

				//ブロック系オブジェクトの作成
				Field[][] fields = new Field[data.length][data[0].length];
				for ( int i = 0;i < data.length;i++ ) {
					int y=i*FieldObject.H;
					for ( int j = 0;j < data[i].length;j++ ) {
						int x=j*FieldObject.W;
						if ( data[i][j] == null ) continue;
						switch(data[i][j].TYPE) {
						case BLOCK:
							fields[i][j] = FieldObjectAppearanceObserverFactory.createBlock(data[i][j], x, y);
							break;
						case FIELD:
							fields[i][j] = FieldObjectAppearanceObserverFactory.createField(data[i][j], x, y);
							break;
						default:
							break;
						}
						if ( fields[i][j] != null ) {
							getChildren().add(fields[i][j].getViewNode());
						}
					}
				}
				field = new GameField(fields);

				//プレイヤーの作成
				p = FieldObjectAppearanceObserverFactory.createPlayer();

				//プレイヤーの表示
				getChildren().add(p.getViewNode());

				//カメラのセッティング
				GTKManagerFX.setGameCamera(camera =
						FixedTargetCamera2DFX.createRangeCamera(
								WIDTH, HEIGHT
								,p
								, 0, 0, MAX_W, MAX_H
						)
				);

				//実際の表示領域
				rect = new Rectangle(WIDTH, HEIGHT);
				rect.setFill(Color.GREEN);
				rect.setOpacity(0.3);
				getChildren().add(rect);

				//背景の作成
				sora = FieldObjectAppearanceObserverFactory.createBackground("sora", 0.7, -100, 0, 1000, 0.1);
				ki = FieldObjectAppearanceObserverFactory.createBackground("ki", 0.2, 000, 350, 10,0);
				getChildren().add(sora.getViewNode());
				getChildren().add(ki.getViewNode());

				//ズームアウトしておく
				camera.setTranslateZ(-1500);
			}

			@Override
			public void process() {

				p.update();

				field.fieldCheck(p);

				sora.update(camera);
				ki.update(camera);

				//カメラ座標に緑領域を移動
				rect.setTranslateX(camera.getTranslateX());
				rect.setTranslateY(camera.getTranslateY());
			}
		});
	}

}
