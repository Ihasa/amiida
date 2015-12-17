package amida;
public class Joint {
	private LineV assignmentV;
	
	private LineH assignmentH;

	
	public Joint(LineV assignV, LineH assignH){
		assignmentV = assignV;
		assignmentH = assignH;
	}
	
	public Joint getConnection() {
		return assignmentH.getConnection(this);
	}

	public Joint getNext(){
		return assignmentV.getNextJoint(this);
	}
	
	public boolean hasNext() {
		return assignmentV.hasNextJoint(this);
	}
	
	public Joint getPrev(){
		return assignmentV.getPrevJoint(this);
	}
	public boolean isFirstJoint(){
		return assignmentV.getOrder(this) == 0;
	}

	public int getStartIndex(){
		return assignmentV.getStartIndex();
	}
	public String getResult(){
		return assignmentV.getEndIndex();
	}
	
	public int getOrder(){
		int res = 0;
		if(!isFirstJoint()){
			res = 1;
			Joint j1 = getPrev();
			Joint j2 = j1.getConnection();
			res += Math.max(j1.getOrderOnPath(), j2.getOrderOnPath());
		}
		return res;
	}
	
	private int getOrderOnPath(){
		int res = 0;
		Joint j = this;
		while(!j.isFirstJoint()){
			res++;
			Joint up = j.getPrev();
			j = up.getConnection();
		}
		return res;
	}
	
	public LineH getAssignmentH(){
		return assignmentH;
	}
	
}
