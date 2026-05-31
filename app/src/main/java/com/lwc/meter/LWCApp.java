package com.lwc.meter;
import android.app.Application;
import android.webkit.WebView;
public class LWCApp extends Application {
    @Override public void onCreate() { super.onCreate(); WebView.setWebContentsDebuggingEnabled(false); }
}