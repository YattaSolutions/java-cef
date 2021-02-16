// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.browser;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.MenuSelectionManager;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.OS;
import org.cef.callback.CefDragData;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.cef.handler.EventListener;
import org.cef.util.SWTEvent;
import org.cef.util.SWTUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.jogamp.nativewindow.NativeSurface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.GLBuffers;

import jogamp.graph.geom.plane.AffineTransform;

/**
 * This class represents an off-screen rendered browser.
 * The visibility of this class is "package". To create a new
 * CefBrowser instance, please use CefBrowserFactory.
 */
public class CefBrowserOsr_SWT extends CefBrowser_N implements CefRenderHandler {
    private CefRenderer renderer_;
    private GLCanvas canvas_;
    private long window_handle_ = 0;
    private boolean justCreated_ = false;
    private java.awt.Rectangle browser_rect_ = new java.awt.Rectangle(0, 0, 1, 1); // Work around CEF issue #1437.
    private Point screenPoint_ = new Point(0, 0);
    private float scaleFactor_ = 1.0f;
    private int depth = 32;
    private int depth_per_component = 8;
    private boolean isTransparent_;
	private Composite parentComponent;
	private java.awt.Point inspectAt_;

	public CefBrowserOsr_SWT(CefClient client, String url, boolean transparent, CefRequestContext context, Composite parentComponent) {
        this(client, url, transparent, context, parentComponent, null, parentComponent);
    }

	public CefBrowserOsr_SWT(CefClient client, String url, boolean transparent,
            CefRequestContext context, Composite parentComponent, CefBrowserOsr_SWT parent, Object inspectAt) {
        super(client, url, context, parent);
        isTransparent_ = transparent;
        this.parentComponent = parentComponent;
        
        if(inspectAt instanceof java.awt.Point) {
        	java.awt.Point guiPoint = (java.awt.Point) inspectAt;
        	this.inspectAt_ = new java.awt.Point(guiPoint.x, guiPoint.y);
        }
        renderer_ = new CefRenderer(transparent);
        createGLCanvas();
    }

    @Override
    public void createImmediately() {
        justCreated_ = true;
        // Create the browser immediately.
        createBrowserIfRequired(false);
    }

    @Override
    public Object getUIComponent() {
        return canvas_;
    }

    @Override
    public CefRenderHandler getRenderHandler() {
        return this;
    }

    @Override
    protected CefBrowser_N createDevToolsBrowser(CefClient client, String url,
            CefRequestContext context, CefBrowser_N parent, java.awt.Point inspectAt) {
        return new CefBrowserOsr_SWT(
                client, url, isTransparent_, context, this.parentComponent, (CefBrowserOsr_SWT) this, inspectAt);
    }

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
    }

    @SuppressWarnings("serial")
    private void createGLCanvas() {
        GLProfile glprofile = GLProfile.getMaxFixedFunc(true);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
//        canvas_ = new GLCanvas(this.parentComponent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        canvas_ = new GLCanvas(this.parentComponent, SWT.EMBEDDED | SWT.NO_BACKGROUND, glcapabilities, null);
        canvas_.setLayout(new FillLayout());
        canvas_.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent arg0) {
				createBrowserIfRequired(true);
			}
		});
        
//            private Method scaleFactorAccessor = null;
//            private boolean removed_ = true;

//FXIME SL            @Override
//            public void paint(Graphics g) {
//                createBrowserIfRequired(true);
//                if (g instanceof Graphics2D) {
//                    GraphicsConfiguration config = ((Graphics2D) g).getDeviceConfiguration();
//                    depth = config.getColorModel().getPixelSize();
//                    depth_per_component = config.getColorModel().getComponentSize()[0];
//
//                    if (OS.isMacintosh()
//                            && System.getProperty("java.runtime.version").startsWith("1.8")) {
//                        // This fixes a weird thing on MacOS: the scale factor being read from
//                        // getTransform().getScaleX() is incorrect for Java 8 VMs; it is always
//                        // 1, even though Retina display scaling of window sizes etc. is
//                        // definitely ongoing somewhere in the lower levels of AWT. This isn't
//                        // too big of a problem for us, because the transparent scaling handles
//                        // the situation, except for one thing: the screenshot-grabbing
//                        // code below, which reads from the OpenGL context, must know the real
//                        // scale factor, because the image to be read is larger by that factor
//                        // and thus a bigger buffer is required. This is why there's some
//                        // admittedly-ugly reflection magic going on below that's able to get
//                        // the real scale factor.
//                        // All of this is not relevant for either Windows or MacOS JDKs > 8,
//                        // for which the official "getScaleX()" approach works fine.
//                        try {
//                            if (scaleFactorAccessor == null) {
//                                scaleFactorAccessor = getClass()
//                                                              .getClassLoader()
//                                                              .loadClass("sun.awt.CGraphicsDevice")
//                                                              .getDeclaredMethod("getScaleFactor");
//                            }
//                            Object factor = scaleFactorAccessor.invoke(config.getDevice());
//                            if (factor instanceof Integer) {
//                                scaleFactor_ = ((Integer) factor).doubleValue();
//                            } else {
//                                scaleFactor_ = 1.0;
//                            }
//                        } catch (InvocationTargetException | IllegalAccessException
//                                | IllegalArgumentException | NoSuchMethodException
//                                | SecurityException | ClassNotFoundException exc) {
//                            scaleFactor_ = 1.0;
//                        }
//                    } else {
//                        scaleFactor_ = ((Graphics2D) g).getTransform().getScaleX();
//                    }
//                }
//                super.paint(g);
//            }
        
//        @Override
//        public void addNotify() {
//            super.addNotify();
//            if (removed_) {
//                notifyAfterParentChanged();
//                removed_ = false;
//            }
//        }
//
//        @Override
//        public void removeNotify() {
//            if (!removed_) {
//                if (!isClosed()) {
//                    notifyAfterParentChanged();
//                }
//                removed_ = true;
//            }
//            super.removeNotify();
//        }

//

//        };

        // The GLContext will be re-initialized when changing displays, resulting in calls to
        // dispose/init/reshape.
        canvas_.addGLEventListener(new GLEventListener() {
			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			  int newWidth = width;
               int newHeight = height;
               if (OS.isMacintosh()) {
                   // HiDPI display scale correction support code
                   // For some reason this does seem to be necessary on MacOS only.
                   // If doing this correction on Windows, the browser content would be too
                   // small and in the lower left corner of the canvas only.
                   newWidth = (int) (width / scaleFactor_);
                   newHeight = (int) (height / scaleFactor_);
               }
               browser_rect_.setBounds(x, y, newWidth, newHeight);
//               org.eclipse.swt.graphics.Point display = canvas_.toDisplay(1, 1);
               org.eclipse.swt.graphics.Point display = canvas_.getLocation();
               screenPoint_ = new Point(display.x+ canvas_.getSize().x, display.y + canvas_.getSize().y);
               wasResized(newWidth, newHeight);				
			}
			
			@Override
			public void init(GLAutoDrawable drawable) {
              renderer_.initialize(drawable.getGL().getGL2());
			}
			
			@Override
			public void dispose(GLAutoDrawable drawable) {
				renderer_.cleanup(drawable.getGL().getGL2());
			}
			
			@Override
			public void display(GLAutoDrawable drawable) {
              renderer_.render(drawable.getGL().getGL2());
			}
        });
        
        
        canvas_.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_RELEASED), SWTEvent.MOUSE_RELEASED);
			}
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_PRESSED), SWTEvent.MOUSE_PRESSED);
			}
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_CLICKED), SWTEvent.MOUSE_PRESSED);
			}
		});
        
        canvas_.addMouseMoveListener( e ->{
			sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_MOVED), SWTEvent.MOUSE_MOVED);
		});
        
        canvas_.addMouseWheelListener(e -> {
			sendMouseWheelEvent(SWTUtils.toAwtMouseWheelEvent(e), SWTEvent.MOUSE_WHEEL);
		});
        
        canvas_.addMouseTrackListener(new MouseTrackListener() {
			@Override
			public void mouseHover(org.eclipse.swt.events.MouseEvent e) {
				sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_ENTERED), SWTEvent.MOUSE_ENTERED);
			}
			
			@Override
			public void mouseExit(org.eclipse.swt.events.MouseEvent e) {
				sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_EXITED), SWTEvent.MOUSE_EXITED);
			}
			
			@Override
			public void mouseEnter(org.eclipse.swt.events.MouseEvent e) {
				sendMouseEvent(SWTUtils.toAwtMouseEvent(e, MouseEvent.MOUSE_ENTERED), SWTEvent.MOUSE_ENTERED);
			}
		});
        
        canvas_.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				sendKeyEvent(SWTUtils.toAwtKeyEvent(e), SWTEvent.KEY_RELEASED);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				sendKeyEvent(SWTUtils.toAwtKeyEvent(e), SWTEvent.KEY_PRESSED);
			}
		});
        
        canvas_.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				setFocus(false);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// Dismiss any Java menus that are currently displayed.
				MenuSelectionManager.defaultManager().clearSelectedPath();
				setFocus(true);
			}
		});

        // Connect the Canvas with a drag and drop listener.
//FIME SL        new DropTarget(canvas_, new CefDropTargetListenerOsr(this));
    }
    
    @Override
    public java.awt.Rectangle getViewRect(CefBrowser browser) {
        return browser_rect_;
    }

    @Override
    public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
    	Point screenPoint = new Point(screenPoint_.x, screenPoint_.y);
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
    public void onPaint(CefBrowser browser, boolean popup, java.awt.Rectangle[] dirtyRects,
            ByteBuffer buffer, int width, int height) {
        // if window is closing, canvas_ or opengl context could be null
        final GLContext context = canvas_ != null ? canvas_.getContext() : null;

        if (context == null) {
            return ;
        }

        // This result can occur due to GLContext re-initialization when changing displays.
        if (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
            return  ;
        }
        if(this.eventListener != null) {
        	this.eventListener.fireEvent(EventListener.PAINTING);
        }

		renderer_.onPaint(canvas_.getGL().getGL2(), popup, dirtyRects, buffer, width, height);
		context.release();
		CefApp.getGuiHandler().asyncExec(new Runnable() {
            public void run() {
				canvas_.display();
	}
        });
    }

    @Override
    public boolean onCursorChange(CefBrowser browser, final int cursorType) {
    	canvas_.setCursor(new Cursor(Display.getCurrent( ), SWTUtils.toSWTCursorEvent(cursorType)));
        // OSR always handles the cursor change.
        return true;
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
            windowHandle = getWindowHandle();
        }

        if (getNativeRef("CefBrowser") == 0) {
            if (getParentBrowser() != null) {
                createDevTools(getParentBrowser(), getClient(), windowHandle, true, isTransparent_,
                        null, inspectAt_);
            } else {
                createBrowser(getClient(), windowHandle, getUrl(), true, isTransparent_, null, getRequestContext());
            }
        } else if (hasParent && justCreated_) {
            notifyAfterParentChanged();
            setFocus(true);
            justCreated_ = false;
        }
    }

    private void notifyAfterParentChanged() {
        // With OSR there is no native window to reparent but we still need to send the
        // notification.
        getClient().onAfterParentChanged(this);
    }

	private Rectangle getBounds() {
		return new Rectangle(browser_rect_.x, browser_rect_.y, browser_rect_.width, browser_rect_.height);
	}
    
    @Override
    public boolean getScreenInfo(CefBrowser browser, CefScreenInfo screenInfo) {
        screenInfo.Set(scaleFactor_, depth, depth_per_component, false, getBounds(), getBounds());

        return true;
    }

    @Override
    public CompletableFuture<BufferedImage> createScreenshot(boolean nativeResolution) {
        int width = (int) (canvas_.getSize().x * scaleFactor_);
        int height = (int) (canvas_.getSize().y * scaleFactor_);

        // In order to grab a screenshot of the browser window, we need to get the OpenGL internals
        // from the GLCanvas that displays the browser.
        GL2 gl = canvas_.getGL().getGL2();
        int textureId = renderer_.getTextureID();

        // This mirrors the two ways in which CefRenderer may render images internally - either via
        // an incrementally updated texture that is the same size as the window and simply rendered
        // onto a textured quad by graphics hardware, in which case we capture the data directly
        // from this texture, or by directly writing pixels into the OpenGL framebuffer, in which
        // case we directly read those pixels back. The latter is the way chosen if there is no
        // hardware rasterizer capability detected. We can simply distinguish both approaches by
        // looking whether the textureId of the renderer is a valid (non-zero) one.
        boolean useReadPixels = (textureId == 0);

        // This Callable encapsulates the pixel-reading code. After running it, the screenshot
        // BufferedImage contains the grabbed image.
        final Callable<BufferedImage> pixelGrabberCallable = new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() {
                BufferedImage screenshot =
                        new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                ByteBuffer buffer = GLBuffers.newDirectByteBuffer(width * height * 4);

                gl.getContext().makeCurrent();
                try {
                    if (useReadPixels) {
                        // If pixels are copied directly to the framebuffer, we also directly read
                        // them back.
                        gl.glReadPixels(
                                0, 0, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
                    } else {
                        // In this case, read the texture pixel data from the previously-retrieved
                        // texture ID
                        gl.glEnable(GL.GL_TEXTURE_2D);
                        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId);
                        gl.glGetTexImage(
                                GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
                        gl.glDisable(GL.GL_TEXTURE_2D);
                    }
                } finally {
                    gl.getContext().release();
                }

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        // The OpenGL functions only support RGBA, while Java BufferedImage uses
                        // ARGB. We must convert.
                        int r = (buffer.get() & 0xff);
                        int g = (buffer.get() & 0xff);
                        int b = (buffer.get() & 0xff);
                        int a = (buffer.get() & 0xff);
                        int argb = (a << 24) | (r << 16) | (g << 8) | (b << 0);
                        // If pixels were read from the framebuffer, we have to flip the resulting
                        // image on the Y axis, as the OpenGL framebuffer's y axis starts at the
                        // bottom of the image pointing "upwards", while BufferedImage has the
                        // origin in the upper left corner. This flipping is done when drawing into
                        // the BufferedImage.
                        screenshot.setRGB(x, useReadPixels ? (height - y - 1) : y, argb);
                    }
                }

                if (!nativeResolution && scaleFactor_ != 1.0) {
                    // HiDPI images should be resized down to "normal" levels
                    BufferedImage resized =
                            new BufferedImage((int) (screenshot.getWidth() / scaleFactor_),
                                    (int) (screenshot.getHeight() / scaleFactor_),
                                    BufferedImage.TYPE_INT_ARGB);
                    AffineTransform tempTransform = new AffineTransform();
//                    java.awt.AffineTransform
                    tempTransform.setToScale(1.0f / scaleFactor_, 1.0f / scaleFactor_);
//FIXME SL                    AffineTransformOp tempScaleOperation =
//                            new AffineTransformOp(tempTransform, AffineTransformOp.TYPE_BILINEAR);
//                    resized = tempScaleOperation.filter(screenshot, resized);
                    return resized;
                } else {
                    return screenshot;
                }
            }
        };

        if (CefApp.getGuiHandler().currentThreadIsGuiThread()) {
            // If called on the AWT event thread, just access the GL API
            try {
                BufferedImage screenshot = pixelGrabberCallable.call();
                return CompletableFuture.completedFuture(screenshot);
            } catch (Exception e) {
                CompletableFuture<BufferedImage> future = new CompletableFuture<BufferedImage>();
                future.completeExceptionally(e);
                return future;
            }
        } else {
            // If called from another thread, register a GLEventListener and trigger an async
            // redraw, during which we use the GL API to grab the pixel data. An unresolved Future
            // is returned, on which the caller can wait for a result (but not with the Event
            // Thread, as we need that for pixel grabbing, which is why there's a safeguard in place
            // to catch that situation if it accidentally happens).
            CompletableFuture<BufferedImage> future = new CompletableFuture<BufferedImage>() {
                private void safeguardGet() {
                    if (CefApp.getGuiHandler().currentThreadIsGuiThread()) {
                        throw new RuntimeException(
                                "Waiting on this Future using the AWT Event Thread is illegal, "
                                + "because it can potentially deadlock the thread.");
                    }
                }

                @Override
                public BufferedImage get() throws InterruptedException, ExecutionException {
                    safeguardGet();
                    return super.get();
                }

                @Override
                public BufferedImage get(long timeout, TimeUnit unit)
                        throws InterruptedException, ExecutionException, TimeoutException {
                    safeguardGet();
                    return super.get(timeout, unit);
                }
            };
            canvas_.addGLEventListener(new GLEventListener() {
                @Override
                public void reshape(
                        GLAutoDrawable aDrawable, int aArg1, int aArg2, int aArg3, int aArg4) {
                    // ignore
                }

                @Override
                public void init(GLAutoDrawable aDrawable) {
                    // ignore
                }

                @Override
                public void dispose(GLAutoDrawable aDrawable) {
                    // ignore
                }

                @Override
                public void display(GLAutoDrawable aDrawable) {
                    canvas_.removeGLEventListener(this);
                    try {
                        future.complete(pixelGrabberCallable.call());
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }
            });

            // This repaint triggers an indirect call to the listeners' display method above, which
            // ultimately completes the future that we return immediately.
//SL FIXME            canvas_.repaint();

            return future;
        }
    }
    
    @Override
	public void setEventListener(EventListener listener) {
		this.eventListener = listener;
	}
}
