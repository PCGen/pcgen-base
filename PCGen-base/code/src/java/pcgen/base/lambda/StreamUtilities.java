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
 * 
 * Portions of this file are in the public domain. See:
 * https://blog.codefx.org/java/stream-findfirst-findany-reduce/
 */
package pcgen.base.lambda;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/**
 * A Utility class that supports methods to be used with Streams.
 */
public class StreamUtilities
{
	/**
	 * This supports a smarter replacement for findFirst() that will guarantee only one
	 * matching element.
	 * 
	 * This is designed to be used after a filter, and will reduce a Stream to a single
	 * matching element, but throwing an exception if more than one element is present.
	 * 
	 * @param <T>
	 *            The type of object in the Stream
	 * @return A BinaryOperator which will throw an exception if more than one element is
	 *         present in the Stream
	 */
	public static <T> BinaryOperator<T> toOnlyElement()
	{
		return toOnlyElementThrowing(IllegalArgumentException::new);
	}

	/**
	 * This supports a smarter replacement for findFirst() that will guarantee only one
	 * matching element.
	 * 
	 * This is designed to be used after a filter, and will reduce a Stream to a single
	 * matching element, but throwing an exception if more than one element is present.
	 * 
	 * @param exception
	 *            A supplier of an exception to be thrown if more than one object is found
	 *            in the Stream
	 * @param <T>
	 *            The type of object in the Stream
	 * @return A BinaryOperator which will throw an exception if more than one element is
	 *         present in the Stream
	 */
	public static <T, E extends RuntimeException> BinaryOperator<T> toOnlyElementThrowing(
		Supplier<E> exception)
	{
		return (element, otherElement) -> {
			throw exception.get();
		};
	}
}
