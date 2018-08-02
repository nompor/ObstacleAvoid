import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TestAll extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//通常の表示画面を作成
		Group root = new Group();
		Scene scene = new Scene(root, 400, 300);
		Rectangle rect1 = new Rectangle(220,125,50,50);

		//赤色をSceneのGroupに追加
		rect1.setFill(Color.RED);
		root.getChildren().add(rect1);

		//SubSceneを作成し、Groupをルートにする
		Group grp = new Group();
		SubScene sub = new SubScene(grp,400,300);
		Rectangle rect2 = new Rectangle(220,125,50,50);

		//青色をSubSceneのGroupに追加
		rect2.setFill(Color.BLUE);
		grp.getChildren().add(rect2);

		//SubSceneにカメラをセットし、右へ動かす
		ParallelCamera c = new ParallelCamera();
		c.setTranslateX(100);
		sub.setCamera(c);

		root.getChildren().add(sub);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
