import com.nompor.gtk.fx.GameViewFX;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TitleMode extends GameViewFX {

	Text title = new Text(0,100,"あいうえお");
	Text start = new Text(0,300,"ゲーム開始");
	Text desc = new Text(0,400,"チュートリアル");
	Text end = new Text(0,500,"終了");

	public TitleMode() {
	}

	public void start() {
		//横幅
		title.setWrappingWidth(AppManager.getW());
		start.setWrappingWidth(AppManager.getW());
		desc.setWrappingWidth(AppManager.getW());
		end.setWrappingWidth(AppManager.getW());

		//中央寄せ
		title.setTextAlignment(TextAlignment.CENTER);
		start.setTextAlignment(TextAlignment.CENTER);
		desc.setTextAlignment(TextAlignment.CENTER);
		end.setTextAlignment(TextAlignment.CENTER);

		//文字の大きさ
		title.setFont(new Font(80));
		start.setFont(new Font(50));
		desc.setFont(new Font(50));
		end.setFont(new Font(50));

		//カーソル
		start.setCursor(Cursor.HAND);
		desc.setCursor(Cursor.HAND);
		end.setCursor(Cursor.HAND);

		//マウスが領域に入った時
		start.setOnMouseEntered(this::onOver);
		desc.setOnMouseEntered(this::onOver);
		end.setOnMouseEntered(this::onOver);

		//マウスが領域から出たとき
		start.setOnMouseExited(this::onExited);
		desc.setOnMouseExited(this::onExited);
		end.setOnMouseExited(this::onExited);

		//テキストがクリックされた時
		start.setOnMouseClicked(e->AppManager.change(ViewType.GAME));
		desc.setOnMouseClicked(e->AppManager.change(ViewType.DESCRIPTION));
		end.setOnMouseClicked(e->AppManager.end());

		//要素の追加
		getChildren().add(title);
		getChildren().add(start);
		getChildren().add(desc);
		getChildren().add(end);
	}

	private void onOver(MouseEvent e) {
		Text obj = (Text)e.getSource();
		obj.setFill(Color.ORANGE);
	}

	private void onExited(MouseEvent e) {
		Text obj = (Text)e.getSource();
		obj.setFill(Color.BLACK);
	}
}