/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.Tuple;

/**
 * A StagingProxy is a Staging object. The InvocationHandler portion is provided methods
 * and arguments that are calls to the interface which is processed by this StagingProxy.
 * 
 * When this StagingProxy is applied to an object, the calls previously recorded by the
 * InvocationHandler are then repeated on the provided target.
 * 
 * In general, this is convenient for staging the initial processing (to ensure something
 * is valid) from the final application of the result of the processing.
 * 
 * Note that it is assumed that ALL methods on the "write" interface return void
 * (Technically it is slightly more lenient: write methods must be capable of being
 * declared a success if they return a null value, but this extra leniency is likely to be
 * useful only in rare cases). This is a primary requirement for the StagingProxy to be
 * able to stage information (StagingProxy cannot guess at what an appropriate answer
 * would be to an arbitrary write if feedback was required).
 * 
 * @param <R>
 *            The Interface (Class) of Object which is the read interface processed by
 *            this StagingProxy.
 * @param <W>
 *            The Interface (Class) of Object which is the write interface processed by
 *            this StagingProxy.
 */
class StagingProxy<R, W> implements InvocationHandler, Staging<W>
{
	/**
	 * The list of PropertyProcessor objects that this StagingProxy will consider when
	 * interpreting the given interfaces.
	 */
	private List<PropertyProcessor> processors;

	/**
	 * This is the read interface (interface with "get" methods) served by this
	 * StagingProxy.
	 */
	private final Class<R> readInterface;

	/**
	 * This is the write interface (interface with "set"/"add"/"put" methods) served by
	 * this StagingProxy.
	 */
	private final Class<W> writeInterface;

	/**
	 * This is the map from the (read) method name to the processors for the methods on
	 * the read interface of this StagingProxy.
	 */
	private Map<String, PropertyProcessor> getProcessors = new HashMap<>();

	/**
	 * This is the set of "set"/"add"/"put" methods served by this StagingProxy.
	 */
	private Set<String> setMethods = new HashSet<>();

	/**
	 * This is the List of method calls to the write interface that this StagingProxy has
	 * captured (while serving as an InvocationHandler).
	 */
	private List<Tuple<Method, Object[]>> stagedMethodCalls;

	/**
	 * Constructs a new StagingProxy for the given List of PropertyProcessor objects and
	 * the given read and write interfaces.
	 * 
	 * @param processorList
	 *            The list of PropertyProcessor objects that this StagingProxy will
	 *            consider when interpreting the given interfaces
	 * @param readInterface
	 *            The read interface (interface with "get" methods) served by this
	 *            StagingProxy.
	 * @param writeInterface
	 *            The write interface (interface with "set"/"add"/"put" methods) served by
	 *            this StagingProxy.
	 */
	StagingProxy(List<PropertyProcessor> processorList, Class<R> readInterface,
		Class<W> writeInterface)
	{
		if (!readInterface.isInterface())
		{
			throw new IllegalArgumentException(
				"ContextFactory must be provided an Interface Class, was given: "
					+ writeInterface.getCanonicalName());
		}
		if (!writeInterface.isInterface())
		{
			throw new IllegalArgumentException(
				"ContextFactory must be provided an Interface Class, was given: "
					+ writeInterface.getCanonicalName());
		}
		processors = new ArrayList<>(processorList);
		this.readInterface = Objects.requireNonNull(readInterface);
		this.writeInterface = Objects.requireNonNull(writeInterface);
		evaluateMethods();
	}

	private void evaluateMethods()
	{
		Method[] writeMethods = writeInterface.getMethods();
		if (writeMethods.length == 0)
		{
			throw new IllegalArgumentException(
				writeInterface.getSimpleName() + " had no methods");
		}
		Method[] readMethods = readInterface.getMethods();
		if (readMethods.length == 0)
		{
			throw new IllegalArgumentException(
				readInterface.getSimpleName() + " had no methods");
		}
		Set<Object> propertyNames = Collections.newSetFromMap(new CaseInsensitiveMap<>());
		Set<Object> writeMethodNames =
				Collections.newSetFromMap(new CaseInsensitiveMap<>());
		List<Method> readMethodList = new ArrayList<>(Arrays.asList(readMethods));
		METHODS: for (Method method : writeMethods)
		{
			String name = method.getName();
			if (!writeMethodNames.add(name))
			{
				throw new IllegalArgumentException(
					"Duplicate Write Method Name: " + name);
			}
			for (PropertyProcessor processor : processors)
			{
				if (processor.isProcessedMethod(method))
				{
					String property = processor.getPropertyName(name);
					if (!propertyNames.add(property))
					{
						throw new IllegalArgumentException(
							"Duplicate Property Name: " + property);
					}
					setMethods.add(name);
					Method claimed = processor.claimMethod(method, readMethods);
					readMethodList.remove(claimed);
					getProcessors.put(claimed.getName(), processor);
					continue METHODS;
				}
			}
			throw new IllegalArgumentException(
				"Unsure how to process Method Name: " + name);
		}
		if (!readMethodList.isEmpty())
		{
			throw new IllegalArgumentException("Had Leftover Methods: " + readMethodList);
		}
	}

	@Override
	public Class<W> getInterface()
	{
		return writeInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		String methodName = method.getName();
		if (setMethods.contains(methodName))
		{
			addMethodCall(method, args);
			//TODO Validate method's return type or pass to Processor??
			return null;
		}
		PropertyProcessor processor = getProcessors.get(methodName);
		ReadableHandler handler = processor.getInvocationHandler(methodName, args);
		@SuppressWarnings("unchecked")
		W extractor = (W) java.lang.reflect.Proxy.newProxyInstance(
			writeInterface.getClassLoader(), new Class[]{writeInterface}, handler);
		if (stagedMethodCalls != null)
		{
			for (Tuple<Method, Object[]> info : stagedMethodCalls)
			{
				info.getFirst().invoke(extractor, info.getSecond());
			}
		}
		return handler.getResult();
	}

	private void addMethodCall(Method method, Object[] args)
	{
		if (stagedMethodCalls == null)
		{
			stagedMethodCalls = new ArrayList<>();
		}
		stagedMethodCalls.add(new Tuple<>(method, args));
	}

	@Override
	public void applyTo(W target)
	{
		if (!writeInterface.isAssignableFrom(target.getClass()))
		{
			throw new IllegalArgumentException(
				"This StagingProxy serves interface " + writeInterface.getCanonicalName()
					+ " but the provided target did not implement that interface");
		}
		if (stagedMethodCalls == null)
		{
			return;
		}
		for (Tuple<Method, Object[]> methodInfo : stagedMethodCalls)
		{
			try
			{
				methodInfo.getFirst().invoke(target, methodInfo.getSecond());
			}
			catch (ReflectiveOperationException e)
			{
				throw new IllegalArgumentException(
					"StagingProxy failure: ReflectiveOperationException: ", e);
			}
		}
	}
}