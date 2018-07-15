

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.nompor.gtk.file.GTKFileUtil;

//ゲームの記録データを処理する
public class RecordManager {

	private static Path dataPath = Paths.get("data/data.dat");

	static {
		//初期化ファイル処理
		if ( !Files.exists(dataPath) ) {
			int[] initData = new int[4];
			Arrays.fill(initData, 1000000);
			saveFile(initData);
		}
	}

	//データ記録ファイルを書き出す
	public static void saveFile(int[] data) {
		ByteBuffer bb = ByteBuffer.allocate(16);
		bb.putInt(data[0]);
		bb.putInt(data[1]);
		bb.putInt(data[2]);
		bb.putInt(data[3]);
		GTKFileUtil.writeBinaryBuffer(dataPath, bb);
	}

	//データ記録ファイルを読み込む
	public static int[] loadFile() {
		int[] data = new int[4];
		ByteBuffer bb = GTKFileUtil.readBinaryBuffer(dataPath);
		data[0] = bb.getInt();
		data[1] = bb.getInt();
		data[2] = bb.getInt();
		data[3] = bb.getInt();
		return data;
	}
}
