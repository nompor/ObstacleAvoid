package com.nompor.app;

import com.nompor.app.manager.AppManager;
import com.nompor.app.view.ViewType;

import javafx.application.Application;
import javafx.stage.Stage;


//アプリケーションエントリポイント
public class ObstacleAvoidStarter extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) throws Exception {

		//初期化時はタイトルを表示する
		AppManager.start(stage, ViewType.TITLE);
	}
}