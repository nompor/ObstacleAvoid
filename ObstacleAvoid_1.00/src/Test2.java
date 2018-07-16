
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewFX;
import com.nompor.gtk.fx.animation.ImageAnimationView;
import com.nompor.gtk.fx.image.ImageManagerFX;
import com.nompor.gtk.fx.input.KeyCodeManagerFX;

import javafx.animation.Animation;
import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Test2 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//GameViewFXはGroupを継承したクラスで、processメソッドはゲームループの処理を実装する
		GTKManagerFX.changeView(new GameViewFX() {

			ImageManagerFX imgMng = GTKManagerFX.getImageManager();
			KeyCodeManagerFX keyMng = GTKManagerFX.getKeyCodeManager();
			Player p;
			GameField fields;

			@Override
			public void start() {

				//ウィンドウ表示領域800*600に合わせて二次元配列構築
				final int ROW=HEIGHT/50,COL=WIDTH/50;
				Field[][] field = new Field[ROW][COL];

				//画面一番下に地面ブロック配置
				final int UNDER_INDEX = (ROW-1);
				for ( int i = 0;i < COL;i++ ) {
					Rectangle rect = new Rectangle(i * 50,UNDER_INDEX*50,50,50);//当たり判定Node
					ImageView imgView = new ImageView(imgMng.getImage("img/jimen.png"));//表示Node
					imgView.setTranslateX(rect.getX());
					imgView.setTranslateY(rect.getY());
					field[UNDER_INDEX][i] = new Block(imgView, rect);
				}

				//画面一番下から二つ上にいくつかブロック配置
				final int BLOCK_INDEX = (ROW-3);
				for ( int i = 5;i < 11;i++ ) {
					Rectangle rect = new Rectangle(i * 50,BLOCK_INDEX*50,50,50);//当たり判定Node
					ImageView imgView = new ImageView(imgMng.getImage("img/block.png"));//表示Node
					imgView.setTranslateX(rect.getX());
					imgView.setTranslateY(rect.getY());
					field[BLOCK_INDEX][i] = new Block(imgView, rect);
				}

				//装飾を追加
				ImageView imgView = new ImageView(imgMng.getImage("img/kusa.png"));//表示Node
				final int idxRow = UNDER_INDEX-1,idxCol = 3;
				imgView.setTranslateX(idxCol*50);
				imgView.setTranslateY(idxRow*50);
				field[UNDER_INDEX-1][3] = new Field(imgView);

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

				//プレイヤーキャラ作成
				ImageAnimationView view = GTKManagerFX.createImageAnimationView(Duration.millis(500), imgMng.getImage("img/mogmol.png"), 50, 50);//表示ノード
				view.setCycleCount(Animation.INDEFINITE);
				view.setIndex(0);
				Rectangle rect = new Rectangle(10,10,30,37);//当たり判定
				p = new Player(view, rect);

				//プレイヤーの表示
				getChildren().add(p.getViewNode());

				//キーの登録
				keyMng.regist(KeyCode.LEFT);
				keyMng.regist(KeyCode.RIGHT);
				keyMng.regist(KeyCode.Z);
			}

			@Override
			public void process() {
				//ゲームループ

				if ( keyMng.isPress(KeyCode.Z)) {
					//ジャンプしたなら落下速度を逆にする
					p.fallSpeed = -15;
				} else {
					//落下速度アップ
					p.fallSpeed++;
				}
				p.moveY(p.fallSpeed);//上下移動
				if ( keyMng.isDown(KeyCode.LEFT) ) {
					//左へ移動
					p.moveX(-3);
				} else if ( keyMng.isDown(KeyCode.RIGHT) ) {
					//右へ移動
					p.moveX(3);
				}

				//ウィンドウ表示領域より下に行ったら止まるようにする
				double y = p.getHitNode().getBoundsInParent().getMaxY();
				if(y>=HEIGHT) {
					p.moveY(-(y-HEIGHT));
				}
			}
		});
	}

}
