/*
 * Copyright (c) Thomas Parker, 2005-2007.
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
 * 
 * Created on May 18, 2005
 */
package pcgen.base.graph.inst;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pcgen.base.graph.base.DirectionalEdge;
import pcgen.base.graph.base.DirectionalGraph;

/**
 * A DirectionalSetMapGraph is a DirectionalGraph. Thus, it requires that all
 * Edges added to the Graph are DirectionalEdges. Failure to abide by this
 * restriction will result in the addition of the edge failing.
 * 
 * This Graph uses normal equality (.equals()) to determine equality for
 * purposes of checking whether nodes and edges are already part of the Graph.
 * 
 * This Graph uses redundant storage to improve query speed for certain methods.
 * In addition to simple lists of the nodes and edges present in a Graph, a Map
 * from each node to the adjacent edges is maintained.
 * 
 * This class provides a more balanced query speed for querying adjacent graph
 * elements. Specifically, an edge knows to which nodes it is connected. To
 * determine which edges a node is connected to requires a query to the Graph.
 * The Map maintained by this class prevents an iteration over the entire List
 * of edges whenever getAdjacentEdgeList(GraphNode n) is called.
 * 
 * WARNING: This DirectionalSetMapGraph contains a CACHE which uses the Nodes as
 * a KEY. Due to the functioning of a Map (it uses the .hashCode() method), if a
 * Node is modified IN PLACE in the Graph (without being removed and readded),
 * it WILL cause the caching to FAIL, because the cache will have indexed the
 * Node by the old hash code. It is therefore HIGHLY advised that this Graph
 * implementation ONLY be used where the Nodes are either Immutable or do not
 * override Object.equals().
 * 
 * Note: It is NOT possible for an Edge to connect to a Node which is not in the
 * graph. There are (at least) two side effects to this limit: (1) If an Edge is
 * added when the Nodes to which it is not connected are not in the Graph, those
 * Nodes will be implicitly added to the graph. (2) If a Node is removed from
 * the Graph, all of the Edges connected to that Node will also be removed from
 * the graph.
 * 
 * WARNING: This Graph has SIDE EFFECTS. When any Node is deleted from the
 * graph, ANY and ALL DirectionalEdges connected to that Node are implicitly
 * deleted from the graph. You CANNOT rely on the GraphNodeRemoved event, as it
 * will occur AFTER all of the attached Edges have been removed. You must check
 * for and clean up adjacent Edges BEFORE removing any Node if you wish for
 * those Edges to remain (in a modified form, of course) in the
 * DirectionalSetMapGraph.
 * 
 * @param <N>
 *            The type of Node stored in this Graph
 * @param <ET>
 *            The type of Edge stored in this Graph
 */
public class DirectionalSetMapGraph<N, ET extends DirectionalEdge<N>> extends
		AbstractSetMapGraph<N, ET> implements DirectionalGraph<N, ET>
{
	/**
	 * Returns a List of the Edges for which the given Node is a Sink.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by DirectionalSetMapGraph.
	 * However, the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the DirectionalSetMapGraph.
	 */
	@Override
	public List<ET> getInwardEdgeList(N node)
	{
		Set<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return null;
		}
		List<ET> inwardEdgeList = adjacentEdgeList.stream()
		                                          .filter(edge ->
				                                          (edge.getNodeInterfaceType(node) & DirectionalEdge.SINK)
						                                          != 0)
		                                          .collect(Collectors.toCollection(LinkedList::new));
		return inwardEdgeList;
	}

	/**
	 * Returns a List of the Edges for which the given Node is a source.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by DirectionalSetMapGraph.
	 * However, the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the DirectionalSetMapGraph.
	 */
	@Override
	public List<ET> getOutwardEdgeList(N node)
	{
		Set<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return null;
		}
		List<ET> outwardEdgeList = adjacentEdgeList.stream()
		                                           .filter(edge ->
				                                           (edge.getNodeInterfaceType(node) & DirectionalEdge.SOURCE)
						                                           != 0)
		                                           .collect(Collectors.toList());
		return outwardEdgeList;
	}

	/**
	 * Returns true if the given Node is connected to any edges as a sink Node.
	 */
	@Override
	public boolean hasInwardEdge(N node)
	{
		Set<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return false;
		}
		return adjacentEdgeList.stream()
		                       .anyMatch(edge -> (edge.getNodeInterfaceType(node) & DirectionalEdge.SINK) != 0);
	}

	/**
	 * Returns true if the given Node is connected to any edges as a source
	 * Node.
	 */
	@Override
	public boolean hasOutwardEdge(N node)
	{
		Set<ET> adjacentEdgeList = getAdjacentEdges(node);
		if (adjacentEdgeList == null)
		{
			return false;
		}
		return adjacentEdgeList.stream()
		                       .anyMatch(edge -> (edge.getNodeInterfaceType(node) & DirectionalEdge.SOURCE) != 0);
	}

}
