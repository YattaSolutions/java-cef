// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.browser;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.ByteBuffer;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.callback.CefDragData;
import org.cef.handler.CefRenderHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.jogamp.opengl.GLProfile;

/**
 * This class represents an off-screen rendered browser. The visibility of this
 * class is "package". To create a new CefBrowser instance, please use
 * CefBrowserFactory.
 */
class CefBrowserOsr extends CefBrowser_N implements CefRenderHandler {
	private CefRenderer renderer_;
	private GLCanvas canvas_;
	private long window_handle_ = 0;
	private Rectangle browser_rect_ = new Rectangle(0, 0, 1, 1); // Work around CEF issue #1437.
	private Point screenPoint_ = new Point(0, 0);
	private boolean isTransparent_;
	private ImageData sourceData;
	private PaletteData palette;

	CefBrowserOsr(CefClient client, String url, boolean transparent, CefRequestContext context) {
		this(client, url, transparent, context, null, null);
	}

	private CefBrowserOsr(CefClient client, String url, boolean transparent, CefRequestContext context,
			CefBrowserOsr parent, Point inspectAt) {
		super(client, url, context, parent, inspectAt);
		isTransparent_ = transparent;
		renderer_ = new CefRenderer(transparent);
	}

	@Override
	public void createImmediately() {
		// Create the browser immediately.
		createBrowserIfRequired(false);
	}

	@Override
	public Composite createSwtUiComponent(Composite parent) {
		createGLCanvas(parent);
		return canvas_;
	}

	@Override
	public CefRenderHandler getRenderHandler() {
		return this;
	}

	@Override
	protected CefBrowser_N createDevToolsBrowser(CefClient client, String url, CefRequestContext context,
			CefBrowser_N parent, Point inspectAt) {
		return new CefBrowserOsr(client, url, isTransparent_, context, (CefBrowserOsr) this, inspectAt);
	}

	/**
	private synchronized long getWindowHandle() {
		if (window_handle_ == 0) {
			NativeSurface surface = canvas_.getNativeSurface();
			if (surface != null) {
				surface.lockSurface();
				window_handle_ = getWindowHandle(surface.getSurfaceHandle());
				surface.unlockSurface();
				assert (window_handle_ != 0);
			}
		}
		return window_handle_;
	}*/

	private void createGLCanvas(final Composite parent) {
		GLProfile glprofile = GLProfile.getMaxFixedFunc(true);
		canvas_ = new GLCanvas(parent, SWT.NO_BACKGROUND, new GLData());
		Display display = Display.getDefault();
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
	    Color black = display.getSystemColor(SWT.COLOR_BLACK);
	    Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
	    Color red = display.getSystemColor(SWT.COLOR_RED);
	    Color green = display.getSystemColor(SWT.COLOR_GREEN);
	    Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		palette = new PaletteData(new RGB[] { black.getRGB(), white.getRGB(),
		        yellow.getRGB(), red.getRGB(), blue.getRGB(), green.getRGB() });		
		
		canvas_.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (null != sourceData) {					
					final Image source = new Image(e.display, sourceData);
					GC gc = e.gc;
					gc.drawImage(source, 0, 0);
				}
				
			}
		});
		
		// The GLContext will be re-initialized when changing displays, resulting in
		// calls to
		// dispose/init/reshape.
		/*
		canvas_.addGLEventListener(new GLEventListener() {
			@Override
			public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
				browser_rect_.setBounds(x, y, width, height);
				screenPoint_ = new Point(canvas_.getLocation().x, canvas_.getLocation().y);
				wasResized(width, height);
			}

			@Override
			public void init(GLAutoDrawable glautodrawable) {
				renderer_.initialize(glautodrawable.getGL().getGL2());
			}

			@Override
			public void dispose(GLAutoDrawable glautodrawable) {
				renderer_.cleanup(glautodrawable.getGL().getGL2());
			}

			@Override
			public void display(GLAutoDrawable glautodrawable) {
				renderer_.render(glautodrawable.getGL().getGL2());
			}
		});*/

		canvas_.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//sendMouseEvent(new java.awt.event.MouseEvent(null, java.awt.event.MouseEvent.MOUSE_CLICKED, (long)e.time, e.x, e.y, e.display.getClientArea().x + e.x, e.display.getClientArea().y + e.y, false, e.button));
			}

			@Override
			public void mouseDown(MouseEvent e) {
				//sendMouseEvent(new java.awt.event.MouseEvent(null, java.awt.event.MouseEvent.MOUSE_PRESSED, (long)e.time, e.x, e.y, e.display.getClientArea().x + e.x, e.display.getClientArea().y + e.y, false, e.button));
			}

			@Override
			public void mouseUp(MouseEvent e) {
				//sendMouseEvent(new java.awt.event.MouseEvent(null, java.awt.event.MouseEvent.MOUSE_RELEASED, (long)e.time, e.x, e.y, e.display.getClientArea().x + e.x, e.display.getClientArea().y + e.y, false, e.button));
			}
		});

		canvas_.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				//sendMouseEvent(new java.awt.event.MouseEvent(null, java.awt.event.MouseEvent.MOUSE_MOVED, (long)e.time, e.x, e.y, e.display.getClientArea().x + e.x, e.display.getClientArea().y + e.y, false, e.button));
			}
		});

		canvas_.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {}
			
			@Override
			public void mouseExit(MouseEvent e) {
				//sendMouseEvent(new java.awt.event.MouseEvent(null, java.awt.event.MouseEvent.MOUSE_EXITED, (long)e.time, e.x, e.y, e.display.getClientArea().x + e.x, e.display.getClientArea().y + e.y, false, e.button));
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
			//	sendMouseEvent(new java.awt.event.MouseEvent(null, java.awt.event.MouseEvent.MOUSE_ENTERED, (long)e.time, e.x, e.y, e.display.getClientArea().x + e.x, e.display.getClientArea().y + e.y, false, e.button));
			}
		});
		
		canvas_.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				//sendKeyEvent(new java.awt.event.KeyEvent(null, java.awt.event.KeyEvent.KEY_RELEASED, (long) e.time, extractModifiers(e), e.keyCode, e.character, e.keyLocation));
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//sendKeyEvent(new java.awt.event.KeyEvent(null, java.awt.event.KeyEvent.KEY_PRESSED, (long) e.time, extractModifiers(e), e.keyCode, e.character, e.keyLocation));
			}
			
			private int extractModifiers(KeyEvent e) {
				int modifiers = 0; // No modifiers
				if((e.stateMask & SWT.CTRL) == SWT.CTRL){
					modifiers |= java.awt.event.KeyEvent.CTRL_DOWN_MASK;
				}
				if((e.stateMask & SWT.ALT) == SWT.ALT){
					modifiers |= java.awt.event.KeyEvent.ALT_DOWN_MASK;
				}
				if((e.stateMask & SWT.ALT_GR) == SWT.ALT_GR){
					modifiers |= java.awt.event.KeyEvent.ALT_GRAPH_DOWN_MASK;
				}
				if((e.stateMask & SWT.COMMAND) == SWT.COMMAND){
					modifiers |= java.awt.event.KeyEvent.META_DOWN_MASK;
				}
				if((e.stateMask & SWT.MOD4) == SWT.MOD4){
					modifiers |= java.awt.event.KeyEvent.META_DOWN_MASK;
				}
				if((e.stateMask & SWT.CAPS_LOCK) == SWT.CAPS_LOCK){
					if((e.stateMask & SWT.SHIFT) == SWT.SHIFT) {
						// modifiers cancel each other
					} else {
						modifiers |= java.awt.event.KeyEvent.SHIFT_DOWN_MASK;
					}
				} else {
					if((e.stateMask & SWT.SHIFT) == SWT.SHIFT) {
						modifiers |= java.awt.event.KeyEvent.SHIFT_DOWN_MASK;
					}
				}
				return modifiers;
			}
		});
		
		// TODO Implement this
		// Connect the Canvas with a drag and drop listener.
		//new DropTarget(canvas_, new CefDropTargetListenerOsr(this));
	}

	@Override
	public Rectangle getViewRect(CefBrowser browser) {
		return browser_rect_;
	}

	@Override
	public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
		Point screenPoint = new Point(screenPoint_);
		screenPoint.translate(viewPoint.x, viewPoint.y);
		return screenPoint;
	}

	@Override
	public void onPopupShow(CefBrowser browser, boolean show) {
		if (!show) {
			renderer_.clearPopupRects();
			invalidate();
		}
	}

	@Override
	public void onPopupSize(CefBrowser browser, Rectangle size) {
		renderer_.onPopupSize(size);
	}

	@Override
	public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width,
			int height) {
		
		
		sourceData = new ImageData(16, 16, 4, palette, 1, buffer.array());
		canvas_.redraw();
		
		/*
		// if window is closing, canvas_ or opengl context could be null
		final GLContext context = canvas_ != null ? canvas_.getContext() : null;

		if (context == null) {
			return;
		}

		// This result can occur due to GLContext re-initialization when changing
		// displays.
		if (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
			return;
		}

		
		renderer_.onPaint(canvas_.getGL().getGL2(), popup, dirtyRects, buffer, width, height);
		context.release();
		CefApp.getGuiHandler().asyncExec(new Runnable() {
			public void run() {
				canvas_.display();
			}
		});
		*/
	}

	@Override
	public void onCursorChange(CefBrowser browser, final int cursorType) {
		CefApp.getGuiHandler().asyncExec(new Runnable() {
			public void run() {
				canvas_.setCursor(new Cursor(Display.getCurrent(), cursorType));
			}
		});
	}

	@Override
	public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
		// TODO(JCEF) Prepared for DnD support using OSR mode.
		return false;
	}

	@Override
	public void updateDragCursor(CefBrowser browser, int operation) {
		// TODO(JCEF) Prepared for DnD support using OSR mode.
	}

	private void createBrowserIfRequired(boolean hasParent) {
		long windowHandle = 0;
		if (hasParent) {
			//windowHandle = getWindowHandle();
		}

		if (getNativeRef("CefBrowser") == 0) {
			if (getParentBrowser() != null) {
				
				// TODO
				//createDevTools(getParentBrowser(), getClient(), windowHandle, true, isTransparent_, null,
				//		getInspectAt());
			} else {
				createBrowser(getClient(), windowHandle, getUrl(), true, isTransparent_, null, getRequestContext());
			}
		} else {
			// OSR windows cannot be reparented after creation.
			setFocus(true);
		}
	}

	@Override
	public Component getUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}
}
