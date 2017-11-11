package com.jockie.bot.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class DecoratedWindow extends JFrame {
	
	private static final long serialVersionUID = -1L;
	
	private JPanel panel_decorator;
	private int width, height;
	
	private Point point_start_drag, point_start_loc;
	
	private boolean exitOnClose = false;
	
	public DecoratedWindow(int width, int height, boolean exitOnClose) {
		this.height = height;
		this.width = width;
		this.exitOnClose = exitOnClose;
		setSize(this.width, this.height);
		setUndecorated(true);
		setResizable(false);
		setLocationRelativeTo(null);
		
		if(exitOnClose)
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		else
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		panel_decorator = createDecorator();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(exitOnClose)
					System.exit(0);
				else dispose();
			}
		});
		
		this.height = (this.height - panel_decorator.getHeight());
		
		getContentPane().setLayout(null);
		
		getRootPane().setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.gray));
		
		getContentPane().add(panel_decorator);
	}
	
	private JPanel createDecorator() {
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, width, 40);
		panel.setBorder(new LineBorder(Color.black));
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				point_start_drag = getScreenLocation(e);
				point_start_loc = getLocation();
			}
		});
		
		panel.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
			    Point current = getScreenLocation(e);
			    Point offset = new Point((int) current.getX() - (int) point_start_drag.getX(), (int) current.getY() - (int) point_start_drag.getY());
			    Point new_location = new Point((int) (point_start_loc.getX() + offset.getX()), (int) (point_start_loc.getY() + offset.getY()));
			    setLocation(new_location);
			}
		});
		
		JButton button_exit = new JButton("X");
		button_exit.setSize(button_exit.getPreferredSize());
		button_exit.setFocusable(false);
		button_exit.setLocation(getUndecoratedWidth() - button_exit.getSize().width - 10, 7);
		button_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(exitOnClose)
					System.exit(0);
				else dispose();
			}
		});
		
		panel.setLayout(null);
		
		panel.add(button_exit);
		
		return panel;
	}
	
	private Point getScreenLocation(MouseEvent e) {
		Point cursor = e.getPoint();
		Point target_location = panel_decorator.getLocationOnScreen();
		return new Point((int) (target_location.getX() + cursor.getX()), (int) (target_location.getY() + cursor.getY()));
	}
	
	public JPanel getDecorator() {
		return panel_decorator;
	}
	
	public int getUndecoratedWidth() {
		return width;
	}
	
	public int getUndecoratedHeight() {
		return height;
	}
	
	public Point getStartPoint() {
		return new Point(getWidth() - getUndecoratedWidth(), getHeight() - getUndecoratedHeight());
	}
}