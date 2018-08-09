package com.nompor.app.game;

import java.util.ArrayList;
import java.util.EnumMap;


//フィールド生成オブジェクトの元データ
public enum FieldObject{

	//フィールド配置用オブジェクト一覧
	NONE(ObjectType.NONE,0)
	,GROUND(ObjectType.BLOCK,1)
	,UNDER_GROUND(ObjectType.BLOCK,2)
	,SLIME(ObjectType.ENEMY,3)
	,BIRD(ObjectType.ENEMY,4)
	,GREEN_SLIME(ObjectType.ENEMY,5)
	,NEEDLE_LEAF(ObjectType.ENEMY,6)
	,RED_SLIME(ObjectType.ENEMY,7)
	,LEAF(ObjectType.FIELD,8)
	,FLOWER(ObjectType.FIELD,9)
	,BLOCK(ObjectType.BLOCK,10)
	,NEGI(ObjectType.ITEM,11);

	private static final EnumMap<ObjectType, FieldObject[]> map = new EnumMap<>(ObjectType.class);
	private static final FieldObject[] objects;

	//オブジェクトサイズ
	public static int W = 50;
	public static int H = 50;

	static {
		//各fieldObjectをObjectTypeごとに分割しておく
		for (ObjectType type : ObjectType.values()) {
			ArrayList<FieldObject> arr = new ArrayList<>();
			for ( FieldObject o : FieldObject.values() ) {
				if ( type == o.TYPE ) {
					arr.add(o);
				}
			}
			map.put(type, arr.toArray(new FieldObject[0]));
		}
		FieldObject[] _objects = FieldObject.values();
		objects = new FieldObject[_objects.length];
		for (FieldObject object : _objects) {
			FieldObject.objects[object.INDEX] = object;
		}
	}

	public final ObjectType TYPE;
	public final int INDEX;
	private FieldObject(ObjectType type, int index) {
		TYPE = type;
		INDEX = index;
	}

	//typeが表すFieldObjectを全取得
	public static FieldObject[] getFieldObjects(ObjectType type) {
		return map.get(type);
	}

	//インデックス番号をenumに変換するメソッド
	public static FieldObject getFieldObjectForIndex(int index) {
		return objects[index];
	}
}