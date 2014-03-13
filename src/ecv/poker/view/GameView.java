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

	private Bitmap cardBack;
	private MyButton foldButton, checkButton, callButton, betButton,
			raiseButton;
	private Game game;
	private RectF table;
	private Paint greenPaint, whitePaint;
	private int screenW, screenH;
	private int cardW, cardH;
	private int buttonW, buttonH;
	private int compCardsX, compCardsY;
	private int playerCardsX, playerCardsY;
	private int communityX, communityY;
	private int sliderX, sliderY, sliderStart, sliderEnd;
	private boolean bitmapsLoaded;
	private int loadingProgress;
	private boolean sliderPressed;

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
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory
				.decodeResource(getResources(), R.drawable.card_back, opts);
		cardW = screenW / 6;
		cardH = cardW * opts.outHeight / opts.outWidth;
		BitmapFactory.decodeResource(getResources(), R.drawable.bet_button_up,
				opts);
		buttonW = screenW / 4;
		buttonH = buttonW * opts.outHeight / opts.outWidth;

		// Load bitmaps asynchronously on a background thread
		new BitmapLoader().execute();

		// set things like cards and chip labels relative to table
		table.left = 0;
		table.right = screenW;
		table.top = cardH;
		table.bottom = table.top + (screenH / 2);
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
		
		sliderY = screenH - 100;
		sliderStart = screenW / 10;
		sliderX = sliderStart;
		sliderEnd = 9 * sliderStart;
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
					+ cardW + 5, playerCardsY + cardH + 40, whitePaint);
			canvas.drawText(game.getBot().getChips() + "", compCardsX - 5,
					compCardsY + cardH + 40, whitePaint);
			canvas.drawText(game.getPot() + "", (table.left + table.right) / 2,
					communityY + cardH + 40, whitePaint);

			foldButton.draw(canvas);
			checkButton.draw(canvas);
			betButton.draw(canvas);

			// draw the bet slider
			canvas.drawLine(sliderStart, sliderY, sliderEnd, sliderY,
					whitePaint);
			canvas.drawCircle(sliderX, sliderY, 20, whitePaint);
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
				if (foldButton.detectCollision(x, y))
					foldButton.setPressed(true);
				else if (checkButton.detectCollision(x, y))
					checkButton.setPressed(true);
				else if (callButton.detectCollision(x, y))
					callButton.setPressed(true);
				else if (betButton.detectCollision(x, y))
					betButton.setPressed(true);
				else if (raiseButton.detectCollision(x, y))
					raiseButton.setPressed(true);
				else if (y > sliderY - 20 && y < sliderY + 20
						&& x > sliderStart && x < sliderEnd) {
					sliderPressed = true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (sliderPressed) {
					if (x < sliderStart)
						sliderX = sliderStart;
					else if (x > sliderEnd)
						sliderX = sliderEnd;
					else
						sliderX = x;
				}
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
				}

				else if (sliderPressed) {
					sliderPressed = false;
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

		// fields used to calculate progress
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
