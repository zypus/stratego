package com.theBombSquad.stratego.player.ai.players.planner.aStar;

import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;

import lombok.Data;
 
public class AstarSearchAlgo {
	
	private Node[][] nodes;
	
	private int targetX = -1;
	private int targetY = -1;
	
	public AstarSearchAlgo(GameBoard board, int targetX, int targetY){
		this.targetX = targetX;
		this.targetY = targetY;
		nodes = new Node[board.getHeight()][board.getWidth()];
		for(int cy=0; cy<nodes.length; cy++){
			for(int cx=0; cx<nodes[cy].length; cx++){
				Node node = new Node(cx+"|"+cy, 1, cx, cy, !board.getUnit(cx, cy).isAir());
				nodes[cy][cx] = node;
			}
		}
		for(int cy=0; cy<nodes.length; cy++){
			for(int cx=0; cx<nodes[cy].length; cx++){
				Node node = nodes[cy][cx];
				ArrayList<Edge> edges = new ArrayList<Edge>();
				for(int[] dir : new int[][]{new int[]{cx, cy-1}, new int[]{cx+1, cy}, new int[]{cx, cy+1}, new int[]{cx-1, cy}}){
					int dirX = dir[0];
					int dirY = dir[1];
					if(board.isInBounds(dirX, dirY)){
						if(board.getUnit(dirX, dirY).isAir()){
							edges.add(new Edge(nodes[dirY][dirX]));
						}
					}
				}
				Edge[] es = new Edge[edges.size()];
				for(int c=0; c<es.length; c++){
					es[c] = edges.get(c);
				}
				node.adjacencies = es;
			}
		}
	}
	
	public List<Node> printPath(Node target) {
		List<Node> path = new ArrayList<Node>();
		for (Node node = target; node != null; node = node.parent) {
			path.add(node);
		}

		Collections.reverse(path);

		return path;
	}
	
	public List<Node> astar(int targetX, int targetY, int sourceX, int sourceY){
		astarSearch(nodes[sourceY][sourceX], nodes[targetY][targetX]);
		List<Node> path = printPath(nodes[targetY][targetX]);
		return path;
	}
	
	public List<Node> astar(int sourceX, int sourceY){
		astarSearch(nodes[sourceY][sourceX], nodes[targetY][targetX]);
		List<Node> path = printPath(nodes[targetY][targetX]);
		return path;
	}
	
	
	
	public void astarSearch(Node source, Node goal) {

		Set<Node> explored = new HashSet<Node>();

		PriorityQueue<Node> queue = new PriorityQueue<Node>(20,
				new Comparator<Node>() {
					// override compare method
					public int compare(Node i, Node j) {
						if (i.f_scores > j.f_scores) {
							return 1;
						}

						else if (i.f_scores < j.f_scores) {
							return -1;
						}

						else {
							return 0;
						}
					}

				});

		// cost from start
		source.g_scores = 0;

		queue.add(source);

		boolean found = false;

		while ((!queue.isEmpty()) && (!found)) {

			// the node in having the lowest f_score value
			Node current = queue.poll();

			explored.add(current);

			// goal found
			if (current.getX() == goal.getX() && current.getY()==goal.getY()) {
				found = true;
			}

			// check every child of current node
			for (Edge e : current.adjacencies) {
				Node child = e.target;
				double cost = e.cost;
				double temp_g_scores = current.g_scores + cost;
				double temp_f_scores = temp_g_scores + child.h_scores;

				/*
				 * if child node has been evaluated and the newer f_score is
				 * higher, skip
				 */
				if ((explored.contains(child))
						&& (temp_f_scores >= child.f_scores)) {
					continue;
				}

				/*
				 * else if child node is not in queue or newer f_score is lower
				 */
				else if ((!queue.contains(child))
						|| (temp_f_scores < child.f_scores)) {

					child.parent = current;
					child.g_scores = temp_g_scores;
					child.f_scores = temp_f_scores;

					if (queue.contains(child)) {
						queue.remove(child);
					}

					queue.add(child);

				}

			}

		}

	}
	
	public class Node {

		public final String value;
		public double g_scores;
		public final double h_scores;
		public double f_scores = 0;
		public Edge[] adjacencies;
		public Node parent;
		
		private int x;
		private int y;
		
		private boolean collision;
		
		public boolean isCollision(){
			return collision;
		}
		
		public int getX(){
			return x;
		}
		
		public int getY(){
			return y;
		}

		public Node(String val, double hVal, int x, int y, boolean collision) {
			value = val;
			h_scores = hVal;
			this.x = x;
			this.y = y;
			this.collision = collision;
		}

		public String toString() {
			return value;
		}

	}
	
	public class Edge {
		public final double cost;
		public final Node target;
		
		public Edge(Node targetNode){
			target = targetNode;
			cost = 1;
		}
		
		public Edge(Node targetNode, double costVal) {
			target = targetNode;
			cost = costVal;
		}
	}
	
}
