package com.nompor.app.manager;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.nompor.app.view.DescriptionMode;
import com.nompor.app.view.GameMode;
import com.nompor.app.view.StageSelectMode;
import com.nompor.app.view.TitleMode;
import com.nompor.app.view.ViewType;
import com.nompor.gtk.fx.GTKManagerFX;
import com.nompor.gtk.fx.GameViewableFX;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

//ゲームの画像や音声再生、画面遷移を制御するメソッド群
public class AppManager {

	//ゲームBGM開始
	public static void gameBGMStart() {
		GTKManagerFX.loopBGM("bgm/aozoranoshitade.mp3");
	}

	//ゲームタイトル開始
	public static void titleBGMStart() {
		GTKManagerFX.loopBGM("bgm/title.mp3");
	}

	//ゲームBGMの停止
	public static void gameBGMStop() {
		GTKManagerFX.stopBGM();
	}

	//ゲームプログラムを開始する
	public static void start(Stage stage, ViewType type) {

		//ウィンドウの表示
		GTKManagerFX.start(stage, "もぐもるのネギあつめ","img/icon.png",800, 600, get(type));

		//デフォルトのフルスクリーン終了処理を無効化
		GTKManagerFX.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		GTKManagerFX.setFullScreenExitHint("");

		//終了時に保存処理
		GTKManagerFX.setOnCloseRequest(e -> end());

		//固定キーイベント、フルスクリーンとアプリケーション終了
		GTKManagerFX.setOnKeyReleased(e -> {
			switch(e.getCode()) {
			case ENTER:
			ConfigManager.fullScreenChange();
			break;
			case ESCAPE:end();break;
			default:
				break;
			}
		});

		//画像フォルダの中身を全ロード
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get("img"))){
			ds.forEach(p -> GTKManagerFX.loadImage(p.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//効果音フォルダの中身を全ロード
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get("se"))){
			ds.forEach(p -> GTKManagerFX.loadSE(p.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//画像を読み込む
	public static Image getImage(String fileName) {
		return GTKManagerFX.getImage("img/"+fileName+".png");
	}

	//効果音を再生する
	public static void playSE(String fileName) {
		GTKManagerFX.playSE("se/"+fileName+".mp3");
	}

	//ゲーム画面を遷移する
	public static void change(ViewType type) {
		GTKManagerFX.changeViewDefaultAnimation(get(type));
	}

	//ウィンドウ横幅取得
	public static double getW() {
		return GTKManagerFX.getWidth();
	}

	//ウィンドウ縦幅取得
	public static double getH() {
		return GTKManagerFX.getHeight();
	}

	//ゲーム画面を取得する
	private static GameViewableFX get(ViewType type) {
		if ( GTKManagerFX.isStart() ) GTKManagerFX.animationClear();
		switch(type) {
		case TITLE:
			return new TitleMode();
		case GAME:
			return new GameMode();
		case DESCRIPTION:
			return new DescriptionMode();
		case STAGE_SELECT:
			return new StageSelectMode();
		default:
			break;
		}
		return null;
	}

	//アプリケーションを終了する
	public static void end() {
		ConfigManager.save();
		GTKManagerFX.end();
	}
}
