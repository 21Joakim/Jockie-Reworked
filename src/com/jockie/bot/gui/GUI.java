package com.jockie.bot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.jockie.bot.main.JockieBot;

public class GUI extends DecoratedWindow {
	
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane tabbed_pane;
	
	private JPanel panel_general;
	private JPanel panel_console;
	
	public GUI() {
		super(1000, 1000/16*10, true);
		this.setTitle("Jockie");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		
		this.GUIInit();
	}
	
	private void GUIInit() {
		this.tabbed_pane = new JTabbedPane();
		this.tabbed_pane.setLocation(this.getStartPoint());
		this.tabbed_pane.setSize(this.getUndecoratedWidth(), this.getUndecoratedHeight());
		
		JLabel label_status = new JLabel("Offline");
		label_status.setLocation(400, 380);
		label_status.setSize(label_status.getPreferredSize());
		
		JButton button_start = new JButton("Start");
		button_start.setLocation(400, 400);
		button_start.setSize(button_start.getPreferredSize());
		button_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						button_start.setEnabled(false);
						label_status.setText("Starting");
						JockieBot.startBot();
						label_status.setText("Online");
					}
				}).start();
			}
		});
		
		this.panel_general = new JPanel();
		this.panel_general.setLayout(null);
		this.panel_general.add(label_status);
		this.panel_general.add(button_start);
		
		this.panel_console = new JPanel();
		this.panel_console.setLayout(null);
		
		JTextArea text_area_console = new JTextArea();
		text_area_console.setBackground(null);
		text_area_console.setEditable(false);
		text_area_console.setLineWrap(true);
		text_area_console.setWrapStyleWord(true);
		
		JScrollPane scroll_pane_console = new JScrollPane(text_area_console);
		scroll_pane_console.setLocation(0, 0);
		scroll_pane_console.setSize(this.tabbed_pane.getSize().width - 5, this.tabbed_pane.getSize().height - 27);
		
		PrintStream print_stream = new PrintStream(new JTextAreaStreamWrapper(text_area_console));
		System.setOut(print_stream);
		System.setErr(print_stream);
		
		this.panel_console.add(scroll_pane_console);
		
		this.tabbed_pane.addTab("General", this.panel_general);
		this.tabbed_pane.addTab("Console", this.panel_console);
		
		this.getContentPane().setLayout(null);
		this.getContentPane().add(this.tabbed_pane);
		
		this.setVisible(true);
	}
}