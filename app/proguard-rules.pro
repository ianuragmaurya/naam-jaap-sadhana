# ============================================
# Room
# ============================================
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# ============================================
# Hilt / Dagger
# ============================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <methods>;
}
-keep,allowobfuscation,allowshrinking class dagger.hilt.internal.GeneratedComponent

# Hilt-generated classes must not be renamed
-keep class **_HiltModules { *; }
-keep class **_HiltComponents { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# ============================================
# Hilt WorkManager (our ReminderWorker)
# ============================================
-keep class androidx.hilt.work.** { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}
-keepclassmembers class * extends androidx.work.ListenableWorker {
    <init>(...);
}

# ============================================
# Kotlinx Serialization (our BackupData models)
# ============================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.am.naamjaap.data.backup.**$$serializer { *; }
-keepclassmembers class com.am.naamjaap.data.backup.** {
    *** Companion;
}
-keepclasseswithmembers class com.am.naamjaap.data.backup.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================
# Glance Widget
# ============================================
-keep class com.am.naamjaap.widget.** { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver
-keep class * extends androidx.glance.appwidget.action.ActionCallback

# Widget EntryPoint — Hilt-generated interface, needed by reflection-based
# ActionCallback execution inside the widget process
-keep interface com.am.naamjaap.di.WidgetEntryPoint { *; }
-keep class com.am.naamjaap.di.WidgetEntryPoint* { *; }

# Ensure widget's ActionCallback keeps its no-arg constructor (Glance
# instantiates it via reflection using the stored class name)
-keepclassmembers class com.am.naamjaap.widget.IncrementWidgetAction {
    <init>();
}

# ============================================
# Kotlin Coroutines
# ============================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.flow.**Flow

# ============================================
# General Compose (usually not needed, but safe)
# ============================================
-keep class androidx.compose.runtime.** { *; }