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
	private Bitmap titleGraphic;
	private Bitmap playButtonUp, playButtonDown;
	private Bitmap settingsButtonUp, settingsButtonDown;
	private int screenW, screenH;
	private int playButtonX, playButtonY; 
	private int settingsButtonX, settingsButtonY;
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

		// scale the title graphic and buttons
		titleGraphic = Bitmap.createScaledBitmap(titleGraphic, 
				screenH / 2 * titleGraphic.getWidth() / titleGraphic.getHeight(), screenH / 2, false);
		int buttonH = screenH / 7;
		int buttonW = buttonH * playButtonUp.getWidth() / playButtonUp.getHeight();
		playButtonUp = Bitmap.createScaledBitmap(playButtonUp, buttonW, buttonH, false);
		playButtonDown = Bitmap.createScaledBitmap(playButtonDown, buttonW, buttonH, false);
		settingsButtonUp = Bitmap.createScaledBitmap(settingsButtonUp, buttonW, buttonH, false);
		settingsButtonDown = Bitmap.createScaledBitmap(settingsButtonDown, buttonW, buttonH, false);
		
		// set the position of the buttons
		playButtonX = (screenW - buttonW) / 2;
		playButtonY = screenH / 2 + 50;
		settingsButtonX = playButtonX;
		settingsButtonY = playButtonY + buttonH + 20;

		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw the title image
		canvas.drawBitmap(titleGraphic, screenW / 2 - titleGraphic.getWidth() / 2, 0, null);

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
