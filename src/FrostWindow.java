import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;


public class FrostWindow extends JFrame implements MouseMotionListener, MouseListener, WindowListener, WindowFocusListener {

	public static Font font = new Font("Monospaced", Font.PLAIN, 12);
	
	public static final JTextPane frostSourceArea = new JTextPane(){
		
		{
			setBackground(new Color(25, 40, 35));
			setForeground(Color.WHITE);
			setFont(new Font("Monospaced", Font.PLAIN, 14));
			setCaretColor(Color.GRAY);
			
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('{'), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					int caret = frostSourceArea.getCaretPosition();
					try {
						frostSourceArea.getStyledDocument().insertString(caret, "{}", plain);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					frostSourceArea.setCaretPosition(caret+1);
				}
			});
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('('), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					int caret = frostSourceArea.getCaretPosition();
					try {
						frostSourceArea.getStyledDocument().insertString(caret, "()", plain);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					frostSourceArea.setCaretPosition(caret+1);
				}
			});
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('\n'), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					int caret = frostSourceArea.getCaretPosition();
					try {
						String text = getText();
						Matcher matcher = Pattern.compile("(^|\n)(\\s*).*?([{(]*)\\s*$").matcher(text.substring(0, caret));
						if(matcher.find()){
							if (matcher.group(3).endsWith("{") || matcher.group(3).endsWith("(")) {
								if (text.substring(caret, text.length()).startsWith("}") || text.substring(caret, text.length()).startsWith(")")) {
									frostSourceArea.getStyledDocument().insertString(caret, matcher.group(2)+"    \n"+matcher.group(2), plain);
								} else {
									frostSourceArea.getStyledDocument().insertString(caret, matcher.group(2)+"    ", plain);
								}
								caret += matcher.group(2).length()+4;
							} else {
								frostSourceArea.getStyledDocument().insertString(caret, matcher.group(2), plain);
								caret += matcher.group(2).length();
							}
						}
						frostSourceArea.setCaretPosition(caret);
					} catch (Exception e) {
						
					}
				}
			});
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(' ', (ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					FrostEditor.compile();
				}
			});
			
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('s', ActionEvent.CTRL_MASK), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					new FrostUtills().printFile(FrostEditor.file.getAbsolutePath(), frostSourceArea.getText());
				}
			});
			
			// undo and reddo
			final UndoManager undoManager = new UndoManager();
			getDocument().addUndoableEditListener(undoManager);
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('z', ActionEvent.CTRL_MASK), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					undoManager.undo();
				}
			});
			
			getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('y', ActionEvent.CTRL_MASK), new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					undoManager.redo();
				}
			});
			
			addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					Point p = getHighlightRegion();
					putHighlights(p.x, p.y);
					FrostEditor.frostWindow.validate();
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	};
	

	private static Style error = frostSourceArea.addStyle("error", null);
	private static Style plain = frostSourceArea.addStyle("plain", null);
	private static Style tag = frostSourceArea.addStyle("tag", null);
	private static Style constant = frostSourceArea.addStyle("constant", null);
	private static Style variable = frostSourceArea.addStyle("variable", null);
	private static Style function = frostSourceArea.addStyle("function", null);
	private static Style comment = frostSourceArea.addStyle("comment", null);
	
	private final static String errorPattern = "([^\\s]+)";
	private final static String plainPattern = "(\\+|-|/|\\*|%|equals|print|\\.|endl|=~|=~all|new|die|file|read|readLine|hasNext|hasNextLine"+
													"|readAll|write|close|return|compile|=|\\(|\\)|\\{|\\}|==|!=|<|<=|>|>=|!)";
	private final static String tagPattern = "(?<=\\(|^|\\s)(class|func|if|else|while|elsif)(?=\\)|$|\\s)";
	private final static String constantPattern = "(?<=\\(|^|\\s)(\"(\\\\\"|[^\"]| )*\"|\\d+\\.\\d+|\\d+)(?=\\)|$|\\s)";
	private final static String variablePattern = "(?<=\\(|^|\\s)(::\\$[^\\$\\s]+|\\$[^\\$\\s]+|[^\\$\\s]+\\$)(?=\\)|$|\\s)";
	private final static String functionPattern = "(?<=\\(|^|\\s)(::[^\\$:\\s]+|[^\\$:\\s]+::|(?<=func |class )[^\\$\\s]+(?=(\\s*\\{)))";
	private final static String commentPattern = "(\\\\\")|(//[^\n]*)(\n|$)";

	private static void putHighlights(int start, int end) {
		
		Matcher matcher = Pattern.compile(errorPattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), error, true);
		}
		matcher = Pattern.compile(plainPattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), plain, true);
		}
		matcher = Pattern.compile(tagPattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), tag, true);
		}
		matcher = Pattern.compile(functionPattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), function, true);
		}
		matcher = Pattern.compile(variablePattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), variable, true);
		}
		matcher = Pattern.compile("(\\s+)").matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), plain, true);
		}
		matcher = Pattern.compile(constantPattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), constant, true);
		}
		matcher = Pattern.compile(commentPattern).matcher(getTextToMatch()).region(start, end);
		while (matcher.find()) {
			frostSourceArea.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.group(1).length(), comment, true);
		}
	}
	
	private static String getTextToMatch() {
		try {
			return frostSourceArea.getStyledDocument().getText(0, frostSourceArea.getStyledDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Point getHighlightRegion() {
		try {
			String doc = frostSourceArea.getStyledDocument().getText(0, frostSourceArea.getStyledDocument().getLength());
			int start = frostSourceArea.getCaretPosition();
			if (start > 0) {
				start --;
			}
			for (; start > 0 && doc.charAt(start) != '\n'; start --);
			int end = frostSourceArea.getCaretPosition();
			for (; end < doc.length() && doc.charAt(end) != '\n'; end ++);
			return new Point(start, end);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void update(Graphics g) {
		super.update(g);
	}
	
	public FrostWindow() {
		StyleConstants.setForeground(error, Color.RED);
		StyleConstants.setBackground(error, Color.PINK);
		StyleConstants.setItalic(error, true);
		
		StyleConstants.setForeground(tag, new Color(235, 176, 53));
		StyleConstants.setBold(tag, true);
		
		StyleConstants.setForeground(constant, new Color(221, 30, 47));
		StyleConstants.setBold(constant, true);
		
		StyleConstants.setForeground(variable, new Color(6, 162, 203));
		
		StyleConstants.setForeground(function, new Color(33, 133, 89));
		
		StyleConstants.setForeground(comment, Color.GRAY);
		
		// new Color(176, 139, 20)
		
		this.setTitle("Frost");
		this.setIconImage(new ImageIcon("icon.png").getImage());
		this.setUndecorated(true);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addWindowListener(this);
		this.addWindowFocusListener(this);
		this.setSize(400, 400);
		this.setBackground(new Color(0, 0, 0, 0));
		
		final JScrollPane jScrollPane = new JScrollPane(new JPanel(new BorderLayout()){{
			add(frostSourceArea);
		}});
		
		JPanel jPanel = new JPanel() {
			{
				setFont(frostSourceArea.getFont());
			}
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                	//g.fillRect(FrostWindow.this.getX(), FrostWindow.this.getY(), this.getWidth(), this.getHeight());
        			g.setColor(Color.GRAY);
        			g.fillRect(0, 20, this.getWidth(), this.getHeight()-20);
        			
        			int fontHeight = getFontMetrics(frostSourceArea.getFont()).getHeight();
        			int fontWidth = getFontMetrics(frostSourceArea.getFont()).getWidths()[' '];
        			int lineStart = jScrollPane.getVerticalScrollBar().getValue() / fontHeight;
        			int lineEnd = lineStart + (int) (jScrollPane.getViewport().getSize().getHeight() / fontHeight);
        			int lineOffset = jScrollPane.getVerticalScrollBar().getValue() % fontHeight;
        			int linesSize = frostSourceArea.getText().split("(?=\n)").length;
//        			
//        			g.setColor(Color.BLACK);
//        			g.fillRect(1, 20, ((""+lineEnd).length()+1)*fontWidth, this.getHeight()-25);
//        			g.setColor(Color.LIGHT_GRAY);
//        			int j = 1;
//        			for (int i = lineStart ; i < lineEnd+1 && i < linesSize ; i ++) {
//        				g.drawString(""+(i+1), 4, 25+((j++)*fontHeight)-lineOffset);
//        			}
        			g.setColor(Color.GRAY);
        			g.fillRect(0, 0, ((FrostEditor.file.getName().length()+1)*fontWidth)+15, 26);
        			g.fillRect(0, FrostWindow.this.getHeight()-7, ((""+lineEnd).length()+1)*fontWidth, FrostWindow.this.getHeight());
        			g.setColor(Color.WHITE);
        			g.drawString(FrostEditor.file.getName(), 5, 15);
                }
            }
        };
        SpringLayout springLayout = new SpringLayout();
        jPanel.setLayout(springLayout);
        JButton exitButton = new JButton();
        exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				FrostWindow.this.dispose();
				System.exit(0);
			}
		});
        exitButton.setBackground(Color.RED);
        exitButton.setOpaque(true);
        
//        jScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//			
//			@Override
//			public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent) {
//				if (updateLineNumbers.get()) {
//					int caret = frostSourceArea.getCaretPosition();
//					update(getGraphics());
//					FrostWindow.this.validate();
//					frostSourceArea.setCaretPosition(caret);
//				}
//			}
//		});
        LineNumberPanel lineNumberPanel = new LineNumberPanel(frostSourceArea, jScrollPane);
        JPanel jPanel2 = new JPanel(new BorderLayout());
        jPanel2.add(lineNumberPanel, BorderLayout.WEST);
        jPanel2.add(jScrollPane, BorderLayout.CENTER);
        jPanel.add(exitButton);
        //jPanel.add(lineNumberPanel);
        jPanel.add(jPanel2);
        springLayout.putConstraint(SpringLayout.NORTH, exitButton, 2, SpringLayout.NORTH, jPanel);
        springLayout.putConstraint(SpringLayout.WEST, exitButton, new Spring() {

			@Override
			public void setValue(int paramInt) {}
			@Override
			public int getValue() {
				int fontWidth = getFontMetrics(frostSourceArea.getFont()).getWidths()[' '];
				return (FrostEditor.file.getName().length()+1)*fontWidth;
			}
			@Override
			public int getPreferredValue() {return getValue();}
			@Override
			public int getMinimumValue() {return 0;}
			@Override
			public int getMaximumValue() {return 0;}
		}, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.EAST, exitButton, 15, SpringLayout.WEST, exitButton);
        springLayout.putConstraint(SpringLayout.SOUTH, exitButton, 18, SpringLayout.NORTH, jPanel);
        
        springLayout.putConstraint(SpringLayout.NORTH, jPanel2, 25, SpringLayout.NORTH, jPanel);
        springLayout.putConstraint(SpringLayout.WEST, jPanel2, 5, SpringLayout.WEST, jPanel);
        springLayout.putConstraint(SpringLayout.SOUTH, jPanel2, -5, SpringLayout.SOUTH, jPanel);
        springLayout.putConstraint(SpringLayout.EAST, jPanel2, -5, SpringLayout.EAST, jPanel);
        
//        springLayout.putConstraint(SpringLayout.NORTH, jScrollPane, 25, SpringLayout.NORTH, jPanel);
//        springLayout.putConstraint(SpringLayout.WEST, jScrollPane, 1, SpringLayout.WEST, lineNumberPanel);
//        springLayout.putConstraint(SpringLayout.WEST, jScrollPane, new Spring() {
//			
//			@Override
//			public void setValue(int paramInt) {}
//			@Override
//			public int getValue() {
//				int fontHeight = getFontMetrics(frostSourceArea.getFont()).getHeight();
//				int fontWidth = getFontMetrics(frostSourceArea.getFont()).getWidths()[' '];
//				int lineStart = jScrollPane.getVerticalScrollBar().getValue() / fontHeight;
//    			int lineEnd = lineStart + (int) (jScrollPane.getViewport().getSize().getHeight() / fontHeight);
//				return ((""+lineEnd).length()+1)*fontWidth;
//			}
//			@Override
//			public int getPreferredValue() {return getValue();}
//			@Override
//			public int getMinimumValue() {return 0;}
//			@Override
//			public int getMaximumValue() {return 0;}
//		}, SpringLayout.WEST, jPanel);
//        springLayout.putConstraint(SpringLayout.EAST, jScrollPane, -5, SpringLayout.EAST, jPanel);
//        springLayout.putConstraint(SpringLayout.SOUTH, jScrollPane, -5, SpringLayout.SOUTH, jPanel);
        
		this.setContentPane(jPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public AtomicBoolean updateLineNumbers = new AtomicBoolean(false);
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}
	
	public void postSetVisible() {
		FrostEditor.loadFile();
		putHighlights(0,frostSourceArea.getStyledDocument().getLength());
		updateLineNumbers.set(true);
		this.validate();
	}
	
	@Override
	public void mouseDragged(MouseEvent paramMouseEvent) {
		if (resizeX) {
			if (paramMouseEvent.getX() > 100) {
				setSize(paramMouseEvent.getX(), getHeight());
			}
		}
		if (resizeY) {
			if (paramMouseEvent.getY() > 100) {
				setSize(getWidth(), paramMouseEvent.getY());
			}
		}
		if (!(resizeX || resizeY)) {
			this.setLocation(
					this.getLocation().x+paramMouseEvent.getX()-mousePressX,
					this.getLocation().y+paramMouseEvent.getY()-mousePressY);
			FrostEditor.consoleFrame.setLocation(getLocationOnScreen().x, getLocationOnScreen().y+getHeight());
		}
		super.update(getGraphics());
		frostSourceArea.update(frostSourceArea.getGraphics());
	}

	@Override
	public void mouseMoved(MouseEvent paramMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent paramMouseEvent) {
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
	
	int mousePressX = 0;
	int mousePressY = 0;
	boolean resizeX = false;
	boolean resizeY = false;
	
	@Override
	public void mousePressed(MouseEvent paramMouseEvent) {
		mousePressX = paramMouseEvent.getX();
		mousePressY = paramMouseEvent.getY();
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
		if (mousePressX < ((FrostEditor.file.getName().length()+1)*frostSourceArea.getFontMetrics(frostSourceArea.getFont()).getWidths()[' '])+15 && mousePressY < 20) {
			if (paramMouseEvent.getButton() == 3) {
				final JPopupMenu jPopupMenu = new JPopupMenu();
				jPopupMenu.setLocation(paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen());
				jPopupMenu.add(new JMenuItem("save"){{
					addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							jPopupMenu.setVisible(false);
							new FrostUtills().printFile(FrostEditor.file.getAbsolutePath(), frostSourceArea.getText());
						}
					});
				}});
				jPopupMenu.add(new JMenuItem("open"){{
					addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							jPopupMenu.setVisible(false);
							try {
								JFileChooser jFileChooser = new JFileChooser(new File(".").getCanonicalPath());
								if (jFileChooser.showOpenDialog(FrostWindow.this) == JFileChooser.APPROVE_OPTION) {
									FrostEditor.file = jFileChooser.getSelectedFile();
									frostSourceArea.setText(new FrostUtills().readFile(FrostEditor.file));
									putHighlights(0,frostSourceArea.getStyledDocument().getLength());
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}});
				jPopupMenu.addFocusListener(new FocusListener() {
					
					@Override
					public void focusLost(FocusEvent arg0) {
						jPopupMenu.setVisible(false);
					}
					
					@Override
					public void focusGained(FocusEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				jPopupMenu.setVisible(true);
				jPopupMenu.requestFocus();
			}
		}
	}
	
	int lastWidth = 0;
	int lastHeight = 0;
	
	@Override
	public void mouseReleased(MouseEvent paramMouseEvent) {
//		if (paramMouseEvent.getLocationOnScreen().getY() == 0) {
//			lastWidth = this.getWidth();
//			lastHeight = this.getHeight();
//			this.setLocation(0, 0);
//			this.setSize(
//					Toolkit.getDefaultToolkit().getScreenSize().width,
//					Toolkit.getDefaultToolkit().getScreenSize().height);
//		} else {
//			this.setSize(lastWidth, lastHeight);
//		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		FrostEditor.consoleFrame.setState(JFrame.NORMAL);
	}

	@Override
	public void dispose() {
		windowClosed(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
		super.dispose();
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		new FrostUtills().printFile(".frostmeta",
				"<file>"+FrostEditor.file.getAbsolutePath()+"</file>\n");
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		FrostEditor.consoleFrame.setState(JFrame.NORMAL);
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		FrostEditor.consoleFrame.setState(JFrame.ICONIFIED);
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		File meta = new File(".frostmeta");
		if (!meta.exists()) {
			try {
				meta.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String doc = new FrostUtills().readFile(meta);
		Matcher matcher = Pattern.compile("<file>(.*)</file>").matcher(doc);
		if (matcher.find()) {
			FrostEditor.file = new File(matcher.group(1));
		}
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		
	}

	public void loadMeta() {
		File meta = new File(".frostmeta");
		if (!meta.exists()) {
			try {
				meta.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String doc = new FrostUtills().readFile(meta);
		Matcher matcher = Pattern.compile("<file>(.*)</file>").matcher(doc);
		if (matcher.find()) {
			FrostEditor.file = new File(matcher.group(1));
		}
	}

}
