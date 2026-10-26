package com.soroban.clock;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.Locale;

/** خلفية حيّة ترسم ساعة السوروبان وتحرّك الخرز كل ثانية. */
public class ClockWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new ClockEngine();
    }

    private class ClockEngine extends Engine
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final long ANIM_MS = 320;
        private static final int[] LAYOUT = {1, 1, 0, 1, 1, 0, 1, 1}; // 1 = عمود، 0 = فاصل

        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        private SharedPreferences prefs;
        private Skins.Skin skin;
        private boolean visible;
        private boolean first = true;
        private long animStart = -1;
        private String timeText = "";

        // لكل عمود: [0] الخرزة العلوية، [1..4] الخرزات السفلية. القيمة 1 = محسوبة
        private final float[][] from = new float[6][5];
        private final float[][] cur = new float[6][5];
        private final float[][] target = new float[6][5];

        private final Runnable tick = this::onTick;
        private final Runnable frame = this::onFrame;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            prefs = getSharedPreferences(Skins.PREFS, MODE_PRIVATE);
            prefs.registerOnSharedPreferenceChangeListener(this);
            skin = Skins.get(prefs.getString(Skins.KEY, "wood"));
            textPaint.setTypeface(Typeface.MONOSPACE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setLetterSpacing(0.2f);
        }

        @Override
        public void onDestroy() {
            handler.removeCallbacksAndMessages(null);
            if (prefs != null) prefs.unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences p, String key) {
            if (Skins.KEY.equals(key)) {
                skin = Skins.get(p.getString(Skins.KEY, "wood"));
                draw();
            }
        }

        @Override
        public void onVisibilityChanged(boolean v) {
            visible = v;
            handler.removeCallbacks(tick);
            handler.removeCallbacks(frame);
            if (v) {
                first = true;
                onTick();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            super.onSurfaceChanged(holder, format, w, h);
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            visible = false;
            handler.removeCallbacksAndMessages(null);
            super.onSurfaceDestroyed(holder);
        }

        /* ---------- التوقيت ---------- */
        private void onTick() {
            if (!visible) return;
            Calendar c = Calendar.getInstance();
            int hh = c.get(Calendar.HOUR_OF_DAY), mm = c.get(Calendar.MINUTE), ss = c.get(Calendar.SECOND);
            timeText = String.format(Locale.US, "%02d:%02d:%02d", hh, mm, ss);
            int[] d = {hh / 10, hh % 10, mm / 10, mm % 10, ss / 10, ss % 10};

            for (int r = 0; r < 6; r++) {
                target[r][0] = d[r] >= 5 ? 1f : 0f;
                int earthOn = d[r] % 5;
                for (int i = 0; i < 4; i++) target[r][i + 1] = i < earthOn ? 1f : 0f;
            }

            if (first) {
                for (int r = 0; r < 6; r++) System.arraycopy(target[r], 0, cur[r], 0, 5);
                first = false;
                animStart = -1;
                draw();
            } else {
                for (int r = 0; r < 6; r++) System.arraycopy(cur[r], 0, from[r], 0, 5);
                animStart = SystemClock.uptimeMillis();
                handler.removeCallbacks(frame);
                onFrame();
            }

            long now = System.currentTimeMillis();
            handler.postDelayed(tick, 1000 - (now % 1000) + 10);
        }

        private void onFrame() {
            if (!visible || animStart < 0) return;
            float t = Math.min(1f, (SystemClock.uptimeMillis() - animStart) / (float) ANIM_MS);
            float e = backOut(t);
            for (int r = 0; r < 6; r++)
                for (int b = 0; b < 5; b++)
                    cur[r][b] = from[r][b] + (target[r][b] - from[r][b]) * e;
            draw();
            if (t < 1f) handler.postDelayed(frame, 16);
            else animStart = -1;
        }

        private float backOut(float t) {
            float s = 1.4f;
            t -= 1f;
            return t * t * ((s + 1f) * t + s) + 1f;
        }

        /* ---------- الرسم ---------- */
        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) render(c);
            } catch (Exception ignored) {
            } finally {
                if (c != null) {
                    try { holder.unlockCanvasAndPost(c); } catch (Exception ignored) {}
                }
            }
        }

        private void resetPaint() {
            paint.reset();
            paint.setAntiAlias(true);
        }

        private void render(Canvas c) {
            final Skins.Skin s = skin;
            final int W = c.getWidth(), H = c.getHeight();

            // الخلفية
            resetPaint();
            paint.setShader(new RadialGradient(W / 2f, H * 0.3f, Math.max(W, H) * 0.9f,
                    s.bg0, s.bg1, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, H, paint);

            // المقاسات بالنسبة لعرض الخرزة
            float bw = Math.min(W * 0.88f / 9.92f, H * 0.45f / 4.4f);
            float beadH = .52f * bw, travel = .35f * bw, colW = 1.2f * bw, beamH = .24f * bw;
            float pad = .43f * bw, gap = .18f * bw, gapEl = .3f * bw;
            float edge = s.edgeW * bw;
            float fw = 2 * pad + 6 * colW + 2 * gapEl + 7 * gap;
            float fh = 2 * pad + beadH + travel + beamH + 4 * beadH + travel;
            float left = (W - fw) / 2f;
            float top = H * 0.56f - fh / 2f;
            float rad = s.radius * bw;

            // الإطار مع الظل والحافة
            resetPaint();
            paint.setColor(s.edgeW > 0 ? s.edge : s.frame1);
            paint.setShadowLayer(bw * .5f, 0, bw * .25f, s.shadow);
            rect.set(left - edge, top - edge, left + fw + edge, top + fh + edge);
            c.drawRoundRect(rect, rad, rad, paint);

            resetPaint();
            paint.setShader(new LinearGradient(0, top, 0, top + fh, s.frame0, s.frame1, Shader.TileMode.CLAMP));
            float ir = Math.max(0f, rad - edge);
            rect.set(left, top, left + fw, top + fh);
            c.drawRoundRect(rect, ir, ir, paint);

            // مواضع الأعمدة والفواصل
            float[] rodX = new float[6];
            float[] gapX = new float[2];
            float x = left + pad;
            int ri = 0, gi = 0;
            for (int i = 0; i < LAYOUT.length; i++) {
                if (LAYOUT[i] == 1) { rodX[ri++] = x + colW / 2f; x += colW; }
                else { gapX[gi++] = x + gapEl / 2f; x += gapEl; }
                x += gap;
            }

            float y0 = top + pad;
            float beamTop = y0 + beadH + travel;
            float earthTop = beamTop + beamH;
            float rodBottom = top + fh - pad;

            // القضبان
            resetPaint();
            paint.setColor(s.rod);
            float rw = Math.max(2f, .065f * bw);
            for (float cx : rodX) c.drawRect(cx - rw / 2f, y0, cx + rw / 2f, rodBottom, paint);

            // الخرز
            for (int r = 0; r < 6; r++) {
                float cx = rodX[r];
                drawBead(c, s, cx, y0 + cur[r][0] * travel, bw, beadH,
                        target[r][0] > .5f, s.heaven);
                for (int i = 0; i < 4; i++) {
                    float y = earthTop + i * beadH + (1f - cur[r][i + 1]) * travel;
                    int col = s.earth != null ? s.earth[i] : s.bead;
                    drawBead(c, s, cx, y, bw, beadH, target[r][i + 1] > .5f, col);
                }
            }

            // العارضة
            resetPaint();
            if (s.beamHoriz) {
                paint.setShader(new LinearGradient(left, 0, left + fw, 0, s.beam0, s.beam1, Shader.TileMode.CLAMP));
            } else {
                paint.setShader(new LinearGradient(0, beamTop, 0, beamTop + beamH, s.beam0, s.beam1, Shader.TileMode.CLAMP));
            }
            if (s.beamGlow != 0) paint.setShadowLayer(bw * .3f, 0, 0, s.beamGlow);
            else paint.setShadowLayer(bw * .12f, 0, bw * .06f, 0x88000000);
            rect.set(left, beamTop, left + fw, beamTop + beamH);
            c.drawRoundRect(rect, bw * .05f, bw * .05f, paint);

            // نقاط الفصل
            resetPaint();
            paint.setColor(s.dot);
            float ds = .13f * bw;
            if (s.dotGlow) paint.setShadowLayer(ds * 1.4f, 0, 0, s.dot);
            float bc = beamTop + beamH / 2f;
            for (float gx : gapX) {
                c.drawCircle(gx, bc - .1f * bw - ds / 2f, ds / 2f, paint);
                c.drawCircle(gx, bc + .1f * bw + ds / 2f, ds / 2f, paint);
            }

            // القراءة الرقمية
            textPaint.setColor(s.digital);
            textPaint.setTextSize(.42f * bw);
            c.drawText(timeText, W / 2f, top + fh + edge + bw * .9f, textPaint);
        }

        private void drawBead(Canvas c, Skins.Skin s, float cx, float y,
                              float w, float h, boolean on, int color) {
            int col = (s.flat && on && s.onColor != 0) ? s.onColor : color;
            rect.set(cx - w / 2f, y, cx + w / 2f, y + h);
            float rx = s.ridge ? w * .44f : w * .5f;
            float ry = h * .5f;
            boolean dim = s.neon && !on;

            // ظل سفلي
            if (!s.flat && !s.neon) {
                resetPaint();
                paint.setColor(0x55000000);
                RectF sh = new RectF(rect);
                sh.offset(0, h * .12f);
                c.drawRoundRect(sh, rx, ry, paint);
            }

            // جسم الخرزة
            resetPaint();
            if (s.flat) {
                paint.setColor(col);
            } else if (s.ridge) {
                paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                        new int[]{mix(col, 0xFFFFFFFF, .45f), col, col, mix(col, 0xFF000000, .2f), mix(col, 0xFF000000, .38f)},
                        new float[]{0f, .28f, .47f, .53f, 1f}, Shader.TileMode.CLAMP));
            } else {
                paint.setShader(new LinearGradient(rect.left, rect.top, rect.right, rect.bottom,
                        new int[]{mix(col, 0xFFFFFFFF, .55f), col, mix(col, 0xFF000000, .45f)},
                        new float[]{0f, .4f, 1f}, Shader.TileMode.CLAMP));
            }
            if (s.neon && on) paint.setShadowLayer(h * .6f, 0, 0, col);
            if (dim) paint.setAlpha(72);
            c.drawRoundRect(rect, rx, ry, paint);

            // اللمعة
            if (!s.flat && !dim && s.shine > 0) {
                resetPaint();
                paint.setShader(new RadialGradient(rect.left + w * .32f, rect.top + h * .26f, w * .45f,
                        (s.shine << 24) | 0xFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP));
                c.drawRoundRect(rect, rx, ry, paint);
            }
        }

        private int mix(int a, int b, float t) {
            int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
            int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
            int r = (int) (ar + (br - ar) * t), g = (int) (ag + (bg - ag) * t), bl = (int) (ab + (bb - ab) * t);
            return (a & 0xFF000000) | (r << 16) | (g << 8) | bl;
        }
    }
}
