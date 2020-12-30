@file:Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")

package com.hfad.gamo

import com.hfad.gamo.Component.sharedPreferences
import java.util.*
import kotlin.collections.HashSet


const val appConstantPreferences = "Moga"

inline fun <reified T> setSharedItem(key: String, data: T) = sharedPreferences.edit().apply {
    when (T::class) {
        String::class -> putString(key, data as String)
        Boolean::class -> putBoolean(key, data as Boolean)
        Float::class -> putFloat(key, data as Float)
        Int::class -> putInt(key, data as Int)
        Long::class -> putLong(key, data as Long)
        HashSet::class -> putStringSet(key, data as HashSet<String>)
    }
}.commit()

inline fun <reified T> setSharedItems(vararg pairs: Pair<String, T>) {
    for (pair in pairs) {
        setSharedItem(pair.first, pair.second)
    }
}

inline fun <reified T> getSharedItem(key: String): T = sharedPreferences.run {
    when (T::class) {
        String::class -> getString(key, "")
        Boolean::class -> getBoolean(key, false)
        Float::class -> getFloat(key, 0.0f)
        Int::class -> getInt(key, 0)
        Long::class -> getLong(key, 0L)
        HashSet::class -> getStringSet(key, HashSet<String>())
        else -> ""
    } as T
}

inline fun <reified T> getSharedItem(key: String, default: T): T = sharedPreferences.run {
    when (T::class) {
        String::class -> getString(key, default as String)
        Boolean::class -> getBoolean(key, default as Boolean)
        Float::class -> getFloat(key, default as Float)
        Int::class -> getInt(key, default as Int)
        Long::class -> getLong(key, default as Long)
        HashSet::class -> getStringSet(key, default as HashSet<String>)
        else -> ""
    } as T
}

inline fun <reified T> getSharedItems(vararg keys: String): HashMap<String, T>? =
    HashMap<String, T>().apply {
        for (key in keys) {
            try {
                put(key, getSharedItem(key))
            } catch (e: Exception) {
            }
        }
    }

fun removeSharedItem(key: String) = sharedPreferences.edit().remove(key).commit()

fun removeSharedItems(vararg keys: String) = sharedPreferences.edit().apply {
    for (key in keys) {
        try {
            remove(key)
        } catch (e: Exception) {
        }
    }
}.commit()

fun setUnread(unread: Int) {
    sharedPreferences.edit().putInt("unread", unread).commit()
}

fun getUnread(): Int {
    return sharedPreferences.getInt("unread", 0)
}

fun setNotifications(newData: String) {
    sharedPreferences.edit().putString("notification_data", newData).commit()
}
fun getNotifications(): String? {
    return sharedPreferences.getString("notification_data", "")
}

fun resetSharedPreference() = sharedPreferences.edit().clear().commit()