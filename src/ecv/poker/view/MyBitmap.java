package ecv.poker.view;

/**
 * Wrapper for a resource ID and dimensions of a bitmap
 * @author evan
 *
 */
public class MyBitmap {
	private int resId;
	private int width, height;
	
	public MyBitmap(int resId, int width, int height) {
		this.setResId(resId);
		this.setWidth(width);
		this.setHeight(height);
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
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
}
