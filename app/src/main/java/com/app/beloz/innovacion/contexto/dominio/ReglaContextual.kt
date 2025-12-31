package com.app.beloz.innovacion.contexto.dominio

/**
 * Una regla encapsula la condición y la sugerencia resultante.
 */
class ReglaContextual(
    private val nombre: String,
    private val condicion: (ContextoEntrada) -> Boolean,
    private val generar: (ContextoEntrada) -> SugerenciaContextual
) {
    fun evaluar(contexto: ContextoEntrada): SugerenciaContextual? =
        if (condicion(contexto)) generar(contexto) else null

    override fun toString(): String = nombre
}
