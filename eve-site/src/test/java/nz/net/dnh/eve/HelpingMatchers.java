package nz.net.dnh.eve;

import static org.hamcrest.Matchers.equalTo;

import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Helper methods for dealing with hamcrest and generic warnings
 */
public class HelpingMatchers {

	/**
	 * @see Matchers#contains(Matcher...)
	 */
	@SafeVarargs
	public static <E> Matcher<Iterable<? extends E>> contains(final Matcher<? super E>... items) {
		return Matchers.contains(items);
	}
	
	/**
	 * @see Matchers#contains(T...)
	 */
	@SafeVarargs
	public static <E> Matcher<Iterable<? extends E>> contains(final E... items) {
		return Matchers.contains(items);
	}

	/**
	 * @see Matchers#containsInAnyOrder(Matcher...)
	 */
	@SafeVarargs
	public static <E> Matcher<Iterable<? extends E>> containsInAnyOrder(final Matcher<? super E>... items) {
		return Matchers.containsInAnyOrder(items);
	}

	/**
	 * @see Matchers#greaterThan(Comparable)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends java.lang.Comparable<? super T>> org.hamcrest.Matcher<T> greaterThan(final T value) {
		return Matchers.greaterThan((Comparable) value);
	}

	/**
	 * @see Matchers#lessThan(Comparable)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends java.lang.Comparable<? super T>> org.hamcrest.Matcher<T> lessThan(final T value) {
		return Matchers.lessThan((Comparable) value);
	}

	/**
	 * Convenience method which takes the raw value as the second parameter
	 * 
	 * @see Matchers#hasEntry(Matcher, Matcher)
	 */
	public static <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(final Matcher<? super K> keyMatcher, final V value) {
		return Matchers.hasEntry(keyMatcher, equalTo(value));
	}

	/**
	 * Convenience method which takes the raw value as the first parameter
	 * 
	 * @see Matchers#hasEntry(Matcher, Matcher)
	 */
	public static <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(final K key, final Matcher<? super V> valueMatcher) {
		return Matchers.hasEntry(equalTo(key), valueMatcher);
	}

	/**
	 * Exactly the same as the original method, here to make static imports
	 * happy
	 * 
	 * @see Matchers#hasEntry(Matcher, Matcher)
	 */
	public static <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(final Matcher<? super K> keyMatcher,
			final Matcher<? super V> valueMatcher) {
		return Matchers.hasEntry(keyMatcher, valueMatcher);
	}

	/**
	 * Exactly the same as the original method, here to make static imports
	 * happy
	 * 
	 * @see Matchers#hasEntry(Object, Object)
	 */
	public static <K, V> Matcher<Map<? extends K, ? extends V>> hasEntry(final K key, final V value) {
		return Matchers.hasEntry(key, value);
	}

}
