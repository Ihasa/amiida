package amida;
public class Joint {

	private Joint connection;//TODO: QÆ‚Â‚Ì‚â‚ß‚ÄLineH‚É–â‚¢‡‚í‚¹‚é•û‚ªƒVƒ“ƒvƒ‹

	private LineV assignmentV;
	
	private LineH assignmentH;

	private Joint(Joint iniConnection, LineV assign){
		connection = iniConnection;
		assignmentV = assign;
	}
	
	public Joint(LineV assign){
		this(null,assign);
	}
	
	public Joint getConnection() {
		return connection;
	}
	public void setConnection(Joint j){
		connection = j;
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
	
	public void setAssignmentH(LineH l){
		assignmentH = l;
	}
	public LineH getAssignmentH(){
		return assignmentH;
	}
	
	public static void connect(Joint j1, Joint j2){
		j1.connection = j2;
		j2.connection = j1;
	}
}
