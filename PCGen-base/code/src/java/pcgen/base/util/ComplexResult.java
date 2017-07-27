/*
 * Copyright (c) 2017 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Interface to provide feedback on complex operations that either return a value, or
 * deserve a message that describes whey they failed (without throwing an exception).
 */
public interface ComplexResult<T> extends Supplier<T> 
{
	/**
	 * Returns any messages contained by this ComplexResult.
	 * 
	 * Note: If the ComplexResult returns a non-null value to passed() then the behavior
	 * of this method is not controlled by this interface.
	 * 
	 * @return A non-null list of messages contained by this ComplexResult.
	 */
	public Collection<String> getMessages();
}
