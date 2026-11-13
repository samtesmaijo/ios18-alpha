package com.complux.ios18sim

import com.complux.ios18sim.data.calculateBasic
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorLogicTest {
    @Test fun addsNumbers() = assertEquals("4", calculateBasic("2+2"))
    @Test fun handlesDecimalMultiplication() = assertEquals("3.75", calculateBasic("1.5×2.5"))
    @Test fun rejectsDivisionByZero() = assertEquals("Não é possível dividir por zero", calculateBasic("8÷0"))
    @Test fun handlesNegativeResult() = assertEquals("-3", calculateBasic("2−5"))
    @Test fun rejectsUnsupportedExpression() = assertEquals(
        "Expressão simples: dois números e um operador",
        calculateBasic("2+3×4")
    )
}
