package com.soroban.clock;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** ينشئ WebView يعرض الساعة من ملفات التطبيق، بلا إنترنت. */
final class ClockWeb {
    private ClockWeb() {}

    static WebView create(Context ctx) {
        WebView web = new WebView(ctx);
        web.setBackgroundColor(Color.BLACK);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // لحفظ السمة المختارة

        web.setWebViewClient(new WebViewClient());
        web.loadUrl("file:///android_asset/index.html#full");
        return web;
    }
}
