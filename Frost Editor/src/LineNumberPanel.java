import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class LineNumberPanel extends JTextArea {

	private JTextPane textPane;
	private JScrollPane jScrollPane;

	public LineNumberPanel(JTextPane textPane, JScrollPane jScrollPane) {
		this.textPane = textPane;
		this.jScrollPane = jScrollPane;
		this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
	}

	@Override
	public void update(Graphics g) {
		String out = "";
		int lineCount = textPane.getText().split("\n").length;
		for (int i = 0 ; i < lineCount ; i ++) {
			out += ""+i;
		}
		this.setText(out);
		setSize((" "+lineCount).length()*getFontMetrics(getFont()).charWidth(' '), getHeight());
		super.update(g);
		
	}
}
