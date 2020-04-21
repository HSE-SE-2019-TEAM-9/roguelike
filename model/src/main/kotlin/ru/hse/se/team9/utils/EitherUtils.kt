package ru.hse.se.team9.utils

import arrow.core.Either

/**
 * Extract Left or Right value in the form of their common superclass
 */
fun <A> Either<A, A>.get() = this.fold({ it }, { it })