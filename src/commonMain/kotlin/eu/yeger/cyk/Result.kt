package eu.yeger.cyk

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure<T : Any>(val error: String) : Result<T>()
}

fun <T : Any> succeed(data: T): Result.Success<T> {
    return Result.Success(data)
}

fun <T : Any> fail(error: String): Result.Failure<T> {
    return Result.Failure(error)
}

fun <T : Any, U : Any> Result<T>.map(transformation: (T) -> U): Result<U> {
    return when (this) {
        is Result.Success -> Result.Success(transformation(data))
        is Result.Failure -> Result.Failure(error)
    }
}

fun <T : Any, U : Any> Result<T>.andThen(transformation: (T) -> Result<U>): Result<U> {
    return when (this) {
        is Result.Success -> transformation(data)
        is Result.Failure -> Result.Failure(error)
    }
}

fun <T : Any, U : Any> Result<T>.and(other: Result<U>): Result<U> {
    return when (this) {
        is Result.Success -> other
        is Result.Failure -> Result.Failure(error)
    }
}

fun <T : Any> Result<T>.getOr(fallback: T): T {
    return when (this) {
        is Result.Success -> data
        is Result.Failure -> fallback
    }
}

fun <T : Any> Result<T>.getOrElse(block: () -> T): T {
    return when (this) {
        is Result.Success -> data
        is Result.Failure -> block()
    }
}
