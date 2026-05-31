package com.lwc.meter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
public class AndroidBridge {
    private final Context ctx;
    AndroidBridge(Context ctx) { this.ctx = ctx; }
    @JavascriptInterface public String getPlatform() { return "android"; }
    @JavascriptInterface
    public void openWhatsApp(String phone, String msg) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/"+phone+"?text="+Uri.encode(msg)));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); ctx.startActivity(i);
    }
}