package openArcanoid;

import java.util.ArrayList;

public class BlockXTree {
// Implementation of an interval-interval-tree for x and y coordinates to store the currently
// existing blocks for efficient collision detection.

	private class XNode {
		private BlockYTree yTree;
		private double max;
		private double low;
		private double high;
		private XNode left = null;
		private XNode right = null;

		public XNode(double low,double high, Block block) {
			this.low =low;
			this.high = high;
			this.max = high;
			left = null;
			right = null;
			this.yTree = new BlockYTree();
			this.yTree.insert(block);
		}

		public double getMax() {
			return max;
		}
		public void setMax(double newMax) {
			max = newMax;
		}
		public double getLow() {
			return low;
		}
		public XNode getLeft() {
			return left;
		}
		public void setLeft(XNode newLeft) {
			left = newLeft;
		}
		public XNode getRight() {
			return right;
		}
		public void setRight(XNode newRight) {
			right = newRight;
		}
		public double getHigh() {
			return high;
		}
		public BlockYTree getYTree() {
			return yTree;
		}
		@Override
		public String toString() {
			return "X:["+low+","+high+"]:"+max;
		}
	}

	private XNode root = null;

	private void insert(XNode curr, Block newBlock, double low, double high) {

		if((curr.getLow() <= low) && (curr.getHigh() != high)) {
			if(curr.getRight() != null)
				insert(curr.getRight(),newBlock,low,high);
			else
				curr.setRight(new XNode(low,high,newBlock));
		}
		else if(curr.getLow() > low) {
			if(curr.getLeft() != null)
				insert(curr.getLeft(),newBlock,low,high);
			else
				curr.setLeft(new XNode(low,high,newBlock));
		}
		else //the Block has the same interval as the current node
			curr.getYTree().insert(newBlock);

		if (curr.getMax() < high)
			curr.setMax(high);
	}

	public void insert(Block newBlock) {
		double low = newBlock.getPosition().getX();
		double high = newBlock.getSize().getX() + low;
		if(root == null)
			root = new XNode(low, high, newBlock);
		else
			insert(root, newBlock, low, high);
	}
	public void delete(Block deadBlock) {
		double low = deadBlock.getPosition().getX();
		double high = deadBlock.getSize().getX() + low;

		root = delete(root, deadBlock, low, high);
	}
	private XNode delete(XNode curr,Block deadBlock, double low, double high) {
		if(curr == null)
			return null;
		else if((curr.getLow() <= low) && (curr.getHigh() != high)) {
			curr.setRight(delete(curr.getRight(), deadBlock, low, high));
			fixMax(curr);
			}
		else if(curr.getLow() > low) {
			curr.setLeft(delete(curr.getLeft(),deadBlock,low,high));
			fixMax(curr);
		}
		else if(curr.getHigh() == high){ //found a match
			curr.getYTree().delete(deadBlock);
			if(curr.getYTree().isEmpty()) { //curr needs to be deleted
				if(curr.getLeft() == null && curr.getRight() == null) //curr has no children
					return null;
				else if(curr.getRight() != null && curr.getLeft() != null) //curr has both children
					if(curr.getRight().getLeft() == null){//right child is already the successor
						curr.getRight().setLeft(curr.getLeft());
						return curr.getRight();
					}
					else { //the successor is further down in the tree
						XNode successor = null;
						XNode tmp = curr.getRight();
						while(tmp.getLeft().getLeft() != null)
							tmp = tmp.getLeft();
						tmp.getLeft().setLeft(curr.getLeft());
						if(tmp.getLeft().getRight() != null) { //the successor has a right child
							XNode rightChild = tmp.getLeft().getRight();
							tmp.getLeft().setRight(curr.getRight());
							successor = tmp.getLeft();
							tmp.setLeft(rightChild);

						}
						else { //the successor has no children
							tmp.getLeft().setLeft(curr.getLeft());
							tmp.getLeft().setRight(curr.getRight());
							successor = tmp.getLeft();
							tmp.setLeft(null);
						}
						fixMax(successor);
						return successor;
					}
				else if(curr.getLeft() != null) //curr only has a left child
					return curr.getLeft();
				else // curr only has a right child
					return curr.getRight();
			}
		}
		return curr;
	}
	private double fixMax(XNode curr) {
		if(curr == null)
			return 0;
		double lmax = 0;
		double rmax = 0;
		lmax = fixMax(curr.getLeft());
		rmax = fixMax(curr.getRight());
		double max = (lmax > rmax ? lmax : rmax);
		curr.setMax(curr.getHigh() > max ? curr.getHigh() : max);
		return curr.getMax();
	}
	public void print() {
	//creates an easily visualizable representation of the tree in the syntax of https://treefun.appspot.com/
		if(root != null)
			print(root,"");
		else
			System.out.println("empty");
	}
	private void print(XNode curr, String prefix) {
		System.out.println(prefix+curr.toString());
		if(curr.getLeft() != null)
			print(curr.getLeft(), " "+prefix);
		else
			System.out.println(prefix+" X:[empty]");
		if(curr.getRight() != null)
			print(curr.getRight(), " "+prefix);
		else
			System.out.println(prefix+" X:[empty]");
	}

	public ArrayList<Block> findColliding(Sprite sprite) {
		double low = sprite.getPosition().getX();
		double high = sprite.getSize().getX() + low;
		return findColliding(root,sprite, low, high);
	}
	private ArrayList<Block> findColliding(XNode curr, Sprite sprite, double low, double high) {
		ArrayList<Block> result = new ArrayList<>();

		if(curr == null)
			return result;
		if(!((curr.getLow() > high) || (curr.getHigh() < low)))
			result.addAll(curr.getYTree().findColliding(sprite));
		if((curr.getLeft()!= null) && (curr.getLeft().getMax() >= low))
			result.addAll(findColliding(curr.getLeft(),sprite,low,high));

		result.addAll(findColliding(curr.getRight(), sprite,low,high));

		return result;
	}
	public boolean isEmpty() {
		return root == null;
	}

	private ArrayList<Block> getAllBlocks(XNode curr){
		if(curr == null)
			return new ArrayList<>();
		ArrayList<Block> result = new ArrayList<>();
		result.addAll(curr.getYTree().getAllBlocks());
		result.addAll(getAllBlocks(curr.getLeft()));
		result.addAll(getAllBlocks(curr.getRight()));
		return result;
	}

	public ArrayList<Block> getAllBlocks() {
		return getAllBlocks(root);
	}
}
