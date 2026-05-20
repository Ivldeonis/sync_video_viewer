# General rules
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Exceptions
-keepattributes Signature

# Preserve line numbers for crash logs
-renamesourcefileattribute SourceFile

# Keep all public classes and methods
-keep public class * {
    public *;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Kotlin
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# WebRTC
-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keep class * extends com.google.gson.TypeAdapter

# Hilt/Dagger
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-dontwarn dagger.hilt.**

# Keep all classes with @Hilt annotations
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Material Design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Data classes
-keep class com.example.syncvideoviewer.data.model.** { *; }
-keepclassmembers class com.example.syncvideoviewer.data.model.** {
    <init>(...);
    <fields>;
    <methods>;
}

# ViewModels
-keep class com.example.syncvideoviewer.presentation.** { *; }
-keep class com.example.syncvideoviewer.webrtc.** { *; }
-keep class com.example.syncvideoviewer.di.** { *; }
