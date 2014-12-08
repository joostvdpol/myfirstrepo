package JavaGO;
  
import java.awt.*; 
import java.awt.image.*; 


/**
 *  Goban class
 */
public class Goban extends Canvas implements Constants
{
  /** Game attached with the Goban */
  private Game game;
  private JavaGO javago;
  int size;
  boolean paint_all = true;
  
  private int gap;
  int border = 20;
  // Stone size
  private int stone_size ;

  // Keyboard position
  private int kx, ky, kxp, kyp;

  // Off-screen plan
  private Image offscreen ;	
  private Graphics g_off;
  private int old_width=-1, old_height=-1, old_size=-1;
 
  // default cursor position
  final private int default_position = 4;

 

  /**
   *  init
   */
  public void init( JavaGO param_javago, Game param_game, int param_size )
  {
    if ( debug > 0 ) System.out.println("Goban.init( " + param_javago + ", " +param_game + ", " +param_size + ", " + " )" );

    javago = param_javago;
    
    game   = param_game;
    size   = param_size;

    resetKeyboardPosition();

    repaintAll();
  }

  
  /**
   *  reSize goban
   */
  public void reSize(  )
  {
    if ( debug > 0 ) System.out.println("Goban.reSize( )");

    int w0, w1, h0, h1;
    w0 = size().width ;
    w1 = w0  - (2*border) ;
    h0 = size().height ;
    h1 = h0 - (2*border) ;

    if ( debug > 1 ) System.out.println("Goban.reSize : w0 = " + w0 + ", h0 = " + h0 );
    if ( debug > 1 ) System.out.println("Goban.reSize : w1 = " + w1 + ", h1 = " + h1 );
    if ( debug > 1 ) System.out.println("Goban.reSize : border = " + border + ", size = " + size );

    // No resize needed
    if (  ( w1 == old_width ) && ( h1 == old_height )  && ( old_size == size )  ) return;
    
   	/** Gap between 2 lines calculation */
    if ( w1*rg > h1*rp )
      gap =  ( h1 * rp ) / ( size * rg );
    else
      gap =  w1 / size ; 

    /** Stone size calculation */
    stone_size = ( gap * rp ) / rg ;

    /** Initialize stone images at that size */
    javago.initAtSize( stone_size );
    
    // offscreen image creation at that size
    offscreen = createImage ( w0, h0 );
    g_off = offscreen.getGraphics() ;

    if ( debug > 0 ) System.out.println("Goban.reSize : gap = " + gap + ", stone_size = " + stone_size );

    old_width  = w1;
    old_height = h1;
    old_size   = size;

    // javago.appendTextln("Resize Goban : " + w0 + ", " + h0 );
  }


  /**
   *  goban paint
   */
  public void paint(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("Goban.paint( g )");

    // resize if needed
    reSize ( );

    // draw background
    JavaGO.drawBackground(g_off, size(), this);

    // draw goban lines
    drawLines(g_off);

    // draw coordinates if not in full window (border=0)
    // always draw
    // if ( border != 0 )  
    drawCoordinates(g_off);
    
    drawOshis(g_off);

    game.setState();
    if ( game.scoring ) 
    {
      game.scorer();
      drawTerritories(g_off);
    }

    drawStones(g_off);

    drawStoneCursor( g_off, getLastX(), getLastY() );
    drawKeyboardCursor( g_off, kx, ky );

    // redraw score
    javago.info_score.repaint();

    // set node number in ># string field
    javago.control_game.setNodeField( game.getNode() );
    
    /** Display of the image generated offscreen */
    g.drawImage( offscreen, 0, 0, this );

    requestFocus();
  }


  /**
   *   goban update
   */
  public void update( Graphics g ) 
  {
    if ( debug > 0 ) System.out.println("Goban.update( g )");

    // if everything is needed to be paint
    if ( paint_all || game.paint_all )
    {
        paint ( g ); paint_all = false; game.paint_all = false;
    }
    else
    // just paint the new stone
    {
      // redraw score, with who to play
      javago.info_score.repaint();
    
      // keyboard focus changed
      if (  ( kxp != kx ) || ( kyp != ky )  )
      {  
        if ( debug > 0 ) System.out.println("Goban.update : keyboard focus changed");
        if ( debug > 0 ) System.out.println("Goban.update : kx  = " + kx  + " ky  = " + ky  );
        if ( debug > 0 ) System.out.println("Goban.update : kxp = " + kxp + " kyp = " + kyp );

        // remove previous keyboard cursor, by drawing a black one
        drawCursor ( g_off,  kxp,  kyp ) ;
        
        // redraw of previous keyboard intersection
        drawIntersection ( g_off, kxp, kyp ) ;
      }


      // new move
      {
        Move m;
        
        // set node number in ># string field
        javago.control_game.setNodeField( game.getNode() );

        if ( debug > 0 ) System.out.println("Goban.update : new stone");
        if ( debug > 0 ) System.out.println("Goban.update : lx  = " + getLastX()  + " ly  = " + getLastY()  );
        if ( debug > 0 ) System.out.println("Goban.update : lxp = " + getPreviousX() + " lyp = " + getPreviousY() );

        // remove previous stone cursor, by redrawing previous stone if alive
        if ( game.getNode() >  1 )
        {
          m = game.move[game.getNode()-1];

          // previous stone cursor removal, by drawing a black one
          drawCursor ( g_off, m.getX(), m.getY() ) ;

          if ( m.isAlive( game.getNode() )  )  drawStone( g_off, m );
        }

        if ( game.getNode() >=  1 )
        {
          m = game.move[game.getNode()];
          if ( m.isAlive( game.getNode() )  )
          {
            if ( m.isPass() )
            {
              // if pass default cursor drawing
              resetKeyboardPosition();
            }
            else
            {
              // last stone drawing
              drawStone( g_off, m );

              // last stone cursor drawing
              drawStoneCursor ( g_off, m.getX(), m.getY() ) ;
            }

          }
        }
      }

      // keyboard cursor drawing
      drawKeyboardCursor ( g_off,  kx,  ky ) ;
    }
    kxp = kx; kyp = ky;

    /** Display of the image generated offscreen */
    g.drawImage( offscreen, 0, 0, this );

    requestFocus();
  }
  

  /**
   *   repaint all the goban
   */
  public void repaintAll()
  {
    // Debug message
    if ( debug > 0 ) System.out.println("Goban.repaintAll( )");

    paint_all = true;
    repaint();
  }


    
  /**
   *  calculation of move coordinates after mouse click
   */
  public boolean mouseDown(Event e, int x, int y) 
  {
    if ( debug > 0 ) System.out.println("Goban.mouseDown( e, " + x + " , " + y + ")");

    int mx, my ;
    
    // Calcul position
    mx =    ( x - border )         /   gap        ;
    my = (  ( y - border ) * rp  ) / ( gap * rg ) ;

    if ( debug > 0 ) System.out.println("Goban.mouseDown mx/my = " + mx + " , " + my );

    // deal with that move
    actionMove ( mx, my );

    return true;
  }

  
  /**
   *  keyDown
   */
  public boolean keyDown( Event e, int key )
  {
    if ( debug > 0 ) System.out.println("Goban.keyDown( e, " + key + " )" );

    switch(key)
    {
      // UP
      case Event.UP:    if ( ky-1  >= 0   ) ky --;  else ky = size-1;
                        if ( e.shiftDown() )
                        {
                          while ( game.p[kx][ky].isStone() )
                          {
                              if ( ky-1  >= 0   ) ky --;  else ky = size-1;
                          }
                        }
                        break;
                        
      // DOWN
      case Event.DOWN:  if ( ky+1  < size ) ky ++; else ky = 0;
                        if ( e.shiftDown() )
                        {
                          while ( game.p[kx][ky].isStone() )
                          {
                              if ( ky+1  < size ) ky ++; else ky = 0;
                          }
                        }
                        break;

      // RIGHT
      case Event.RIGHT: if ( kx+1  < size ) kx ++; else kx = 0;
                        if ( e.shiftDown() )
                        {
                          while ( game.p[kx][ky].isStone() )
                          {
                              if ( kx+1  < size ) kx ++; else kx = 0;
                          }
                        }
                        break;

      // UP
      case Event.LEFT:  if ( kx-1  >= 0   ) kx --; else kx = size-1;
                        if ( e.shiftDown() )
                        {
                          while ( game.p[kx][ky].isStone() )
                          {
                              if ( kx-1  >= 0   ) kx --; else kx = size-1;
                          }
                        }
                        break;
                        
      // Return ou ESPACE
      case 32 :
      case 10 :         actionMove ( kx, ky ); break;

      // "P" or "p" for pass
      case 112 :
      case  80 :        actionPass(); break;

      // "R" or "r" for refresh
      case 114 :
      case  82 :        repaintAll(); break;

      // "N" or "n" for new
      case  110 :
      case   78 :       javago.newGame(); break;

      // "X" or "x" for full window
      case 120 :
      case  88 :        javago.fullWindow(); break;

      // "O" for floating window
      case 111 :
      case  79 :        javago.floatWindow(); break;

      // "F" or "f" for first move
      case 102 :
      case  70 :        setNode( first_node ); break;

      // ">" or "W"  or "w"  for next move
      case 119 :
      case  87 :
      case  62 :        setNode( next_node ); break;

      // "<" for previous move
      case  60 :        setNode( previous_node );  break;

      // "L" or "l" for last move
      case 108 :
      case  76 :        setNode( last_node ); break;


    }

    if ( debug > 2 )  System.out.println("kx = " + kx + "  ky = " + ky );

    repaint();
    return true;
  }


  /**
   *  action move
   */
  public void actionMove( int x, int y )
  {
    if ( debug > 0 ) System.out.println("Goban.actionMove( " + x + ", " + y + " )");

    boolean click=true;
    
    // verify that the move is valid
    if (  ( x < size ) && ( y < size ) && ( x >= 0 ) && ( y >= 0 )  )
    {
      // valid move
      if ( game.actionMove ( x, y ) ) 
      {
        // ok sound effect  
        JavaGO.okPlay();
      
        kx = x; ky = y;
        repaint(); 
      }
      // illegal move
      else click = false ;
    }
    // move not on the board
    else click = false ;

    if ( ! click ) JavaGO.koPlay();
  }


  /**
   *  action pass
   */
  public void actionPass( )
  {
    if ( debug > 0 ) System.out.println("Goban.actionPass( )");

    game.actionPass ( );
    JavaGO.okPlay();
    resetKeyboardPosition();
    repaintAll(); 
  }


  /**
   *  set node 
   */
  public void setNode( int n )
  {
    if ( debug > 0 ) System.out.println("Goban.setNode( " + n + " )");

    if (  game.setNode( n )  )
    {
      kx = getLastX(); ky = getLastY();
      kxp = kx; kyp = ky;
      JavaGO.okPlay();
      repaintAll(); 
    }
    else  JavaGO.koPlay();
  }


  /**
   *  Draw all the stones
   */
  public void drawStones( Graphics g )
  {
    if ( debug > 0 ) System.out.println("Goban.drawStones( g )");

    int node = game.getNode();
    Move move_i;

    // game.setIntersections();
    for ( int i=1; i <= node; i++) 
    {
      move_i = game.move[i];
      
      if ( move_i.isStone( node )  )
      {
        // draw stone
        drawStone( g, move_i );
      }
    }
  }


  /**
   *  Draw one stone
   */
  public void drawStone( Graphics g, Move m )
  {
    if ( debug > 2 ) System.out.println("Goban.drawStone( g )");

    // draw stone if alive
    JavaGO.drawStoneImage( g,
                           m.getColor(),
                           posx(m.getX())-stone_size/2,
                           posy(m.getY())-stone_size/2,
                           stone_size,
                           this                            );
  }

  
  /**
   *  Draw one intersection
   */
  public void drawIntersection( Graphics g, int x, int y )
  {
    if ( debug > 0 ) System.out.println("Goban.drawIntersection( " + x + ", " + y + " )");

    Intersection intersection = game.p[x][y];

    if ( intersection.isStone( ) )
    {
      JavaGO.drawStoneImage( g, intersection.getType(),
                             posx(x)-stone_size/2, posy(y)-stone_size/2, stone_size, this );
    }
  }


  /**
   *  draw goban lines
   */
  public void drawLines(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("Goban.drawLines( )");

    g.setColor(Color.black);
    for (int i=0; i < size; i++) 
    {
      g.drawLine (  posx(i), posy(0), posx(i), posy(size-1)  );
      g.drawLine (  posx(0), posy(i), posx(size-1), posy(i)  );
    }
  }


  /**
   *  draw goban coordinates 
   */
  public void drawCoordinates(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("Goban.drawCoordinates( )");

    g.setColor(Color.black);
    for (int i=0; i < size; i++) 
    {
      drawCoordinate( g, i, i );
    }
  }

  
  /**
   *  draw x,y goban coordinates 
   */
  public void drawCoordinate(Graphics g, int x, int y) 
  {
    int t = stone_size / 2;

    // no white coordinate
    if ( g.getColor() != Color.green ) g.setColor(Color.black);

    g.drawString (  ""+coord_no_I.charAt(x), posx(x)-4, posy(0)      -t -4 );
    g.drawString (  ""+coord_no_I.charAt(x), posx(x)-4, posy(size-1) +t +14 );

    if ( (size-y) <= 9 ) g.drawString (  ""+(size-y), posx(0)      -t -14, posy(y)+4    );
    else                 g.drawString (  ""+(size-y), posx(0) -t -18     , posy(y)   +4 );

    if ( (size-y) <= 9 ) g.drawString (  ""+(size-y), posx(size-1) +t +10, posy(y)+4    );
    else                 g.drawString (  ""+(size-y), posx(size-1) +t+4  , posy(y) +4   );
  }


  /**
   *  Draw territories
   */
  public void drawTerritories( Graphics g )
  {
    if ( debug > 0 ) System.out.println("Goban.drawTerritories( )");

    Intersection pp;
    
    for ( int ix=0; ix < size; ix++) 
    {
      for ( int iy=0; iy < size; iy++) 
      {
        pp = game.p[ix][iy];

        if ( debug > 0 ) System.out.println("Goban.drawTerritories : ix = " + ix + " , iy = " + iy + " > " + pp.getType() );
        
        if ( pp.isEmpty() )
        { 
          // Territory color
          if      ( pp.isTerr_black() ) g.setColor( Color.black );
          else if ( pp.isTerr_white() ) g.setColor( Color.white );
          else if ( pp.isDame () )      g.setColor( Color.green );
          // error !
          else                          g.setColor( Color.red   );

          g.fillOval( posx(ix)-4, posy(iy)-4, 9, 9 );
          g.setColor(Color.black);
        }
      }
    }
  }


  /**
   *  draw all the Oshis
   */
  public void drawOshis( Graphics g )
  {
    if ( debug > 0 ) System.out.println("Goban.drawOshis( g )"  );

	if ( size == 19 )
	{
		for( int i=3; i<19; i=i+6 )
			for( int j=3; j<19; j=j+6 )
				drawOshi(g, i, j);
	}
	else if (  size >= 10 && size <= 19 )
	{
		drawOshi( g,        3,        3 );
		drawOshi( g,        3, size - 4 );
		drawOshi( g, size - 4,        3 );
		drawOshi( g, size - 4, size - 4 );
	}
	else if (  size >= 7 && size <= 9 )
	{
		drawOshi( g, 	    2,        2 );
		drawOshi( g,        2, size - 3 );
		drawOshi( g, size - 3,        2 );
		drawOshi( g, size - 3, size - 3 );
	}
  }


  /**
   *  draw one Oshi
   */
  public void drawOshi( Graphics g, int x, int y)
  {
    if ( debug > 3 ) System.out.println("Goban.drawOshi( g, " + x + " , " + y + ")");

    g.fillOval( posx(x)-2, posy(y)-2, 5, 5 );
  }
  

  /**
   *  Draw one cursor
   */
  public void drawCursor( Graphics g, int x, int y )
  {
	if ( debug > 0 ) System.out.println("Goban.drawCursor( g, " + x + " , " + y + ")");

	// parameters test
	if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return ;

    int t1, t2, t3, t4;
	int t = stone_size/4;
    t1 = t2 = t3 = t4 = t;

    // Redraw
    if ( game.p[x][y].isEmpty() )
    {
      if ( x == 0      ) t1 = 0;
      if ( x == size-1 ) t3 = 0;
      if ( y == 0      ) t2 = 0;
      if ( y == size-1 ) t4 = 0;
    }
    
    // Cross drawing, possibly cut at the edges
	g.drawLine (  posx(x)-t1, posy(y)   , posx(x)+t3, posy(y)    ) ; 
	g.drawLine (  posx(x)   , posy(y)-t2, posx(x)   , posy(y)+t4 ) ; 

    drawCoordinate( g, x, y);
	g.setColor(Color.black);
  }


  /**
   *  Draw stone cursor, call drawCursor at the end
   */
  public void drawStoneCursor( Graphics g, int x, int y )
  {
	if ( debug > 0 ) System.out.println("Goban.drawStoneCursor( g, " + x + " , " + y + ")");

	// parameters test
	if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return ;

	if 	    ( game.p[x][y].isBlack() )	g.setColor(Color.white);
	else if ( game.p[x][y].isWhite() )	g.setColor(Color.black);
	else return;

    drawCursor ( g, x, y );
  }


  /**
   *  Draw keyboard cursor, call drawCursor at the end
   */
  public void drawKeyboardCursor( Graphics g, int x, int y )
  {
	// Message pour debug
	if ( debug > 0 ) System.out.println("Goban.drawKeyboardCursor( g, " + x + " , " + y + ")");

	// Test les parameters
	if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return ;

    g.setColor(Color.green);
    drawCursor ( g, x, y );
  }


  /**
   *  Give X position in pixel for X position on the Goban
   */
  public int posx( int x )
  {
    int cr = gap*x + gap/2 + border ;
    
	if ( debug > 3 ) System.out.println("Goban.posx ( " + x + ") = " + cr );

	return ( cr );
  }


  /**
   *  Give Y position in pixel for Y position on the Goban
   */
  public int posy( int y )
  {
    int cr = (  ( gap*y + gap/2 ) * rg ) / rp  + border ;
    
	if ( debug > 3 ) System.out.println("Goban.posy ( " + y + ") = " + cr );

	return ( cr );
  }


  /**
   *  get last X position 
   */
  public int getLastX( )
  {
    int ret;
    
    if ( game.getNode() < 1 )   ret = default_position;
    else                        ret = game.move[ game.getNode() ].getX(); ;
    
    if ( ret < 0 ) ret = default_position;

    if ( debug > 3 ) System.out.println("Goban.getLastX( ) = " + ret );

    return ( ret );
  }


  /**
   *  get last Y position 
   */
  public int getLastY( )
  {
    int ret;
    
    if ( game.getNode() < 1 )   ret = default_position;
    else                        ret = game.move[ game.getNode() ].getY(); ;
    
    if ( ret < 0 ) ret = default_position;

	if ( debug > 3 ) System.out.println("Goban.getLastY( ) = " + ret );

	return ( ret );
  }


  /**
   *  get previous X position 
   */
  public int getPreviousX( )
  {
    int ret;

    if ( game.getNode() <= 1 )  ret = default_position;
    else                        ret = game.move[ game.getNode() - 1 ].getX(); ;
    
    if ( ret < 0 ) ret = default_position;

	if ( debug > 3 ) System.out.println("Goban.getPreviousX( ) = " + ret );

	return ( ret );
  }


  /**
   *  get previous Y position 
   */
  public int getPreviousY( )
  {
    int ret;

    if ( game.getNode() <= 1 )  ret = default_position;
    else                        ret = game.move[ game.getNode() - 1 ].getY(); ;
    
    if ( ret < 0 ) ret = default_position;

  	if ( debug > 3 ) System.out.println("Goban.getPreviousY( ) = " + ret );

	return ( ret );
  }

    
  /**
   *  reset keyboard position
   */
  public void resetKeyboardPosition()
  {
  	if ( debug > 3 ) System.out.println("Goban.resetKeyboardPosition( )");

    kx  = default_position;
    ky  = default_position;
    kxp = default_position;
    kyp = default_position;
  }

}


