package repository

import android.database.Cursor

// Small helpers so the repositories can read a column by NAME (like rs.getString("id") in JDBC).
fun Cursor.str(col: String): String = getString(getColumnIndexOrThrow(col))

fun Cursor.strOrNull(col: String): String? {
    val i = getColumnIndexOrThrow(col)
    return if (isNull(i)) null else getString(i)
}

fun Cursor.dbl(col: String): Double = getDouble(getColumnIndexOrThrow(col))

fun Cursor.int(col: String): Int = getInt(getColumnIndexOrThrow(col))

fun Cursor.intOrNull(col: String): Int? {
    val i = getColumnIndexOrThrow(col)
    return if (isNull(i)) null else getInt(i)
}
