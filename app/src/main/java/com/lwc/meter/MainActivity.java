package com.lwc.meter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String LOCAL_URL = "file:///android_asset/index.html";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setAllowFileAccess(true);
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setTextZoom(100);

        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(ws, true);
        }

        webView.addJavascriptInterface(new AndroidBridge(this), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                String scheme = request.getUrl().getScheme();

                // ── فتح التطبيقات الخارجية ──────────────────
                // واتساب
                if (url.startsWith("https://wa.me") ||
                    url.startsWith("https://api.whatsapp.com") ||
                    url.startsWith("whatsapp://")) {
                    openExternalApp(url);
                    return true;
                }

                // موبيكاش — روابط التطبيق المباشرة
                if (url.startsWith("mobicash://") ||
                    url.startsWith("ly.mobicash://") ||
                    url.contains("mobicash.ly") ||
                    url.contains("mobicash.com.ly")) {
                    openExternalApp(url);
                    return true;
                }

                // روابط هاتف
                if (scheme != null && scheme.equals("tel")) {
                    openExternalApp(url);
                    return true;
                }

                // روابط بريد
                if (scheme != null && scheme.equals("mailto")) {
                    openExternalApp(url);
                    return true;
                }

                // intent:// روابط
                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            // فتح المتجر إذا التطبيق غير مثبّت
                            String pkg = intent.getPackage();
                            if (pkg != null) {
                                openExternalApp("https://play.google.com/store/apps/details?id=" + pkg);
                            }
                        }
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }

                // الروابط الخارجية الأخرى (غير السيرفر المحلي وغير الملفات)
                if (!url.startsWith("file://") &&
                    !url.contains("192.168.0.100") &&
                    !url.contains("localhost") &&
                    !url.contains("127.0.0.1") &&
                    (url.startsWith("https://") || url.startsWith("http://"))) {
                    // روابط HTTP خارجية — افتح في المتصفح
                    openExternalApp(url);
                    return true;
                }

                return false;
            }

            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) v.loadUrl(LOCAL_URL);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(LOCAL_URL);
    }

    private void openExternalApp(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // التطبيق غير مثبّت — افتح في المتصفح
            try {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
            } catch (Exception ex) {
                // تجاهل
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onResume() { super.onResume(); webView.onResume(); }
    @Override protected void onPause()  { super.onPause();  webView.onPause();  }
}
