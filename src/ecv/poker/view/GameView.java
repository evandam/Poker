package ecv.poker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import ecv.poker.R;
import ecv.poker.card.Card;
import ecv.poker.game.Game;

public class GameView extends View {

	private Context context;
	private Bitmap cardBack;
	private Game game;
	private RectF table;
	private Paint greenPaint, textPaint;
	private int screenH, screenW;
	private int cardW, cardH;
	private int playerCardsX, playerCardsY;
	private int playerChipsX, playerChipsY;
	private int computerCardsX, computerCardsY;
	private int computerChipsX, computerChipsY;
	private int communityCardsX, communityCardsY;
	private int cardPaddingX, cardPaddingY;
	private int potX, potY;
	

	public GameView(Context context) {
		super(context);
		this.context = context;
		greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);
		textPaint = new Paint();
		textPaint.setTextSize(32);
		textPaint.setTextAlign(Align.CENTER);
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

		// Handle all scaling and positioning here
		
		// load all bitmaps of cards and scale them
		cardBack = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.card_back);
		cardW = screenW / 6;
		cardH = cardW * cardBack.getHeight() / cardBack.getWidth();
		cardBack = Bitmap.createScaledBitmap(cardBack, cardW, cardH, false);
		for (Card c : game.getAllCards()) {
			int resourceId = getResources().getIdentifier("card" + c.getId(),
					"drawable", "ecv.poker");
			Bitmap tempBitmap = BitmapFactory.decodeResource(
					context.getResources(), resourceId);
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(tempBitmap,
					cardW, cardH, false);
			c.setBitmap(scaledBitmap);
		}
		
		// set the size and position of the "table," or green oval on screen
		table.set(0, screenH / 4, screenW, 3 * screenH / 4);
				
		// set the positions for cards and chips relative to the table
		cardPaddingX = cardW + 10;
		cardPaddingY = cardH + 40;
		computerCardsX = (int) table.right - cardW - 50;
		computerCardsY = (int) table.top - cardH / 4;
		computerChipsX = computerCardsX;
		computerChipsY = computerCardsY + cardPaddingY;
		playerCardsX = (int) table.left + 50;
		playerCardsY = (int) table.bottom - 3 * cardH / 4;
		playerChipsX = playerCardsX + cardPaddingX;
		playerChipsY = playerCardsY + cardPaddingY;
		communityCardsY = screenH / 2 - cardH / 2;
		communityCardsX = screenW  / 2 - 5 * cardW / 2 - 20;
		potX = screenW / 2;
		potY = screenH / 2 + cardPaddingY;
	}

	@Override
	protected void onDraw(Canvas canvas) {		
		// draw table and chip counts
		canvas.drawOval(table, greenPaint);
		canvas.drawText(game.getUser().getChips() + "", playerChipsX, playerChipsY, textPaint); 
		canvas.drawText(game.getComputer().getChips() + "", computerChipsX, computerChipsY, textPaint);
		if(game.getPot() > 0) 
			canvas.drawText(game.getPot() + "", potX, potY, textPaint);

		// draw player, computer, and community cards
		for (int i = 0; i < game.getComputer().getHoleCards().size(); i++) {
			canvas.drawBitmap(cardBack, computerCardsX - i * cardPaddingX, computerCardsY, null);
		}
		for (int i = 0; i < game.getUser().getHoleCards().size(); i++) {
			canvas.drawBitmap(game.getUser().getHoleCards().get(i).getBitmap(),
					playerCardsX + i * cardPaddingX, playerCardsY, null);
		}
		for (int i = 0; i < game.getCommunityCards().size(); i++) {
			canvas.drawBitmap(game.getCommunityCards().get(i).getBitmap(),
					communityCardsX + i * cardPaddingX, communityCardsY, null);
		}
	}

	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		// int x = (int) evt.getX();
		// int y = (int) evt.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:
			// for testing, any touch deals cards...
			if (game.getCommunityCards().size() < 3) {
				for (int i = 0; i < 3; i++)
					game.getCommunityCards().add(game.getDeck().deal());
			} else if (game.getCommunityCards().size() < 5)
				game.getCommunityCards().add(game.getDeck().deal());
			else {
				game.endHand();
				game.setupHand();
			}
			invalidate();
			break;
		}
		return true;
	}
}
