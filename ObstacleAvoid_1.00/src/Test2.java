
import static com.nompor.gtk.fx.GTKManagerFX.*;

import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewFX;
import com.nompor.gtk.fx.animation.ImageAnimationView;
import com.nompor.gtk.fx.image.ImageManagerFX;

import javafx.animation.Animation;
import javafx.application.Application;
import javafx.scene.image.ImageView;
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

				//プレイヤーの作成
				ImageAnimationView view = createImageAnimationView(Duration.millis(500), imgMng.getImage("img/mogmol.png"), 50, 50);//表示ノード
				view.setCycleCount(Animation.INDEFINITE);
				view.setIndex(0);
				Rectangle rect = new Rectangle(10,510,30,37);//当たり判定
				view.setTranslateY(500);
				p = new Player(view, rect);
				view.play();

				//プレイヤーの表示
				getChildren().add(p.getViewNode());

				//敵の作成
				ImageAnimationView img = createImageAnimationView(Duration.millis(300), imgMng.getImage("img/slime.png"),50,50);//表示ノード
				img.setCycleCount(Animation.INDEFINITE);
				img.setIndex(0);
				Rectangle r = new Rectangle(300+8,550+26,35,20);//当たり判定
				img.setTranslateX(300);
				img.setTranslateY(550);
				e = new Slime(img, r);

				//敵表示
				getChildren().add(e.getViewNode());
			}

			@Override
			public void process() {
				//ゲームループ
				move(p);
				move(e);
			}

			private void move(CharaObject o) {
				o.moveX(1.5);
				o.moveY(7);

				//ウィンドウ表示領域より下に行ったら止まるようにする
				double y = o.getHitNode().getBoundsInParent().getMaxY();
				if(y>=HEIGHT) {
					o.moveY(-(y-HEIGHT));
					o.isGround = true;
				} else {
					o.isGround = false;
				}
				o.isAir = !o.isGround;
			}
		});
	}

}
