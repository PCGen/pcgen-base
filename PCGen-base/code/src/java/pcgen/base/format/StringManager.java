/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * A StringManager is a FormatManager for dealing with String objects.
 */
public class StringManager implements FormatManager<String>
{

	/**
	 * Converts the given String to an object of the type processed by this
	 * FormatManager.
	 */
	@Override
	public String convert(String s)
	{
		if (s == null)
		{
			throw new IllegalArgumentException("String cannot be null");
		}
		return s;
	}

	/**
	 * Converts the given String to an Indirect containing an object of the type
	 * processed by this FormatManager.
	 */
	@Override
	public Indirect<String> convertIndirect(String s)
	{
		if (s == null)
		{
			throw new IllegalArgumentException("String cannot be null");
		}
		return new BasicIndirect<>(this, s);
	}

	/**
	 * "Unconverts" the object (converts the object to a "serializable" String
	 * format that can be reinterpreted by the convert* methods).
	 */
	@Override
	public String unconvert(String s)
	{
		if (s == null)
		{
			throw new IllegalArgumentException(
				"String to unconvert cannot be null");
		}
		return s;
	}

	/**
	 * The Class that this FormatManager can convert or unconvert.
	 */
	@Override
	public Class<String> getManagedClass()
	{
		return String.class;
	}

	/**
	 * The String used to refer to this format in files like the variable
	 * definition file.
	 */
	@Override
	public String getIdentifierType()
	{
		return "STRING";
	}

	@Override
	public int hashCode()
	{
		return 987;
	}

	@Override
	public boolean equals(Object o)
	{
		return (o == this) || (o instanceof StringManager);
	}

	/**
	 * Returns null, as this FormatManager does not have a component.
	 */
	@Override
	public FormatManager<?> getComponentManager()
	{
		return null;
	}
}
