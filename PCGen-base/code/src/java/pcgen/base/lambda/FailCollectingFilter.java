/*
 * Copyright (c) Thomas Parker, 2018.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.lambda;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A Predicate that collects objects when they fail the test of a provided underlying
 * Predicate.
 * 
 * @param <T>
 *            The type of object processed by this FailCollectingFilter
 */
public class FailCollectingFilter<T> implements Predicate<T>
{
	/**
	 * The underlying Predicate used to determine if items are kept in the stream (true)
	 * or diverted to the collector (false).
	 */
	private final Predicate<T> predicate;

	/**
	 * The collector for situations where the Predicate returned false.
	 */
	private final Collection<T> collector;

	/**
	 * Constructs a new FailCollectingFilter for the given predicate and collection List.
	 * 
	 * This makes no attempt to validate that the given collector will accept new items.
	 * That situation and all others related to the underlying behavior of the given
	 * Collection are the responsibility of the object calling this constructor.
	 * 
	 * @param predicate
	 *            The underlying Predicate used to determine if items are kept in the
	 *            stream (true) or diverted to the collector (false)
	 * @param collector
	 *            The collector for situations where the Predicate returned false
	 */
	public FailCollectingFilter(Predicate<T> predicate, Collection<T> collector)
	{
		this.predicate = Objects.requireNonNull(predicate);
		this.collector = Objects.requireNonNull(collector);
	}

	@Override
	public boolean test(T t)
	{
		boolean passed = predicate.test(t);
		if (!passed)
		{
			collector.add(t);
		}
		return passed;
	}
}
