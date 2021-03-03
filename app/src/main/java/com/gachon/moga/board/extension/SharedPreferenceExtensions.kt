@file:Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")

package com.gachon.moga.board.extension

import android.content.SharedPreferences
import com.gachon.moga.Component
import com.gachon.moga.getSharedItem
import com.gachon.moga.setSharedItem


inline fun <reified T> SharedPreferences.setSharedItem(key: String, data: T) = this.edit().apply {
  when (T::class) {
    String::class -> putString(key, data as String)
    Boolean::class -> putBoolean(key, data as Boolean)
    Float::class -> putFloat(key, data as Float)
    Int::class -> putInt(key, data as Int)
    Long::class -> putLong(key, data as Long)
    HashSet::class -> putStringSet(key, data as HashSet<String>)
  }
}.commit()

inline fun <reified T> SharedPreferences.setSharedItems(vararg pairs: Pair<String, T>) {
  for (pair in pairs) {
    setSharedItem(pair.first, pair.second)
  }
}

inline fun <reified T> SharedPreferences.getSharedItem(key: String): T = this.run {
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

fun SharedPreferences.getDepartment(): String? {
  return getSharedItem("department")
}

fun SharedPreferences.getUserNo(): Int {
  return try {
    (getSharedItem("number") as String).toInt()
  }
  catch(e: NumberFormatException) {
    e.stackTrace
    -1
  }
}
