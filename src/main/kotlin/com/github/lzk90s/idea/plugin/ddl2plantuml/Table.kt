package com.github.lzk90s.idea.plugin.ddl2plantuml

import java.io.Serializable
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.round

/**
 * 表
 *
 * @author wangyuheng@outlook.com
 * @date 2019-01-20 12:54
 */
data class Table(var name: String, var comment: String, var columnList: List<Column>) : Serializable


fun Table.columnFieldMaxLen(field: String): Int {
    var maxLen = 0
    for (column in this.columnList) {
        maxLen = maxLen.coerceAtLeast(column.getField(field).length)
    }
    return 1 + floor((maxLen / 4.0)).toInt()
}

/**
 * 字段
 */
data class Column(
    var name: String,
    var comment: String,
    var type: String,
    var size: Int?,
    var defaultValue: String,
    var notNull: Boolean
) : Serializable

fun Column.getField(field: String): String {
    val k = when (field) {
        "name" -> {
            this.name
        }
        "type" -> {
            this.type
        }
        "comment" -> {
            this.comment
        }
        else -> {
            throw UnsupportedOperationException()
        }
    }
    return k
}

fun Column.fillDelimiter(field: String, fieldMaxLen: Int, delimiter: String = "\\t"): String {
    val len = this.getField(field).length
    return delimiter.repeat(max(1, fieldMaxLen - round(len / 4.0).toInt()))
}