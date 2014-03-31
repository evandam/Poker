package ecv.poker.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import ecv.poker.R;
import ecv.poker.activity.GameActivity;

public class TitleView extends View {

	// width:height ratios of bitmaps
	private static final float TITLE_GRAPHIC_RATIO = 489f / 457;
	private static final float BUTTON_RATIO = 412f / 162;
	
	private Context context;
	private Bitmap titleGraphic;
	private MyButton playButton, settingsButton;
	private int screenW, screenH;
	private Bitmap playButtonUp, playButtonDown;
	private Bitmap settingsButtonUp, settingsButtonDown;

	public TitleView(Context context) {
		super(context);
		this.context = context;
		playButton = new MyButton(R.drawable.play_button_up, R.drawable.play_button_down);
		settingsButton = new MyButton(R.drawable.settings_button_up, R.drawable.settings_button_down);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		screenH = h;
		screenW = w;

		// scale the title graphic and buttons
		int titleH = screenH / 2;
		int titleW = (int) (titleH * TITLE_GRAPHIC_RATIO);
		titleGraphic = getScaledBitmap(R.drawable.aces, titleW, titleH);

		int buttonH = screenH / 7;
		int buttonW = (int) (buttonH * BUTTON_RATIO);
		playButtonUp = getScaledBitmap(playButton.getUpId(), buttonW, buttonH);
		playButtonDown = getScaledBitmap(playButton.getDownId(), buttonW, buttonH);
		playButton.setWidth(buttonW);
		playButton.setHeight(buttonH);
		settingsButtonUp = getScaledBitmap(settingsButton.getUpId(), buttonW, buttonH);
		settingsButtonDown = getScaledBitmap(settingsButton.getDownId(), buttonW, buttonH);
		settingsButton.setWidth(buttonW);
		settingsButton.setHeight(buttonH);
		
		// set the position of the buttons
		playButton.setX(screenW / 2 - buttonW / 2);
		playButton.setY(3 * screenH / 4 - buttonH);
		settingsButton.setX(playButton.getX());
		settingsButton.setY(playButton.getY() + buttonH + 10);		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw the title image
		canvas.drawBitmap(titleGraphic, screenW / 2 - titleGraphic.getWidth() / 2, 0, null);
		if(playButton.isPressed())
			canvas.drawBitmap(playButtonDown, playButton.getX(), playButton.getY(), null);
		else
			canvas.drawBitmap(playButtonUp, playButton.getX(), playButton.getY(), null);
		
		if(settingsButton.isPressed())
			canvas.drawBitmap(settingsButtonDown, settingsButton.getX(), settingsButton.getY(), null);
		else
			canvas.drawBitmap(settingsButtonUp, settingsButton.getX(), settingsButton.getY(), null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		int x = (int) evt.getX();
		int y = (int) evt.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			playButton.detectCollision(x, y);
			settingsButton.detectCollision(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (playButton.isPressed()) {
				Intent gameIntent = new Intent(context, GameActivity.class);
				context.startActivity(gameIntent);
				playButton.setPressed(false);
			} else if (settingsButton.isPressed()) {
				// do something here
				settingsButton.setPressed(false);
			}
			break;
		}
		invalidate();
		return true;
	}
	// helper method to load and scale a bitmap in one step
	private Bitmap getScaledBitmap(int resId, int scaledW, int scaledH) {
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
		return Bitmap.createScaledBitmap(bmp, scaledW, scaledH, false);
	}
}
