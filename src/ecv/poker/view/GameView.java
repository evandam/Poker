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
import android.widget.Toast;
import ecv.poker.R;
import ecv.poker.card.Card;
import ecv.poker.game.Game;

public class GameView extends View {

	private Context context;
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

	public GameView(Context context) {
		super(context);
		this.context = context;
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
	}

	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		super.onSizeChanged(w, h, oldW, oldH);
		screenH = h;
		screenW = w;
		
		// TODO: consider loading bitmaps in a worker thread?
		// load and scale bitmaps of cards	
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.card_back, opts);
		cardW = screenW / 6;
		cardH = cardW * opts.outHeight / opts.outWidth;
		
		BitmapFactory.decodeResource(getResources(), R.drawable.custom_bet_button_up, opts);
		buttonW = screenW / 4;
		buttonH = buttonW * opts.outHeight / opts.outWidth;

		// LOAD THE BITMAPS ASYNC
		BitmapLoader loader = new BitmapLoader();
		loader.execute();
		
		buttonX = 10;
		buttonY = screenH - buttonH - 200;

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

		// resources ready to go!
		game.setupHand();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawOval(table, greenPaint);
		if(bitmapsLoaded) {
			// draw player, computer, and community cards
			for (int i = 0; i < game.getBot().getCards().size(); i++) {
				canvas.drawBitmap(game.getBot().getCard(i).getBitmap(), compCardsX
						- i * (cardW + 10), compCardsY, null);
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
			canvas.drawText(game.getUser().getChips() + "", playerCardsX + cardW
					+ 5, playerCardsY + cardH + 40, textPaint);
			canvas.drawText(game.getBot().getChips() + "", compCardsX - 5,
					compCardsY + cardH + 40, textPaint);
			if (game.getPot() > 0) {
				canvas.drawText(game.getPot() + "", (table.left + table.right) / 2,
						communityY + cardH + 40, textPaint);
			}
			// draw control buttons on bottom
			if (foldButtonPressed)
				canvas.drawBitmap(foldButtonDown, buttonX, buttonY, null);
			else
				canvas.drawBitmap(foldButtonUp, buttonX, buttonY, null);
			// decide if check or call button should be drawn...
			if (callButtonPressed)
				canvas.drawBitmap(checkButtonDown, buttonX + buttonW + 10, buttonY,
						null);
			else
				canvas.drawBitmap(checkButtonUp, buttonX + buttonW + 10, buttonY,
						null);
	
			if (betButtonPressed)
				canvas.drawBitmap(betButtonDown, buttonX + 2 * buttonW + 20,
						buttonY, null);
			else
				canvas.drawBitmap(betButtonUp, buttonX + 2 * buttonW + 20, buttonY,
						null);
		} else {
			canvas.drawText("LOADING...", screenW / 2, screenH / 2, textPaint);
		}
	}

	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		int x = (int) evt.getX();
		int y = (int) evt.getY();

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
				foldButtonPressed = false;
			} else if (callButtonPressed) {
				game.getUser().call(0);
				callButtonPressed = false;
			} else if (betButtonPressed) {
				game.getUser().bet(10);
				betButtonPressed = false;
			} else {
				// for testing, any touch deals cards...
				if (game.getCommunityCards().size() < 3)
					game.dealFlop();
				else if (game.getCommunityCards().size() < 4)
					game.dealTurn();
				else if (game.getCommunityCards().size() < 5)
					game.dealRiver();
				else {
					// response message of who won and how much
					String str = game.endHand();
					Toast.makeText(context, str, Toast.LENGTH_LONG).show();
					game.setupHand();
				}
			}
			break;
		}
		invalidate();
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
	 * @return true if collision. otherwise false.
	 */
	private boolean detectCollision(Bitmap bitmap, int left, int top, int evtX,
			int evtY) {
		return evtX > left && evtX < bitmap.getWidth() + left && evtY > top
				&& evtY < bitmap.getHeight() + top;
	}
	
	private class BitmapLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			List<Card> cards = new ArrayList<Card>();
			cards.addAll(game.getDeck());
			cards.addAll(game.getBot().getCards());
			cards.addAll(game.getUser().getCards());
			for (Card c : cards) {
				int resourceId = getResources().getIdentifier("card" + c.getId(),
						"drawable", "ecv.poker");
				Bitmap bmp = BitmapFactory.decodeResource(
						context.getResources(), resourceId);
				c.setBitmap(Bitmap.createScaledBitmap(bmp, cardW, cardH, false));
			}
			cardBack = BitmapFactory.decodeResource(getResources(),
					R.drawable.card_back);
			cardBack = Bitmap.createScaledBitmap(cardBack, cardW, cardH, false);
			
			foldButtonUp = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_fold_button_up);
			foldButtonUp = Bitmap.createScaledBitmap(foldButtonUp, buttonW, buttonH, false);
			checkButtonUp = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_check_button_up);
			checkButtonUp = Bitmap.createScaledBitmap(checkButtonUp, buttonW, buttonH, false);
			callButtonUp = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_call_button_up);
			callButtonUp = Bitmap.createScaledBitmap(callButtonUp, buttonW, buttonH, false);
			betButtonUp = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_bet_button_up);
			betButtonUp = Bitmap.createScaledBitmap(betButtonUp, buttonW, buttonH, false);
			
			foldButtonDown = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_fold_button_down);
			foldButtonDown = Bitmap.createScaledBitmap(foldButtonDown, buttonW, buttonH, false);
			checkButtonDown = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_check_button_down);
			checkButtonDown = Bitmap.createScaledBitmap(checkButtonDown, buttonW, buttonH, false);
			callButtonDown = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_call_button_down);
			callButtonDown = Bitmap.createScaledBitmap(callButtonDown, buttonW, buttonH, false);
			betButtonDown = BitmapFactory.decodeResource(getResources(),
					R.drawable.custom_bet_button_down);
			betButtonDown = Bitmap.createScaledBitmap(betButtonDown, buttonW, buttonH, false);
			
			bitmapsLoaded = true;
			postInvalidate();
			return null;
		}
		
	}
}
