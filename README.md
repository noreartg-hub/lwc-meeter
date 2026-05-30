# LWC Meter — Android Project

## بناء APK

### المتطلبات
- Android Studio (Hedgehog أو أحدث)
- JDK 17+
- Android SDK 34

### خطوات البناء

1. افتح Android Studio
2. File → Open → اختر هذا المجلد
3. انتظر Gradle Sync
4. قبل البناء، عدّل `SERVER_URL` في:
   `app/src/main/java/com/lwc/meter/MainActivity.java`
5. Build → Generate Signed Bundle/APK → APK
6. اختر debug للاختبار أو release للنشر

### بناء APK بالسطر الأوامر
```bash
./gradlew assembleDebug
# APK يكون في: app/build/outputs/apk/debug/app-debug.apk
```

### بناء Release APK
```bash
./gradlew assembleRelease
```

## هيكل المشروع
- `app/src/main/assets/` — تطبيق الويب (HTML/JS/CSS)
- `app/src/main/java/` — كود Android الأصلي
- `app/src/main/res/` — موارد التطبيق (أيقونات، تصميم)
