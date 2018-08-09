package com.nompor.app.manager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.nompor.gtk.file.GTKFileUtil;
import com.nompor.gtk.fx.GTKManagerFX;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;


//ゲームの設定データを管理する
public class ConfigManager {

	//デバッグモード時はキャラ判定を表示する
	public static final boolean DEBUG;
	static Button autoRightMoveBtn = new Button("右移動自動化");
	static boolean isAutoRightMode;
	static Path path = Paths.get("data/conf.txt");
	static{
		if ( !Files.exists(path) ) {
			Properties prop = new Properties();
			prop.setProperty("auto_right_mode", "0");
			prop.setProperty("debug", "0");
			GTKFileUtil.writeProperty(path.toFile(), prop);
		}
		Properties prop = GTKFileUtil.readProperty(path.toFile());
		isAutoRightMode = "1".equals(prop.get("auto_right_mode"));
		DEBUG = "1".equals(prop.get("debug"));
		if ( "1".equals(prop.get("full_screen")) ) fullScreenChange();
		autoRightMoveBtn.setOnMouseClicked(e -> {
			change(!isAutoRightMode);
		});
		autoRightMoveBtn.setCursor(Cursor.HAND);
		autoRightMoveBtn.setBorder(new Border(new BorderStroke(Color.LIME, BorderStrokeStyle.DASHED, new CornerRadii(10), BorderWidths.DEFAULT)));
		autoRightMoveBtn.setFocusTraversable(false);//ボタンにフォーカスが当たるとキーイベントが正常に動かない
		setAutoRightMode(isAutoRightMode);
	}

	//設定の保存
	public static void save() {
		Properties prop = new Properties();
		prop.setProperty("auto_right_mode", isAutoRightMode ? "1" : "0");
		prop.setProperty("debug", DEBUG ? "1" : "0");
		prop.setProperty("full_screen", isFullScreen() ? "1" : "0");
		GTKFileUtil.writeProperty(path.toFile(), prop);
	}

	private static void change(boolean isAutoRightMode) {
		ConfigManager.isAutoRightMode = isAutoRightMode;
		if ( isAutoRightMode ) {
			autoRightMoveBtn.setTextFill(Color.WHITE);
			Background back = new Background(
					new BackgroundFill(
							new LinearGradient(
								0, 0, 0, 1, true, CycleMethod.NO_CYCLE
								, new Stop(0, Color.rgb(200,255,200))
								, new Stop(1, Color.LIME)
							), new CornerRadii(10)
					, Insets.EMPTY
					)
				);
			autoRightMoveBtn.setBackground(back);
		} else {
			autoRightMoveBtn.setTextFill(Color.WHITE);
			Background back = new Background(
					new BackgroundFill(
							new LinearGradient(
								0, 0, 0, 1, true, CycleMethod.NO_CYCLE
								, new Stop(0, Color.gray(0.5))
								, new Stop(1, Color.BLACK)
							), new CornerRadii(10)
					, Insets.EMPTY
					)
				);
			autoRightMoveBtn.setBackground(back);
		}
	}

	public static void setAutoRightMode(boolean isAutoRightMode) {
		change(isAutoRightMode);
	}
	public static boolean isAutoRightMode() {
		return isAutoRightMode;
	}
	public static Node getAutoMoveButton() {
		return autoRightMoveBtn;
	}
	public static boolean isFullScreen() {
		return GTKManagerFX.isFullScreenWithResolution();
	}

	@SuppressWarnings("deprecation")
	public static void fullScreenChange() {
		GTKManagerFX.setFullScreenWithResolution(!isFullScreen());
	}
}
