/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.BorderFactory;

/**
 *
 * @author luix_
 */
public class JPanelGradient extends javax.swing.JPanel{

    Color c1 = new Color(255, 255, 255);
    Color c2 = new Color(255, 255, 255);
    
    public JPanelGradient(LayoutManager lm, boolean bln) {
        super(lm, bln);
    }

    public JPanelGradient(LayoutManager lm) {
        super(lm);
    }

    public JPanelGradient(boolean bln) {
        super(bln);
    }

    public JPanelGradient(Color c1, Color c2) {
        this.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint verticalGradient = new GradientPaint(0, 0, c1, 0, getHeight(), c2, true);
        g2d.setPaint(verticalGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    
    
}