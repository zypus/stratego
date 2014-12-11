package com.theBombSquad.stratego.player.ai.players.planner.aStar;

import java.util.ArrayList;
import java.util.Collections;
import lombok.Data;
@Data
public class AStar {
	
	private ArrayList<Node> openList;
	
	private boolean[][] checkedNode;
	
	private boolean[][] collisionMap;
	
	private Node[][] nodeMap;
	
	private int destX;
	private int destY;
	
	public AStar(boolean[][] collisionMap, int destX, int destY){
		this.openList = new ArrayList<Node>();
		this.collisionMap = collisionMap;
		
		this.checkedNode = new boolean[collisionMap.length][collisionMap[0].length];
		
		this.nodeMap = new Node[collisionMap.length][collisionMap[0].length];
		for(int cy=0; cy<nodeMap.length; cy++){
			for(int cx=0; cx<nodeMap[cy].length; cx++){
				this.nodeMap[cy][cx] = new Node(cx, cy);
			}
		}
		
		this.destX = destX;
		this.destY = destY;
	}
	
	public ArrayList<Node> getPath(int originX, int originY){
		openList.add(nodeMap[originY][originX]);
		Node dest = null;
		
		while(dest==null){
			if(openList.size()==0){
				//No Way To Get To Target
				return new ArrayList<Node>();
			}
			Node n = openList.get(0);
			checkedNode[n.getY()][n.getX()] = true;
			openList.remove(0);
			
			ArrayList<Node> adjacent = getAdjacent(n);
			
			for(int c=0; c<adjacent.size(); c++){
				Node a = adjacent.get(c);
				if(!a.isChecked()){
					if(a.getParent()==null || a.getComplete()>n.getComplete()+1){
						a.setParent(n);
					}
					openList.add(a);
					if(a.getManhattan()==0){
						dest = a;
						break;
					}
				}
			}
			
			Collections.sort(openList);
			
		}
		
		ArrayList<Node> path = new ArrayList<Node>();
		Node pointer = dest;
		while(pointer.getParent()!=null){
			path.add(0, pointer);
			pointer = pointer.getParent();
		}
		
		return path;
	}
	
	private ArrayList<Node> getAdjacent(Node node){
		ArrayList<Node> nodes = new ArrayList<Node>();
		if(node.getX()>0){
			if(!collisionMap[node.getY()][node.getX()-1] || nodeMap[node.getY()][node.getX()-1].getManhattan()==0){
				nodes.add(nodeMap[node.getY()][node.getX()-1]);
			}
		}
		if(node.getY()>0){
			if(!collisionMap[node.getY()-1][node.getX()] || nodeMap[node.getY()-1][node.getX()].getManhattan()==0){
				nodes.add(nodeMap[node.getY()-1][node.getX()]);
			}
		}
		if(node.getX()<checkedNode[0].length-1){
			if(!collisionMap[node.getY()][node.getX()+1] || nodeMap[node.getY()][node.getX()+1].getManhattan()==0){
				nodes.add(nodeMap[node.getY()][node.getX()+1]);
			}
		}
		if(node.getY()<checkedNode.length-1){
			if(!collisionMap[node.getY()+1][node.getX()] || nodeMap[node.getY()+1][node.getX()].getManhattan()==0){
				nodes.add(nodeMap[node.getY()+1][node.getX()]);
			}
		}
		return nodes;
	}
	
	@Data
	public class Node implements Comparable<Node>{
		private int x;
		private int y;
		private Node parent;
		
		public Node(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		public int getManhattan(){
			return Math.abs(x-destX)+Math.abs(y-destY);
		}
		
		public int getComplete(){
			if(parent==null)return 0;
			else return 1 + parent.getComplete();
		}
		
		public int getManhattanComplete(){
			return getComplete() + getManhattan();
		}
		
		public boolean isChecked(){
			return getCheckedNode()[getY()][getX()];
		}

		public int compareTo(Node otherNode) {
			return getManhattanComplete()-otherNode.getManhattanComplete();
		}
		
	}

}
