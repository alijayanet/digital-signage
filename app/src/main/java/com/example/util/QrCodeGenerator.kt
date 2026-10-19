package com.example.util

/**
 * Lightweight, self-contained QR Code Matrix generator in pure Kotlin.
 * Generates ISO/IEC 18004 compliant QR Code matrices (Version 1 to 4) with Byte mode encoding.
 */
object QrCodeGenerator {

    data class QrMatrix(val size: Int, val modules: Array<BooleanArray>) {
        fun isDark(row: Int, col: Int): Boolean {
            if (row < 0 || row >= size || col < 0 || col >= size) return false
            return modules[row][col]
        }
    }

    fun encode(text: String): QrMatrix {
        val dataBytes = text.toByteArray(Charsets.ISO_8859_1)
        val dataLength = dataBytes.size

        // Determine smallest QR version capable of holding the data with ECC Level M
        // V1-M: 14 bytes, V2-M: 26 bytes, V3-M: 42 bytes, V4-M: 62 bytes, V5-M: 84 bytes
        val (version, totalDataCodewords, ecCodewords) = when {
            dataLength <= 14 -> Triple(1, 16, 10)
            dataLength <= 26 -> Triple(2, 28, 16)
            dataLength <= 42 -> Triple(3, 44, 26)
            dataLength <= 62 -> Triple(4, 64, 36)
            dataLength <= 84 -> Triple(5, 86, 48)
            else -> Triple(6, 108, 64)
        }

        val size = version * 4 + 17
        val matrix = Array(size) { BooleanArray(size) }
        val isReserved = Array(size) { BooleanArray(size) }

        // 1. Finder Patterns
        addFinderPattern(matrix, isReserved, 0, 0)
        addFinderPattern(matrix, isReserved, size - 7, 0)
        addFinderPattern(matrix, isReserved, 0, size - 7)

        // 2. Alignment Patterns for Version >= 2
        val alignCoords = getAlignmentCoordinates(version)
        for (r in alignCoords) {
            for (c in alignCoords) {
                if (!isReserved[r][c]) {
                    addAlignmentPattern(matrix, isReserved, r, c)
                }
            }
        }

        // 3. Timing Patterns
        for (i in 8 until size - 8) {
            val bit = (i % 2 == 0)
            if (!isReserved[6][i]) {
                matrix[6][i] = bit
                isReserved[6][i] = true
            }
            if (!isReserved[i][6]) {
                matrix[i][6] = bit
                isReserved[i][6] = true
            }
        }

        // Dark Module
        matrix[4 * version + 9][8] = true
        isReserved[4 * version + 9][8] = true

        // Reserve Format Information areas
        for (i in 0..8) {
            isReserved[8][i] = true
            isReserved[i][8] = true
            isReserved[8][size - 1 - i] = true
            isReserved[size - 1 - i][8] = true
        }

        // 4. Encode Data Bits
        val bitBuffer = mutableListOf<Boolean>()
        // Byte mode indicator: 0100
        appendBits(bitBuffer, 4, 4)
        // Character count indicator (8 bits for V1-9)
        appendBits(bitBuffer, dataLength, 8)
        // Data
        for (b in dataBytes) {
            appendBits(bitBuffer, b.toInt() and 0xFF, 8)
        }
        // Terminator (up to 4 zeroes)
        val capacityBits = totalDataCodewords * 8
        val terminatorLen = minOf(4, capacityBits - bitBuffer.size)
        appendBits(bitBuffer, 0, terminatorLen)
        // Pad to byte boundary
        while (bitBuffer.size % 8 != 0) {
            bitBuffer.add(false)
        }
        // Pad bytes (0xEC, 0x11)
        val padBytes = intArrayOf(0xEC, 0x11)
        var padIdx = 0
        while (bitBuffer.size < capacityBits) {
            appendBits(bitBuffer, padBytes[padIdx % 2], 8)
            padIdx++
        }

        // Convert data bits to data codewords
        val dataCodewords = IntArray(totalDataCodewords)
        for (i in 0 until totalDataCodewords) {
            var byteVal = 0
            for (j in 0 until 8) {
                if (bitBuffer[i * 8 + j]) {
                    byteVal = byteVal or (1 shl (7 - j))
                }
            }
            dataCodewords[i] = byteVal
        }

        // 5. Compute Error Correction (Reed-Solomon)
        val ecBytes = calculateReedSolomon(dataCodewords, ecCodewords)
        val allCodewords = dataCodewords + ecBytes

        // Convert all codewords to bitstream
        val finalBits = mutableListOf<Boolean>()
        for (cw in allCodewords) {
            appendBits(finalBits, cw, 8)
        }

        // 6. Place data bits using Mask Pattern 0: (row + col) % 2 == 0
        var bitIndex = 0
        var right = size - 1
        var upward = true

        while (right > 0) {
            if (right == 6) right-- // Skip vertical timing column

            val colList = if (upward) (size - 1 downTo 0) else (0 until size)
            for (row in colList) {
                for (cOffset in 0..1) {
                    val col = right - cOffset
                    if (!isReserved[row][col]) {
                        var bit = if (bitIndex < finalBits.size) finalBits[bitIndex++] else false
                        // Mask 0: flip if (row + col) % 2 == 0
                        if ((row + col) % 2 == 0) {
                            bit = !bit
                        }
                        matrix[row][col] = bit
                    }
                }
            }
            upward = !upward
            right -= 2
        }

        // 7. Write Format Information (ECC Level M = 00, Mask 0 = 000 -> 00000)
        // With BCH error correction & mask 101010000010010 -> 0x5412 (standard for M + Mask 0)
        val formatBits = getFormatBits(0) // Mask 0, ECC M
        writeFormatInfo(matrix, size, formatBits)

        return QrMatrix(size, matrix)
    }

    private fun appendBits(buffer: MutableList<Boolean>, value: Int, length: Int) {
        for (i in length - 1 downTo 0) {
            buffer.add(((value shr i) and 1) == 1)
        }
    }

    private fun addFinderPattern(matrix: Array<BooleanArray>, isReserved: Array<BooleanArray>, startR: Int, startC: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                val isDark = (r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4))
                val targetR = startR + r
                val targetC = startC + c
                if (targetR in matrix.indices && targetC in matrix.indices) {
                    matrix[targetR][targetC] = isDark
                    isReserved[targetR][targetC] = true
                }
            }
        }
        // Separator ring
        for (r in -1..7) {
            for (c in -1..7) {
                val targetR = startR + r
                val targetC = startC + c
                if (targetR in matrix.indices && targetC in matrix.indices) {
                    isReserved[targetR][targetC] = true
                }
            }
        }
    }

    private fun addAlignmentPattern(matrix: Array<BooleanArray>, isReserved: Array<BooleanArray>, centerR: Int, centerC: Int) {
        for (r in -2..2) {
            for (c in -2..2) {
                val isDark = (r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0))
                val targetR = centerR + r
                val targetC = centerC + c
                matrix[targetR][targetC] = isDark
                isReserved[targetR][targetC] = true
            }
        }
    }

    private fun getAlignmentCoordinates(version: Int): IntArray {
        return when (version) {
            1 -> intArrayOf()
            2 -> intArrayOf(6, 18)
            3 -> intArrayOf(6, 22)
            4 -> intArrayOf(6, 26)
            5 -> intArrayOf(6, 30)
            6 -> intArrayOf(6, 34)
            else -> intArrayOf(6, 22)
        }
    }

    private fun getFormatBits(mask: Int): Int {
        // ECC Level M is 00 (2 bits). Mask 0 is 000.
        // Standard precomputed format word for Level M and Mask 0 is 0x5412 (010101000010010)
        return when (mask) {
            0 -> 0x5412
            1 -> 0x5125
            2 -> 0x5E7C
            3 -> 0x5B4B
            4 -> 0x45F9
            5 -> 0x40CE
            6 -> 0x4F97
            else -> 0x4AA0
        }
    }

    private fun writeFormatInfo(matrix: Array<BooleanArray>, size: Int, formatBits: Int) {
        for (i in 0..14) {
            val bit = ((formatBits shr (14 - i)) and 1) == 1
            // Top-left
            when {
                i <= 5 -> matrix[8][i] = bit
                i == 6 -> matrix[8][7] = bit
                i == 7 -> matrix[8][8] = bit
                i == 8 -> matrix[7][8] = bit
                else -> matrix[14 - i][8] = bit
            }
            // Bottom-left / Top-right
            when {
                i < 8 -> matrix[size - 1 - i][8] = bit
                else -> matrix[8][size - 15 + i] = bit
            }
        }
    }

    // Reed-Solomon Code Generator for GF(256) with primitive polynomial 0x11D
    private fun calculateReedSolomon(data: IntArray, ecCount: Int): IntArray {
        val gfExp = IntArray(512)
        val gfLog = IntArray(256)
        var x = 1
        for (i in 0 until 255) {
            gfExp[i] = x
            gfExp[i + 255] = x
            gfLog[x] = i
            x = (x shl 1)
            if (x >= 256) x = x xor 0x11D
        }

        fun gfMul(a: Int, b: Int): Int {
            if (a == 0 || b == 0) return 0
            return gfExp[gfLog[a] + gfLog[b]]
        }

        // Generator polynomial for ecCount
        var gen = intArrayOf(1)
        for (i in 0 until ecCount) {
            val nextGen = IntArray(gen.size + 1)
            val root = gfExp[i]
            for (j in gen.indices) {
                nextGen[j] = nextGen[j] xor gfMul(gen[j], root)
                nextGen[j + 1] = nextGen[j + 1] xor gen[j]
            }
            gen = nextGen
        }

        val result = IntArray(ecCount)
        val remainder = IntArray(data.size + ecCount)
        System.arraycopy(data, 0, remainder, 0, data.size)

        for (i in data.indices) {
            val coef = remainder[i]
            if (coef != 0) {
                for (j in gen.indices) {
                    remainder[i + j] = remainder[i + j] xor gfMul(gen[j], coef)
                }
            }
        }

        System.arraycopy(remainder, data.size, result, 0, ecCount)
        return result
    }
}
