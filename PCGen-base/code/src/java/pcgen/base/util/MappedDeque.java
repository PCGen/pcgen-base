/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * A MappedDeque is a wrapper that effectively contains a Map from TypedKey
 * objects to Deque objects, and manages the changes to the underlying Deque
 * objects.
 * 
 * By using TypedKey objects to manage the Deque objects, this also provides
 * type safety (using generics) for the values of each Deque.
 */
public class MappedDeque
{

	/**
	 * The underlying map for this MappedDeque that contains the Deque objects.
	 */
	private final Map<TypedKey<?>, Deque<Object>> map =
			new HashMap<TypedKey<?>, Deque<Object>>();

	/**
	 * The object used to represent the null value in the Deque objects. (Deque
	 * does not support null values, we want to support that.)
	 */
	private static final Object NULL = new Object();

	/**
	 * Pushes a new value onto the Deque for the given TypedKey.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be pushed onto
	 *            the Deque
	 * @param value
	 *            The value to be pushed onto the Deque for the given TypeKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> void push(TypedKey<T> key, T value)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Key cannot be null");
		}
		getDeque(key).push(MappedDeque.wrap(value));
	}

	/**
	 * Pops a value from the Deque for the given TypedKey.
	 * 
	 * Note that this method will not block or throw an error if the Deque is
	 * empty. It will simply return the "Default Value" for the given TypeKey.
	 * Note null is a legal default value.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be popped from
	 *            the Deque
	 * @return The value popped from the Deque for the given TypeKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> T pop(TypedKey<T> key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Key cannot be null");
		}
		Deque<Object> dq = map.get(key);
		if ((dq == null) || dq.isEmpty())
		{
			return key.getDefaultValue();
		}
		return key.cast(MappedDeque.unwrap(dq.pop()));
	}

	/**
	 * Returns the top value of the Deque for the given TypedKey, without
	 * performing a pop.
	 * 
	 * Note that this method will not block or throw an error if the Deque is
	 * empty. It will simply return the "Default Value" for the given TypeKey.
	 * Note null is a legal default value.
	 * 
	 * @param key
	 *            The TypeKey for which the top value should be returned
	 * @return The top value of the Deque for the given TypedKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> T peek(TypedKey<T> key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Key cannot be null");
		}
		Deque<Object> dq = map.get(key);
		T value;
		if ((dq == null) || dq.isEmpty())
		{
			value = key.getDefaultValue();
		}
		else
		{
			value = key.cast(MappedDeque.unwrap(dq.peek()));
		}
		return value;
	}

	/**
	 * Sets a new value onto the Deque for the given TypedKey.
	 * 
	 * This is effectively a shortcut for calling pop(key) followed by push(key,
	 * value). This has the same effects as pop of not throwing an error if the
	 * Deque is currently empty.
	 * 
	 * @param key
	 *            The TypeKey for which the given value should be set as the top
	 *            value on the Deque
	 * @param value
	 *            The value to be set as the top value on the Deque for the
	 *            given TypeKey
	 * @throws IllegalArgumentException
	 *             if the given key is null
	 */
	public <T> void set(TypedKey<T> key, T value)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Key cannot be null");
		}
		Deque<Object> dq = getDeque(key);
		if (!dq.isEmpty())
		{
			dq.pop();
		}
		dq.push(MappedDeque.wrap(value));
	}

	/**
	 * Returns a Deque for the given TypedKey, building and storing it if the
	 * Deque does not currently exist.
	 */
	private <T> Deque<Object> getDeque(TypedKey<T> key)
	{
		Deque<Object> dq = map.get(key);
		if (dq == null)
		{
			dq = new ArrayDeque<>();
			map.put(key, dq);
		}
		return dq;
	}

	/**
	 * Unwraps the null object, since Deque does not support null values
	 */
	private Object unwrap(Object o)
	{
		return (NULL == o) ? null : o;
	}

	/**
	 * Wraps the null object, since Deque does not support null values
	 */
	private Object wrap(Object o)
	{
		return (o == null) ? NULL : o;
	}
}
