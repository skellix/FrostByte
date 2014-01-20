import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;


public class CustomFrostWindow extends JFrame implements MouseMotionListener, MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	public SpringLayout springLayout = new SpringLayout();
	private JFrame parent;
	
	public CustomFrostWindow(JFrame parent) {
		setParent(parent);
		init();
	}

	public CustomFrostWindow() {
		init();
	}

	private void init() {
		setIconImage(new ImageIcon("icon.png").getImage());
		setUndecorated(true);
		addMouseMotionListener(this);
		addMouseListener(this);
		setBackground(new Color(0, 0, 0, 0));
		JPanel jPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                	//g.fillRect(FrostWindow.this.getX(), FrostWindow.this.getY(), this.getWidth(), this.getHeight());
        			g.setColor(Color.GRAY);
        			g.fillRect(0, 0, 100, 20);
        			g.fillRect(0, 20, this.getWidth(), this.getHeight()-20);
        			g.setColor(Color.WHITE);
        			g.drawString(getTitle(), 5, 15);
                }
            }
        };
        jPanel.setLayout(springLayout);
        JButton exitButton = new JButton();
        exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				CustomFrostWindow.this.setVisible(false);
			}
		});
        exitButton.setBackground(Color.RED);
        exitButton.setOpaque(true);
        
        jPanel.add(exitButton);
        springLayout.putConstraint(SpringLayout.NORTH, exitButton, 2, SpringLayout.NORTH, jPanel);
        springLayout.putConstraint(SpringLayout.WEST, exitButton, 82, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.EAST, exitButton, 98, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.SOUTH, exitButton, 18, SpringLayout.NORTH, jPanel);
		this.setContentPane(jPanel);
	}

	int mousePressX = 0;
	int mousePressY = 0;
	boolean resizeX = false;
	boolean resizeY = false;
	int lastWidth = 0;
	int lastHeight = 0;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressX = e.getX();
		mousePressY = e.getY();
		if (mousePressX > getWidth()-20) {
			resizeX = true;
		} else {
			resizeX = false;
		}
		if (mousePressY > getHeight()-20) {
			resizeY = true;
		} else {
			resizeY = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (resizeX) {
			setSize(e.getX(), getHeight());
		}
		if (resizeY) {
			setSize(getWidth(), e.getY());
		}
		if (!(resizeX || resizeY)) {
			boolean moved = false;
			if (getParent() != null) {
				int parentBottomLeftX = getParent().getLocationOnScreen().x;
				int parentBottomLeftY = getParent().getLocationOnScreen().y+getParent().getHeight();
				if (new Point(getLocation().x+e.getX()-mousePressX, getLocation().y+e.getY()-mousePressY
						).distance(new Point(parentBottomLeftX, parentBottomLeftY)) < 10) {
					moved = true;
					setLocation(parentBottomLeftX, parentBottomLeftY);
				}
			}
			if (!moved) {
				setLocation(
						getLocation().x+e.getX()-mousePressX,
						getLocation().y+e.getY()-mousePressY);
			}
		}
		super.update(getGraphics());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public JFrame getParent() {
		return parent;
	}

	public void setParent(JFrame parent) {
		this.parent = parent;
	}
}
