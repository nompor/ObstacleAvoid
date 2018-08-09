package com.nompor.app.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.nompor.app.manager.ConfigManager;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

//ゲームフィールド内に存在するオブジェクトを管理するクラス
public class FieldObjectManager {
	//各種フィールドに存在するオブジェクトを管理する
	Player player;
	List<Enemy> enemys = new LinkedList<>();
	List<Item> items = new LinkedList<>();
	List<Effect> effects = new LinkedList<>();
	List<Field> fields = new LinkedList<>();
	List<BackgroundScrollObject> scrolls = new LinkedList<>();
	GameField field;
	ObservableList<Node> views;//JavaFX表示用リスト
	public FieldObjectManager(ObservableList<Node> views, Player player, GameField field) {
		this.views = views;
		this.player = player;
		this.field = field;
		player.isAlive = true;
		addView(player);
	}
	//プレイヤー取得
	public Player getPlayer() {
		return player;
	}
	//フィールドに存在している敵一覧を取得
	public Iterator<Enemy> getEnemys(){
		return new FOMItr<Enemy>(enemys.iterator());
	}
	//フィールドに存在しているアイテム一覧を取得
	public Iterator<Item> getItems(){
		return new FOMItr<Item>(items.iterator());
	}
	//フィールドに存在しているエフェクト一覧を取得
	public Iterator<Effect> getEffects(){
		return new FOMItr<Effect>(effects.iterator());
	}
	//フィールドに存在しているブロック一覧を取得
	public Iterator<Field> getFields(){
		return new FOMItr<Field>(fields.iterator());
	}
	//フィールドに存在している背景一覧を取得
	public Iterator<BackgroundScrollObject> getBackgrounds(){
		return new FOMItr<BackgroundScrollObject>(scrolls.iterator());
	}
	//フィールドにアイテムを追加
	public void addItem(Item item) {
		item.isAlive = true;
		items.add(item);
		addView(item);
	}
	//フィールドに敵を追加
	public void addEnemy(Enemy enemy) {
		enemy.isAlive = true;
		enemys.add(enemy);
		addView(enemy);
	}
	//フィールドにエフェクトを追加
	public void addEffect(Effect ef) {
		ef.isAlive = true;
		effects.add(ef);
		addView(ef);
	}
	//フィールドにブロックを追加
	public void addField(Field fld) {
		fld.isAlive = true;
		fields.add(fld);
		addView(fld);
	}
	//フィールドに背景を追加
	public void addBackground(BackgroundScrollObject back) {
		back.isAlive = true;
		scrolls.add(back);
		addView(back);
	}
	//全フィールドブロック一覧
	public GameField getField() {
		return field;
	}
	private void addView(GameObject go) {
		views.add(go.getViewNode());

		//デバッグ用
		if ( ConfigManager.DEBUG && go.getHitNode() !=null ) {
			Rectangle rect = (Rectangle) go.getHitNode();
			rect.setOpacity(0.5);
			rect.setFill(Color.ORANGE);
			views.add(go.getHitNode());
		}
	}

	//削除命令を受けたら表示リストからも削除するように改造したIterator
	class FOMItr<E extends GameObject> implements Iterator<E>{
		Iterator<E> it;
		E e;

		FOMItr(Iterator<E> it){
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public E next() {
			e = it.next();
			return e;
		}

		@Override
	    public void remove() {
			//リストから削除
	        it.remove();

	        //非生存フラグを立てる
	        e.isAlive = false;

	        //表示リストから削除
	        views.remove(e.getViewNode());

			if ( ConfigManager.DEBUG && e.getHitNode() !=null ) {
	        	views.remove(e.getHitNode());
	        }
	    }
	}
}
