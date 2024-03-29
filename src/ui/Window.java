package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Window extends JFrame{
	
	private int width;
    private int height;
    private Board tablero;
    private String title;
    private Scores scores;
    
    public Window(String title, Board tablero, Scores scores) {
    	super(title);
    	this.tablero = tablero;
    	this.scores = scores;
    	this.width = tablero.width+scores.width+20;
    	this.height = tablero.height*105/100;
    	this.title = title;
    	buildWindow();
    }
    
    private void buildWindow() {
    	setPreferredSize(new Dimension(this.width, this.height));
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	FlowLayout layout = new FlowLayout();
    	JPanel pane = new JPanel(layout);
    	pane.add(this.tablero, BorderLayout.WEST);
    	pane.add(this.scores, BorderLayout.EAST);
    	this.add(pane);
    }

}
