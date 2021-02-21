@file:Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")

package com.gachon.moga

import com.gachon.moga.Component.sharedPreferences
import com.gachon.moga.Component.shared_notification_data
import java.util.*
import kotlin.collections.HashSet


private const val TAG = "DataIOkt"
const val appConstantPreferences = "Moga"
const val amountPerOnePage = 10

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


fun getDepartment(): String? {
    return getSharedItem("department")
}

fun getUserNo(): Int {
    return try {
            (getSharedItem("number") as String).toInt()
    }
    catch(e: NumberFormatException) {
        e.stackTrace
        -1
    }
}

fun getNickname(): String? {
    return getSharedItem("nickname")
}

fun getSubjectProfessorJSONObject(): String {
    return getSharedItem("subject_professorJSONObject", "")
}

fun getSubjectSet(): HashSet<String>{
    return getSharedItem("subjectSet", java.util.HashSet<String>())
}


//------------------------ notification data ---------------------------

fun setNotificationInit(user_no: String?) {
    shared_notification_data.edit().putString("user_no", user_no).apply()
    shared_notification_data.edit().putInt("unread", 0).apply()
    setSharedItem("notification", true)
}
fun getNotificationUserNo(): String? {
    return shared_notification_data.getString("user_no", "")
}
fun clearNotificationData() {
    shared_notification_data.edit().clear().apply()
}
fun setNotificationSetting(flag: Boolean) {
    setSharedItem("notification", flag)
}
fun getNotificationSetting(): Boolean {
    return getSharedItem("notification", true)
}


fun setUnread(unread: Int) {
    shared_notification_data.edit().putInt("unread", unread).apply()
}

fun getUnread(): Int {
    return shared_notification_data.getInt("unread", 0)
}

fun setNotifications(newData: String) {
    shared_notification_data.edit().putString("notification_data", newData).apply()
}
fun getNotifications(): String? {
    return shared_notification_data.getString("notification_data", "")
}







//------------------------ notification data ---------------------------
//
//fun setNotificationInit(user_no: String?) {
//    shared_notification_data.edit().putString("user_no", user_no).apply()
//    shared_notification_data.edit().putInt("index", -1).apply()
//    shared_notification_data.edit().putInt("unread", 0).apply()
//}
//fun getNotificationUserNo(): String? {
//    return shared_notification_data.getString("user_no", "")
//}
//fun clearNotificationData() {
//    shared_notification_data.edit().clear().commit()
//}
//
//fun setUnread(unread: Int) {
//    shared_notification_data.edit().putInt("unread", unread).apply()
//    }
//
//fun getUnread(): Int {
//    return shared_notification_data.getInt("unread", 0)
////    return 0
//}
//
//fun getNotificationIndex(): Int {
//    return shared_notification_data.getInt("index", 0)
////    return 0
//}
//
//fun setNotifications(newData: String) {
//    val index = getNotificationIndex() + 1
//    shared_notification_data.edit().putInt("index", index).apply()
//    shared_notification_data.edit().putString(index.toString(), newData).apply()
//}
//fun getNotifications(): MutableMap<String, *>? {
//    val entries = shared_notification_data.all;
////    Log.d(TAG, "getNotifications: keys : $entries")
//    entries.remove("index")
//    entries.remove("user_no")
//    entries.remove("unread")
////    val dataArray = JSONArray()
////    for (entry in keys.entries) {
////        var obj = entry.toPair()
////        dataArray.put(obj)
////    }
////    Log.d(TAG, "getNotifications: dataArray : ${dataArray.toString()}")
//    Log.d(TAG, "getNotifications: entries : $entries")
//    return entries
//}
//
//fun removeNotifications(index: Int) {
//    shared_notification_data.edit().remove(index.toString()).apply()
//}
//
//fun setNotificationSetting(flag: Boolean) {
//    setSharedItem("notification", flag)
//}
//
//fun getNotificationSetting(): Boolean {
//    return getSharedItem("notification", true)
//}

fun resetSharedPreference() = sharedPreferences.edit().clear().commit()