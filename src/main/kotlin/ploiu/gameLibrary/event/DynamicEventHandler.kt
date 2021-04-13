package ploiu.gameLibrary.event

import kotlin.reflect.KClass

/**
 * a functional interface used to create a dynamic event handler
 */
fun interface Applicator<T : GameEvent> {
    // must take raw GameEvent as (for some reason) Kotlin will change the reference to `T` to `Nothing`
    fun apply(event: GameEvent)
}

/**
 * Because java has type erasure at run time, we need to create a wrapper class that carries
 * extra type information
 */
class DynamicEventHandler<T : GameEvent>(val event: KClass<T>, val handler: Applicator<T>)