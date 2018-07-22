
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewFX;

import javafx.application.Application;
import javafx.stage.Stage;

public class Test3 extends Application {

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

				//ウィンドウ表示領域800*600に合わせて二次元配列構築
				final int ROW=HEIGHT/50,COL=WIDTH/50;
				Field[][] field = new Field[ROW][COL];

				//画面一番下に地面ブロック配置
				final int UNDER_INDEX = (ROW-1);
				for ( int i = 0;i < COL;i++ ) {
					field[UNDER_INDEX][i] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.GROUND, i * 50,UNDER_INDEX*50);
				}

				{
					field[10][5] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.BLOCK, 5 * 50,10*50);
					field[10][12] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.BLOCK, 12 * 50,10*50);
					field[9][12] = FieldObjectAppearanceObserverFactory.createBlock(FieldObject.BLOCK, 12 * 50,9*50);
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
			}

			@Override
			public void process() {
				//ゲームループ

				//キャラの移動
				p.update();
				e.update();

				//キャラとブロック判定
				fields.fieldCheck(p);
				fields.fieldCheck(e);
			}
		});
	}

}
