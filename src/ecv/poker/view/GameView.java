package ecv.poker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	
	private Paint redPaint;
	private int circleX, circleY;
	private float radius;
		
	public GameView(Context context) {
		super(context);
		redPaint = new Paint();
		redPaint.setAntiAlias(true);
		redPaint.setColor(Color.RED);
		circleX = circleY = 100;
		radius = 30;		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(circleX, circleY, radius, redPaint);
	}
	
	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getAction();
		int x = (int) evt.getX();
		int y = (int) evt.getY();
		
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			circleX = x;
			circleY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			circleX = x;
			circleY = y;
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		
		invalidate();
		return true;
	}
}
