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

import java.util.List;
import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.lambda.testsupport.SupportingBuilder;

public class StreamUtilitiesTest extends TestCase
{

	public void testToOnlyElementSuccess()
	{
		List<Integer> list = SupportingBuilder.buildIntegerList();
		Optional<Integer> s =
				list.stream().filter(t -> t == 2).reduce(StreamUtilities.toOnlyElement());
		assertTrue(s.isPresent());
		assertEquals(Integer.valueOf(2), s.get());
	}

	public void testToOnlyElementMissed()
	{
		List<Integer> list = SupportingBuilder.buildIntegerList();
		Optional<Integer> s = list.stream().filter(t -> t == 42)
			.reduce(StreamUtilities.toOnlyElement());
		assertFalse(s.isPresent());
	}

	public void testToOnlyElementFailure()
	{
		List<Integer> list = SupportingBuilder.buildIntegerList();
		try
		{
			list.stream().filter(t -> t == 3).reduce(StreamUtilities.toOnlyElement());
			fail("Expected reduce to fail since there are two elements of value 3");
		}
		catch (IllegalArgumentException e)
		{
			//Expected
		}
	}

}
