package ecv.poker.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import ecv.poker.R;
import ecv.poker.card.Card;
import ecv.poker.game.Game;

public class GameView extends View {
	
	// width:height ratios of bitmaps
	private static final float BUTTON_RATIO = 412f / 162;
	private static final float CARD_RATIO = 222f / 284;
	
	private Bitmap cardBack;
	private MyButton foldButton, checkButton, callButton, betButton, raiseButton;
	private Slider slider;
	private Game game;
	private RectF table;
	private Paint greenPaint, whitePaint;
	private int screenW, screenH;
	private int cardW, cardH;
	private int buttonW, buttonH;
	private int compCardsX, compCardsY;
	private int playerCardsX, playerCardsY;
	private int communityX, communityY;
	private boolean bitmapsLoaded;
	private int loadingProgress;
	
	public GameView(Context context) {
		super(context);
		greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);
		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		whitePaint.setAntiAlias(true);
		whitePaint.setTextSize(32);
		whitePaint.setTextAlign(Align.CENTER);

		foldButton = new MyButton();
		checkButton = new MyButton();
		callButton = new MyButton();
		betButton = new MyButton();
		raiseButton = new MyButton();
		
		slider = new Slider();
		slider.setMinVal(0);
		slider.setMaxVal(100);
		
		table = new RectF();
		game = new Game();
		game.setupHand();
	}

	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		screenH = h;
		screenW = w;

		// Get dimensions of bitmaps to set scales
		cardW = screenW / 6;
		cardH = (int) (cardW / CARD_RATIO);
		
		buttonW = screenW / 4;
		buttonH = (int) (buttonW / BUTTON_RATIO);

		// Load bitmaps asynchronously on a background thread
		new BitmapLoader().execute();

		// set things like cards and chip labels relative to table
		table.set(0, cardH, screenW, cardH + screenH / 2);
		compCardsX = (int) table.right - cardW - 50;
		compCardsY = (int) table.top - cardH / 2;
		playerCardsX = (int) table.left + 50;
		playerCardsY = (int) table.bottom - cardH / 2;
		communityX = (int) (table.right + table.left) / 5 - cardW + 5;
		communityY = (int) (table.top + table.bottom) / 2 - cardH / 2;

		int buttonX = 10;
		int buttonY = screenH - buttonH - 200;
		foldButton.setXY(buttonX, buttonY);
		checkButton.setXY(foldButton.getX() + buttonW + 10, buttonY);
		callButton.setXY(checkButton.getX(), buttonY);
		betButton.setXY(callButton.getX() + buttonW + 10, buttonY);
		raiseButton.setXY(betButton.getX(), buttonY);

		slider.setY(screenH - 100);
		slider.setStartX(screenW / 10);
		slider.setStopX(9 * slider.getStartX());
		slider.setCurX(slider.getStartX());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawOval(table, greenPaint);
		if (bitmapsLoaded) {
			// draw player, computer, and community cards
			for (int i = 0; i < game.getBot().getCards().size(); i++) {
				canvas.drawBitmap(cardBack, compCardsX - i * (cardW + 10),
						compCardsY, null);
			}
			for (int i = 0; i < game.getUser().getCards().size(); i++) {
				canvas.drawBitmap(game.getUser().getCards().get(i).getBitmap(),
						playerCardsX + i * (cardW + 10), playerCardsY, null);
			}
			for (int i = 0; i < game.getCommunityCards().size(); i++) {
				canvas.drawBitmap(game.getCommunityCards().get(i).getBitmap(),
						communityX + i * (cardW + 10), communityY, null);
			}

			// draw chip counts
			canvas.drawText(game.getUser().getChips() + "", playerCardsX
					+ cardW + 5, playerCardsY + cardH + whitePaint.getFontSpacing(), whitePaint);
			canvas.drawText(game.getBot().getChips() + "", compCardsX - 5,
					compCardsY + cardH + whitePaint.getFontSpacing(), whitePaint);
			canvas.drawText(game.getPot() + "", (table.left + table.right) / 2,
					communityY + cardH + whitePaint.getFontSpacing(), whitePaint);

			foldButton.draw(canvas);
			checkButton.draw(canvas);
			betButton.draw(canvas);
			slider.draw(canvas, whitePaint);
			
			// draw value of the bet
			int betValX = betButton.getX() + buttonW + buttonW / 2 - 20;
			int betValY = betButton.getY() + (int) whitePaint.getFontSpacing();
			canvas.drawText(slider.getCurVal() + "", betValX, betValY, whitePaint);
		}
		// Bitmaps are still being loaded. Display the progress
		else {
			canvas.drawText("LOADING... " + loadingProgress + "%", screenW / 2,
					screenH / 2, whitePaint);
		}
	}

	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		int x = (int) evt.getX();
		int y = (int) evt.getY();
		// TODO: only check if player's turn...allow queuing moves?
		if (bitmapsLoaded) {
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				foldButton.detectCollision(x, y);
				checkButton.detectCollision(x, y);
				callButton.detectCollision(x, y);
				betButton.detectCollision(x, y);
				raiseButton.detectCollision(x, y);
				slider.detectCollision(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				if (slider.isPressed())
					slider.setCurX(x);
				break;
			case MotionEvent.ACTION_UP:
				if (foldButton.isPressed()) {
					foldButton.setPressed(false);
				} else if (checkButton.isPressed()) {
					checkButton.setPressed(false);
				} else if (callButton.isPressed()) {
					callButton.setPressed(false);
				} else if (betButton.isPressed()) {
					betButton.setPressed(false);
				} else if (raiseButton.isPressed()) {
					raiseButton.setPressed(false);
				} else if (slider.isPressed()) {
					slider.setPressed(false);
				}
				break;
			}
			invalidate();
		}
		return true;
	}

	/**
	 * Load bitmaps in asynchronously
	 * 
	 * @author Evan
	 * 
	 */
	private class BitmapLoader extends AsyncTask<Void, Integer, Boolean> {

		// fields used to calculate progress (61 bitmaps being loaded)
		private static final int PROGRESS_INCREMENT = (int) (1.0 / 61 * 100);
		private int CURRENT_PROGRESS = 0;

		@Override
		protected Boolean doInBackground(Void... params) {
			// get one list of all cards, regardless of where they are in the
			// game
			List<Card> cards = new ArrayList<Card>(game.getDeck());
			cards.addAll(game.getBot().getCards());
			cards.addAll(game.getUser().getCards());
			cards.addAll(game.getCommunityCards());

			for (Card c : cards) {
				int resId = getResources().getIdentifier("card" + c.getId(),
						"drawable", "ecv.poker");
				c.setBitmap(getScaledBitmap(c.getBitmap(), resId, cardW, cardH));
			}
			cardBack = getScaledBitmap(cardBack, R.drawable.card_back, cardW,
					cardH);

			foldButton.setDown(getScaledBitmap(foldButton.getDown(),
					R.drawable.fold_button_down, buttonW, buttonH));
			foldButton.setUp(getScaledBitmap(foldButton.getUp(),
					R.drawable.fold_button_up, buttonW, buttonH));
			checkButton.setDown(getScaledBitmap(checkButton.getDown(),
					R.drawable.check_button_down, buttonW, buttonH));
			checkButton.setUp(getScaledBitmap(checkButton.getUp(),
					R.drawable.check_button_up, buttonW, buttonH));
			callButton.setDown(getScaledBitmap(callButton.getDown(),
					R.drawable.call_button_down, buttonW, buttonH));
			callButton.setUp(getScaledBitmap(callButton.getUp(),
					R.drawable.call_button_up, buttonW, buttonH));
			betButton.setDown(getScaledBitmap(betButton.getDown(),
					R.drawable.bet_button_down, buttonW, buttonH));
			betButton.setUp(getScaledBitmap(betButton.getUp(),
					R.drawable.bet_button_up, buttonW, buttonH));
//			raiseButton.setDown(getScaledBitmap(raiseButton.getDown(),
//					R.drawable.raise_button_down, buttonW, buttonH));
//			raiseButton.setUp(getScaledBitmap(raiseButton.getUp(),
//					R.drawable.raise_button_up, buttonW, buttonH));

			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			loadingProgress = progress[0];
			invalidate();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			bitmapsLoaded = result;
			invalidate();
		}

		/**
		 * Scale the target bitmap. Only loads the bitmap if target is null, and
		 * only scales if the target dimensions differ from the scaled
		 * 
		 * @param target
		 * @param resId
		 * @param scaledW
		 * @param scaledH
		 * @return
		 */
		private Bitmap getScaledBitmap(Bitmap target, int resId, int scaledW,
				int scaledH) {
			publishProgress(CURRENT_PROGRESS);
			CURRENT_PROGRESS += PROGRESS_INCREMENT;
			if (target == null) {
				Bitmap bmp = BitmapFactory
						.decodeResource(getResources(), resId);
				return Bitmap.createScaledBitmap(bmp, scaledW, scaledH, false);
			} else if (target.getWidth() != scaledW
					&& target.getHeight() != scaledH) {
				return Bitmap.createScaledBitmap(target, scaledW, scaledH,
						false);
			} else {
				return target;
			}
		}
	}
}
