import java.io.File;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Test1 extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//ウィンドウ表示領域800*600に合わせて二次元配列構築
		final int WIDTH = 800, HEIGHT = 600;
		final int ROW=HEIGHT/50,COL=WIDTH/50;
		Field[][] field = new Field[ROW][COL];

		//画面一番下に地面ブロック配置
		final int UNDER_INDEX = (ROW-1);
		for ( int i = 0;i < COL;i++ ) {
			Rectangle rect = new Rectangle(i * 50,UNDER_INDEX*50,50,50);//当たり判定Node
			ImageView imgView = new ImageView(new File("img/jimen.png").toURI().toString());//表示Node
			imgView.setTranslateX(rect.getX());
			imgView.setTranslateY(rect.getY());
			field[UNDER_INDEX][i] = new Block(imgView, rect);
		}

		//画面一番下から二つ上にいくつかブロック配置
		final int BLOCK_INDEX = (ROW-3);
		for ( int i = 5;i < 11;i++ ) {
			Rectangle rect = new Rectangle(i * 50,BLOCK_INDEX*50,50,50);//当たり判定Node
			ImageView imgView = new ImageView(new File("img/block.png").toURI().toString());//表示Node
			imgView.setTranslateX(rect.getX());
			imgView.setTranslateY(rect.getY());
			field[BLOCK_INDEX][i] = new Block(imgView, rect);
		}

		//装飾を追加
		ImageView imgView = new ImageView(new File("img/kusa.png").toURI().toString());//表示Node
		final int idxRow = UNDER_INDEX-1,idxCol = 3;
		imgView.setTranslateX(idxCol*50);
		imgView.setTranslateY(idxRow*50);
		field[UNDER_INDEX-1][3] = new Field(imgView);

		//GameFieldオブジェクトの構築
		GameField fields = new GameField(field);
		Field[][] fieldList = fields.getFieldList();
		Group jxGrp = new Group();
		for ( int i = 0;i < fieldList.length;i++ ) {
			for ( int j = 0;j < fieldList[i].length;j++ ) {
				Field fld = fieldList[i][j];
				if ( fld != null ) {
					jxGrp.getChildren().add(fld.getViewNode());
				}
			}
		}

		Scene scene = new Scene(jxGrp, WIDTH, HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.show();

		//キャラクターとの判定
		//CharaObject ch = ...;
		//fields.fieldCheck(ch);
	}
}
