package amida;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

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
	
	public List<LineH> getLineH(int from, int to, int index){
		List<LineH> res = new LinkedList<LineH>();
		for(int i = from; i < to; i++){
			LineH lh = joints.get(i).getAssignmentH();
			if(lh.getStartIndex() == index)
				res.add(lh);
		}
		return res;
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
		LineH lineH = new LineH(l1, l2);
		l1.joints.add(order1, lineH.leftJoint);
		l2.joints.add(order2, lineH.rightJoint);
		return lineH;
	}

	public Joint getPrevJoint(Joint joint) {
		int index = joints.indexOf(joint);
		return joints.get(index - 1);
	}

}
