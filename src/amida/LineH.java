package amida;

public class LineH {
	public final Joint leftJoint;
	public final Joint rightJoint;
	public LineH(Joint left, Joint right) {
		leftJoint = left;
		rightJoint = right;
		leftJoint.setAssignmentH(this);
		rightJoint.setAssignmentH(this);
	}
	
	public int getOrder(){
		if(leftJoint.isFirstJoint() && rightJoint.isFirstJoint())
			return 0;
		else if(leftJoint.isFirstJoint()){
			return getRightPrev().getOrder() + 1;
		}else if(rightJoint.isFirstJoint()){
			return getLeftPrev().getOrder() + 1;
		}else{
			LineH leftPrev = getLeftPrev();
			LineH rightPrev = getRightPrev();
			if(leftPrev == rightPrev)
				return leftPrev.getOrder() + 1;
			else
				return Math.max(leftPrev.getOrder(), rightPrev.getOrder()) + 1;
		}
	}
	
	private LineH getLeftPrev(){
		return leftJoint.getPrev().getAssignmentH();
	}
	
	private LineH getRightPrev(){
		return rightJoint.getPrev().getAssignmentH();
	}
	
//	public int getOrderOnPath(){
//		return Math.max(leftJoint.getOrderOnPath(), rightJoint.getOrderOnPath());
//	}
	
	public int getStartIndex(){
		return leftJoint.getStartIndex();
	}
}
