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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.lambda.testsupport.SupportingBuilder;

public class FailCollectingFilterTest extends TestCase
{

	public void testCollectionList()
	{
		List<Integer> list = SupportingBuilder.buildIntegerList();
		List<Integer> failures = new ArrayList<>();
		Optional<Integer> s =
				list.stream().filter(new FailCollectingFilter<>(t -> t == 2, failures))
					.reduce(StreamUtilities.toOnlyElement());
		assertTrue(s.isPresent());
		assertEquals(Integer.valueOf(2), s.get());
		assertEquals(3, failures.size());
		assertTrue(failures.remove(Integer.valueOf(1)));
		assertTrue(failures.remove(Integer.valueOf(3)));
		assertTrue(failures.remove(Integer.valueOf(3)));
	}

	public void testCollectionListNoSuccess()
	{
		List<Integer> list = SupportingBuilder.buildIntegerList();
		List<Integer> failures = new ArrayList<>();
		Optional<Integer> s =
				list.stream().filter(new FailCollectingFilter<>(t -> t == 42, failures))
					.reduce(StreamUtilities.toOnlyElement());
		assertFalse(s.isPresent());
		assertEquals(4, failures.size());
		assertTrue(failures.remove(Integer.valueOf(1)));
		assertTrue(failures.remove(Integer.valueOf(2)));
		assertTrue(failures.remove(Integer.valueOf(3)));
		assertTrue(failures.remove(Integer.valueOf(3)));
	}
}
