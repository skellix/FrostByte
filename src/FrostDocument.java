import java.awt.Color;
import java.awt.Font;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;


public class FrostDocument implements StyledDocument {

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addDocumentListener(DocumentListener paramDocumentListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDocumentListener(DocumentListener paramDocumentListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUndoableEditListener(
			UndoableEditListener paramUndoableEditListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUndoableEditListener(
			UndoableEditListener paramUndoableEditListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getProperty(Object paramObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putProperty(Object paramObject1, Object paramObject2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(int paramInt1, int paramInt2)
			throws BadLocationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertString(int paramInt, String paramString,
			AttributeSet paramAttributeSet) throws BadLocationException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getText(int paramInt1, int paramInt2)
			throws BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getText(int paramInt1, int paramInt2, Segment paramSegment)
			throws BadLocationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Position getStartPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position getEndPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position createPosition(int paramInt) throws BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element[] getRootElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getDefaultRootElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(Runnable paramRunnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public Style addStyle(String paramString, Style paramStyle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeStyle(String paramString) {
		// TODO Auto-generated method stub

	}

	@Override
	public Style getStyle(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterAttributes(int paramInt1, int paramInt2,
			AttributeSet paramAttributeSet, boolean paramBoolean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParagraphAttributes(int paramInt1, int paramInt2,
			AttributeSet paramAttributeSet, boolean paramBoolean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLogicalStyle(int paramInt, Style paramStyle) {
		// TODO Auto-generated method stub

	}

	@Override
	public Style getLogicalStyle(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getParagraphElement(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getCharacterElement(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(AttributeSet paramAttributeSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBackground(AttributeSet paramAttributeSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Font getFont(AttributeSet paramAttributeSet) {
		// TODO Auto-generated method stub
		return null;
	}

}
