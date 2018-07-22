
import java.util.Iterator;

import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewFX;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Test5 extends Application {

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
			FieldObjectManager objMng;
			FixedTargetCamera2DFX camera;
			Rectangle rect;

			@Override
			public void start() {

				//ワールド領域2000*600に合わせて二次元配列構築
				final int MAX_W=2000,MAX_H=HEIGHT;
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

				//敵
				data[ROW-3][30] = FieldObject.SLIME;

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
					}
				}
				GameField field = new GameField(fields);

				//プレイヤーの作成
				p = FieldObjectAppearanceObserverFactory.createPlayer();

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

				//ズームアウトしておく
				camera.setTranslateZ(-800);

				objMng = new FieldObjectManager(getChildren(), p, field);
				initCheck();
			}

			@Override
			public void process() {
				//ゲームループ

				GameField field = objMng.getField();

				//出現チェック
				check();

				p.update();

				field.fieldCheck(p);


				//敵の動き
				Iterator<Enemy> enemys = objMng.getEnemys();
				while(enemys.hasNext()) {
					Enemy enemy = enemys.next();
					enemy.update();
					field.fieldCheck(enemy);

					if ( !isObjectInWindow(enemy) ) {
						enemys.remove();
					}
				}

				//アイテムの動き（画面範囲外、取得された場合は削除する）
				Iterator<Item> items = objMng.getItems();
				while(items.hasNext()) {
					Item item = items.next();
					item.update();
					if ( !isObjectInWindow(item) ) {
						items.remove();
					}
				}

				//ブロックの動き（画面範囲外、破壊の場合は削除する）
				Iterator<Field> fields = objMng.getFields();
				while(fields.hasNext()) {
					Field blk = fields.next();
					blk.update();
					if ( !isObjectInWindow(blk) ) {
						fields.remove();
					}
				}

				//カメラ座標に緑領域を移動
				rect.setTranslateX(camera.getTranslateX());
				rect.setTranslateY(camera.getTranslateY());
			}


			//初期データ生成メソッド（カメラ付近の領域に存在するすべてを出現チェックする）
			public void initCheck() {

				//生成したオブジェクトはFieldObjectManagerに追加されていく
				double maxX = camera.getRight();

				//左右の追加対象インデックスを取得
				int addChkIdxX_R = (int) (maxX + 100) / FieldObject.W;

				//配列のインデックスが限界値を越えていた場合は端のインデックスに変更
				if ( addChkIdxX_R < 0 ) addChkIdxX_R = 0;
				if ( addChkIdxX_R >= data[0].length ) addChkIdxX_R = data[0].length - 1;

				//出現すべきであるなら出現処理を行います
				for ( int i = 0;i < data.length;i++ ) {
					int y=i*FieldObject.H;
					for ( int j = 0;j <= addChkIdxX_R;j++ ) {
						FieldObject fo = data[i][j];
						if (fo != null) {
							int x = j*FieldObject.W;
							switch(fo.TYPE) {
							case BLOCK:
							case FIELD:
								//ブロックは既に生成済みであるためGameFieldから取得して表示処理を要求
								Field fld = objMng.getField().getField(i, j);
								if ( !fld.isAlive && !fld.isDelete ) {
									objMng.addField(fld);
								}
								break;
							case ENEMY:
								//敵なら新たに生成して表示処理
								objMng.addEnemy(FieldObjectAppearanceObserverFactory.createEnemy(fo, x, y));

								//次回以降表示されないようにnull代入
								data[i][j] = null;
								break;
							case ITEM:
								//アイテムなら新たに生成して表示処理
								objMng.addItem(FieldObjectAppearanceObserverFactory.createItem(fo, x, y));

								//次回以降表示されないようにnull代入
								data[i][j] = null;
								break;
							default:
								break;
							}
						}
					}
				}
			}

			//オブジェクト生成チェックメソッド
			public void check() {

				//生成したオブジェクトはFieldObjectManagerに追加されていく
				double maxX = camera.getRight();

				//左右の追加対象インデックスを取得
				int addChkIdxX_R = (int) (maxX + 100) / FieldObject.W;

				//左右の配列のインデックスの座標を計算
				int x_R=addChkIdxX_R*FieldObject.W;

				//配列のインデックスが限界値を越えていた場合は負の数を代入し、処理無効とする
				if ( addChkIdxX_R >= data[0].length ) addChkIdxX_R = -1;

				//出現すべきであるなら出現処理を行います
				for ( int i = 0;i < data.length;i++ ) {
					FieldObject fo_R = addChkIdxX_R < 0 ? null : data[i][addChkIdxX_R];
					int y=i*FieldObject.H;

					if ( fo_R != null ) {
						switch(fo_R.TYPE) {
						case BLOCK:
						case FIELD:
							//ブロックやフィールドが表示されていなくて、破壊済みでない場合は追加
							Field fld = objMng.getField().getField(i, addChkIdxX_R);
							if ( !fld.isAlive && !fld.isDelete ) {
								objMng.addField(fld);
							}
							break;
						case ENEMY:
							objMng.addEnemy(FieldObjectAppearanceObserverFactory.createEnemy(fo_R, x_R, y));

							//次回以降表示されないようにnull代入
							data[i][addChkIdxX_R] = null;
							break;
						case ITEM:
							//アイテムなら新たに生成して表示処理
							objMng.addItem(FieldObjectAppearanceObserverFactory.createItem(fo_R, x_R, y));

							//次回以降表示されないようにnull代入
							data[i][addChkIdxX_R] = null;
						default:
							break;
						}
					}
				}
			}

			//ゲームオブジェクトが画面内かどうか
			public boolean isObjectInWindow(GameObject go) {
				double l = camera.getLeft() - 200;
				double t = camera.getTop() - 200;
				double r = camera.getRight() + 200;
				double b = camera.getBottom() + 200;
				Bounds bounds = go.getNode().getBoundsInParent();
				return bounds.getMaxX() < r
				&& bounds.getMinY() > t
				&& bounds.getMinX() > l
				&& bounds.getMaxY() < b;
			}
		});
	}

}
