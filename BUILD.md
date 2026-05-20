# 🔨 Гайд з Збірки APK

## Швидкий Старт

```bash
# Основна команда
./gradlew assembleRelease

# Результат: app/build/outputs/apk/release/app-release.apk
```

## Детальна Інструкція

### 1. Debug версія (для тестування)

```bash
# Завантажити на пристрій
./gradlew installDebug

# Або лише побудувати APK
./gradlew assembleDebug
# Результат: app/build/outputs/apk/debug/app-debug.apk
```

### 2. Release версія (для розповсюдження)

```bash
# Побудувати з обфускацією
./gradlew assembleRelease

# Подробні логи
./gradlew assembleRelease --info

# Без кешу
./gradlew clean assembleRelease
```

### 3. Оптимізація розміру

```bash
# Включити мініфікацію та обфускацію
# Вже включено в build.gradle.kts:
isMinifyEnabled = true
proguardFiles(...)

# Розділити за ABI (для Play Store)
./gradlew bundleRelease
# Результат: app/build/outputs/bundle/release/app-release.aab
```

## Структура Вихідних Файлів

```
app/build/outputs/
├── apk/
│   ├── debug/
│   │   └── app-debug.apk          (неоптимізований, для тестування)
│   └── release/
│       └── app-release.apk        (оптимізований, обфускований)
├── bundle/
│   └── release/
│       └── app-release.aab        (для Play Store)
└── mapping/
    └── release/
        └── mapping.txt             (для розшифрування crash stacktraces)
```

## Параметри Conifguration

### build.gradle.kts

```kotlin
defaultConfig {
    minSdk = 24              // Android 7.0
    targetSdk = 34           // Android 14
    compileSdk = 34
    versionCode = 1          // Збільшуйте для кожного релізу
    versionName = "1.0.0"    // Семантичне версіонування
}

buildTypes {
    release {
        isMinifyEnabled = true
        shrinkResources = true  // Видаляє невикористані ресурси
        proguardFiles(...)
    }
    debug {
        isMinifyEnabled = false
        debuggable = true
    }
}

android {
    packagingOptions {
        // Виключає зайві файли для зменшення розміру
        resources.excludes += "META-INF/..."
    }
}
```

## ProGuard Правила

### Збереження Важливих Класів

```proguard
# У proguard-rules.pro

# Зберегти всі Hilt компоненти
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Зберегти ViewModels
-keep class com.example.syncvideoviewer.presentation.** { *; }

# Зберегти Compose
-keep class androidx.compose.** { *; }

# Зберегти Firebase
-keep class com.google.firebase.** { *; }

# Зберегти WebRTC
-keep class org.webrtc.** { *; }
```

## Розмір APK

### Експектовані Розміри

```
Debug:     ~45 MB (без оптимізації)
Release:   ~25-30 MB (з R8 обфускацією)
Universal: ~25 MB (для всіх ABI)
```

### Аналіз Розміру

```bash
# Звіт про розмір
./gradlew assembleRelease --build-cache

# Детальна аналізація у Android Studio
Build → Analyze APK → app-release.apk
```

## Підписування APK

### Android Studio (рекомендовано)

1. **Build → Generate Signed Bundle / APK**
2. Виберіть **APK**
3. Натисніть **Create new...**
4. Заповніть інформацію про ключ:
   ```
   Store File: [Виберіть місце]
   Key Store Password: [Пароль]
   Key Alias: release
   Key Password: [Пароль]
   ```
5. Натисніть **Next**
6. Виберіть **Release**
7. Натисніть **Finish**

### Command Line

```bash
# Якщо у вас вже є keystore
jarsigner -verbose \
  -sigalg SHA256withRSA \
  -digestalg SHA-256 \
  -keystore my-release-key.jks \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  alias_name

# Поліпшення
zipalign -v 4 \
  app-release-unsigned.apk \
  app-release-signed.apk
```

## Розповсюдження

### Play Store

```bash
# Побудувати bundle
./gradlew bundleRelease

# Завантажити у Play Console
1. Перейдіть до https://play.google.com/console
2. Виберіть ваш додаток
3. Release → Production
4. Upload app bundle
5. Виберіть app-release.aab
6. Review та Publish
```

### Direct APK Distribution

```bash
# Email, file sharing, тощо
./gradlew assembleRelease

# Файл буде в:
app/build/outputs/apk/release/app-release.apk

# Користувачи встановлюють:
adb install app-release.apk
```

## Версіонування

### Семантичне Версіонування (MAJOR.MINOR.PATCH)

```
1.0.0 - Перший релізу
1.0.1 - Багфікс
1.1.0 - Новий функціонал
2.0.0 - Великі зміни (breaking changes)
```

### Оновлення версії у build.gradle.kts

```kotlin
defaultConfig {
    versionCode = 2          // Інкрементуй для кожного релізу
    versionName = "1.0.1"    // Описова версія
}
```

## CI/CD (GitHub Actions приклад)

```yaml
name: Build APK

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: 17
          distribution: temurin
      
      - name: Build APK
        run: ./gradlew assembleRelease
      
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: APK
          path: app/build/outputs/apk/release/app-release.apk
```

## Відстеження Багів на Production

### Mapping Files

```
app/build/outputs/mapping/release/mapping.txt
```

Використовується для розшифрування обфускованих stacktraces з crash logs.

```bash
# Розшифрування crash
echo "com.example.syncvideoviewer.MainActivity -> b:" >> mapping.txt
# Лінія з mapping файлу відповідає оригінальному класу
```

## Перевірка перед релізом

- ✅ Всі тести пройшли
- ✅ Жодних lint помилок
- ✅ Жодних тестів про безпеку
- ✅ Оновлена версія в build.gradle.kts
- ✅ Оновлена версія в README
- ✅ Обновлений CHANGELOG
- ✅ Перевірено на різних пристроях
- ✅ Проведено performance тестування

## Трубешутинг

### Помилка: "Unable to compute delete operations"
```bash
./gradlew clean
./gradlew assembleRelease
```

### Помилка: "Could not write to file"
```
Перевірьте, що немає блокуючих файлів (видеофайли, тощо)
Закрийте Android Studio та спробуйте знову
```

### Дуже великий розмір APK
```bash
# Проведіть аналізу
Build → Analyze APK

# Видаліть невикористані депенденції
# Включіть shrinkResources = true
```

## 📊 Метрики Якості

```
APK Size:       < 30 MB ✅
Build Time:     < 2 min ✅
Compile Errors: 0 ✅
ProGuard Errors: 0 ✅
```

## 🎉 Готово!

Ваш APK готовий для розповсюдження!
