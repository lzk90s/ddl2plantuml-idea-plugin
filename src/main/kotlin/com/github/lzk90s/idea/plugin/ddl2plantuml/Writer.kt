package com.github.lzk90s.idea.plugin.ddl2plantuml

import java.nio.file.Files
import java.nio.file.Path

const val TEMPLATE = """
@startuml

!define Table(name,desc) class name as "desc" << (T,#FFAAAA) >>
!define PK(x) <color:red><b>x</b></color>
!define T(x) <color:royalBlue>x</color>
!define C(x) <color:green>x</color>
hide methods
hide stereotypes

__content__

@enduml
"""

interface Writer {
    fun write()

    fun parse(tables: Iterable<Table>): String {
        val content = tables.joinToString("") { table ->
            val nameMaxLen = table.columnFieldMaxLen("name")
            val typeMaxLen = table.columnFieldMaxLen("type")
            val columns = table.columnList.joinToString("\n") {
                "${it.name}${it.fillDelimiter("name", nameMaxLen)} ${it.typeWrapper()}${
                    it.fillDelimiter(
                        "type",
                        typeMaxLen
                    )
                } ${it.commentWrapper()}"
            }
            "Table(${table.name}, \"${table.name}\\n(${table.comment})\"){\n$columns\n} \n\n"
        }

        return TEMPLATE.replace("__content__", content)
    }

    private fun Column.typeWrapper(): String {
        return "T(\"${this.type}\")"
    }

    private fun Column.commentWrapper(): String {
        return "C(\"${this.comment}\")"
    }
}

class FileWriter(private val path: Path, private val tables: Iterable<Table>) : Writer {

    override fun write() {
        Files.write(path, parse(tables).toByteArray())
    }

}

class ConsoleWriter(private val tables: Iterable<Table>) : Writer {

    override fun write() {
        println(parse(tables))
    }
}

class StringBuilderWriter(private val sb: StringBuilder, private val tables: Iterable<Table>) : Writer {

    override fun write() {
        sb.append(parse(tables))
    }
}