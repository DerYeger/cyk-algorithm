package eu.yeger.cyk

public sealed class Result<out T : Any> {
    public data class Success<out T : Any>(val data: T) : Result<T>()
    public data class Failure<T : Any>(val error: String) : Result<T>()
}

public fun <T : Any> succeed(data: T): Result.Success<T> {
    return Result.Success(data)
}

public fun <T : Any> fail(error: String): Result.Failure<T> {
    return Result.Failure(error)
}

public fun <T : Any, U : Any> Result<T>.map(transformation: (T) -> U): Result<U> {
    return when (this) {
        is Result.Success -> Result.Success(transformation(data))
        is Result.Failure -> Result.Failure(error)
    }
}

public fun <T : Any> Result<T>.mapError(transformation: (String) -> String): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> fail(transformation(error))
    }
}

public fun <T : Any, U : Any> Result<T>.andThen(transformation: (T) -> Result<U>): Result<U> {
    return when (this) {
        is Result.Success -> transformation(data)
        is Result.Failure -> Result.Failure(error)
    }
}

public fun <T : Any, U : Any> Result<T>.and(other: Result<U>): Result<U> {
    return when (this) {
        is Result.Success -> other
        is Result.Failure -> Result.Failure(error)
    }
}

public fun <T : Any> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        is Result.Failure -> null
    }
}

public fun <T : Any> Result<T>.getOr(fallback: T): T {
    return when (this) {
        is Result.Success -> data
        is Result.Failure -> fallback
    }
}

public fun <T : Any> Result<T>.getOrElse(block: (String) -> T): T {
    return when (this) {
        is Result.Success -> data
        is Result.Failure -> block(error)
    }
}

public fun <T : Any> Result<T>.getErrorOrNull(): String? {
    return when (this) {
        is Result.Success -> null
        is Result.Failure -> error
    }
}

public fun <T : Any> Result<T>.getErrorOr(fallback: String): String {
    return when (this) {
        is Result.Success -> fallback
        is Result.Failure -> error
    }
}

public fun <T : Any> Result<T>.getErrorOrElse(block: (T) -> String): String {
    return when (this) {
        is Result.Success -> block(data)
        is Result.Failure -> error
    }
}
