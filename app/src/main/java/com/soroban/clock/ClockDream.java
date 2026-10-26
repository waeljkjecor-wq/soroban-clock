package com.soroban.clock;

import android.service.dreams.DreamService;
import android.webkit.WebView;

/** شاشة توقف (Screen saver) تعرض الساعة أثناء الشحن. */
public class ClockDream extends DreamService {
    private WebView web;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);   // أي لمسة تُنهي شاشة التوقف
        setFullscreen(true);
        setScreenBright(true);
        web = ClockWeb.create(this);
        setContentView(web);
    }

    @Override
    public void onDetachedFromWindow() {
        if (web != null) { web.destroy(); web = null; }
        super.onDetachedFromWindow();
    }
}
