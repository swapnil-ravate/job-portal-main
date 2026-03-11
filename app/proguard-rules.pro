# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep line number attributes for crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== Firebase Model Classes =====
# Firebase Realtime Database requires all model fields to be preserved
# during obfuscation so it can deserialize JSON payloads correctly.
-keep class com.swapnil.jobportal.Model.** { *; }
-keepclassmembers class com.swapnil.jobportal.Model.** {
    public <init>();      # default constructor required by Firebase
    public *;
}

# ===== Firebase SDK =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**

# ===== FirebaseUI =====
-keep class com.firebase.ui.** { *; }
-dontwarn com.firebase.ui.**

# ===== Picasso =====
-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

# ===== AndroidX Credentials =====
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**

# ===== CircleImageView =====
-keep class de.hdodenhof.circleimageview.** { *; }

# ===== General Android rules =====
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}