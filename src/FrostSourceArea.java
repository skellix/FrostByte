import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;


public class FrostSourceArea extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable, KeyListener{

	private String text = "this is a test\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\n"+
	"this is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\nthis is a new line\n";
	
	private int scrollIndex = 0;
	
	public FrostSourceArea() {
		this.setBackground(Color.WHITE);
		this.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		//this.setFocusTraversalKeysEnabled(false);
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		String[] lines = getText().split("\n");
		// draw the scrollbar
		g.setColor(Color.BLACK);
		g.fillRect(
				this.getWidth()-10,
				(int) (((float) getScrollIndex()/ ((float) maxScrollIndex()))*this.getHeight()),
				10,
				10//((int) (((float) this.getHeight()/(float) maxScrollIndex())*this.getHeight())-20)/3
				);
		//draw the left bar
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getLineNumberWidth(), this.getHeight());
		// move the origin
		g.translate(0, 20);
		g.translate(0, -scrollIndex);
		// draw the text
		g.setColor(Color.BLACK);
		for (int i = 0 ; i < lines.length ; i ++) {
			g.drawString(""+i, 5, i*getFontMetrics().getHeight());
			g.drawString(lines[i].replace("\t", " "), getLineNumberWidth(), i*getFontMetrics().getHeight());
		}
		// draw the cursor
		g.fillRect(
				getLineNumberWidth()+(caretX*getFontMetrics().getWidths()[' ']),
				caretY*getFontMetrics().getHeight(),
				2,
				-getFontMetrics().getHeight());
	}
	
	@Override
	public void run() {
		this.setText(new FrostUtills().fixTabs(this.getText()));
		this.updateUI();
	}

	public int getLineNumberWidth() {
		return (""+getText().split("\n").length).length()*getFontMetrics().getWidths()[' ']+10;
	}
	
	public FontMetrics getFontMetrics() {
		return this.getFontMetrics(this.getFont());
	}
	@Override
	public void componentResized(ComponentEvent paramComponentEvent) {
		updateUI();
	}

	@Override
	public void componentMoved(ComponentEvent paramComponentEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent paramComponentEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent paramComponentEvent) {
		// TODO Auto-generated method stub
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (text == null) {
			this.text = "";
		} else {
			this.text = text;
		}
	}

	public int getScrollIndex() {
		return scrollIndex;
	}

	public void setScrollIndex(int scrollIndex) {
		if (scrollIndex < 0) {
			this.scrollIndex = 0;
		} else if (scrollIndex > maxScrollIndex()) {
			this.scrollIndex = maxScrollIndex();
		} else {
			this.scrollIndex = scrollIndex;
		}
	}

	public int maxScrollIndex() {
		return ((getText().split("\n").length*getFontMetrics().getHeight())-this.getHeight()+10);
	}
	
	int caretX = 0;
	int caretY = 0;
	private void setCaretTo(int x, int y) {
		int thisY = ((y-5+scrollIndex)/getFontMetrics().getHeight());
		if (thisY > getText().split("\n").length-1) {
			caretY = getText().split("\n").length-1;
		} else if (thisY < 0) {
			caretY = 0;
		} else {
			caretY = thisY;
		}
		int thisX = ((x-getLineNumberWidth())/getFontMetrics().getWidths()[' ']);
		String[] lines = getText().split("\n");
		if (thisX < 0) {
			caretX = 0;
		} else if (thisX > lines[caretY].length()) {
			caretX = lines[caretY].length();
		} else {
			caretX = thisX;
		}
		System.out.println(thisX+","+thisY);
	}
	
	public int getCaret() {
		String[] lines = getText().split("\n");
		int caret = 0;
		for (int i = 0 ; i < caretY ; i ++) {
			caret += lines[i].length()+1;
		}
		caret += caretX;
		return caret;
	}
	
	private void incrementCaret() {
		String[] lines = getText().split("\n");
		caretX ++;
		if (caretX > lines[caretY].length()) {
			if (caretY == lines.length-1) {
				caretX = lines[caretY].length();
			} else {
				caretX = 0;
				caretY ++;
			}
		}
	}
	
	private void decrementCaret() {
		String[] lines = getText().split("\n");
		caretX --;
		if (caretX < 0) {
			if (caretY == 0) {
				caretX = 0;
			} else {
				caretX = lines[caretY].length();
				caretY --;
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent paramMouseEvent) {
		// TODO Auto-generated method stub
		
	}
	
	int mousePressX = 0;
	int mousePressY = 0;
	int mousePressScrollIndex = 0;
	
	@Override
	public void mousePressed(MouseEvent paramMouseEvent) {
		mousePressX = paramMouseEvent.getX();
		mousePressY = paramMouseEvent.getY();
		mousePressScrollIndex = getScrollIndex();
		setCaretTo(mousePressX,mousePressY);
		this.requestFocus();
		updateUI();
	}

	@Override
	public void mouseReleased(MouseEvent paramMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent paramMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent paramMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent paramMouseEvent) {
		if (paramMouseEvent.getX() > this.getWidth()-20) {
			this.setScrollIndex(mousePressScrollIndex+paramMouseEvent.getY()-mousePressY);
		}
		updateUI();
	}

	@Override
	public void mouseMoved(MouseEvent paramMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		char keyChar = arg0.getKeyChar();
		switch (arg0.getKeyCode()) {
		case KeyEvent.VK_LEFT : {
			decrementCaret();
			break;
		}
		case KeyEvent.VK_RIGHT : {
			incrementCaret();
			break;
		}
		case KeyEvent.VK_TAB : {
			int caret = getCaret();
			this.setText(getText().substring(0, caret)+'\t'+getText().substring(caret, getText().length()));
			caretX ++;
			break;
		}
		case KeyEvent.CHAR_UNDEFINED : {
			break;
		}
		case KeyEvent.VK_BACK_SPACE : {
			int caret = getCaret();
			if (caret > 0) {
				this.setText(getText().substring(0, caret-1)+getText().substring(caret, getText().length()));
				decrementCaret();
			}
			break;
		}
		case KeyEvent.VK_ENTER : {
			int caret = getCaret();
			this.setText(getText().substring(0, caret)+keyChar+getText().substring(caret, getText().length()));
			caretX = 0;
			caretY ++;
			break;
		}
		default : {
			int caret = getCaret();
			this.setText(getText().substring(0, caret)+keyChar+getText().substring(caret, getText().length()));
			caretX ++;
		}
		}
		updateUI();
	}

}
