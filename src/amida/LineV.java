package amida;

import java.util.List;
import java.util.ArrayList;

public class LineV {
	
	private int startIndex;

	private String endIndex;

	private List<Joint> joints;

	public LineV(int start, String end){
		startIndex = start;
		endIndex = end;
		joints = new ArrayList<Joint>();
	}
	
	public Joint getJoint(int order) {
		return joints.get(order);
	}
	
	public LineH getLineH(int order){
		return joints.get(order).getAssignmentH();
	}
	
	public Joint getNextJoint(Joint j){
		int idx = joints.indexOf(j);
		return joints.get(idx + 1);
	}
	public boolean hasNextJoint(Joint j){
		int idx = joints.indexOf(j);
		return idx < joints.size() - 1;
	}

	public boolean hasJoints(){
		return joints.size() > 0;
	}
	
	public int getJointNum(){
		return joints.size();
	}
	public int getOrder(Joint j){
		return joints.indexOf(j);
	}
	public int getStartIndex(){
		return startIndex;
	}
	public String getEndIndex(){
		return endIndex;
	}
	public static LineH connect(LineV l1, int order1, LineV l2, int order2){
		Joint joint1 = new Joint(l1);
		Joint joint2 = new Joint(l2);
		Joint.connect(joint1, joint2);
		l1.joints.add(order1, joint1);
		l2.joints.add(order2, joint2);
		return new LineH(joint1, joint2);
	}

	public Joint getPrevJoint(Joint joint) {
		int index = joints.indexOf(joint);
		return joints.get(index - 1);
	}

}
