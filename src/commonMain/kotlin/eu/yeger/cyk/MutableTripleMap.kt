package eu.yeger.cyk

class MutableTripleMap<A, B, C, D>(
    private val mapImplementation: MutableMap<Triple<A, B, C>, D> = HashMap(),
): Map<Triple<A, B, C>, D> by mapImplementation {

    operator fun get(a: A, b: B, c: C): D? {
        return mapImplementation[Triple(a, b, c)]
    }

    fun has(a: A, b: B, c: C): Boolean {
        return get(a, b, c) != null
    }

    operator fun set(a: A, b: B, c: C, d: D) {
        mapImplementation[Triple(a, b, c)] = d
    }
}