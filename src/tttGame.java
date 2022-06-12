import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class tttGame extends JPanel implements ActionListener {
    private Dimension window;
    private short gameState; // 0 for menu, 1 game , 2 x win, 3 o win ,4 draw
    Game game;
    private Timer t;
    tttGame(Dimension d)
    {
        super();
        window=d;
        gameState=0;
        game= new Game(100,false,(int)(window.getWidth()/2),(int)(window.getHeight()/2),Color.RED,Color.BLACK);
        addMouseListener(new mouse());
        addMouseMotionListener(new mouseMotion());
        t=new Timer(33,this);
        t.start();
    }

    //////////////////////////DRAW FUNCTIONS/////////////////////////////////////////////

    //Function to draw the window
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(Color.WHITE);
        if(gameState==1)
            game.drawGame(g2);
        else
            game.drawWin(g2);
    }
    //////////////////////////EVENT HANDLING FUNCTIONS//////////////////////////////////////
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    //Mouse Listener and Mouse Motion Listener
    private class mouse extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if(e.isShiftDown())
            {
                if(gameState==0) {
                    gameState = 1;
                }
                else
                    gameState=0;
                return;
            }
            if(gameState==1)
            {
                game.update(e.getX(),e.getY());
            }
        }
    }
    private class mouseMotion extends MouseMotionAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if(e.isShiftDown())
            {
                if(gameState==0) {
                    gameState = 1;

                }
                else
                    gameState=0;
                return;
            }
            if(gameState==1)
            {
                game.update(e.getX(),e.getY());
            }
        }
    }

    ///////////////////////////////GAME CLASS/////////////////////////////////////////
    private class Game
    {
        private int size,x,y;
        private short available; //available counts remaining available spaces
        //winState determines current win state 0=no one 1= x won 2=o won 3=draw
        private short[][] grid; // The grid (0=empty,1=X,2=O)
        private boolean AI; //Variable to indicate if the AI is on
        private boolean xTurn; //Variable storing the current turn ( true=X, false=0)
        private Color X,O; // Stores colors for
        //Constructor
        Game(int s,boolean ai,int x,int y,Color xColor,Color oColor)
        {
            available=9;
            size=s;
            grid = new short[3][3];
            for(int i=0;i<3;i++)
                for(int j=0;j<3;j++)
                    grid[i][j]=0;
            AI=ai;
            this.x=x;
            this.y=y;
            X=xColor;
            O=oColor;
            xTurn=true;
        }

        //Draw Shapes
        private void drawX(Graphics2D g, int x, int y)
        {
            int width=(int)Math.sqrt(Math.pow(size/2,2)*2);
            int height=(int)Math.sqrt(Math.pow(size/6,2)*2);
            AffineTransform af=g.getTransform();
            g.translate(x,y);
            //Rect at 45 degrees
            g.rotate(radians(45));
            g.setColor(X);
            Rectangle2D.Double rArm = new Rectangle2D.Double(-width/2,-height/2,width,height);
            Rectangle2D.Double lArm = new Rectangle2D.Double(-width/2,-height/2,width,height);
            //First arm
            g.fill(rArm);
            //Rotate additional 90 degrees
            g.rotate(radians(90));
            //Second arm;
            g.setColor(X);
            g.fill(lArm);
            g.setTransform(af);
        }
        private void drawO(Graphics2D g, int x, int y)
        {
            int diameter= (int)Math.sqrt(Math.pow(size/2,2)*2);
            AffineTransform af=g.getTransform();
            g.translate(x,y);
            g.setColor(O);
            Ellipse2D.Double outerCircle=new Ellipse2D.Double(-diameter/2,-diameter/2,
                    diameter,diameter);
            Ellipse2D.Double innerCircle=new Ellipse2D.Double(-diameter/4,-diameter/4,
                    diameter/2,diameter/2);
            g.fill(outerCircle);
            g.setColor(Color.WHITE);
            g.fill(innerCircle);
            g.setTransform(af);
        }
        private void drawGrid(Graphics2D g)
        {
            int gridSize=size*3;
            AffineTransform af=g.getTransform();
            g.translate(x,y);
            //Vertical Lines
            g.drawLine(-size/2,gridSize/2,-size/2,-gridSize/2);
            g.drawLine(size/2,gridSize/2,size/2,-gridSize/2);
            //Horizontal Lines
            g.drawLine(gridSize/2,-size/2,-gridSize/2,-size/2);
            g.drawLine(gridSize/2,size/2,-gridSize/2,size/2);
            g.setTransform(af);
        }
        private void handleClick(int xpos, int ypos)
        {
            //Check if click is in x range and determine which section
            int xIndex=getXIndex(xpos);
            //Check if click is in y range and determine which section
            int yIndex=getYIndex(ypos);
            if(xIndex!=-1&&yIndex!=-1)
            {
                if(xTurn)
                {
                    if(grid[xIndex][yIndex]==0)
                    {
                        grid[xIndex][yIndex] = 1;
                        xTurn=false;
                        available--;
                    }
                }
                else
                {
                    if(grid[xIndex][yIndex]==0 && !AI)
                    {
                        xTurn=true;
                        grid[xIndex][yIndex] = 2;
                        available--;
                    }
                }
            }
        }
        public void drawGame(Graphics2D g)
        {
            int gridSize=size*3;
            g.setColor(Color.BLACK);
            g.drawRect(x-gridSize/2,y-gridSize/2,gridSize,gridSize);
            drawGrid(g);
            for(int i=0;i<3;i++)
            {
                int yPos=y+(-size+(size*i));
                for(int j=0;j<3;j++)
                {
                    int xPos=x+(-size+(size*j));
                    if(grid[j][i]==2) {
                        drawO(g, xPos, yPos);
                    }
                        else if(grid[j][i]==1) {
                        drawX(g, xPos, yPos);
                    }
                }
            }
        }

        private void drawWin(Graphics2D g)
        {
            String s;
            if(gameState==2) {
                drawX(g,(int)(window.getWidth()/2),(int)(window.getHeight()/2));
                s="X won!";
                g.setColor(Color.BLACK);
                g.drawChars(s.toCharArray(),0,6,(int)(window.getWidth()/2),20);
            }
            else if(gameState==3) {
                drawO(g,(int)(window.getWidth()/2),(int)(window.getHeight()/2));
                s="O won!";
                g.setColor(Color.BLACK);
                g.drawChars(s.toCharArray(),0,6,(int)(window.getWidth()/2),20);
            }
            else if(gameState==4) {
                g.setColor(Color.ORANGE);
                g.fillRect(0, 0, (int) (window.getWidth()), (int) (window.getHeight()));
                s="Draw";
                g.setColor(Color.BLACK);
                g.drawChars(s.toCharArray(),0,4,(int)(window.getWidth()/2),20);
            }
        }
        //Operation functions
        private void checkWin()
        {
            short cell1,cell2,cell3;
            //Check horizontally and vertically
            for(int i=0;i<3;i++)
            {
                //Vertical checking
                cell1= grid[0][i];
                cell2= grid[1][i];
                cell3= grid[2][i];
                if(cell1==1&&cell2==1&&cell3==1) {
                    gameState=2;
                    clear();
                    return;
                }
                    else if(cell1==2&&cell2==2&&cell3==2) {
                    gameState=3;
                    clear();
                    return;
                }
                //Horizontal checking
                cell1= grid[i][0];
                cell2= grid[i][1];
                cell3= grid[i][2];
                if(cell1==1&&cell2==1&&cell3==1) {
                    gameState=2;
                    clear();
                    return;
                }
                else if(cell1==2&&cell2==2&&cell3==2) {
                    gameState=3;
                    clear();
                    return;
                }
            }
            //Check diagonally

            //Top left to bottom right
            cell1= grid[0][0];
            cell2= grid[1][1];
            cell3= grid[2][2];
            if(cell1==1&&cell2==1&&cell3==1) {
                gameState=2;
                clear();
                return;
            }
            else if(cell1==2&&cell2==2&&cell3==2) {
                gameState=3;
                clear();
                return;
            }
            //Top right to bottom left
            cell1= grid[2][0];
            cell2= grid[1][1];
            cell3= grid[0][2];
            if(cell1==1&&cell2==1&&cell3==1) {
                gameState=2;
                clear();
                return;
            }
            else if(cell1==2&&cell2==2&&cell3==2) {
                gameState=3;
                clear();
                return;
            }
            //Check if there is no more available cells
            if(available==0) {
                clear();
                gameState=4;
                return;
            }
        }
        private void clear()
        {
            for(int i=0;i<3;i++)
                for(int j=0;j<3;j++)
                    grid[i][j]=0;
            available=9;
            xTurn=true;
        }
        public void update(int xpos, int ypos)
        {
            //if(xTurn)
                handleClick(xpos, ypos);
//            if(available>0)
//                //play(getXIndex(xpos), getYIndex(ypos));
            checkWin();
        }


        private int getXIndex(int xpos)
        {
            int xIndex=-1;
            int distanceFromCenter=size*3/2;
            //Check if click is in x range and determine which section
            if(xpos>=x-distanceFromCenter&&xpos<=x+distanceFromCenter)
            {
                if(xpos<=x-size/2)
                    xIndex=0;
                else if(xpos<=x+size/2)
                    xIndex=1;
                else
                    xIndex=2;
            }
            return xIndex;
        }
        private int getYIndex(int ypos)
        {
            int yIndex=-1;
            int distanceFromCenter=size*3/2;
            if(ypos>=y-distanceFromCenter&&ypos<=y+distanceFromCenter)
            {
                if(ypos<=y-size/2)
                    yIndex=0;
                else if(ypos<=y+size/2)
                    yIndex=1;
                else
                    yIndex=2;
            }
            return yIndex;
        }
    }
    private double radians(int degrees)
    {
        return (degrees*Math.PI/180);
    }
    private double degrees(double radians)
    {
        return (radians*180/Math.PI);
    }
}
