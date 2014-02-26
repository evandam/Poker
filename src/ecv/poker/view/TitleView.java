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

	private Context context;
	private Bitmap titleGraphic, playButtonUp, playButtonDown,
			settingsButtonUp, settingsButtonDown;
	private int screenW, screenH;
	private int playButtonX, playButtonY, settingsButtonX, settingsButtonY;
	private boolean playButtonPressed, settingsButtonPressed;

	public TitleView(Context context) {
		super(context);
		this.context = context;
		titleGraphic = BitmapFactory.decodeResource(getResources(),
				R.drawable.aces);
		playButtonDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.play_button_down);
		playButtonUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.play_button_up);
		settingsButtonDown = BitmapFactory.decodeResource(getResources(),
				R.drawable.settings_button_down);
		settingsButtonUp = BitmapFactory.decodeResource(getResources(),
				R.drawable.settings_button_up);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		screenH = h;
		screenW = w;

		// scale the title graphic
		float heightRatio = (float) titleGraphic.getHeight()
				/ titleGraphic.getWidth();
		int newHeight = (int) (screenW * heightRatio);
		titleGraphic = Bitmap.createScaledBitmap(titleGraphic, screenW,
				newHeight, false);

		// set the position of the play button
		playButtonX = (screenW - playButtonUp.getWidth()) / 2;
		playButtonY = (int) (screenH * 0.7);

		// set position of settings button - below play button
		settingsButtonX = playButtonX;
		settingsButtonY = playButtonY + playButtonUp.getHeight() + 10;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw the title image centered on screen
		int titleX = (screenW - titleGraphic.getWidth()) / 2;
		canvas.drawBitmap(titleGraphic, titleX, 0, null);

		if (playButtonPressed)
			canvas.drawBitmap(playButtonDown, playButtonX, playButtonY, null);
		else
			canvas.drawBitmap(playButtonUp, playButtonX, playButtonY, null);

		if (settingsButtonPressed)
			canvas.drawBitmap(settingsButtonDown, settingsButtonX,
					settingsButtonY, null);
		else
			canvas.drawBitmap(settingsButtonUp, settingsButtonX,
					settingsButtonY, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		int x = (int) evt.getX();
		int y = (int) evt.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// C.D. for play button
			if (x > playButtonX && x < playButtonX + playButtonUp.getWidth()
					&& y > playButtonY
					&& y < playButtonY + playButtonUp.getHeight()) {
				playButtonPressed = true;
			}
			// C.D. for settings button
			else if (x > settingsButtonX
					&& x < settingsButtonX + settingsButtonUp.getWidth()
					&& y > settingsButtonY
					&& y < settingsButtonY + settingsButtonUp.getHeight()) {
				settingsButtonPressed = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (playButtonPressed) {
				Intent gameIntent = new Intent(context, GameActivity.class);
				context.startActivity(gameIntent);
				playButtonPressed = false;
			} else if (settingsButtonPressed) {
				// do something here
				settingsButtonPressed = false;
			}
			break;
		}
		invalidate();
		return true;
	}
}
