/**
 *  Copyright (C) 2013  Mitesh Pathak <miteshpathak05@gmail.com>
 *
 *  This file is part of JIA.
 *
 *  JIA is free software: you can redistribute it and/or modify it under the
 *  terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JIA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JIA.  If not, see <http://www.gnu.org/licenses/>.
 */

package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * The class {@code Graph} implements a graph.
 * The class assumes that vertices are numbered from [0 to N - 1]
 * for N vertices and weight of edge is {@code Integer} value.
 * 
 * @author Mitesh Pathak
 * 
 */
public final class Graph {
	private static enum Color {
		BLACK,	// Finished | Visited 
		GREY,	// Discover
		WHITE;	// Initial | Unvisited
	}
	
	private static class Vertex {
		private final int id;
		private Hashtable<Vertex, Integer> adjList; // <VertexID, Weight>
		
		boolean visited; // can be used instead of Color
		Color color;
		int distance;
		Vertex predVertex;
		
		private Vertex(int id) {
			this.id = id;
			this.adjList = new Hashtable<>();
			this.visited = false;
			this.color = Color.WHITE;
		}		
	}
	
	/*
	 * Graph variable 
	 */
	
	private final int numberOfVertices;
	private int numberOfEdges;
	
	private Vertex[] vertices;
	
	public Graph(int numberOfVertices) {
		if (numberOfVertices < 1) {
			throw new IllegalArgumentException();
		}
		this.numberOfVertices = numberOfVertices;
		this.numberOfEdges = 0;
		vertices = new Vertex[numberOfVertices];
		
		for (int i = 0; i < numberOfVertices; i++) {
			vertices[i] = new Vertex(i);
		}
	}

	/**
	 * Add new edge for undirected graph.
	 * 
	 * @param vertex1 index of source vertex
	 * @param vertex2 index of destination vertex
	 * @param weight the weight of the edge
	 */	
	public void addEdgeUndirected(int vertex1, int vertex2, int weight) {
		if (vertex1 < 0 || vertex2 < 0 || vertex1 >= numberOfVertices || vertex2 >= numberOfVertices) {
			throw new IllegalArgumentException();
		}
		numberOfEdges++;
		vertices[vertex1].adjList.put(vertices[vertex2], weight);
		vertices[vertex2].adjList.put(vertices[vertex1], weight);
	}
	
	/**
	 * Add new edge for directed graph.
	 * 
	 * @param srcVertex index of source vertex
	 * @param destVertex index of destination vertex
	 * @param weight the weight of the edge
	 */	
	public void addEdge(int srcVertex, int destVertex, int weight) {
		if (srcVertex < 0 || destVertex < 0 || srcVertex >= numberOfVertices || destVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}
		numberOfEdges++;
		vertices[srcVertex].adjList.put(vertices[destVertex], weight);
	}

	
	public int getNumberOfEdges() {
		return numberOfEdges;
	}
	
	public int getNumberOfVertices() {
		return numberOfVertices;
	}
	
	/**
	 * Implementations of Breadth First Search Algorithm
	 * from: Algorithms (CLRS).
	 * The {@code distance} variable stores smallest number of edges
	 * to reach from source to other vertices.
	 * 
	 * @param srcVertex index of source vertex
	 */
	public void bfs(int srcVertex) {
		if (srcVertex < 0 || srcVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}

		for (Vertex u : vertices) {
			u.color = Color.WHITE;
			u.distance = Integer.MAX_VALUE;
			u.predVertex = null;
		}
		
		Vertex s = vertices[srcVertex];
		Queue<Vertex> queue = new ArrayDeque<>();
		
		s.color = Color.GREY;
		s.distance = 0;
		s.predVertex = null;
		
		queue.add(s);
		
		while (!queue.isEmpty()) {
			Vertex u = queue.remove();
			Set<Vertex> adjVertices = u.adjList.keySet();
			for (Vertex v : adjVertices) {
				if (v.color == Color.WHITE) {
					v.color = Color.GREY;
					v.distance = u.distance + 1;
					v.predVertex = u;
					queue.add(v);
				}
			}
			u.color = Color.BLACK;
		}
	}

	/**
	 * Implementations of Depth First Search Algorithm.
	 * 
	 * @param srcVertex index of source vertex 
	 */
	public void dfs(int srcVertex) {
		if (srcVertex < 0 || srcVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}

		for (Vertex u : vertices) {
			u.color = Color.WHITE;
			u.distance = Integer.MAX_VALUE;
			u.predVertex = null;
		}
		
		Vertex s = vertices[srcVertex];
		Stack<Vertex> stack = new Stack<>();
		
		s.color = Color.GREY;
		s.distance = 0;
		s.predVertex = null;
		
		stack.push(s);
		
		while (!stack.isEmpty()) {
			Vertex u = stack.pop();
			Set<Vertex> adjVertices = u.adjList.keySet();
			for (Vertex v : adjVertices) {
				if (v.color == Color.WHITE) {
					v.color = Color.GREY;
					v.distance = u.distance + 1;
					v.predVertex = u;
					stack.push(v);
				}
			}
			u.color = Color.BLACK;
		}
	}
	
	/**
	 * Implementations of Dijkstra's Algorithm for shortest path
	 * from source to destination.
	 * The {@code distance} variable in vertex stores shortest distance
	 * from source. [SOURCE to ALL DEST - SHORTEST PATH]
	 * 
	 * @param srcVertex index of source vertex
	 * @param destVertex index of destination vertex
	 * @return int shortest distance from {@code srcVertex} to {@code destVertex}
	 */
	public int dijkstra(int srcVertex, int destVertex) {
		if (srcVertex < 0 || destVertex < 0 || srcVertex >= numberOfVertices || destVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}
		
		for (Vertex u : vertices) {
			u.visited = false;
			u.distance = Integer.MAX_VALUE;
			u.predVertex = null;
		}

		PriorityQueue<Vertex> pq = new PriorityQueue<>(1, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex a, Vertex b) {
				return b.distance - a.distance;	// min wins
			}
		});
		
		
		Vertex src = vertices[srcVertex];
		Vertex dest = vertices[destVertex];
		
		src.distance = 0;
		src.predVertex = null;
		
		pq.add(src);
		
		while (!pq.isEmpty()) {
			
			Vertex u = pq.remove();
			u.visited = true;
			
			if (u == dest) {
				break;
			}
			
			Set<Vertex> adjVertices = u.adjList.keySet();
			int ds = u.distance;
			
			for (Vertex v : adjVertices) {
				if ((!v.visited) && (v.distance > (ds + u.adjList.get(v)))) { // if visited == false
					v.distance = u.distance + ds;
					v.predVertex = u;
					pq.add(v);
				}
			}
		}
		return dest.distance; 
	}

	/**
	 * Implementations of Dijkstra's Algorithm for shortest path
	 * from source to destination. 
	 * The {@code distance} variable in vertex stores shortest distance
	 * from source. [SOURCE to ALL DEST - SHORTEST PATH]
	 * 
	 * @param srcVertex the source index of vertex
	 * @return List<Integer> contains shortest distance from {@code source} 
	 * to all other vertices.
	 */
	public List<Integer> dijkstra(int srcVertex) {
		if (srcVertex < 0 || srcVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}

		for (Vertex u : vertices) {
			u.visited = false;
			u.distance = Integer.MAX_VALUE;
			u.predVertex = null;
		}

		PriorityQueue<Vertex> pq = new PriorityQueue<>(1, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex a, Vertex b) {
				return b.distance - a.distance;	// min wins
			}
		});
		
		
		Vertex src = vertices[srcVertex];
		
		src.distance = 0;
		src.predVertex = null;
		
		pq.add(src);
		
		while (!pq.isEmpty()) {			
			Vertex u = pq.remove();
			u.visited = true;
			
			int ds = u.distance;
			int weight;
			
			Set<Vertex> adjVertices = u.adjList.keySet();
			
			for (Vertex v : adjVertices) {
				if ((!v.visited) && (v.distance > (ds + (weight = u.adjList.get(v))))) { // if visited == false
					v.distance = weight + ds;
					v.predVertex = u;
					pq.add(v);
				}
			}
		}

		List<Integer> distances = new ArrayList<Integer>();
		for (Vertex v : vertices) {
			distances.add(v.distance);
		}
		
		return distances; 
	}
	
	/**
	 * Checks whether vertex {@code destVertex} is reachable from {@code srcVertex}.
	 * Returns {@code distance} (i.e minimum number of edges) if vertex {@code destVertex} is 
	 * reachable from {@code srcVertex} else returns -1.
	 * 
	 * @param srcVertex index of source vertex
	 * @param destVertex index of destination vertex
	 * @return int minimum number of edges or -1
	 */
	public int isReachable(int srcVertex, int destVertex) {
		if (srcVertex < 0 || destVertex < 0 || srcVertex >= numberOfVertices || destVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}

		dijkstra(srcVertex, destVertex);
		
		if (vertices[destVertex].distance == Integer.MAX_VALUE) {
			return -1;
		}
		
		return vertices[destVertex].distance;
	}
	
	/**
	 * Return path from {@code srcVertex} to {@code destvertex} if exist,
	 * else returns "No path exist".
	 * 
	 * @param srcVertex index of source vertex
	 * @param destVertex index of destination vertex
	 * @return String the path 
	 */
	public String displayPath(int srcVertex, int destVertex) {
		if (srcVertex < 0 || destVertex < 0 || srcVertex >= numberOfVertices || destVertex >= numberOfVertices) {
			throw new IllegalArgumentException();
		}

		if (isReachable(srcVertex, destVertex) == -1) {
			return "No path exist";
		}
		StringBuilder op = new StringBuilder();
		
		Vertex curr = vertices[destVertex];
		
		while (curr != null) {
			op.append(curr.id + " >-- ");
			curr = curr.predVertex;
		}
		
		return op.reverse().toString();
	}
	
	@Override
	public String toString() {
		StringBuilder op = new StringBuilder();
		
		for (Vertex v : vertices) {
			op.append(v.id + "\t:\t");
			Set<Vertex> adjVertices = v.adjList.keySet(); 
			for (Vertex adjVertex : adjVertices) {
				op.append(adjVertex.id + "(" + v.adjList.get(adjVertex) + ")\t");
			}
			op.append("\n");
		}
		return op.toString();
	}
	
	public static void main(String[] args) {
		Graph g = new Graph(6);
		g.addEdgeUndirected(0, 1, 1);
		g.addEdgeUndirected(1, 2, 1);
		g.addEdgeUndirected(2, 3, 1);
		g.addEdgeUndirected(3, 0, 1);
		g.addEdgeUndirected(1, 3, 1);
		g.addEdgeUndirected(2, 4, 1);
		g.addEdgeUndirected(4, 5, 1);
		g.addEdgeUndirected(5, 5, 1);
		System.out.println(g.displayPath(0, 5));
		System.out.println(g.dijkstra(0));
		System.out.print(g);
	}

}