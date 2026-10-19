package com.example.util

/**
 * Utilitas untuk membaca dan menulis format CSV dengan dukungan:
 * 1. Pemisah Koma (,) dan Titik Koma (;) yang lazim digunakan di Excel Indonesia.
 * 2. Tanda petik ganda ("..."), baris baru di dalam sel, dan karakter khusus.
 * 3. UTF-8 Byte Order Mark (BOM) agar karakter Bahasa Indonesia terbaca rapi di Microsoft Excel.
 */
object CsvUtils {

    fun parseCsv(rawText: String): List<List<String>> {
        val clean = rawText.removePrefix("\uFEFF").replace("\r\n", "\n").replace("\r", "\n")
        val lines = clean.split("\n").filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        // Deteksi pemisah koma vs titik-koma dari baris pertama
        val firstLine = lines.first()
        val delimiter = if (firstLine.count { it == ';' } > firstLine.count { it == ',' }) ';' else ','

        val rows = mutableListOf<List<String>>()
        for (line in lines) {
            rows.add(parseLine(line, delimiter))
        }
        return rows
    }

    private fun parseLine(line: String, delimiter: Char): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    sb.append('"')
                    i++ // Skip escaped quote
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == delimiter && !inQuotes) {
                tokens.add(sb.toString().trim())
                sb.setLength(0)
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    fun escapeCsv(value: String): String {
        val str = value.trim()
        return if (str.contains(",") || str.contains(";") || str.contains("\"") || str.contains("\n")) {
            "\"" + str.replace("\"", "\"\"") + "\""
        } else {
            str
        }
    }
}
