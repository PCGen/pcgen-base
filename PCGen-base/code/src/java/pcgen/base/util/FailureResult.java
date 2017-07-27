/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a failed operation on a method that returns a Boolean result. This will
 * return FALSE rather than null from get().
 */
public class FailureResult implements ComplexResult<Boolean>
{
	/**
	 * The error String indicating the reason for the failure.
	 */
	private final String error;

	/**
	 * Constructs a new FailureResult containing the given String as the error message.
	 * 
	 * @param error
	 *            The non-null error message for this FailureResult
	 */
	public FailureResult(String error)
	{
		this.error = Objects.requireNonNull(error);
	}

	@Override
	public Boolean get()
	{
		return Boolean.FALSE;
	}

	@Override
	public String toString()
	{
		return "Error Result: " + error;
	}

	@Override
	public Collection<String> getMessages()
	{
		return Collections.singleton(error);
	}
}
