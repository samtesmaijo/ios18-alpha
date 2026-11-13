package com.complux.ios18sim.data

/** Evaluates the two-operand arithmetic expressions accepted by the simulator calculator. */
fun calculateBasic(expression: String): String {
    val normalized = expression.replace("×", "*").replace("÷", "/").replace("−", "-").replace(" ", "")
    val match = Regex("^(-?\\d+(?:\\.\\d+)?)([+*/-])(-?\\d+(?:\\.\\d+)?)$").matchEntire(normalized)
        ?: return "Expressão simples: dois números e um operador"
    val a = match.groupValues[1].toDoubleOrNull() ?: return "Erro"
    val op = match.groupValues[2]
    val b = match.groupValues[3].toDoubleOrNull() ?: return "Erro"
    val value = when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> if (b == 0.0) return "Não é possível dividir por zero" else a / b
        else -> return "Erro"
    }
    return if (value.isFinite() && value % 1.0 == 0.0) value.toLong().toString() else value.toString()
}
