

import java.util.Iterator;

import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GameSceneFX;

import javafx.geometry.Bounds;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.shape.Rectangle;

//ゲームの進行を行うクラス
public class GameManager extends GameSceneFX{

	FieldObjectAppearanceObserverFactory factory;
	FixedTargetCamera2DFX camera;
	int frameCounter;
	int negiCount;
	boolean isGoal;
	boolean isStart;
	boolean isGameOver;
	Rectangle mouseRange = new Rectangle(30,30);

	public GameManager(FieldObject[][] stageData) {
		//サイズはウィンドウと同じ
		super(AppManager.getW(),AppManager.getH());

		//ファクトリの作成
		factory = FieldObjectAppearanceObserverFactory.createFactory(this, stageData);

		//カメラは頻繁にアクセスするのでメンバ変数に保持しておく
		camera = (FixedTargetCamera2DFX) getGameCamera();

		this.setEffect(new ColorAdjust(0, 0, 0, 0));
	}

	//初期処理
	public void init() {
		factory.init();
	}

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

		//全フィールドオブジェクトを取得
		FieldObjectManager objects = factory.getFieldObjectManager();

		//プレイヤーの動作命令
		Player player = objects.getPlayer();
		mouseAction(player, isMouseAction);
		player.update();

		//プレイヤーとフィールド判定
		GameField fieldGround = objects.getField();
		fieldGround.fieldCheck(player);

		//敵の動き
		Iterator<Enemy> enemys = objects.getEnemys();
		while(enemys.hasNext()) {
			Enemy enemy = enemys.next();
			enemy.update();
			fieldGround.fieldCheck(enemy);

			//プレイヤーと敵が当たった
			if ( player.isHit(enemy) ) {
				//プレイヤーが敵を踏んだ場合は敵にダメージを与え、そうでなければプレイヤーがダメージを受ける
				if ( enemy.pret >= player.preb ) {
					enemy.onDamage();
					//敵を踏んだ場合はジャンプ
					if ( PlayerControllerManager.isJumping() ) {
						player.fallSpeed = -18;
					} else {
						player.fallSpeed = -10;
					}


				} else if (!player.isInvisible()) {
					player.onDamage();
					AppManager.playSE("se1");
				}
			}

			mouseAction(enemy, isMouseAction);

			if ( !isObjectInWindow(enemy) ) {
				enemys.remove();
			} else if ( !enemy.isAlive() ) {
				//敵が倒された時は効果音とエフェクトを処理
				AppManager.playSE("se2");
				Bounds b = enemy.getViewNode().getBoundsInParent();
				Effect ef = factory.createEffect(Math.round(b.getMinX() + b.getWidth() / 2), Math.round(b.getMinY() + b.getHeight() / 2));
				objects.addEffect(ef);
				enemys.remove();
			}
		}

		//アイテムの動き（画面範囲外、取得された場合は削除する）
		Iterator<Item> items = objects.getItems();
		while(items.hasNext()) {
			Item item = items.next();
			item.update();
			//プレイヤーとアイテムが当たった
			if ( player.isHit(item) ) {
				player.speed++;
				item.isAlive = false;
				AppManager.playSE("se3");
				negiCount++;
			}
			mouseAction(item, isMouseAction);
			if ( !isObjectInWindow(item) ) {
				items.remove();
			} else if ( !item.isAlive() ) {
				if ( item.isClick ) {
					player.speed++;
					AppManager.playSE("se3");
					negiCount++;
				}
				items.remove();
			}
		}

		//ブロックの動き（画面範囲外、破壊の場合は削除する）
		Iterator<Field> fields = objects.getFields();
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
				objects.addEffect(ef);
				fields.remove();
			}
		}

		//エフェクトの動き
		Iterator<Effect> effects = objects.getEffects();
		while(effects.hasNext()) {
			Effect effect = effects.next();
			effect.update();
			if ( !isObjectInWindow(effect) ) {
				effects.remove();
			} else if ( !effect.isAlive() ) {
				effects.remove();
			}
		}

		//カメラ座標の更新
		updateCamera();

		//背景の動き
		Iterator<BackgroundScrollObject> backs = objects.getBackgrounds();
		while(backs.hasNext()) {
			BackgroundScrollObject back = backs.next();
			back.update(camera);
		}

		//ゴール判定
		//プレイヤーがフィールドの右端まで辿り着いたかを判定する
		Bounds b = player.getViewNode().getBoundsInParent();
		if ( b.getMaxX() > objects.getField().maxX - 50 ) {
			isGoal = true;
		} else if ( b.getMinY() > objects.getField().maxY ) {
			isGameOver = true;
		} else {
			//経過フレームを加算
			if ( !isGoal && isStart ) frameCounter++;
		}
	}

	//ファクトリの取得
	public FieldObjectAppearanceObserverFactory getFactory() {
		return factory;
	}

	//フレームカウント数
	public int getFrameCount() {
		return frameCounter;
	}

	//ネギ取得数
	public int getNegiCount() {
		return negiCount;
	}

	//ゲームクリアかどうか
	public boolean isGameClear() {
		return isGoal;
	}

	//ゲームオーバーかどうか
	public boolean isGameOver() {
		return isGameOver;
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

	//開始フラグ
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	//マウスイベント
	public void mouseAction(GameObject go, boolean isMouseAction) {
		if(isMouseAction && go.getHitNode() != null && mouseRange.intersects(go.getHitNode().getBoundsInParent())) {
			go.onMouseAction();
		}
	}
}
