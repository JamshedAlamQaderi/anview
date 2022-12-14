package com.jamshedalamqaderi.anview.ext

fun String?.isMatchExists(targets: List<String>): Boolean {
    for (target in targets) {
        if (this.isMatched(target)) {
            return true
        }
    }
    return false
}

fun String?.isMatched(target: String?): Boolean {
    if (target == null) return false
    val targetRegex = Regex(target)
    return targetRegex.matches(this ?: "")
}

fun CharSequence?.isMatched(target: String?): Boolean {
    return this?.toString()?.isMatched(target) ?: false
}