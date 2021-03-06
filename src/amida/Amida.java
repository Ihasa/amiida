package amida;

import java.util.ArrayList;
import java.util.List;

public class Amida {

	private final LineV[] lineV;
	private List<LineH> lineH;
	
	public Amida(int vLines){
		lineV = new LineV[vLines];
		for(int i = 0; i < vLines; i++){
			lineV[i] = new LineV(i,""+i);
		}
		lineH = new ArrayList<LineH>();
	}
	
	public Amida(String[] endIndexes){
		lineV = new LineV[endIndexes.length];
		for(int i = 0; i < endIndexes.length; i++){
			lineV[i] = new LineV(i,endIndexes[i]);
		}
		lineH = new ArrayList<LineH>();	
	}
	
	public LineH addLineH(int fromIndex, int fromOrder, int toIndex, int toOrder){
		if(invalidArgs(fromIndex, fromOrder) || invalidArgs(toIndex, toOrder) || 
		   invalidConnection(fromIndex, fromOrder, toIndex, toOrder))
			return null;
		
		LineV from = lineV[fromIndex];
		LineV to = lineV[toIndex];
		LineH hLine = LineV.connect(from, fromOrder, to, toOrder);
		lineH.add(hLine);
		
		return hLine;
	}

	private boolean invalidConnection(int fromIndex, int fromOrder, int toIndex, int toOrder) {
		if(fromIndex == toIndex || toIndex - fromIndex != 1)
			return true;
		
		LineV from = lineV[fromIndex];
		LineV to = lineV[toIndex];
		List<LineH> fromAbove = from.getLineH(0, fromOrder, fromIndex);
		List<LineH> fromBelow = from.getLineH(fromOrder, from.getJointNum(), fromIndex);
		List<LineH> toAbove = to.getLineH(0, toOrder, fromIndex);
		List<LineH> toBelow = to.getLineH(toOrder, to.getJointNum(), fromIndex);
		
		return !(fromAbove.equals(toAbove) && fromBelow.equals(toBelow));
	}
	
	private boolean invalidArgs(int index, int order){
		if (index < 0 || index >= lineV.length)
			return true;
		if(order < 0 || order > lineV[index].getJointNum())
			return true;
		return false;
	}
	
	public LineV[] getLineV(){
		return lineV.clone();
	}
	
	public int getLineVNum(){
		return lineV.length;
	}
	
	public LineH[] getLineH(){
		return lineH.toArray(new LineH[0]);
	}
	
	public String getResult(int idx) {
		LineV line = lineV[idx];
		if(line.hasJoints()){
			return getResult(line.getJoint(0));
		}
		return line.getEndIndex();
	}

	public String getResult(Joint joint) {
		Joint j = joint.getConnection();
		while(j.hasNext()){
			j = j.getNext().getConnection();
		}
		return j.getResult();		
	}

	public String toString(){
		int length = maxIndexLength();
		String space = String.format("%" + length + "s", " ");
		String hyphen = space.replaceAll(" ", "-");
		
		StringBuilder sb = new StringBuilder();
		for(LineV l : lineV){
			sb.append(String.format("%-" + (1 + length) + "d", l.getStartIndex()));
		}
		sb.append("\n");
		
		for(int i = 0; i < maxOrder() + 1; i++){
			sb.append("|");
			for(int j = 0; j < lineV.length - 1; j++){
				sb.append(hasLineH(lineV[j], i) ? hyphen : space);
				sb.append("|");
			}
			sb.append("\n");
		}

		for(LineV l : lineV){
			sb.append(String.format("%-" + (1 + length) + "s", l.getEndIndex()));
		}
		return sb.toString();
	}
	private boolean hasLineH(LineV lv, int order){
		for(LineH lh : lineH){
			if(lh.getStartIndex() == lv.getStartIndex() && lh.getOrder() == order)
				return true;
		}
		return false;
	}
	
	private int maxOrder(){
		int res = 0;
		for(LineH lh : lineH){
			if(lh.getOrder() > res)
				res = lh.getOrder();
		}
		return res;
	}
	
	private int maxIndexLength(){
		int res = 1;
		for(LineV lv : lineV){
			int startIndexLength = ("" + lv.getStartIndex()).length();
			int endIndexLength = lv.getEndIndex().length();
			int length = Math.max(startIndexLength, endIndexLength);
			if(res < length){
				res = length;
			}
		}
		return res;
	}
}
