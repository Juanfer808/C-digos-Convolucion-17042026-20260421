package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GridViewer extends javax.swing.JFrame implements KeyListener {
    GridPanel gridPanel;
    ArrayList<int[][][]> states = new ArrayList<>();
    int currentStateIndex = 0;
    int historySize = Integer.MAX_VALUE;
    boolean replayEnabled = false;
    int scale = 1;

    public GridViewer(int width, int height) {
        initComponents(width, height, 1);
    }

    public GridViewer(int width, int height, int scale, int historySize) {
        this.historySize = historySize;
        initComponents(width, height, scale);
    }

    private void initComponents(int width, int height, int scale) {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        int titleBarHeight = 30;
        setPreferredSize(new java.awt.Dimension(width * scale, (height * scale) + titleBarHeight));
        getContentPane().setLayout(new FlowLayout());
        gridPanel = new GridPanel(scale);
        gridPanel.setPreferredSize(new java.awt.Dimension(width * scale, height * scale));
        getContentPane().add(gridPanel);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        pack();
        setVisible(true);
    }

    public void addState(int[][][] state){
        states.add(state);

        if(states.size() > historySize) {
            states.remove(0);
        }
    }

    public int getStateCount(){
        return states.size();
    }

    public void setState(int index){
        this.gridPanel.setState(this.states.get(index));
        this.gridPanel.repaint();
        this.currentStateIndex = index;
    }

    public int getCurrentStateIndex() {
        return currentStateIndex;
    }

    public void setReplayEnabled(boolean replayEnabled) {
        this.replayEnabled = replayEnabled;
    }

    public void keyPressed(KeyEvent e) {
        if(this.replayEnabled) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                this.setState(Math.min(this.getStateCount() - 1, this.getCurrentStateIndex() + 1));
            else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                this.setState(Math.max(0, this.getCurrentStateIndex() - 1));
            else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                this.setState(Math.min(this.getStateCount() - 1, this.getCurrentStateIndex() + 10));
            else if (e.getKeyCode() == KeyEvent.VK_UP)
                this.setState(Math.max(0, this.getCurrentStateIndex() - 10));
        }
    }

    public void keyReleased(KeyEvent e) {

    }
    public void keyTyped(KeyEvent e) {

    }

    public void log(String message) {
        gridPanel.setText(message);
    }
}

class GridPanel extends JPanel {
    private int[][][] state;
    int scale = 1;
    String text = "";
    Color textColor = Color.WHITE;

    public GridPanel() {
        super();
    }

    public GridPanel(int scale) {
        super();
        this.scale = scale;
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public void setState(int[][][] state) {
        this.state = state;
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if(state != null) {
            for (int i = 0; i < state.length; i++) {
                for (int j = 0; j < state[0].length; j++) {
                    g.setColor(new java.awt.Color(state[i][j][Image.RED], state[i][j][Image.GREEN], state[i][j][Image.BLUE]));
                    g.fillRect(j * scale, i * scale, scale, scale);
                }
            }
        }
        g.setColor(textColor);
        int i = 0;
        int lineHeight = 10;
        for(String line : text.split("\n")) {
            g.drawString(line, 10, lineHeight + lineHeight * i);
            i++;
        }
    }

    public void setText(String text) {
        this.text = text;
    }
}
