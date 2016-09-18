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
package pcgen.base.formatmanager;

import java.util.regex.Pattern;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.util.FormatManager;

/**
 * An ArrayFormatFactory builds a FormatManager supporting Arrays from the name
 * of the format of the component of the Array
 */
public class ArrayFormatFactory implements FormatManagerFactory
{

	/**
	 * A pattern to ensure no multidimensional arrays
	 */
	private static final Pattern ARRAY_PATTERN = Pattern.compile(
		Pattern.quote("ARRAY["), Pattern.CASE_INSENSITIVE);

	@Override
	public FormatManager<?> build(String subFormatName,
		FormatManagerLibrary library)
	{
		if (subFormatName == null)
		{
			throw new IllegalArgumentException(
				"Array Format cannot be built from no instructions");
		}
		if (ARRAY_PATTERN.matcher(subFormatName).find())
		{
			/*
			 * This is currently prohibited because - among other things -
			 * ArrayFormatManager has no way to convert a multi-dimensional
			 * array
			 */
			throw new IllegalArgumentException(
				"Multidimensional Array format not supported: " + subFormatName
					+ " may not contain brackets");
		}
		return new ArrayFormatManager<>(
			library.getFormatManager(subFormatName), ',');
	}

	@Override
	public String getBuilderBaseFormat()
	{
		return "ARRAY";
	}

}
