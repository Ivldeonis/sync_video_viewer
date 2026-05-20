# 🚀 Детальне Керівництво Встановлення

## Крок 1: Встановлення Android Studio

1. Завантажьте [Android Studio](https://developer.android.com/studio)
2. Встановіть з усіма компонентами (Android SDK, Emulator, NDK)
3. На першому запуску виберіть "Do not import settings"

## Крок 2: Налаштування Android SDK

1. Відкрийте Android Studio
2. Перейдіть **Tools → SDK Manager**
3. Встановіть:
   - Android SDK Platform API Level 34 ✅
   - Android SDK Build-Tools 34.x ✅
   - Android Emulator (якщо потрібен) ✅
   - Google Play services ✅
   - Kotlin Plugin ✅

## Крок 3: Налаштування Firebase

### 3.1 Створення Firebase проекту

1. Перейдіть на [Firebase Console](https://console.firebase.google.com)
2. Натисніть **Create a new project**
3. Назвіть проект: `sync-video-viewer`
4. Отримайте Google Analytics (опціонально)

### 3.2 Додавання Android-додатка

1. У Firebase Console натисніть **Add app**
2. Виберіть **Android**
3. Заповніть форму:
   - **Package name**: `com.example.syncvideoviewer`
   - **App nickname**: `Sync Video Viewer`
   - **SHA-1**: Скопіюйте из Android Studio
     - Tools → APP Signing → View Certificate Details
     - Копіюйте SHA-1

4. Натисніть **Register app**
5. Завантажьте `google-services.json`
6. Помістіть у папку `app/google-services.json`

### 3.3 Увімкнення Firestore

1. У Firebase Console перейдіть **Firestore Database**
2. Натисніть **Create database**
3. Виберіть регіон (наприклад, `europe-west1`)
4. Виберіть **Start in test mode** (потім змініть правила)
5. Натисніть **Create**

### 3.4 Увімкнення Realtime Database

1. Перейдіть **Realtime Database**
2. Натисніть **Create Database**
3. Виберіть регіон та режим
4. Натисніть **Enable**

### 3.5 Налаштування Authentication

1. Перейдіть **Authentication**
2. Натисніть **Get started**
3. Включіть **Anonymous** (для тестування):
   - Sign-in methods → Anonymous → Enable
4. Натисніть **Save**

## Крок 4: Налаштування Проекту

### 4.1 Клонування / Імпорт

```bash
# Якщо у вас є Git
git clone <repo_url> sync_video_viewer
cd sync_video_viewer

# Або розпакуйте ZIP файл
unzip sync_video_viewer.zip
cd sync_video_viewer
```

### 4.2 Відкриття в Android Studio

1. Запустіть Android Studio
2. **File → Open**
3. Виберіть папку `sync_video_viewer`
4. Натисніть **Open**
5. Дочекайтесь синхронізації Gradle (~2-5 хв)

### 4.3 Встановлення Залежностей

Gradle автоматично завантажить усі залежності. Якщо розпочалася помилка:

```bash
./gradlew clean
./gradlew build
```

## Крок 5: Налаштування Emulator (опціонально)

### 5.1 Створення AVD (Android Virtual Device)

1. **Tools → Device Manager**
2. Натисніть **Create device**
3. Виберіть устрій (напр. Pixel 6)
4. Виберіть систему API 34
5. Натисніть **Finish**

### 5.2 Запуск Emulator

```bash
# Або z Android Studio: Run → Select Device
./gradlew emulator -avd Pixel_6_API_34
```

## Крок 6: Запуск Додатка

### На реальному пристрої:

1. Підключіть Android пристрій via USB
2. Включіть **Developer Mode**:
   - Settings → About phone → Build number (7 разів тапніть)
   - Повернеться: Settings → Developer Options → USB Debugging (включіть)
3. Дозвольте отладку на пристрої
4. У Android Studio натисніть **Run** або:
   ```bash
   ./gradlew installDebug
   ```

### На емуляторі:

1. Запустіть AVD
2. Натисніть **Run → Run 'app'** у Android Studio
3. Виберіть running emulator

## Крок 7: Перевірка Firebase Connection

1. Відкрийте **logcat** у Android Studio
2. Запустіть додаток
3. Перевірьте логи на помилки Firebase:
   ```
   I/FirebaseInitProvider: Initializing Firebase
   I/Firebase: Firebase init completed successfully
   ```

## Крок 8: Тестування Функціоналу

### ✅ Тестуйте:

1. **Створення кімнати**
   - Натисніть FAB (+)
   - Заповніть форму
   - Натисніть "Створити"

2. **Пошук HLS-потоку**
   - Використовуйте тестові URL:
     ```
     https://test-streams.mux.dev/x36xhzz/x3zzjze.m3u8
     https://commondatastorage.googleapis.com/gtv-videos-library/sample/ElephantsDream.mp4
     ```

3. **Приєднання до кімнати**
   - Відкрийте іншу сесію (другий пристрій/емулятор)
   - Приєднайтесь за ID

4. **Чат**
   - Надішліть повідомлення
   - Перевірьте, чи воно з'являється на іншій сесії

## Крок 9: Побудова Release APK

```bash
# Повна build-версія
./gradlew assembleRelease

# APK буде в:
# app/build/outputs/apk/release/app-release.apk

# З обфускацією (R8):
./gradlew assembleRelease --gradle-version 8.2.0
```

## Крок 10: Розповсюдження

### На Play Store:

1. Підпишіть APK
2. Завантажте у **Google Play Console**
3. Заповніть метадані додатка
4. Завантажте на Play Store

### APK-розповсюдження:

1. Поділіться файлом `app-release.apk`
2. Користувачі можуть встановити via:
   ```bash
   adb install app-release.apk
   ```

## 🔧 Виправлення Проблем

### Помилка: "Could not find google-services plugin"
```
Рішення:
1. Переконайтесь google-services.json у app/
2. Перезавантажте Gradle: File → Sync Now
```

### Помилка: "Duplicate class"
```bash
./gradlew clean
./gradlew build -x test
```

### WebRTC не компілюється
```bash
# Оновіть версію у build.gradle.kts
org.webrtc:google-webrtc:1.0.32006
```

### ExoPlayer помилки
```
Убедитесь в:
1. INTERNET дозвіл у AndroidManifest
2. Правильний m3u8 URL (тестуйте у браузері)
3. Версія androidx.media3:media3-exoplayer:1.2.0
```

### Firebase помилки
```
1. Перевірьте безпеку правила у Console
2. Включіть Anonymous auth
3. Перевірьте з'єднання з Інтернетом на пристрої
```

## 📞 Отримання Допомоги

- **Android Studio**: Help → Find Action → Search
- **Firebase**: Перейдіть до Firebase Console → Support
- **Логи**: Tools → Logcat (фільтруйте за вашим package name)

## ✅ Контрольний список успіху

- [ ] Android Studio встановлено
- [ ] Firebase проект створено
- [ ] google-services.json у app/
- [ ] Проект синхронізовано в Gradle
- [ ] App запускається на пристрої/емуляторі
- [ ] Можна створити кімнату
- [ ] Можна приєднатися до кімнати
- [ ] Чат працює в реальному часі
- [ ] Видео відтворюється

## 🎉 Готово!

Ваш Sync Video Viewer готовий до використання!

Для питань - дивіться README.md або посилання на документацію.
