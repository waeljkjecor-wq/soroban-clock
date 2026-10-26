package com.soroban.clock;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.webkit.JavascriptInterface;
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

        web.addJavascriptInterface(new Bridge(ctx), "AndroidBridge");
        web.setWebViewClient(new WebViewClient());
        web.loadUrl("file:///android_asset/index.html#full");
        return web;
    }

    /** جسر بين الصفحة والتطبيق: حفظ السمة للخلفية الحيّة، وفتح شاشة تعيين الخلفية. */
    static final class Bridge {
        private final Context ctx;

        Bridge(Context ctx) { this.ctx = ctx; }

        @JavascriptInterface
        public void setSkin(String id) {
            ctx.getSharedPreferences(Skins.PREFS, Context.MODE_PRIVATE)
                    .edit().putString(Skins.KEY, id).apply();
        }

        @JavascriptInterface
        public void setAsWallpaper() {
            Intent i = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(ctx, ClockWallpaper.class));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                ctx.startActivity(i);
            } catch (Exception e) {
                try {
                    Intent j = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(j);
                } catch (Exception ignored) {}
            }
        }
    }
}
