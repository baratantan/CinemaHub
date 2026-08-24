# 🔧 دليل التثبيت والتشغيل - CinemaHub

## المتطلبات الأساسية

- Android Studio Hedgehog أو أحدث
- JDK 11 أو أحدث
- Android SDK 24 (API Level 24) أو أحدث
- Gradle 8.1+
- Kotlin 1.9.10+

## خطوات التثبيت

### 1. استنساخ المشروع
```bash
git clone https://github.com/baratantan/CinemaHub.git
cd CinemaHub
```

### 2. فتح المشروع في Android Studio
```bash
# في Android Studio:
File → Open → اختر مجلد CinemaHub
```

### 3. تحديث Gradle
```bash
# Android Studio سيطلب تحديث Gradle تلقائياً
# انقر على "Update" إذا ظهرت النافذة
```

### 4. بناء المشروع
```bash
# من خلال Android Studio:
Build → Make Project

# أو من خلال Terminal:
./gradlew build
```

### 5. تشغيل التطبيق
```bash
# على محاكي أو جهاز فعلي:
Run → Run 'app'

# أو من خلال Terminal:
./gradlew installDebug
```

## قد تواجه هذه المشاكل وحلولها

### مشكلة: "Cannot find symbol"
**الحل:**
```bash
# نظف المشروع وأعد بناؤه
./gradlew clean
./gradlew build
```

### مشكلة: "Gradle sync failed"
**الحل:**
- تأكد من اتصالك بالإنترنت
- قم بتحديث gradle:
  ```bash
  ./gradlew wrapper --gradle-version=8.1.2
  ```

### مشكلة: "ExoPlayer not found"
**الحل:**
```bash
# تأكد من إضافة المكتبات في build.gradle.kts
# انظر ملف build.gradle.kts في المشروع
```

## البيانات التجريبية

التطبيق يأتي مع MockData جاهزة:
- 8 أفلام تجريبية
- 3 مسلسلات
- حلقات تجريبية

لتحميل البيانات:
```kotlin
// في HomeViewModel:
init {
    loadMovies() // يحمل البيانات من MockData
}
```

## اختبار Web Scraping

### اختبار محلي:
```kotlin
@Test
suspend fun testScraping() {
    val scraper = VideoScraper()
    val links = scraper.scrapeVideoLinks("https://example.com")
    // يجب أن يعيد قائمة الروابط
}
```

## تخصيص المشروع

### تغيير اسم ال��طبيق:
```xml
<!-- في strings.xml -->
<string name="app_name">Your App Name</string>
```

### تغيير الألوان:
```xml
<!-- في colors.xml -->
<color name="primary_red">#E50914</color>
<color name="background_primary">#0F0F0F</color>
```

### إضافة شعارك:
```bash
# ضع ملف الشعار في:
app/src/main/res/drawable/ic_launcher.png
app/src/main/res/drawable/ic_launcher_foreground.png
```

## البناء للإطلاق

### 1. إنشاء Keystore:
```bash
keytool -genkey -v -keystore ~/my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

### 2. تكوين Gradle:
```kotlin
// في build.gradle.kts
signingConfigs {
    create("release") {
        storeFile = file("path/to/keystore.jks")
        storePassword = "your-password"
        keyAlias = "your-key-alias"
        keyPassword = "your-key-password"
    }
}
```

### 3. بناء APK:
```bash
./gradlew bundleRelease
```

## الدعم والمساعدة

إذا واجهت مشاكل:
1. تحقق من الملف `PROJECT_REPORT.md`
2. ابحث في Issues على GitHub
3. أفتح Issue جديدة مع وصف المشكلة

## الترخيص

هذا المشروع مرخص تحت MIT License
