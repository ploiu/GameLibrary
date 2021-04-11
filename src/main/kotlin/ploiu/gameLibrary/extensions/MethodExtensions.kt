package ploiu.gameLibrary.extensions

import ploiu.gameLibrary.exception.UnrecoverableGameException
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * checks if the method is annotated with the passed annotation class
 */
fun Method.hasAnnotation(annotationClass: KClass<*>): Boolean {
    if (this.annotations.isEmpty()) {
        return false
    } else {
        for (annotation in this.declaredAnnotations) {
            if (annotation::annotationClass.get() == annotationClass) {
                return true
            }
        }
    }
    return false
}

/**
 * if this method has an annotation of the passed class, get the annotation and return it
 * else return `null`
 */
@Suppress("UNCHECKED_CAST")
fun <T> Method.getAnnotation(annotationClass: KClass<out T>): T? where T : Any {
    if (!this.hasAnnotation(annotationClass)) {
        return null
    } else {
        for (annotation in this.declaredAnnotations) {
            if (annotation::annotationClass.get() == annotationClass) {
                return annotation as T
            }
        }
        throw UnrecoverableGameException("This should never happen, but issue is that we have an annotation but could not find it!")
    }
}
