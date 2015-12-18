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
	AmidaGUI amida;
	
	private int mode = MODE_MOVE;
	private static final int MODE_MOVE = 0;
	private static final int MODE_ADD_LINE = 1;
	
	private int prevX = 0;
	private int prevY = 0;
	private int pressedX = 0;
	private int pressedY = 0;
	
	public void init(){
		amida = new AmidaGUI(
				new Amida(new String[]{"hoge","foo","bar","buz"}),
				100,100,300,300
		);
		this.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent me){
				updatePrev(me);
			}
			public void mouseDragged(MouseEvent me){
				if(mode == MODE_MOVE){
					amida.moveRel(me.getX() - prevX,  me.getY() - prevY);
					updatePrev(me);
				}else if(me.isShiftDown()){
					prevX = me.getX();
					prevY = pressedY;
				}else
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
					amida.scale(me.getX(), me.getY(), -size);
				}
				else if(me.isShiftDown())
					amida.scaleX(-size);
				else
					amida.scaleY(-size);

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
				}else if(!me.isControlDown() && me.getClickCount() == 2){
					amida.alignLineH();
					repaint();
					return;
				}
				else
					mode = MODE_ADD_LINE;
				int mx = me.getX();
				int my = me.getY();
				if(me.isControlDown()){
					if(amida.contains(mx, my)){
						amida.addLineH(mx, my);
						repaint();
					}
					mode = MODE_MOVE;
				}else {//開始終了位置指定
					fromIdxBuf = amida.getClickedVLineIndex(mx);
					if(fromIdxBuf != -1){
						fromOrdBuf = amida.getOrder(fromIdxBuf, my);
						pressedX = mx;
						pressedY = my;
					}else mode = MODE_MOVE;
				}
			}

			public void mouseReleased(MouseEvent me){
				if(fromIdxBuf == -1 || fromOrdBuf == -1)
					return;
				int mx = me.getX();
				int my = prevY;//me.getY();
				int toIndex = amida.getClickedVLineIndex(mx);
				if(toIndex != -1){
					int toOrder = amida.getOrder(toIndex, my);
					
					int offsetY = amida.getY();
					int height = amida.getHeight();
					
					float fromOffset = (float)(pressedY - offsetY) / height;
					float toOffset = (float)(my - offsetY) / height;
					
					if(fromIdxBuf > toIndex){
						int tmp = fromIdxBuf;
						fromIdxBuf = toIndex;
						toIndex = tmp;
						
						tmp = fromOrdBuf;
						fromOrdBuf = toOrder;
						toOrder = tmp;
						
						float tmp2 = fromOffset;
						fromOffset = toOffset;
						toOffset = tmp2;
					}
					amida.addLineH(fromIdxBuf, fromOrdBuf, fromOffset, toIndex, toOrder, toOffset);
				}
				repaint();
				fromOrdBuf = fromIdxBuf = -1;
				mode = MODE_MOVE;
			}
			

		});
	}

	public void paint(Graphics g){
		amida.paint(g);
		//max(i+1) == hLines.length
		//max(lh.order()) == getMaxOrder(hLines)
		//max(lh.order() + 1) == getMaxOrder(hLines) + 1
		if(mode == MODE_ADD_LINE){
			g.drawLine(pressedX, pressedY, prevX, prevY);
		}
	}

	private void drawLineH(Graphics g, int x, int y, int length, int thickness){
		g.drawLine(x, y, x + length, y);
		g.fillRect(x, y - thickness/2, length, thickness);
	}
}
