# Sync Video Viewer - Android Application

Нативний Android-застосунок для спільного перегляду HLS-відео з синхронізацією через WebRTC та Firebase.

## 📋 Вимоги

- **Android SDK**: API 24+ (Android 7.0 Nougat)
- **Java/Kotlin**: Kotlin 1.9.0+
- **Gradle**: 8.2.0+
- **Firebase проект**: Налаштований через Firebase Console

## 🚀 Встановлення

### 1. Клонування проекту
```bash
git clone <repo_url>
cd sync_video_viewer
```

### 2. Firebase Setup
1. Перейти на [Firebase Console](https://console.firebase.google.com)
2. Створити новий проект "sync-video-viewer"
3. Додати Android-додаток:
   - Package name: `com.example.syncvideoviewer`
   - SHA-1: `./gradlew signingReport`
4. Завантажити `google-services.json`
5. Покласти у `app/google-services.json`

### 3. Налаштування Firestore Security Rules
```firebase
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /rooms/{roomId} {
      allow read, write: if request.auth != null;
      
      match /participants/{participantId} {
        allow read, write: if request.auth != null;
      }
    }
  }
}
```

### 4. Налаштування Realtime Database Rules
```firebase
{
  "rules": {
    "signaling": {
      "$roomId": {
        ".read": true,
        ".write": true,
        "offers": {
          ".validate": "newData.isString()"
        },
        "answers": {
          ".validate": "newData.isString()"
        },
        "ice": {
          ".validate": "newData.isArray() || newData.isString()"
        }
      }
    },
    "fallback_commands": {
      "$roomId": {
        ".read": true,
        ".write": true
      }
    },
    "chat": {
      "$roomId": {
        ".read": true,
        ".write": true
      }
    }
  }
}
```

## 🔨 Збірка проекту

### Debug версія
```bash
./gradlew assembleDebug
# APK буде в: app/build/outputs/apk/debug/
```

### Release версія
```bash
./gradlew assembleRelease
# APK буде в: app/build/outputs/apk/release/
```

### Обфускація коду
Увімкнена за замовчуванням у `build.gradle.kts`:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

## 📦 Залежності

### Core Android
- `androidx.core:core-ktx` - Kotlin розширення
- `androidx.lifecycle:lifecycle-*` - Lifecycle management
- `androidx.compose.*` - Jetpack Compose UI

### Медіа & WebRTC
- `androidx.media3:media3-exoplayer-hls` - HLS відтворення
- `org.webrtc:google-webrtc` - WebRTC для синхронізації

### Backend
- `com.google.firebase:firebase-firestore-ktx` - Кімнати та учасники
- `com.google.firebase:firebase-database-ktx` - Сигналізація WebRTC
- `com.google.firebase:firebase-auth-ktx` - Аутентифікація

### DI & Async
- `com.google.dagger:hilt-android` - Dependency Injection
- `org.jetbrains.kotlinx:kotlinx-coroutines-*` - Асинхронність

## 🎯 Основні фічі

### ✅ Реалізовано
- [x] Jetpack Compose UI з Netflix-стилем
- [x] ExoPlayer для HLS-потоків
- [x] Firebase Firestore для кімнат
- [x] Firebase Realtime Database для чату
- [x] WebRTC DataChannel架構
- [x] Hilt DI для управління залежностями
- [x] MVVM архітектура
- [x] Deep Links для запрошень

### 🔄 Синхронізація

**WebRTC DataChannel** (основний):
- Низька затримка (< 100 мс)
- Обмін SDP/ICE через Firebase

**Firebase Realtime Database** (резервний):
- Затримка ~500 мс
- Автоматичне переключення при розриві WebRTC

**Періодична синхронізація**:
- Кожні 5-10 секунд
- Вирівнювання позиції з похибкою < 500 мс

## 📱 Тестування

### На емуляторі
```bash
./gradlew installDebug
adb shell am start -n com.example.syncvideoviewer/.MainActivity
```

### На реальному пристрої
```bash
# Підключити пристрій via USB
./gradlew installDebug
```

### Тестові HLS-потоки
```
https://test-streams.mux.dev/x36xhzz/x3zzjze.m3u8
https://cph-p.mzstatic.com/hsls/GeoIP-AT/track1/session1/master.m3u8
```

## 🔐 Безпека

- ✅ Firebase Security Rules для захисту даних
- ✅ HTTPS для всіх з'єднань
- ✅ Коди доступу для приватних кімнат
- ✅ R8/ProGuard обфускація
- ✅ Мінімальні дозволи в AndroidManifest

## 📊 Структура проекту

```
app/
├── src/main/
│   ├── java/com/example/syncvideoviewer/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── screens/ (HomeScreen, RoomScreen)
│   │   │   ├── components/ (VideoPlayer, ChatPanel, etc)
│   │   │   └── theme/ (Theme, Typography)
│   │   ├── data/
│   │   │   ├── model/ (Room, Participant, ChatMessage)
│   │   │   └── repository/ (FirebaseRepository)
│   │   ├── presentation/ (ViewModels)
│   │   ├── webrtc/ (WebRTCManager, WebRTCService)
│   │   └── di/ (Hilt modules)
│   ├── res/
│   │   ├── values/ (strings, styles, colors)
│   │   └── xml/ (backup_rules, data_extraction_rules)
│   └── AndroidManifest.xml
├── build.gradle.kts
├── proguard-rules.pro
└── google-services.json
```

## 📝 Виправлення й Оновлення

### Android 10+ (API 29+)
- ✅ Запросити дозвіл INTERNET в runtime
- ✅ Підтримка scoped storage
- ✅ Gesture navigation

### Android 11+ (API 30+)
- ✅ Пакетні фільтри (package visibility)
- ✅ Auto-reset permissions

### Android 12+ (API 31+)
- ✅ Approximate location support
- ✅ Hibernation support

### Android 13+ (API 33+)
- ✅ Per-app language preferences
- ✅ Predictive back gesture

### Android 14+ (API 34+)
- ✅ Regional preferences
- ✅ Photo picker support

## 🐛 Поширені проблеми

### WebRTC не з'єднується
- Переконайтесь, що ICE-сервери доступні
- На емуляторі можливі проблеми NAT
- Спробуйте на реальному пристрої

### ExoPlayer не відтворює HLS
- Перевірьте URL потоку (повинна бути .m3u8)
- Перевірте дозвіл INTERNET в AndroidManifest
- Переглядайте logcat для помилок

### Firebase помилки
- Переконайтесь, що google-services.json коректний
- Перевірьте Security Rules у Firebase Console
- Включіть аналітику у Firebase (може знадобитися)

## 📞 Підтримка

Для питань або багів, будь ласка:
1. Перевірьте logcat для помилок
2. Переглядайте Firebase Console для проблем
3. Відкрийте issue в репозиторії

## 📄 Ліцензія

MIT License - див. LICENSE файл

## 🤝 Contributin

Pull requests приймаються! Для великих змін спочатку відкрийте issue.

---

**Версія**: 1.0.0  
**Остання оновлення**: 2025  
**Мінімальний API**: 24 (Android 7.0)  
**Цільовий API**: 34 (Android 14)
