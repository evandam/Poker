package ecv.poker.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * A button has two states - up and down.
 * Collision detection is managed here,
 * and logic is handled here to render the correct bitmap.
 * 
 * @author Evan
 *
 */
public class MyButton {
	private Bitmap up, down;
	private int x, y;
	private boolean isPressed;
	
	public boolean detectCollision(int evtX, int evtY) {
		int width, height;
		if(up != null) {
			width = up.getWidth();
			height = up.getHeight();
		} else if (down != null) {
			width = down.getWidth();
			height = down.getWidth();
		} else
			return false;
		
		return evtX > x && evtX < x + width &&
				evtY > y && evtY < y + height;
	}	
	public void draw(Canvas canvas) {
		if(isPressed)
			canvas.drawBitmap(down, x, y, null);
		else
			canvas.drawBitmap(up, x, y, null);
	}	
	public Bitmap getUp() {
		return up;
	}
	public void setUp(Bitmap up) {
		this.up = up;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public boolean isPressed() {
		return isPressed;
	}
	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
	public Bitmap getDown() {
		return down;
	}
	public void setDown(Bitmap down) {
		this.down = down;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
