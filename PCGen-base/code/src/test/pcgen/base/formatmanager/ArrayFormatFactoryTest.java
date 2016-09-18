/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.formatmanager;

import java.util.Arrays;

import junit.framework.TestCase;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;

public class ArrayFormatFactoryTest extends TestCase
{
	private static final Number[] ARR_N3_4_5 = {
		Integer.valueOf(-3), Integer.valueOf(4), Integer.valueOf(5)};
	private static final Number[] ARR_N3_4P1_5 = {
		Integer.valueOf(-3), Double.valueOf(4.1), Integer.valueOf(5)};
	private static final Number[] ARR_1P4 = {Double.valueOf(1.4)};
	private static final Number[] ARR_N3 = {Integer.valueOf(-3)};
	private static final Number[] ARR_1 = {Integer.valueOf(1)};

	private SimpleFormatManagerLibrary library;
	private ArrayFormatFactory factory;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(library);
		factory = new ArrayFormatFactory();
	}

	public void testFailBadSubFormat()
	{
		try
		{
			factory.build("NUM", library);
			fail("bad sub form should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testFailNullSubFormat()
	{
		try
		{
			factory.build(null, library);
			fail("null sub form should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testConvert()
	{
		FormatManager<Number[]> manager =
				(FormatManager<Number[]>) factory.build("NUMBER", library);
		assertTrue(Arrays.equals(new Number[]{}, manager.convert(null)));
		assertTrue(Arrays.equals(new Number[]{}, manager.convert("")));
		assertTrue(Arrays.equals(ARR_1, manager.convert("1")));
		assertTrue(Arrays.equals(ARR_N3, manager.convert("-3")));
		assertTrue(Arrays.equals(ARR_N3_4_5, manager.convert("-3,4,5")));
		assertTrue(Arrays.equals(ARR_1P4, manager.convert("1.4")));
		assertTrue(Arrays.equals(ARR_N3_4P1_5, manager.convert("-3,4.1,5")));
	}

	public void testGetIdentifier()
	{
		FormatManager<?> manager = factory.build("NUMBER", library);
		assertEquals("ARRAY[NUMBER]", manager.getIdentifierType());
		manager = factory.build("STRING", library);
		assertEquals("ARRAY[STRING]", manager.getIdentifierType());
	}

	public void testManagedClass()
	{
		FormatManager<?> manager = factory.build("NUMBER", library);
		assertEquals(Number[].class, manager.getManagedClass());
		manager = factory.build("STRING", library);
		assertEquals(String[].class, manager.getManagedClass());
	}

	public void testGetComponent()
	{
		FormatManager<?> manager = factory.build("NUMBER", library);
		assertEquals(new NumberManager(), manager.getComponentManager());
		manager = factory.build("STRING", library);
		assertEquals(new StringManager(), manager.getComponentManager());
	}
}
