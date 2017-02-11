/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
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
package pcgen.base.lang;

import junit.framework.TestCase;
import pcgen.testsupport.TestSupport;

public class ObjectUtilTest extends TestCase
{

	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(ObjectUtil.class);
	}

	public void testNulls()
	{
		assertTrue(ObjectUtil.compareWithNull(null, null));
	}

	public void testNullObject()
	{
		assertFalse(ObjectUtil.compareWithNull(null, new Object()));
	}

	public void testObjectNull()
	{
		assertFalse(ObjectUtil.compareWithNull(new Object(), null));
	}

	public void testDifferent()
	{
		assertFalse(ObjectUtil.compareWithNull(new Object(), new Object()));
	}

	public void testSameInstance()
	{
		Object obj = new Object();
		assertTrue(ObjectUtil.compareWithNull(obj, obj));
	}

	public void testEqualsMethod()
	{
		assertTrue(ObjectUtil.compareWithNull(Integer.valueOf(1), Integer.valueOf(1)));
	}

}
