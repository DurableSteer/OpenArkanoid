package openArcanoid;

import java.util.ArrayList;


public class BlockYTree extends BlockXTree {

	protected class YNode {
		private Block block;
		private double max;
		private double low;
		private double high;
		private YNode left = null;
		private YNode right = null;

		public YNode(double low,double high, Block block) {
			this.low =low;
			this.high = high;
			this.max = high;
			left = null;
			right = null;
			this.block = block;
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
		public YNode getLeft() {
			return left;
		}
		public void setLeft(YNode newLeft) {
			left = newLeft;
		}
		public YNode getRight() {
			return right;
		}
		public void setRight(YNode newRight) {
			right = newRight;
		}
		public double[] getInterval() {
			return new double[] {low,high};
		}
		public double getHigh() {
			return high;
		}
		public Block getBlock() {
			return block;
		}
		@Override
		public String toString() {
			return "Y:["+low+","+high+"]:"+max;
		}
	}

	private YNode root;


private void insert(YNode curr, Block newBlock, double low, double high) {
		if(curr.getLow() <= low) {
			if(curr.getRight() != null)
				insert(curr.getRight(),newBlock,low,high);
			else
				curr.setRight(new YNode(low,high,newBlock));
		}
		else if(curr.getLow() > low) {
			if(curr.getLeft() != null)
				insert(curr.getLeft(),newBlock,low,high);
			else
				curr.setLeft(new YNode(low,high,newBlock));
		}

		if (curr.getMax() < high)
			curr.setMax(high);
	}

	@Override
	public void insert(Block newBlock) {
		double low = newBlock.getPosition().getY();
		double high = newBlock.getSize().getY() + low;
		if(root == null)
			root = new YNode(low, high, newBlock);
		else
			insert(root, newBlock, low, high);
	}
	@Override
	public void delete(Block deadBlock) {
		double low = deadBlock.getPosition().getY();
		double high = deadBlock.getSize().getY() + low;

		root = delete(root, deadBlock, low, high);
	}
	private YNode delete(YNode curr,Block deadBlock, double low, double high) {
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
			if(curr.getLeft() == null && curr.getRight() == null) //curr has no children
				return null;
			else if(curr.getRight() != null && curr.getLeft() != null) //curr has both children
				if(curr.getRight().getLeft() == null){//right child is already the successor
					curr.getRight().setLeft(curr.getLeft());
					return curr.getRight();
				}
				else { //the successor is further down in the tree
					YNode successor = null;
					YNode tmp = curr.getRight();
					while(tmp.getLeft().getLeft() != null)
						tmp = tmp.getLeft();
					tmp.getLeft().setLeft(curr.getLeft());
					if(tmp.getLeft().getRight() != null) { //the successor has a right child
						YNode rightChild = tmp.getLeft().getRight();
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
		return curr;
	}
	@Override
	public void print() {
		//creates an easily visualizable representation of the tree in the syntax of https://treefun.appspot.com/
			if(root != null)
				print(root,"");
			else
				System.out.println("empty");
		}
	private void print(YNode curr, String prefix) {
		System.out.println(prefix+curr.toString());
		if(curr.getLeft() != null)
			print(curr.getLeft(), " "+prefix);
		else
			System.out.println(prefix+" Y:[empty]");
		if(curr.getRight() != null)
			print(curr.getRight(), " "+prefix);
		else
			System.out.println(prefix+" Y:[empty]");
	}
	private double fixMax(YNode curr) {

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
	@Override
	public ArrayList<Block> findColliding(Sprite sprite){
		double low = sprite.getPosition().getY();
		double high = sprite.getSize().getY() + low;
		return findColliding(root,sprite,low,high);
	}
	private ArrayList<Block> findColliding(YNode curr, Sprite sprite, double low, double high) {
		ArrayList<Block> result = new ArrayList<>();

		if(curr == null)
			return result;
		if(!((curr.getLow() > high) || (curr.getHigh() < low)))
			result.add(curr.getBlock());
		if((curr.getLeft()!= null) && (curr.getLeft().getMax() >= low))
			result.addAll(findColliding(curr.getLeft(),sprite,low,high));
		result.addAll(findColliding(curr.getRight(), sprite,low,high));
		return result;
	}

	private ArrayList<Block> getAllBlocks(YNode curr){
		if(curr == null)
			return new ArrayList<>();
		ArrayList<Block> result = new ArrayList<>();
		result.add(curr.getBlock());
		result.addAll(getAllBlocks(curr.getLeft()));
		result.addAll(getAllBlocks(curr.getRight()));
		return result;
	}
	@Override
	public ArrayList<Block> getAllBlocks(){
		return getAllBlocks(root);
	}
	@Override
	public boolean isEmpty() {
		return root == null;
	}
}
