package ecv.poker.view;

/**
 * A button has two states - up and down. Collision detection is managed here,
 * and logic is handled here to render the correct bitmap.
 * 
 * @author Evan
 * 
 */
public class MyButton {
	private int upId, downId;
	private int x, y;
	private int width, height;
	private boolean isPressed;
	private boolean isEnabled;

	public MyButton(int upId, int downId) {
		isPressed = false;
		isEnabled = true;
		this.upId = upId;
		this.downId = downId;
	}

	/**
	 * Set the pressed flag to true if event is inside the button.
	 * 
	 * @param evtX
	 * @param evtY
	 */
	public void detectCollision(int evtX, int evtY) {
		if (isEnabled) {
			isPressed = evtX > x && evtX < x + width && evtY > y
					&& evtY < y + height;
		} else
			isPressed = false;
	}

	/**
	 * For either up or down, if enabled, -1 if not
	 * 
	 * @return
	 */
	public int getStateResId() {
		if (isEnabled) {
			if (isPressed)
				return downId;
			else
				return upId;
		} else
			return -1;
	}

	public int getUpId() {
		return upId;
	}

	public int getDownId() {
		return downId;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}

	/**
	 * Enable drawing and collision detection
	 */
	public void enable() {
		this.isEnabled = true;
	}

	/**
	 * Disable drawing and collision detection
	 */
	public void disable() {
		this.isEnabled = false;
	}
}
