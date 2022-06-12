import java.awt.Dimension;
import javax.swing.JFrame;
public class tttApp extends JFrame{
    private Dimension d;
    private tttGame game;
    tttApp(String title)
    {
        super();
        d=new Dimension();
        setPreferredSize(new Dimension(500,500));
        setSize(new Dimension(500,500));
        setTitle(title);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        d=this.getSize();
        game=new tttGame(d);
        add(game);
        setVisible(true);
        pack();
    }
    public static void main(String[] args)
    {
        tttApp app= new tttApp("Tic Tac Toe");
    }
}
