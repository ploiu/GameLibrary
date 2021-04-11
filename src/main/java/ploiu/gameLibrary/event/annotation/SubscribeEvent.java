package ploiu.gameLibrary.event.annotation;

import ploiu.gameLibrary.event.GameEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used to annotate a function as a static event handler. These functions must take a single argument which must be the same type
 * as the Class passed into this annotation
 * e.g.
 * <pre>
 * // ... class declaration etc
 * {@code @SubscribeEvent(GameCloseEvent.class)
 * fun example(event: GameCloseEvent) {
 * // ...
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {
	Class<? extends GameEvent> value();
}
