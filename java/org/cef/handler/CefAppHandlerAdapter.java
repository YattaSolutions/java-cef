// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.handler;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.callback.CefCommandLine;
import org.cef.callback.CefSchemeRegistrar;

/**
 * An abstract adapter class for managing app handler events.
 * The methods in this class are using a default implementation.
 * This class exists as convenience for creating handler objects.
 */
public abstract class CefAppHandlerAdapter implements CefAppHandler {
    private String[] args_;

    public CefAppHandlerAdapter(String[] args) {
        args_ = args;
    }

    @Override
    public void onBeforeCommandLineProcessing(String process_type, CefCommandLine command_line) {
    	System.out.println("CefAppHandlerAdapter:onBeforeCommandLineProcessing");
        if (process_type.isEmpty() && args_ != null) {
            // Forward switches and arguments from Java to Cef
            boolean parseSwitchesDone = false;
            for (String arg : args_) {
                if (parseSwitchesDone || arg.length() < 2) {
                    command_line.appendArgument(arg);
                    continue;
                }
                // Arguments with '--', '-' and, on Windows, '/' prefixes are considered switches.
                int switchCnt = arg.startsWith("--")
                        ? 2
                        : arg.startsWith("/") ? 1 : arg.startsWith("-") ? 1 : 0;
                switch (switchCnt) {
                    case 2:
                        // An argument of "--" will terminate switch parsing with all subsequent
                        // tokens
                        if (arg.length() == 2) {
                            parseSwitchesDone = true;
                            continue;
                        }
                    // FALL THRU
                    case 1: {
                        // Switches can optionally have a value specified using the '=' delimiter
                        // (e.g. "-switch=value").
                        String[] switchVals = arg.substring(switchCnt).split("=");
                        if (switchVals.length == 2) {
                            command_line.appendSwitchWithValue(switchVals[0], switchVals[1]);
                        } else {
                            command_line.appendSwitch(switchVals[0]);
                        }
                        break;
                    }
                    case 0:
                        command_line.appendArgument(arg);
                        break;
                }
            }
        }

        String value = CefApp.getJcefLibPath();
    	command_line.appendSwitchWithValue("--framework-dir-path", value);
    	command_line.appendSwitchWithValue("--main-bundle-path", value);
    	
    }

    @Override
    public boolean onBeforeTerminate() {
    	System.out.println("CefAppHandlerAdapter:onBeforeTerminate");
        // The default implementation does nothing
        return false;
    }

    @Override
    public void stateHasChanged(CefAppState state) {
    	System.out.println("CefAppHandlerAdapter:stateHasChanged: "+state);
        // The default implementation does nothing
    }

    @Override
    public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
    	System.out.println("CefAppHandlerAdapter:onRegisterCustomSchemes: "+registrar);
        // The default implementation does nothing
    }

    @Override
    public void onContextInitialized() {
    	System.out.println("CefAppHandlerAdapter:onContextInitialized");
        // The default implementation does nothing
    }

    @Override
    public CefPrintHandler getPrintHandler() {
    	System.out.println("CefAppHandlerAdapter:getPrintHandler");
        // The default implementation does nothing
        return null;
    }

    @Override
    public void onScheduleMessagePumpWork(long delay_ms) {
    	System.out.println("CefAppHandlerAdapter:onScheduleMessagePumpWork");
        CefApp.getInstance().doMessageLoopWork(delay_ms);
    }
}
