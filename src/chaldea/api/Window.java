package chaldea.api;

import chaldea.runtime.anno.*;
import chaldea.runtime.ChaldeaValue;
import chaldea.runtime.TypeSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@ChaldeaType("Window")
public class Window {
	JFrame frame;
	JPanel pan;
	
	public Window() {
		frame = new JFrame();
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);
		
		pan = new JPanel();
		pan.setLayout(new FlowLayout());
		
		frame.add(pan);
	}
	
	@Method("button")
	public void addButton(final TypeSpace ts, ChaldeaValue label, final ChaldeaValue callback) {
		JButton but = new JButton(label.toString());
		
		final chaldea.runtime.Method meth = callback.getType().getMethod("#call");
		
		if (meth != null) {
			but.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					meth.evaluate(ts, callback, new ChaldeaValue[0]);
				}
			});
		}
		
		pan.add(but);
		frame.pack();
	}
	
	@Method("show")
	public void show() {
		frame.setVisible(true);
	}
}