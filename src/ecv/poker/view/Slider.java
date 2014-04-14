package ecv.poker.view;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A bar with a sliding component to select from a range of values.
 * 
 * @author Evan
 * 
 */
public class Slider {
	private int startX, stopX;
	private int curX;
	private int y;
	private int minVal, maxVal;
	private int radius = 20;
	private boolean isPressed;

	/**
	 * Collision anywhere along slider, not just the circle
	 * 
	 * @param evtX
	 * @param evtY
	 * @return
	 */
	public void detectCollision(int evtX, int evtY) {
		setPressed(evtX > startX && evtX < stopX && evtY > y - radius
				&& evtY < y + radius);
	}

	public void draw(Canvas canvas, Paint paint) {
		canvas.drawText(minVal + "", startX,
				y + radius + paint.getFontSpacing(), paint);
		canvas.drawText(maxVal + "", stopX,
				y + radius + paint.getFontSpacing(), paint);
		canvas.drawLine(startX, y, stopX, y, paint);
		if (isPressed)
			canvas.drawCircle(curX, y, 1.5f * radius, paint);
		else
			canvas.drawCircle(curX, y, radius, paint);
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStopX() {
		return stopX;
	}

	public void setStopX(int stopX) {
		this.stopX = stopX;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getVal() {
		// somewhere between 0 and 1.0
		float multiplier = (float) (curX - startX) / (stopX - startX);
		return (int) (multiplier * (maxVal - minVal)) + minVal;
	}

	public int getCurX() {
		return curX;
	}

	public void setCurX(int curX) {
		if (curX < startX)
			this.curX = startX;
		else if (curX > stopX)
			this.curX = stopX;
		else
			this.curX = curX;
	}

	public int getMinVal() {
		return minVal;
	}

	public void setMinVal(int minVal) {
		this.minVal = minVal;
	}

	public int getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(int maxVal) {
		this.maxVal = maxVal;
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
}
