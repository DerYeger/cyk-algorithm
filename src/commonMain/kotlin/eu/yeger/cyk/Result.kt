package eu.yeger.cyk

public sealed class Result<out T : Any> {
    public data class Success<out T : Any>(val data: T) : Result<T>()
    public data class Failure<T : Any>(val error: String) : Result<T>()
}

public fun <T : Any> T.asSuccess(): Result.Success<T> {
    return Result.Success(this)
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

public fun <T : Any, U : Any, V : Any> Result<T>.with(other: Result<U>, block: (T, U) -> V): Result<V> {
    return when {
        this is Result.Success && other is Result.Success -> Result.Success(block(this.data, other.data))
        this is Result.Failure && other is Result.Failure -> Result.Failure("${this.error}\n${other.error}")
        this is Result.Failure -> Result.Failure(this.error)
        other is Result.Failure -> Result.Failure(other.error)
        else -> Result.Failure("Impossible state reached.")
    }
}

public fun <T : Any, U : Any> List<T>.mapAsResult(
    transform: T.() -> Result<U>,
): Result<List<U>> {
    return fold(Result.Success(emptyList<U>()) as Result<List<U>>) { acc, x ->
        acc.with(transform(x), List<U>::plus)
    }
}
