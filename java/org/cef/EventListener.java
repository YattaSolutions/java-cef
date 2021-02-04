package org.cef;

public interface EventListener {
	public static final int MOUSE=0;
	public static final int KEYBOARD=1;
	public static final int PAINTING=42;
	public boolean fireEvent(int type, int event, long delay_ms);
}
