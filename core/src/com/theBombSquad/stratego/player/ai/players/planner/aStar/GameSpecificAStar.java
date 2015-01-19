package com.theBombSquad.stratego.player.ai.players.planner.aStar;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.theBombSquad.stratego.gameMechanics.board.GameBoard;

@Data
public class GameSpecificAStar {
	
	private Node[][] map;
	private int targetX;
	private int targetY;
	
	public GameSpecificAStar(GameBoard board, int targetX, int targetY){
		this.targetX = targetX;
		this.targetY = targetY;
		this.map = new Node[board.getHeight()][board.getWidth()];
		for(int cy=0; cy<map.length; cy++){
			for(int cx=0; cx<map[0].length; cx++){
				boolean collision = !board.getUnit(cx, cy).isAir();
				this.map[cy][cx] = new Node(cx, cy, collision);
			}
		}
		Node source = map[targetY][targetX];
		source.setParent(source, 0);
		setChild(source);
	}
	
	private void setChild(Node root){
		int cx = root.x;
		int cy = root.y;
		for(int[] dir : new int[][]{new int[]{cx, cy-1}, new int[]{cx+1, cy}, new int[]{cx, cy+1}, new int[]{cx-1, cy}}){
			int dirX = dir[0];
			int dirY = dir[1];
			if(dirX>=0 && dirX<map[0].length && dirY>=0 && dirY<map.length){
				Node n = map[dirY][dirX];
				if(!n.hasParent() || (n.hasParent() && n.getDebth()>root.getDebth()+1) && !n.isCollision()){
					n.setParent(root, root.getDebth()+1);
					setChild(n);
				}
			}
		}
	}
	
	
	public List<Node> findPath(int fromX, int fromY){
		if(fromX>=0 && fromX<map[0].length && fromY>=0 && fromY<map.length){
			Node source = map[fromY][fromX];
			if(source.hasParent()){
				List<Node> nodeList = new ArrayList<Node>();
				while(source.hasParent() && source.getDebth()>0){
					nodeList.add(source.getParent());
					source = source.getParent();
				}
				return nodeList;
			}
		}
		return new ArrayList<Node>();
	}
	
	
	@Data
	public class Node{
		
		private boolean collision;
		private int x;
		private int y;
		
		private int debth;
		private Node parent;
		
		public Node(int x, int y, boolean collision){
			this.x = x;
			this.y = y;
			this.collision = collision;
		}
		
		public void setParent(Node parent, int debth){
			this.parent = parent;
			this.debth = debth;
		}
		
		public boolean hasParent(){
			return parent != null;
		}
		
	}
	
}
