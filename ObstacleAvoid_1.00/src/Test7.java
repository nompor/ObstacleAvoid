
import java.util.Iterator;

import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameSceneFX;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Test7 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int WIDTH = 800, HEIGHT = 600;
		GTKManagerFX.start(primaryStage,WIDTH, HEIGHT);

		//GameSceneFXはSubSceneを継承したクラスで、processメソッドはゲームループの処理を実装する
		GTKManagerFX.changeView(new GameSceneFX(WIDTH,HEIGHT) {
			Player p;
			FieldObject[][] data;
			FixedTargetCamera2DFX camera;
			GameField field;
			FieldObjectAppearanceObserverFactory factory;
			FieldObjectManager objMng;
			Rectangle mouseRange = new Rectangle(30,30);

			@Override
			public void start() {

				//ワールド領域6000*600に合わせて二次元配列構築
				final int MAX_W=6000,MAX_H=HEIGHT;
				final int ROW=MAX_H/50,COL=MAX_W/50;
				data = new FieldObject[ROW][COL];

				//画面一番下に地面ブロック配置
				final int UNDER_INDEX = (ROW-1);
				for ( int i = 0;i < COL;i++ ) {
					data[UNDER_INDEX][i] = FieldObject.GROUND;
				}

				//穴を設置
				for ( int i = 50;i < 100;i++ ) {
					data[UNDER_INDEX][i] = FieldObject.NONE;
				}

				//敵の配置
				for ( int i = 47;i < 100;i++ ) {
					data[UNDER_INDEX-1][i] = FieldObject.BIRD;
				}
				data[UNDER_INDEX-2][10] = FieldObject.SLIME;

				//ブロックはテスト用に適当に追加
				for ( int i = 18;i < 25;i++ ) {
					data[ROW-3][i] = FieldObject.BLOCK;
				}
				//アイテム設置
				for ( int i = 20;i < 23;i++ ) {
					data[ROW-4][i] = FieldObject.NEGI;
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

				//マウスボタンが押されたか
				boolean isMouseAction = PlayerControllerManager.isMPressLeft();
				if ( isMouseAction ) {

					//マウス座標をワールド座標に変換して判定を作成
					mouseRange.setX(PlayerControllerManager.getMX()-15+camera.getLeft());
					mouseRange.setY(PlayerControllerManager.getMY()-15+camera.getTop());
				}

				//プレイヤー移動
				p.update();

				//プレイヤーとフィールド判定
				field.fieldCheck(p);

				//敵の動き
				Iterator<Enemy> enemys = objMng.getEnemys();
				while(enemys.hasNext()) {
					Enemy enemy = enemys.next();
					enemy.update();
					field.fieldCheck(enemy);

					//プレイヤーと敵が当たった
					if ( p.isHit(enemy) ) {
						//プレイヤーが敵を踏んだ場合は敵にダメージを与え、そうでなければプレイヤーがダメージを受ける
						if ( enemy.pret >= p.preb ) {
							enemy.onDamage();
							//敵を踏んだ場合はジャンプ
							if ( PlayerControllerManager.isJumping() ) {
								//ジャンプボタンを押し続けている場合は高くジャンプさせる
								p.fallSpeed = -18;
							} else {
								p.fallSpeed = -10;
							}


						} else if (!p.isInvisible()) {
							AppManager.playSE("se1");
							p.onDamage();
							//プレイヤーを強制削除
							p.getViewNode().setVisible(false);
							PlayerControllerManager.setActive(false);
							Bounds b = p.getViewNode().getBoundsInParent();
							Effect ef = factory.createEffect(Math.round(b.getMinX() + b.getWidth() / 2), Math.round(b.getMinY() + b.getHeight() / 2));
							objMng.addEffect(ef);
						}
					}

					if ( !isObjectInWindow(enemy) ) {
						enemys.remove();
					} else if ( !enemy.isAlive() ) {
						//敵が倒された時は効果音とエフェクトを処理
						AppManager.playSE("se2");
						Bounds b = enemy.getViewNode().getBoundsInParent();
						Effect ef = factory.createEffect(Math.round(b.getMinX() + b.getWidth() / 2), Math.round(b.getMinY() + b.getHeight() / 2));
						objMng.addEffect(ef);
						enemys.remove();
					}
				}

				//アイテムの動き（画面範囲外、取得された場合は削除する）
				Iterator<Item> items = objMng.getItems();
				while(items.hasNext()) {
					Item item = items.next();
					item.update();
					//プレイヤーとアイテムが当たった
					if ( p.isHit(item) ) {
						p.speed++;
						item.isAlive = false;
						AppManager.playSE("se3");
					}
					if ( !isObjectInWindow(item) ) {
						items.remove();
					} else if ( !item.isAlive() ) {
						items.remove();
					}
				}

				//ブロックの動き（画面範囲外、破壊の場合は削除する）
				Iterator<Field> fields = objMng.getFields();
				while(fields.hasNext()) {
					Field blk = fields.next();
					blk.update();
					mouseAction(blk, isMouseAction);
					if ( !isObjectInWindow(blk) ) {
						fields.remove();
					} else if ( !blk.isAlive() ) {
						AppManager.playSE("se2");
						Bounds b = blk.getViewNode().getBoundsInParent();
						Effect ef = factory.createEffect(Math.round(b.getMinX() + b.getWidth() / 2), Math.round(b.getMinY() + b.getHeight() / 2));
						objMng.addEffect(ef);
						fields.remove();
					}
				}

				//エフェクトの動き
				Iterator<Effect> effects = objMng.getEffects();
				while(effects.hasNext()) {
					Effect effect = effects.next();
					effect.update();
					if ( !isObjectInWindow(effect) ) {
						effects.remove();
					} else if ( !effect.isAlive() ) {
						effects.remove();
					}
				}

				//背景の動き
				Iterator<BackgroundScrollObject> backs = objMng.getBackgrounds();
				while(backs.hasNext()) {
					BackgroundScrollObject back = backs.next();
					back.update(camera);
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

			//マウスイベント
			public void mouseAction(GameObject go, boolean isMouseAction) {
				if(isMouseAction && go.getHitNode() != null && mouseRange.intersects(go.getHitNode().getBoundsInParent())) {
					go.onMouseAction();
				}
			}
		});
	}
}
