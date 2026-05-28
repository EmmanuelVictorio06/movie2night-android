package com.movie2night.core.network

import retrofit2.HttpException
import java.io.IOException

/**
 * Converte exceções em mensagens amigáveis em português para a UI.
 * Erros de validação (Exception comum com mensagem própria) preservam a mensagem.
 */
fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Sem conexão com o servidor. Verifique sua internet e tente novamente."
    is HttpException -> when (code()) {
        400 -> "Não foi possível concluir. Verifique os dados e tente novamente."
        401 -> "Sua sessão expirou. Faça login novamente."
        403 -> "Você não tem permissão para essa ação."
        404 -> "Conteúdo não encontrado."
        409 -> "Isso já foi feito anteriormente."
        in 500..599 -> "Erro no servidor. Tente novamente em instantes."
        else -> "Algo deu errado. Tente novamente."
    }
    else -> message ?: "Algo deu errado. Tente novamente."
}
