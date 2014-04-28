// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.cef.callback;

import org.cef.handler.CefNative;

/**
 * Callback interface used to asynchronously continue a download.
 */
public interface CefBeforeDownloadCallback extends CefNative {
  /**
   * Call to continue the download.
   * @param downloadPath Set it to the full file path for the download
   * including the file name or leave blank to use the suggested name and
   * the default temp directory.
   * @param showDialog Set it to true if you do wish to show the default
   * "Save As" dialog.
   */
  public void Continue(String downloadPath, boolean showDialog);
}
