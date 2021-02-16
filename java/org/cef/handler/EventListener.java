package org.cef.handler;

public interface EventListener {
	/* Event can be
	 * 
	 * PAINTING=42
	 * 
	 * KeyEvent.KEY_TYPED = 400
	 * KeyEvent.KEY_PRESSED = 401
	 * KeyEvent.KEY_RELEASED = 402
	 * 
	 * MouseEvent.MOUSE_CLICKED = 500
	 * MouseEvent.MOUSE_PRESSED = 501
	 * MouseEvent.MOUSE_RELEASED = 502
	 * MouseEvent.MOUSE_MOVED = 503
	 * MouseEvent.MOUSE_ENTERED = 504
	 * MouseEvent.MOUSE_EXITED = 505
	 * MouseEvent.MOUSE_DRAGGED = 506
	 * MouseEvent.MOUSE_WHEEL = 507
	 */
	public static final int PAINTING=42;
	public void fireEvent(int event);
}
