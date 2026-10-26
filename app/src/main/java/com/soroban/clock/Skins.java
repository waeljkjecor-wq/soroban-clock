package com.soroban.clock;

/** ألوان السمات الست للخلفية الحيّة (مطابقة لسمات صفحة الساعة). */
final class Skins {
    static final String PREFS = "soroban";
    static final String KEY = "skin";

    private Skins() {}

    static final class Skin {
        int bg0, bg1;               // خلفية الشاشة (من المركز إلى الأطراف)
        int frame0, frame1;         // الإطار (من الأعلى إلى الأسفل)
        int edge; float edgeW;      // حافة الإطار (عرضها بالنسبة لعرض الخرزة)
        float radius;               // استدارة الإطار
        int shadow;                 // ظل الإطار
        int rod;                    // القضبان
        int beam0, beam1; boolean beamHoriz; int beamGlow;
        int bead, heaven;           // لون الخرز
        int[] earth;                // ألوان الخرزات السفلية (اختياري)
        int onColor;                // لون الخرز المحسوب (للسمة البسيطة)
        boolean ridge, flat, neon;
        int shine;                  // شدة اللمعة (0-255)
        int dot; boolean dotGlow;
        int digital;
    }

    static Skin get(String id) {
        Skin s = new Skin();
        if (id == null) id = "wood";
        switch (id) {
            case "kids":
                s.bg0 = 0xFFF9FBFD; s.bg1 = 0xFFE4E9F0;
                s.frame0 = 0xFFFFFFFF; s.frame1 = 0xFFECEEF1;
                s.edge = 0xFF2C2D31; s.edgeW = .25f; s.radius = .35f;
                s.shadow = 0x66203050;
                s.rod = 0xFFD9A86C;
                s.beam0 = 0xFFFFFFFF; s.beam1 = 0xFF8E939A;
                s.bead = 0xFF2F7DE1; s.heaven = 0xFF2F7DE1;
                s.earth = new int[]{0xFF22B04C, 0xFFFFD400, 0xFFFF8A1A, 0xFFEC2A23};
                s.ridge = true; s.shine = 170;
                s.dot = 0xFFEC2A23; s.dotGlow = false;
                s.digital = 0xFF5A6575;
                break;
            case "ebony":
                s.bg0 = 0xFF241A1B; s.bg1 = 0xFF000000;
                s.frame0 = 0xFF2A1E1C; s.frame1 = 0xFF120C0B;
                s.edge = 0xFF7A1D18; s.edgeW = .09f; s.radius = .18f;
                s.shadow = 0xEE000000;
                s.rod = 0xFFC4B482;
                s.beam0 = 0xFF3B2B27; s.beam1 = 0xFF120B0A;
                s.bead = 0xFFE6D8BC; s.heaven = 0xFFE6D8BC;
                s.shine = 215;
                s.dot = 0xFFD9B77A; s.dotGlow = true;
                s.digital = 0xFF9C8661;
                break;
            case "neon":
                s.bg0 = 0xFF1A0F3A; s.bg1 = 0xFF020108;
                s.frame0 = 0xFF161A40; s.frame1 = 0xFF08081A;
                s.edge = 0x8C00E5FF; s.edgeW = .04f; s.radius = .4f;
                s.shadow = 0x5500E5FF;
                s.rod = 0x7300E5FF;
                s.beam0 = 0xFFFF2BD6; s.beam1 = 0xFF00E5FF; s.beamHoriz = true; s.beamGlow = 0xCCFF2BD6;
                s.bead = 0xFF00E5FF; s.heaven = 0xFFFF2BD6;
                s.neon = true; s.shine = 150;
                s.dot = 0xFFFF2BD6; s.dotGlow = true;
                s.digital = 0xFFFF7AE6;
                break;
            case "minimal":
                s.bg0 = 0xFFECEFF1; s.bg1 = 0xFFECEFF1;
                s.frame0 = 0xFFFBFCFD; s.frame1 = 0xFFFBFCFD;
                s.edge = 0xFFCFD5DB; s.edgeW = .02f; s.radius = .13f;
                s.shadow = 0x40283240;
                s.rod = 0xFFB7BEC6;
                s.beam0 = 0xFF22262B; s.beam1 = 0xFF22262B;
                s.bead = 0xFFC9CFD6; s.heaven = 0xFFC9CFD6; s.onColor = 0xFF1F6FEB;
                s.flat = true;
                s.dot = 0xFF1F6FEB; s.dotGlow = false;
                s.digital = 0xFF6B7480;
                break;
            case "jade":
                s.bg0 = 0xFF12352A; s.bg1 = 0xFF020A06;
                s.frame0 = 0xFF3D2615; s.frame1 = 0xFF1F130A;
                s.edge = 0xFFB8913F; s.edgeW = .05f; s.radius = .3f;
                s.shadow = 0xDD000000;
                s.rod = 0xFFD4B060;
                s.beam0 = 0xFFF2D68A; s.beam1 = 0xFF7A5C1F;
                s.bead = 0xFF2FA37A; s.heaven = 0xFF2FA37A;
                s.shine = 180;
                s.dot = 0xFFE2C06E; s.dotGlow = true;
                s.digital = 0xFF9FC9B2;
                break;
            default: // wood
                s.bg0 = 0xFF2B211A; s.bg1 = 0xFF050302;
                s.frame0 = 0xFF7D4B23; s.frame1 = 0xFF42220E;
                s.edge = 0; s.edgeW = 0f; s.radius = .45f;
                s.shadow = 0xCC000000;
                s.rod = 0xFFC9A26E;
                s.beam0 = 0xFF8A5A28; s.beam1 = 0xFF35200C;
                s.bead = 0xFFDF9A3A; s.heaven = 0xFFDF9A3A;
                s.shine = 190;
                s.dot = 0xFFFFC861; s.dotGlow = true;
                s.digital = 0xFFA88556;
        }
        return s;
    }
}
