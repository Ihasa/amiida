package main;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import amida.*;

public class MainApplet extends Applet{
	private Amida amida;
	private int offsetX = 100;
	private int offsetY = 100;
	private int width = 300;
	private int height = 300;
	private int lineThickness = width / 10;
	
	private int mode = MODE_MOVE;
	private static final int MODE_MOVE = 0;
	private static final int MODE_ADD_LINE = 1;
	
	private int prevX = 0;
	private int prevY = 0;
	private int pressedX = 0;
	private int pressedY = 0;
		
	public void init(){
		amida = new Amida(new String[]{"hoge","foo","bar","buz"});
		this.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent me){
				updatePrev(me);
			}
			public void mouseDragged(MouseEvent me){
				if(mode == MODE_MOVE){
					offsetX += me.getX() - prevX;
					offsetY += me.getY() - prevY;
				}
				updatePrev(me);
				repaint();
			}
			private void updatePrev(MouseEvent me){
				prevX = me.getX();
				prevY = me.getY();
			}
		});
		
		this.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent me) {
				int size = me.getWheelRotation() * 20;
				if(me.isControlDown()){
					if(width > 1 || (width == 1 && size < 0)){
						float x = (float)(me.getX() - offsetX) / width;
						offsetX += (int)(x * size);
						width -= size;
					}
					if(height > 1 || (height == 1 && size < 0)){
						float y = (float)(me.getY() - offsetY) / height;
						offsetY += (int)(y * size);
						height -= size;
					}
				}
				else if(me.isShiftDown())
					width -= size;
				else
					height -= size;

				if(width <= 0)
					width = 1;
				if(height <= 0)
					height = 1;

				repaint();
			}
		});
		
		this.addMouseListener(new MouseAdapter(){
			int fromIdxBuf = -1;
			int fromOrdBuf = -1;
			public void mousePressed(MouseEvent me){
				if(me.getButton() != MouseEvent.BUTTON1){
					mode = MODE_MOVE;
					return;
				}
				else
					mode = MODE_ADD_LINE;
				int mx = me.getX();
				int my = me.getY();
				if(me.isControlDown()){
					if(containsMouse(mx, my)){
						amida.addLineH(getLeftVLineIndex(mx), getOrder(my));
						repaint();
					}
					mode = MODE_MOVE;
				}else {//開始終了位置指定
					fromIdxBuf = getClickedVLineIndex(mx);
					if(fromIdxBuf != -1){
						fromOrdBuf = getOrder(fromIdxBuf, my);
						pressedX = mx;
						pressedY = my;
					}else mode = MODE_MOVE;
				}
			}
			public void mouseReleased(MouseEvent me){
				if(fromIdxBuf == -1 || fromOrdBuf == -1)
					return;
				int mx = me.getX();
				int my = me.getY();
				int toIndex = getClickedVLineIndex(mx);
				if(toIndex != -1){
					int toOrder = getOrder(toIndex, my);
					if(fromIdxBuf > toIndex){
						int tmp = fromIdxBuf;
						fromIdxBuf = toIndex;
						toIndex = tmp;
						
						tmp = fromOrdBuf;
						fromOrdBuf = toOrder;
						toOrder = tmp;
					}
					amida.addLineH(fromIdxBuf, fromOrdBuf, toIndex, toOrder);
				}
				repaint();
				fromOrdBuf = fromIdxBuf = -1;
				mode = MODE_MOVE;
			}

			private int getLeftVLineIndex(int mx) {
				return (amida.getLineVNum() - 1) * (mx - offsetX) / width;
			}
			private boolean containsMouse(int mx, int my){
				return mx > offsetX && mx < offsetX + width&& 
						my > offsetY && my < offsetY + height;
			}
			private int getOrder(int mouseY){
				return (int)((mouseY - offsetY) / getHLineOffset(amida.getLineH()));
			}
			
			private int getClickedVLineIndex(int mx){
				float offset = getVLineOffset(amida.getLineV());
				for(int i = 0; i < amida.getLineVNum(); i++){
					if(mx-offsetX > offset * i - lineThickness/2 && mx-offsetX < offset * i + lineThickness/2)
						return i;
				}
				return -1;
			}
			
			private int getOrder(int vLineIndex, int mouseY){
				int hOrder = getOrder(mouseY);
				LineV lv = amida.getLineV()[vLineIndex];
				int res = 0;
				for(int i = 0; i < lv.getJointNum(); i++){
					Joint j = lv.getJoint(i);
					if(j.getAssignmentH().getOrder() < hOrder)
						res++;
				}
				return res;
			}
		});
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
		float hLineOffset = getHLineOffset(hLines);
		for(int i = 0; i < hLines.length; i++){
			LineH lh = hLines[i];
			int x = offsetX + (int)(lh.getStartIndex() * vLineOffset);
			int y = offsetY + (int)((lh.getOrder()+1)*hLineOffset);
			drawLineH(g, x, y, (int)vLineOffset, lineThickness / 4);
		}
		//max(i+1) == hLines.length
		//max(lh.order()) == getMaxOrder(hLines)
		//max(lh.order() + 1) == getMaxOrder(hLines) + 1
		if(mode == MODE_ADD_LINE){
			g.drawLine(pressedX, pressedY, prevX, prevY);
		}
	}

	private float getVLineOffset(LineV[] vLines) {
		return (float)width / (vLines.length - 1);
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
	
	private void drawLineV(Graphics g, int x, int y, int length, int thickness){
		g.drawLine(x, y, x, y + length);
		g.fillRect(x - thickness/2, y,thickness,length);
	}

	private void drawLineH(Graphics g, int x, int y, int length, int thickness){
		g.drawLine(x, y, x + length, y);
		g.fillRect(x, y - thickness/2, length, thickness);
	}
}
