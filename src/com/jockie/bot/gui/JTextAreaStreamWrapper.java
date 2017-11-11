package com.jockie.bot.gui;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class JTextAreaStreamWrapper extends OutputStream {
	
	private JTextArea text_area;
	
	public JTextAreaStreamWrapper(JTextArea text_area) {
		this.text_area = text_area;
	}
	
	public void write(int b) throws IOException {
		write(new byte[]{(byte) b}, 0, 1);
	}
	
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		this.text_area.append(new String(b, off, len, "UTF-8"));
	}
}