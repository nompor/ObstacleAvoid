
import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewFX;

import javafx.application.Application;
import javafx.stage.Stage;

public class Test4 extends Application {

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
			Enemy e;
			GameField fields;

			@Override
			public void start() {

				//ワールド領域2000*2000に合わせて二次元配列構築
				final int MAX_W=2000,MAX_H=1200;
				final int ROW=MAX_H/50,COL=MAX_W/50;
				Field[][] field = new Field[ROW][COL];

				//画面一番下に地面ブロック配置
				final int UNDER_INDEX = (ROW-1);
				for ( int i = 0;i < COL;i++ ) {
					field[UNDER_INDEX][i] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.GROUND, i * 50,UNDER_INDEX*50);
				}

				//ブロックはテスト用に適当に追加
				for ( int i = 0;i < 5;i++ ) {
					field[10][i] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.BLOCK, i * 50,10*50);
				}

				for ( int i = 6;i < 12;i++ ) {
					field[15][i] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.BLOCK, i * 50,15*50);
				}

				for ( int i = 18;i < 25;i++ ) {
					field[ROW-3][i] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.BLOCK, i * 50,(ROW-3)*50);
				}

				//GameFieldオブジェクトの構築
				fields = new GameField(field);
				Field[][] fieldList = fields.getFieldList();
				for ( int i = 0;i < fieldList.length;i++ ) {
					for ( int j = 0;j < fieldList[i].length;j++ ) {
						Field fld = fieldList[i][j];
						if ( fld != null ) {
							getChildren().add(fld.getViewNode());
						}
					}
				}

				//プレイヤーの作成
				p = FieldObjectAppearanceObserverFactory.createPlayer();

				//プレイヤーの表示
				getChildren().add(p.getViewNode());

				//敵の作成
				e = FieldObjectAppearanceObserverFactory.createEnemy(FieldObject.SLIME,300,50);

				//敵表示
				getChildren().add(e.getViewNode());

				//カメラのセッティング
				GTKManagerFX.setGameCamera(
						FixedTargetCamera2DFX.createRangeCamera(
								WIDTH, HEIGHT
								,p
								, 0, 0, MAX_W, MAX_H
						)
				);
			}

			@Override
			public void process() {
				//ゲームループ

				p.update();
				e.update();

				fields.fieldCheck(p);
				fields.fieldCheck(e);
			}
		});
	}

}
