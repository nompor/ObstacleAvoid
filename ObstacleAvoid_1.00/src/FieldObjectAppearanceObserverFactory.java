

import static com.nompor.gtk.fx.GTKManagerFX.*;

import com.nompor.gtk.fx.FixedTargetCamera2DFX;
import com.nompor.gtk.fx.GameSceneFX;
import com.nompor.gtk.fx.animation.ImageAnimationView;

import javafx.animation.Animation;
import javafx.scene.PerspectiveCamera;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

//ゲーム内のフィールドにあらかじめ設定されたオブジェクトを生成するクラス
public class FieldObjectAppearanceObserverFactory {
	FieldObjectManager objMng;
	FieldObject[][] data;
	FixedTargetCamera2DFX camera;

	private FieldObjectAppearanceObserverFactory(FixedTargetCamera2DFX camera,FieldObjectManager objMng, FieldObject[][] data) {
		this.objMng = objMng;
		this.data = data;
		this.camera = camera;
	}


	//初期データ生成メソッド（カメラ付近の領域に存在するすべてを出現チェックする）
	public void init() {
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
						objMng.addEnemy(createEnemy(fo, x, y));

						//次回以降表示されないようにnull代入
						data[i][j] = null;
						break;
					case ITEM:
						//アイテムなら新たに生成して表示処理
						objMng.addItem(createItem(fo, x, y));

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
	public void execute() {

		//生成したオブジェクトはFieldObjectManagerに追加されていく
		FixedTargetCamera2DFX camera = (FixedTargetCamera2DFX) this.camera;
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
					objMng.addEnemy(createEnemy(fo_R, x_R, y));

					//次回以降表示されないようにnull代入
					data[i][addChkIdxX_R] = null;
					break;
				case ITEM:
					//アイテムなら新たに生成して表示処理
					objMng.addItem(createItem(fo_R, x_R, y));

					//次回以降表示されないようにnull代入
					data[i][addChkIdxX_R] = null;
				default:
					break;
				}
			}
		}
	}


	//フィールドに存在するオブジェクトを集約したオブジェクトを取得します
	public FieldObjectManager getFieldObjectManager() {
		return objMng;
	}

	//FieldObjectが表す配置情報から実オブジェクトを生成し、表示するためのオブジェクトを作成する
	public static FieldObjectAppearanceObserverFactory createFactory(GameSceneFX view, FieldObject[][] data) {
		//ブロック系オブジェクトの作成
		Field[][] fields = new Field[data.length][data[0].length];
		for ( int i = 0;i < data.length;i++ ) {
			int y=i*FieldObject.H;
			for ( int j = 0;j < data[i].length;j++ ) {
				int x=j*FieldObject.W;
				if ( data[i][j] == null ) continue;
				switch(data[i][j].TYPE) {
				case BLOCK:
					fields[i][j] = createBlock(data[i][j], x, y);
					break;
				case FIELD:
					fields[i][j] = createField(data[i][j], x, y);
					break;
				default:
					break;
				}
			}
		}
		GameField field = new GameField(fields);

		//プレイヤーを生成
		Player player = createPlayer();

		//デフォルトカメラのセッティング
		//カメラスクロールはプレイヤーを中心にする
		FixedTargetCamera2DFX camera = new FixedTargetCamera2DFX(new PerspectiveCamera(), AppManager.getW(), AppManager.getH(), player);

		//カメラの移動範囲はマップ内に制限
		camera.setMoveRange(true);
		camera.setMinX(0);
		camera.setMinY(0);
		camera.setMaxX(data[0].length * FieldObject.W - 100);//ゴール領域の分はカメラを移動不能にする
		camera.setMaxY(data.length * FieldObject.H);
		view.setGameCamera(camera);

		//カメラの座標をずらして更新しておく
		camera.setOffsetX(300);
		camera.update();

		FieldObjectManager objMng = new FieldObjectManager(view.getChildren(), player, field);
		FieldObjectAppearanceObserverFactory factory = new FieldObjectAppearanceObserverFactory(camera,objMng,data);

		//背景オブジェクトの挿入
		objMng.addBackground(createBackground("sora", 0.7, -100, 0, 1000, 0.1));
		objMng.addBackground(createBackground("yama", 0.55, 0, 200, 100,0));
		objMng.addBackground(createBackground("yama", 0.6, 200, 250, 200,0));
		objMng.addBackground(createBackground("yama", 0.55, 400, 200, 100,0));
		objMng.addBackground(createBackground("ki", 0.2, 000, 350, 10,0));
		objMng.addBackground(createBackground("ki", 0.2, 300, 350, 10,0));

		return factory;
	}

	//敵オブジェクトを作成するメソッド
	public static Enemy createEnemy(FieldObject type, int x, int y) {
		Enemy e = null;
		switch(type) {
		case NONE:break;
		case SLIME:{
			//スライム
			ImageAnimationView img = createImageAnimationView(Duration.millis(300), AppManager.getImage("slime"),50,50);//表示ノード
			img.setCycleCount(Animation.INDEFINITE);
			img.setIndex(0);
			Rectangle r = new Rectangle(x+8,y+26,35,20);//当たり判定
			img.setTranslateX(x);
			img.setTranslateY(y);
			e = new Slime(img, r);
			break;
		}
		case BIRD:{
			//鳥
			ImageAnimationView img = createImageAnimationView(Duration.millis(500), AppManager.getImage("bird"),50,50);//表示ノード
			img.setCycleCount(Animation.INDEFINITE);
			img.setIndex(0);
			Rectangle r = new Rectangle(x+5,y+20,40,15);//当たり判定
			img.setTranslateX(x);
			img.setTranslateY(y);
			e = new Bird(img, r);
			break;
		}
		default:break;
		}
		setOnMouseAction(e);
		return e;
	}

	//ブロックオブジェクトを作成するメソッド
	public static Block createBlock(FieldObject fo, int x, int y) {
		Block blk = null;
		Rectangle r = new Rectangle(x,y,Block.W,Block.H);//当たり判定
		switch(fo) {
		case GROUND:
		{
			//地上
			ImageView img = new ImageView(AppManager.getImage("jimen"));//表示ノード
			img.setTranslateX(x);
			img.setTranslateY(y);
			blk = new Block(img, r);
			blk.setType(FieldObject.GROUND);
			break;
		}
		case UNDER_GROUND:
		{
			//土
			ImageView img = new ImageView(AppManager.getImage("jimen2"));//表示ノード
			img.setTranslateX(x);
			img.setTranslateY(y);
			blk = new Block(img,r);
			blk.setType(FieldObject.UNDER_GROUND);
			break;
		}
		case BLOCK:
		{
			//ブロック
			ImageView img = new ImageView(AppManager.getImage("block"));//表示ノード
			img.setTranslateX(x);
			img.setTranslateY(y);
			blk = new Block(img, r);
			setOnMouseAction(blk);
			blk.setType(FieldObject.BLOCK);
			break;
		}
		default:break;
		}
		return blk;
	}

	public Effect createEffect(double x, double y) {
		ImageAnimationView img = createImageAnimationView(Duration.millis(250), AppManager.getImage("star"), 100, 100);//表示ノード
		img.setDefaultAnimationRange();
		img.setIndex(0);
		img.setCycleCount(1);
		img.setTranslateX(x-50);
		img.setTranslateY(y-50);

		return new Effect(img);
	}

	public static Item createItem(FieldObject fo, double x, double y) {

		Item item = null;
		switch(fo) {
		case NEGI:
		{
			//ネギ
			ImageView img = new ImageView(AppManager.getImage("negi"));//表示ノード
			img.setTranslateX(x);
			img.setTranslateY(y);
			Rectangle r = new Rectangle(x+16,y,13,FieldObject.H);//当たり判定
			item = new Item(img, r);
			//setOnMouseAction(item);
			break;
		}
		default:break;
		}
		return item;
	}

	//装飾オブジェクトを作成するメソッド
	public static Field createField(FieldObject fo, int x, int y) {
		Field fld = null;
		switch(fo) {
		case LEAF:
		{
			//草
			ImageView img = new ImageView(AppManager.getImage("kusa2"));//表示ノード
			img.setTranslateX(x);
			img.setTranslateY(y);
			fld = new Field(img);
			setOnMouseAction(fld);
			fld.setType(FieldObject.LEAF);
			break;
		}
		case FLOWER:
		{
			//花
			ImageView img = new ImageView(AppManager.getImage("kusa"));//表示ノード
			img.setTranslateX(x);
			img.setTranslateY(y);
			fld = new Field(img);
			setOnMouseAction(fld);
			fld.setType(FieldObject.FLOWER);
			break;
		}
		default:break;
		}
		return fld;
	}

	//プレイヤーを作成するメソッド
	public static Player createPlayer() {
		//表示
		ImageAnimationView view = createImageAnimationView(Duration.millis(500), AppManager.getImage("mogmol"), 50, 50);//表示ノード
		view.setCycleCount(Animation.INDEFINITE);
		view.setIndex(0);

		//当たり判定
		Rectangle rect = new Rectangle(10,10,30,37);//当たり判定

		//インスタンス作成
		Player p = new Player(view, rect);
		setOnMouseAction(p);
		return p;
	}

	//背景オブジェクトを作成するメソッド
	public static BackgroundScrollObject createBackground(String imgName, double scrollRatio, int x, int y, double viewOrder, double autoMoveX) {
		//表示元
		Image img = AppManager.getImage(imgName);

		//BackgroundScrollObjectクラスは元画像からウィンドウ横幅分右側に画像のコピーを貼り付けた状態の物を前提とした処理とし、
		//それに合わせて、元画像からウィンドウ横幅分右側に同じ画像を描画した1枚の画像を生成する
		WritableImage newImage = new WritableImage((int)img.getWidth() + (int)AppManager.getW(), (int)img.getHeight());
		PixelWriter pw = newImage.getPixelWriter();

		//元画像をそのまま描画
		pw.setPixels(0, 0, (int)img.getWidth(), (int)img.getHeight(), img.getPixelReader(), 0, 0);

		//元画像から右側に描画
		pw.setPixels((int)AppManager.getW(), 0, (int)img.getWidth(), (int)img.getHeight(), img.getPixelReader(), 0, 0);

		//画像Nodeの作成
		ImageView viewNode = new ImageView(newImage);//表示ノード
		viewNode.setImage(newImage);
		viewNode.setTranslateX(x);
		viewNode.setTranslateY(y);
		viewNode.setViewOrder(viewOrder);
		BackgroundScrollObject bso = new BackgroundScrollObject(viewNode, scrollRatio);
		bso.setAutoMoveX(autoMoveX);
		return bso;
	}

	//マウスイベントの設定をするメソッド
	private static void setOnMouseAction(GameObject go) {
		go.isMouseAction = true;
	}

	//指定インデックスにFieldObjectを設定する
	public void setFieldObject(FieldObject fo, int i, int j){
		data[i][j] = fo;
		switch(fo.TYPE) {
		case BLOCK:objMng.getField().getFieldList()[i][j]=createBlock(fo,  j*Block.W, i*Block.H);break;
		case FIELD:objMng.getField().getFieldList()[i][j]=createField(fo,  j*Block.W, i*Block.H);break;
		default:
			break;
		}
	}
}
