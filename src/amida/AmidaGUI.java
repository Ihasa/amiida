package amida;

import java.awt.Graphics;
import java.util.HashMap;

public class AmidaGUI {
	private Amida amida;
	private int offsetX;
	private int offsetY;
	private int width;
	private int height;
	private int lineThickness = 32;
	
	private HashMap<Joint, Float> jointOffsets = new HashMap<Joint, Float>();

	public AmidaGUI(Amida amida, int x, int y, int w, int h) {
		this.amida = amida;
		offsetX = x;
		offsetY = y;
		width = w;
		height = h;
	}
	
	public void moveRel(int x, int y){
		offsetX += x;
		offsetY += y;
	}
	public void scaleX(int pixelX){
		width += pixelX;
		if(width < 1)
			width = 1;
	}
	public void scaleY(int pixelY){
		height += pixelY;
		if(height < 1)
			height = 1;
	}
	public void scale(int pivotX, int pivotY, int pixel){
		if(width > 1 || (width == 1 && pixel > 0)){
			float x = (float)(pivotX - offsetX) / width;
			offsetX -= (int)(x * pixel);
			scaleX(pixel);
		}
		if(height > 1 || (height == 1 && pixel > 0)){
			float y = (float)(pivotY - offsetY) / height;
			offsetY -= (int)(y * pixel);
			scaleY(pixel);
		}
	}
	
	public void alignLineH(){
		LineH[] hLines = amida.getLineH();
		float hLineOffset = getHLineOffset(hLines);
		for(LineH lh : hLines){
			float offset = ((lh.getOrder()+1) * hLineOffset) / height;
			jointOffsets.put(lh.leftJoint, offset);
			jointOffsets.put(lh.rightJoint, offset);
		}
	}

	private float getHLineOffset(LineH[] hLines) {
		return (float)height / (getMaxOrder(hLines) + 1 + 1);
	}
	
	private int getMaxOrder(LineH[] lineH){
		int res = 0;
		for(int i = 0; i < lineH.length; i++){
			res = Math.max(res, lineH[i].getOrder());
		}
		return res;
	}
	
	public void addLineH(int mx, int my) {
		int vIndex = getLeftVLineIndex(mx);
		int fromOrder = getOrder(vIndex, my);
		int toOrder = getOrder(vIndex + 1, my);
		LineH lh = amida.addLineH(vIndex, fromOrder, vIndex + 1, toOrder);
		if(lh != null){
			float offset = (float)(my - offsetY) / height;
			jointOffsets.put(lh.leftJoint, offset);
			jointOffsets.put(lh.rightJoint, offset);
		}
	}
	
	private int getLeftVLineIndex(int mx) {
		return (amida.getLineVNum() - 1) * (mx - offsetX) / width;
	}
	
	public int getOrder(int vLineIndex, int mouseY){
		float offset = (float)(mouseY - offsetY) / height;
		LineV lv = amida.getLineV()[vLineIndex];
		int res = 0;
		for(int i = 0; i < lv.getJointNum(); i++){
			Joint j = lv.getJoint(i);
			if(offset > jointOffsets.get(j))
				res++;
		}
		return res;
	}
	
	public int getClickedVLineIndex(int mx){
		float offset = getVLineOffset(amida.getLineV());
		for(int i = 0; i < amida.getLineVNum(); i++){
			if(mx-offsetX > offset * i - lineThickness/2 && mx-offsetX < offset * i + lineThickness/2)
				return i;
		}
		return -1;
	}
	
	private float getVLineOffset(LineV[] vLines) {
		return (float)width / (vLines.length - 1);
	}
	
	public boolean contains(int mx, int my){
		return mx > offsetX && mx < offsetX + width&& 
				my > offsetY && my < offsetY + height;
	}
	
	public void addLineH(
			int fromIndex, int fromOrder, float fromOffset, 
			int toIndex, int toOrder, float toOffset){
		LineH lh = amida.addLineH(fromIndex, fromOrder, toIndex, toOrder);
		if(lh != null){
			jointOffsets.put(lh.leftJoint, fromOffset);
			jointOffsets.put(lh.rightJoint, toOffset);
		}
	}
	
	public void paint(Graphics g){
		LineV[] vLines = amida.getLineV();
		float vLineOffset = getVLineOffset(vLines);
		for(int i = 0; i < vLines.length; i++){
			LineV lv = vLines[i];
			int x = offsetX + (int)(i * vLineOffset);
			g.drawString(String.format("%s(%s)",lv.getStartIndex(),amida.getResult(i)), x, offsetY);
			
			drawLineV(g, x, offsetY, height, lineThickness / 4);
			
			g.drawString(String.format("%s",lv.getEndIndex()), x, offsetY + height + 10);
		}
		
		LineH[] hLines = amida.getLineH();
		for(int i = 0; i < hLines.length; i++){
			LineH lh = hLines[i];
			int x = offsetX + (int)(lh.getStartIndex() * vLineOffset);
			int y1 = offsetY + (int)(jointOffsets.get(lh.leftJoint) * height);
			int y2 = offsetY + (int)(jointOffsets.get(lh.rightJoint) * height);
			g.drawLine(x, y1, x + (int)vLineOffset, y2);
		}
	}
	
	private void drawLineV(Graphics g, int x, int y, int length, int thickness){
		g.drawLine(x, y, x, y + length);
		g.fillRect(x - thickness/2, y,thickness,length);
	}
	
	public int getY(){ return offsetY; }
	
	public int getHeight(){ return height; }
}
