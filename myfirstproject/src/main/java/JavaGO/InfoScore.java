package JavaGO;
  
import java.awt.*; 
import java.awt.image.*; 


/**
 *  InfoScore class
 */
public class InfoScore extends Canvas implements Constants
{
  // size of stones for InfoScore
  final private int stone_size = 40;

  // game on which score is given
  private Game game;
  
  // off-screen plan
  private Image offscreen ;	
  private Graphics g_off;

  // old size of canvas, to know if resize is needed
  private int old_width=-1, old_height=-1;

  /**
   *  constructor
   */
  InfoScore( Game param_game )
  {
    if ( debug > 0 ) System.out.println("InfoScore.InfoScore()" );

    // fixed size of the InfoScore canvas
    resize ( 50, 390 );

    game = param_game;
  }

  
  /**
   *  paint
   */
  public void paint(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("InfoScore.paint( )");

    // resize if needed
    reSize( );

    // draw canvas background
    JavaGO.drawBackground(g_off, size(), this);

    // draw deads count
    drawDeads(g_off);


    // if scoring draw territories and winner
    if ( game.scoring )
    {
      drawTerritories(g_off);
      drawWinner(g_off);
    }
    else
    {
      // draw next and previous move
      drawNext(g_off);
      drawPrev(g_off);
    }

    /** Display of the image generated offscreen */
    g.drawImage( offscreen, 0, 0, this );
  }


  /**
   *  update
   */
  public void update(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("InfoScore.update( )");

    // simply repaint all
    paint (g);
  }


   /**
   *  reSize InfoScore
   */
  public void reSize(  )
  {
    if ( debug > 0 ) System.out.println("InfoScore.reSize( )");

    // size of the InfoScore canvas
    int w = size().width;
    int h = size().height;
    if ( debug > 1 ) System.out.println("Infoscore.reSize : w= " + w + ", h = " + h );

    // resize needed ?
    if (  ( w == old_width ) && ( h == old_height )  ) return;

    // new offscreen creation
    offscreen = createImage ( w, h );
    g_off = offscreen.getGraphics() ;

    // size saving
    old_width  = w;
    old_height = h;
  }


  /**
   *  draw count of deads stones for each player
   */
  public void drawDeads( Graphics g)
  {
    if ( debug > 0 ) System.out.println("InfoScore.drawDeads ( g ) " );

    // draw dead stones for black and white 
    JavaGO.drawStoneImage( g, black, 2, 20, stone_size, this );
    JavaGO.drawStoneImage( g, white, 2, 60, stone_size, this );
    g.drawString("Dead", 2, 115  );

    // draw dead count for white and black
    g.setColor( Color.white );    
    drawNumberInStone( g, game.getDeads(black), 45 );
    g.setColor( Color.black );    
    drawNumberInStone( g, game.getDeads(white), 85 );
  }


  /**
   *  draw count of territories for each players
   */
  public void drawTerritories( Graphics g)
  {
    if ( debug > 0 ) System.out.println("InfoScore.drawTerritory ( g ) " );

    // draw territory stones for black and white 
    JavaGO.drawStoneImage( g, black, 2, 160, stone_size, this );
    JavaGO.drawStoneImage( g, white, 2, 200, stone_size, this );
    g.drawString("Territory", 2, 255 );
  
    // draw territory count for white and black
    g.setColor( Color.white );    
    drawNumberInStone( g, game.getTerritory(terr_black), 185 );
    g.setColor( Color.black );    
    drawNumberInStone( g, game.getTerritory(terr_white), 225 );
  }


  /**
   *  next move
   */
  public void drawNext( Graphics g)
  {
    if ( debug > 0 ) System.out.println("InfoScore.drawToPlay ( g ) " );

    // draw which color has to play
    JavaGO.drawStoneImage( g, game.otherColor( game.getPlayer() ), 2, 160,  stone_size, this );

    // draw node numer
    // if ( game.getPlayer() == white )  g.setColor( Color.white );
    // int n = ( game.getNode()+ 1 );
    // drawNumberInStone( g, n, 185 );

    g.setColor( Color.black );    
    g.drawString( "Next" , 6, 155 );
  }


  /**
   *  previous move
   */
  public void drawPrev( Graphics g)
  {
    if ( debug > 0 ) System.out.println("InfoScore.drawPrevious( g ) " );

    int node = game.getNode();
    if ( node >= 1) 
    {
      // draw which color has played
      JavaGO.drawStoneImage( g, game.getPlayer(), 2, 200,  stone_size, this );

      // draw coordinates
      if ( game.getPlayer() == black )  g.setColor( Color.white );

      String prev = game.move[node].getCoord( game.getSize() );
      if      ( prev.length() == 2 ) g.drawString(  prev, 13, 225 );
      else if ( prev.length() == 3 ) g.drawString(  prev,  9, 225 );
      else                           g.drawString(  prev,  7, 225 );
  
      g.setColor( Color.black );    

      g.drawString( "Prev" , 6, 255 );
    }
  }


  /**
   *  Draw who wins
   */
  public void drawWinner( Graphics g)
  {
    if ( debug > 0 ) System.out.println("InfoScore.drawWinner ( g ) " );

    if ( game.getScore() > 0 )
    // White wins !
    {
      JavaGO.drawStoneImage( g, white, 2, 300,  stone_size, this );
      drawNumberInStone( g, game.getScore() , 325 );
    }
    else
    // Black wins !
    {
      JavaGO.drawStoneImage( g, black, 2, 300, stone_size, this );
      g.setColor( Color.white );    
      drawNumberInStone( g, -game.getScore(), 325 );
      g.setColor( Color.black );    
    }
    g.drawString("Winner !", 2, 355 );

  }


  /**
   *  draw int number in stone
   */
  public void drawNumberInStone( Graphics g, int n, int y )
  {
    int pos;

    if      ( n < 10   ) pos = 18;
    else if ( n < 100  ) pos = 14;
    else                 pos = 12;
    
    if ( debug > 2 ) System.out.println("InfoScore.pos( " + n + " ) = " + pos );

    g.drawString( "" + n, pos, y );
  }


  /**
   *  draw double number in stone
   */
  public void drawNumberInStone( Graphics g, double d, int y )
  {
    int pos;

    if      ( d < -99  ) pos =  2;
    else if ( d <  -9  ) pos =  4;
    else if ( d <   0  ) pos =  7;
    else if ( d <  10  ) pos = 12;
    else if ( d < 100  ) pos =  8;
    else                 pos =  4;
    
    if ( debug > 2 ) System.out.println("InfoScore.pos( " + d + " ) = " + pos );

    g.drawString( "" + d, pos, y );
  }

}


