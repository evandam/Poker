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
	
	/**
	 * Set the pressed flag to true if event is inside the button.
	 * @param evtX
	 * @param evtY
	 */
	public void detectCollision(int evtX, int evtY) {
		if(up != null) {
			isPressed = evtX > x && evtX < x + up.getWidth() &&
					evtY > y && evtY < y + up.getHeight();
		} else if (down != null) {
			isPressed = evtX > x && evtX < x + down.getWidth() &&
					evtY > y && evtY < y + down.getHeight();
		} else {
			isPressed = false;
		} 
		
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
	public Bitmap getDown() {
		return down;
	}
	public void setDown(Bitmap down) {
		this.down = down;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
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
	public boolean isPressed() {
		return isPressed;
	}
	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
}
