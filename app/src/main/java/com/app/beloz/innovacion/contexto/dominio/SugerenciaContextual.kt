package com.app.beloz.innovacion.contexto.dominio

/**
 * Mensaje o recomendación que luego podremos mostrar en la UI.
 */
data class SugerenciaContextual(
    val titulo: String,
    val descripcion: String,
    val etiquetas: List<String> = emptyList(),
    val motivo: String? = null
)
