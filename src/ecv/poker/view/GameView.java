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
	private Bitmap foldButtonUp, foldButtonDown;
	private Bitmap checkButtonUp, checkButtonDown;
	private Bitmap callButtonUp, callButtonDown;
	private Bitmap betButtonUp, betButtonDown;
	private Game game;
	private RectF table;
	private Paint greenPaint, textPaint;
	private int screenW, screenH;
	private int cardW, cardH;
	private int buttonW, buttonH;
	private int compCardsX, compCardsY;
	private int playerCardsX, playerCardsY;
	private int communityX, communityY;
	private int buttonX, buttonY;
	private boolean foldButtonPressed, callButtonPressed, betButtonPressed;
	private boolean bitmapsLoaded;
	private int loadingProgress;

	public GameView(Context context) {
		super(context);
		greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);
		textPaint = new Paint();
		textPaint.setTextSize(32);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
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

		// LOAD THE BITMAPS ASYNC
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
		
		buttonX = 10;
		buttonY = screenH - buttonH - 200;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawOval(table, greenPaint);
		if (bitmapsLoaded) {
			// draw player, computer, and community cards
			for (int i = 0; i < game.getBot().getCards().size(); i++) {
				canvas.drawBitmap(cardBack,	compCardsX - i * (cardW + 10), compCardsY, null);
			}
			for (int i = 0; i < game.getUser().getCards().size(); i++) {
				canvas.drawBitmap(game.getUser().getCard(i).getBitmap(),
						playerCardsX + i * (cardW + 10), playerCardsY, null);
			}
			for (int i = 0; i < game.getCommunityCards().size(); i++) {
				canvas.drawBitmap(game.getCommunityCards().get(i).getBitmap(),
						communityX + i * (cardW + 10), communityY, null);
			}
			// draw chip counts
			canvas.drawText(game.getUser().getChips() + "", playerCardsX
					+ cardW + 5, playerCardsY + cardH + 40, textPaint);
			canvas.drawText(game.getBot().getChips() + "", compCardsX - 5,
					compCardsY + cardH + 40, textPaint);
			if (game.getPot() > 0) {
				canvas.drawText(game.getPot() + "",
						(table.left + table.right) / 2,
						communityY + cardH + 40, textPaint);
			}
			// draw control buttons on bottom
			if (foldButtonPressed)
				canvas.drawBitmap(foldButtonDown, buttonX, buttonY, null);
			else
				canvas.drawBitmap(foldButtonUp, buttonX, buttonY, null);
			// TODO: decide if check or call button should be drawn...
			if (callButtonPressed)
				canvas.drawBitmap(checkButtonDown, buttonX + buttonW + 10,
						buttonY, null);
			else
				canvas.drawBitmap(checkButtonUp, buttonX + buttonW + 10,
						buttonY, null);

			if (betButtonPressed)
				canvas.drawBitmap(betButtonDown, buttonX + 2 * buttonW + 20,
						buttonY, null);
			else
				canvas.drawBitmap(betButtonUp, buttonX + 2 * buttonW + 20,
						buttonY, null);
		} 
		// Bitmaps worker thread is running. Display the progress
		else {
			canvas.drawText("LOADING... " + loadingProgress + "%", screenW / 2,
					screenH / 2, textPaint);
		}
	}

	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		int x = (int) evt.getX();
		int y = (int) evt.getY();
		// TODO: only check if player's turn...allow queuing moves?
		if(bitmapsLoaded) {
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (detectCollision(foldButtonUp, buttonX, buttonY, x, y))
					foldButtonPressed = true;
				else if (detectCollision(callButtonUp, buttonX + buttonW + 10,
						buttonY, x, y))
					callButtonPressed = true;
				else if (detectCollision(betButtonUp, buttonX + 2 * buttonW + 20,
						buttonY, x, y))
					betButtonPressed = true;
				break;
			case MotionEvent.ACTION_MOVE:
				// slider bar stuff?
				break;
			case MotionEvent.ACTION_UP:
				if (foldButtonPressed) {
					game.getUser().fold();
					game.dealNextCard();
					foldButtonPressed = false;
				} else if (callButtonPressed) {
					game.getUser().call(0);
					game.dealNextCard();
					callButtonPressed = false;
				} else if (betButtonPressed) {
					game.getUser().bet(10);
					game.dealNextCard();
					betButtonPressed = false;
				} 
				break;
			}
			invalidate();
		}
		return true;
	}

	/**
	 * Rectangular collision detection for a bitmap and specified coordinates.
	 * 
	 * @param bitmap
	 * @param left
	 *            - x coordinate of bitmap
	 * @param top
	 *            - y coordinate of bitmap
	 * @param evtX
	 * @param evtY
	 * @return true if collision. otherwise false
	 */
	private boolean detectCollision(Bitmap bitmap, int left, int top, int evtX,
			int evtY) {
		
			return evtX > left && evtX < bitmap.getWidth() + left && evtY > top
					&& evtY < bitmap.getHeight() + top;
	}

	/**
	 * Load bitmaps in asynchronously
	 * 
	 * @author Evan
	 * 
	 */
	private class BitmapLoader extends AsyncTask<Void, Integer, Boolean> {

		private final float TOTAL_BITMAPS = 61; // cards and buttons
		private int PROCESSED_BITMAPS = 0;

		/**
		 * @param lists of cards to get bitmaps for
		 */
		@Override
		protected Boolean doInBackground(Void... params) {
			List<Card> cards = new ArrayList<Card>(game.getDeck());
			cards.addAll(game.getBot().getCards());
			cards.addAll(game.getUser().getCards());
			cards.addAll(game.getCommunityCards());
			
			for (Card c : cards) {
				int resId = getResources().getIdentifier(
						"card" + c.getId(), "drawable", "ecv.poker");
				c.setBitmap(getScaledBitmap(resId, cardW, cardH));
			}
			cardBack = getScaledBitmap(R.drawable.card_back, cardW, cardH);

			foldButtonUp = getScaledBitmap(R.drawable.fold_button_up, buttonW,buttonH);
			checkButtonUp = getScaledBitmap(R.drawable.check_button_up, buttonW, buttonH);
			callButtonUp = getScaledBitmap(R.drawable.call_button_up, buttonW, buttonH);
			betButtonUp = getScaledBitmap(R.drawable.bet_button_up, buttonW, buttonH);

			foldButtonDown = getScaledBitmap(R.drawable.fold_button_down, buttonW, buttonH);
			checkButtonDown = getScaledBitmap(R.drawable.check_button_down, buttonW, buttonH);
			callButtonDown = getScaledBitmap(R.drawable.call_button_down, buttonW, buttonH);
			betButtonDown = getScaledBitmap(R.drawable.bet_button_down, buttonW, buttonH);

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

		private Bitmap getScaledBitmap(int resId, int scaledW, int scaledH) {
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
			bmp = Bitmap.createScaledBitmap(bmp, scaledW, scaledH, false);
			int progress = (int) (PROCESSED_BITMAPS / TOTAL_BITMAPS * 100);
			PROCESSED_BITMAPS++;
			publishProgress(progress);
			return bmp;
		}
	}
}
