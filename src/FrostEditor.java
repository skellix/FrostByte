import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;


public class FrostEditor {

	public static File file = new File("test.frost");
	public static FrostWindow frostWindow = new FrostWindow();
	
	public static class ConsolePane extends JTextPane implements Runnable {
		public ConsolePane() {
			setText("");
			setForeground(Color.WHITE);
			setBackground(Color.BLACK);
			setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
			setEditable(false);
			new Thread(this).start();
		}
		private class StyledText {
			public String text;
			public Style style;
		}
		private ConcurrentLinkedQueue<StyledText> textQueue = new ConcurrentLinkedQueue<StyledText>();
		public void appendText(String text, Style style) {
			StyledText styledText = new StyledText();
			styledText.text = text;
			styledText.style = style;
			textQueue.add(styledText);
		}
		@Override
		public void run() {
			while (true) {
				StyledText next = textQueue.poll();
				if (next != null) {
					try {
						getStyledDocument().insertString(getStyledDocument().getLength(), next.text, next.style);
						Thread.sleep(100);
					} catch (BadLocationException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public static ConsolePane consolePane = new ConsolePane();
	public static JFrame consoleFrame = new JFrame(){{
		//setTitle("STDOUT");
		add(consolePane);
		setLocation(frostWindow.getLocation().x, frostWindow.getLocation().y+frostWindow.getHeight());
		setSize(400, 250);
	}};
	
	public static Style stdioStyle = consolePane.addStyle("stdioStyle", null);
	public static Style stderrStyle = consolePane.addStyle("stderrStyle", null);
	
	public static void main(String[] args) {
		
		StyleConstants.setForeground(stdioStyle, Color.WHITE);
		StyleConstants.setForeground(stderrStyle, Color.RED);
		
		if (args.length > 0) {
			file = new File(args[0]);
		}
		loadFile();
		frostWindow.setVisible(true);
	}

	public static void compile() {
		consolePane.setText("");
		consoleFrame.setVisible(true);
		saveFile();
		Object o = System.getenv().get("Path").split(""+File.pathSeparatorChar);
		String javaRoot = "";
		for (String path : System.getenv("PATH").split(File.pathSeparator)) {
			if (path.contains("jdk")) {
				javaRoot = "\""+path+File.separatorChar;
			}
		}
		for (String path : System.getenv().get("Path").split(""+File.pathSeparatorChar)) {
			if (path.contains("Frost")) {
				System.out.println("compiler found at '"+path+"'");
				try {
					List<String> command = Arrays.asList(javaRoot+"java.exe\"", "-jar", "\""+path+File.separatorChar+"frost.jar\"", "\""+file.getAbsolutePath()+"\"");
					final Process process = new ProcessBuilder(command).start();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							Scanner scanner = new Scanner(process.getInputStream());
							while (scanner.hasNextLine()) {
								consolePane.appendText("compiler:"+scanner.nextLine()+"\n", stdioStyle);
							}
							scanner.close();
						}
					}).start();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							Scanner scanner = new Scanner(process.getErrorStream());
							while (scanner.hasNextLine()) {
								consolePane.appendText("compiler:"+scanner.nextLine()+"\n", stderrStyle);
							}
							scanner.close();
						}
					}).start();
					process.waitFor();
					execute(javaRoot+"java.exe\"");
					return;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		JDialog jDialog = new JDialog(new JFrame(){{
			add(new JLabel("No Compiler was found in system Path"){{
				setForeground(Color.RED);
			}});
		}}, true);
	}

	private static void execute(String javaDir) {
		try {
			String srcName = file.getName().replaceAll("^(.*)\\.[^\\.]+$", "$1");
			final Process process = new ProcessBuilder(javaDir, "-jar", "\""+new File(file.getAbsolutePath()).getParent()+File.separatorChar+srcName+".jar\"").start();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Scanner scanner = new Scanner(process.getInputStream());
					while (scanner.hasNextLine()) {
						consolePane.appendText(scanner.nextLine()+"\n", stdioStyle);
					}
					scanner.close();
				}
			}).start();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Scanner scanner = new Scanner(process.getErrorStream());
					while (scanner.hasNextLine()) {
						consolePane.appendText(scanner.nextLine()+"\n", stderrStyle);
					}
					scanner.close();
				}
			}).start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void loadFile() {
		if (file.exists()) {
			frostWindow.frostSourceArea.setText(new FrostUtills().readFile(file));
		}
	}

	private static void saveFile() {
		new FrostUtills().printFile(file.getAbsolutePath(), frostWindow.frostSourceArea.getText());
		
	}

}
