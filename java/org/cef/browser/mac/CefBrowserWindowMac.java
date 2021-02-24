// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.browser.mac;

import org.cef.browser.CefBrowserWindow;

import java.awt.Component;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.Field;

import sun.awt.AWTAccessor;
import sun.lwawt.LWComponentPeer;
import sun.lwawt.PlatformWindow;
import sun.lwawt.macosx.CFRetainedResource;
import sun.lwawt.macosx.CPlatformView;
import sun.lwawt.macosx.CPlatformWindow;
import sun.lwawt.macosx.CViewPlatformEmbeddedFrame;

public class CefBrowserWindowMac implements CefBrowserWindow {
    @Override
    public long getWindowHandle(Component comp) {
        final long[] result = new long[1];
        while (comp != null) {
            if (comp.isLightweight()) {
                comp = comp.getParent();
                continue;
            }
            System.out.println(comp.getClass().getSimpleName());
            
//            Class<?> cls = Class.forName("sun.awt.AWTAccessor");
//            if(cls != null) {
//            	cls.getDeclaredMethods()
//            }
            CefBrowserWindow browserWindow = (CefBrowserWindow) cls.newInstance();
            
            if (browserWindow != null) {
                return browserWindow.getWindowHandle(component);
            
            
            ComponentPeer peer = AWTAccessor.getComponentAccessor().getPeer(comp);
            if (peer instanceof LWComponentPeer) {
            	Object nativeActionProxy;
            	PlatformWindow pWindow = ((LWComponentPeer<?,?>) peer).getPlatformWindow();

                if (pWindow instanceof CPlatformWindow) {
                    ((CPlatformWindow) pWindow).execute(new CFRetainedResource.CFNativeAction() {
                        @Override
                        public void run(long l) {
                            result[0] = l;
                        }
                    });
                    break;
                }else if(pWindow instanceof CViewPlatformEmbeddedFrame) {
                	// SL
                	CViewPlatformEmbeddedFrame p=(CViewPlatformEmbeddedFrame)pWindow;
                	try {
						Field f = p.getClass().getDeclaredField("view");
						f.setAccessible(true);
						Object object = f.get(p);
						System.out.println(object);
						if(object instanceof CPlatformView) {
							CPlatformView view = (CPlatformView) object;
							view.execute(new CFRetainedResource.CFNativeAction() {
                        @Override
                        public void run(long l) {
                            result[0] = l;
                        }
                    });
						}
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	long handle =  ((CViewPlatformEmbeddedFrame)pWindow).getNSViewPtr();
                	System.out.println(handle+":::"+result[0]);
                	return result[0];
                }
            }
            comp = comp.getParent();
        }
        return result[0];
    }
}
