package org.flightofstairs.kpd.utils

internal fun <X> X.maybeUpdate(updateFn: (X) -> X): X? = updateFn(this).let { if (this === it) null else it }
