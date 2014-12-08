package JavaGO;

  
/**
 *  Game class
 */
public class Game implements Constants
{    
  // game size 
  private int size;
  
  // Komi and relative white score, negative if black wins
  private double komi, score;

  // Intersection table,
  Intersection  p[][] ; 

  // moves sequence
  Move[] move;

  // Mark table for liberty treatment
  private boolean mark[][] ;

  // Territories of each type
  private int territory[] = new int[MAX_INTERSECTION_TYPES];
    

  // scoring ?
  boolean scoring;

  // current node
  private int node=0;

  // repaint all flag
  boolean paint_all = true;

  String info;
  
  /*
   * Javago applet
   */
  private JavaGO javago;


  /**
   *  init
   */
  public void init( JavaGO param_javago, int param_game_size, int param_handicap, double param_komi  )
  {
    if ( debug > 0 ) System.out.println("Game.init()" );

    javago   = param_javago;
    
    size     = param_game_size;
    komi     = param_komi;

    javago.appendTextln("Size : " + size + " | Komi : " + komi 
					 + " | Handicap : " + param_handicap );

    p    = new Intersection [size][size]; 
    mark = new boolean      [size][size];

    // new move sequence 
    node = 0;
    move = new Move[MAX_STONES];

    // set handicaps
    setHandicaps( param_handicap );
    
    // set intersections table
    setState();

    // init mark table 
    initMark();   

    // score is intialised to komi value
    score = komi ;

    info = "";
  }


  /**
   *   Processing of a possible new move
   */
  public boolean actionMove( int x, int y ) 
  {
    boolean ok = true;

    Intersection pp = p[x][y];

    if ( debug  > 0 ) System.out.println(
            "Game.actionMove(  " + x + " , " + y + ") on " + pp.getType ()+ "/" + pp.getNode() );

    // scoring ?
    if ( scoring ) 
    { 
      if ( debug  > 0 ) System.out.println("Game.actionMove : scoring... ");

      // stone to remove ?
      if (  pp.isStone() ) 
      {        
        if ( debug  > 0 ) System.out.println("Game.actionMove : remove deads ");

        // remove dead chain
        remove( x, y ) ;
	
     	paint_all = true;
      }
      // bad click
      else ok = false ;
    }
    // playing 
    else if (  pp.isEmpty()  )
    {
      if ( debug  > 0 ) System.out.println("Game.actionMove : empty intersection... ");

      // is there any KO
      if ( ! ko( x, y ) ) 
      {
        if ( debug  > 0 ) System.out.println("Game.actionMove : no ko, new move ");
  
        newStoneMove( x, y );
        
        ok = true;
           
        // any deads ?
        if ( ( isDead( x  , y   ) ) ||
             ( isDead( x-1, y   ) ) ||
             ( isDead( x  , y-1 ) ) ||
             ( isDead( x+1, y   ) ) ||
             ( isDead( x  , y+1 ) )     )
        {
          // remove dead stones of next player
          removeDeads( otherColor( getPlayer() ) );

          // remove dead stones of current player (suicide ?)
          removeDeads( getPlayer() );

          paint_all = true;
        }
      }
      else ok = false ;
    }
    else ok = false;

    return ok ;
  }

  
  /**
   *  new stone move (without color, normal move)
   */
  public void newStoneMove( int x, int y )
  {
    if ( debug > 0 ) System.out.println("Game.newStoneMove( " + x + ", " + y + " )"  );

    newStoneMove( x, y, otherColor(getPlayer()) );
  }


  /**
   *  new stone move (direct call by handicap() )
   */
  public void newStoneMove( int x, int y, int color)
  {
    if ( debug > 0 ) System.out.println("Game.newStoneMove( " + x + ", " + y + ", " + color + " )"  );

    if (  ( node == 0 ) && ( info.length() != 0 )  ) javago.appendTextln( info );
    
    // registration
    node++;
    
    // if already played here, reset stone dead after this stone
    if ( move[node] != null )  resetDeads();

    // if      ( color == white ) javago.appendTextln("n°" + node + " : White["+ Move.getCoord(x,y,size)+ "]" );
    // else if ( color == black ) javago.appendTextln("n°" + node + " : Black["+ Move.getCoord(x,y,size)+ "]" );
    javago.appendText(".");

    // move registration
    move[node]   = new Move( x, y, color );
    move[node+1] = null;
    
    setState();

    if ( debug > 0 ) System.out.println(
        "Game.newStoneMove : ( " + x + ", " + y + " )  " + node + ", " + color );


  }


  /**
   *  Processing of possible new pass move
   */
  public void actionPass ()
  {
    if ( debug > 0 ) System.out.println("Game.actionPass()" );

    // if not scoring new pass move
    if ( ! scoring ) newPassMove();
  }


  /**
   *  Processing of new pass move
   */
  public void newPassMove ()
  {
    if ( debug > 0 ) System.out.println("Game.newPassMove()" );

      int color = otherColor( getPlayer() );

      // registration
      node++;
    
      if      ( color == white ) javago.appendTextln("White pass");
      else if ( color == black ) javago.appendTextln("Black pass");

      // move registration
      move[node] = new Move( color );
      move[node+1] = null;
  }


  /**
   *  set intersection data table
   */
  public void setState() 
  {
    if ( debug > 0 ) System.out.println("Game.setState()" );

    // reset all stone intersections to unknown
    for ( int ix=0; ix < size; ix++) 
    {
      for ( int iy=0; iy < size; iy++) 
      {
        if ( p[ix][iy] == null ) p[ix][iy] = new Intersection();
        else
        {
          p[ix][iy].setType(unknown);
        }
      }
    }

    // set moves
    for ( int i=1; i <= node; i++) 
    {
      if ( move[i].isStone( node )  )
      {
        // set intersection
        p[move[i].getX()][move[i].getY()].setNode( i, move[i].getColor() );                                     
      }
    }

    // if already scoring, stop
    if ( scoring ) scoring = false;
    // scoring if last move and previous move are pass
    else scoring = (  (node > 1) && move[node].isPass() && move[node-1].isPass()  ) ;
  }


  /**
   *  Init of Handicaps stones
   */
  public void setHandicaps( int handicap ) 
  {
    if ( debug > 0 ) System.out.println("Game.setHandicaps() : " + handicap );

    if ( handicap < 2 ) return;

    // handicap count
    int i=0;

    if ( size == 19 )
    {
      if ( handicap > 17 ) handicap = 17;

      // oshis positions
      if (    handicap >= 4 ) move[++node] = new Move( 3, 3, black);            
      if (    handicap >= 8 ) move[++node] = new Move( 3, 9, black);            
                              move[++node] = new Move( 3,15, black);            
      if (    handicap >= 6 ) move[++node] = new Move( 9, 3, black);            
      if (  ( handicap >= 9 ) || ( handicap == 7 ) ||
            ( handicap == 5 ) || ( handicap == 3 )     )
                              move[++node] = new Move( 9, 9, black);            
      if (    handicap >= 6 ) move[++node] = new Move( 9,15, black);            
                              move[++node] = new Move(15, 3, black);            
      if (    handicap >= 8 ) move[++node] = new Move(15, 9, black);            
      if (    handicap >= 4 ) move[++node] = new Move(15,15, black);            

      // other positions
      if ( handicap >= 10 ) move[++node] = new Move(   5,  2, black); 
      if ( handicap >= 11 ) move[++node] = new Move(   2,  5, black); 
      if ( handicap >= 12 ) move[++node] = new Move(  13, 16, black); 
      if ( handicap >= 13 ) move[++node] = new Move(  16, 13, black); 
      if ( handicap >= 14 ) move[++node] = new Move(   5, 16, black); 
      if ( handicap >= 15 ) move[++node] = new Move(   2, 13, black); 
      if ( handicap >= 16 ) move[++node] = new Move(  13,  2, black); 
      if ( handicap >= 17 ) move[++node] = new Move(  16,  5, black); 
    }
    else if (  ( size >= 13 ) && ( size <= 19 )  )
    {
      if ( handicap > 12 ) handicap = 12;

      if (    handicap >= 3 ) move[++node] = new Move(      3,     3,black);            
                              move[++node] = new Move(      3,size-4,black);            
                              move[++node] = new Move( size-4,     3,black);            
      if (    handicap >= 4 ) move[++node] = new Move( size-4,size-4,black);            

      if ( handicap == 12 )
      {
         move[++node] = new Move(      5,     2,black); move[++node] = new Move(      2,     5,black);
         move[++node] = new Move(      5,size-3,black); move[++node] = new Move(      2,size-6,black);
         move[++node] = new Move( size-6,     2,black); move[++node] = new Move( size-3,     5,black);
         move[++node] = new Move( size-6,size-3,black); move[++node] = new Move( size-3,size-6,black);
      }
    }
    else if (  ( size >= 10 ) && ( size <= 13 )  )
    {
      if ( handicap > 4 ) handicap = 4;

      if (    handicap >= 3 ) move[++node] = new Move(      3,     3,black);            
                              move[++node] = new Move(      3,size-4,black);            
                              move[++node] = new Move( size-4,     3,black);            
      if (    handicap == 4 ) move[++node] = new Move( size-4,size-4,black);            
    }
    else if (  ( size >= 7 ) && ( size <= 9 )  )
    {
      if ( handicap > 4 ) handicap = 4;

      if (    handicap >= 3 ) move[++node] = new Move(      2,     2,black);            
                              move[++node] = new Move(      2,size-3,black);            
                              move[++node] = new Move( size-3,     2,black);            
      if (    handicap == 4 ) move[++node] = new Move( size-3,size-3,black);            
    }
  }


  /**
   *  init of mark data table
   */
  public void initMark()
  {
    if ( debug > 2 ) System.out.println("Game.initMark()" );

    for ( int ix=0; ix < size; ix++) 
    {
        for ( int iy=0; iy < size; iy++) 
        {
            mark[ix][iy] = false;
        }
    }
  }


  /**
   *  Deads removal for one color
   */
  public void removeDeads( int color )
  {
    int ix, iy;

    if ( debug > 0 ) System.out.println("Game.removeDeads( " + color + " )");

    Intersection pp;
    
    // Dead stones table
    boolean mort[][] = new boolean [size][size];

    // Dead stones seeking
    for ( int i=1; i <= node; i++) 
    {
      if ( move[i].isStone( node, color ) )
      {
        ix = move[i].getX();
        iy = move[i].getY();
        mort[ix][iy] = isDead( ix, iy );
      }
    }

    // Number of dead stones
    int n=0;

    // Dead stone removal
    for ( int i=1; i <= node; i++) 
    {
      // if stone
      if ( move[i].isStone( node, color ) )
      {
        ix = move[i].getX();
        iy = move[i].getY();
        if ( mort[ix][iy] ) 
        {
          if ( debug > 0 ) System.out.println(
            "Game.removeDeads : ( " + ix + ", " + iy + " ) node " + i + " dead");

          move[i].kill(node);
          n++;
          // if ( n == 1 ) javago.appendTextln("Remove :");
          // javago.appendText( " [" + Move.getCoord( ix, iy, size ) + "]" );
        }
      }
    }
    if ( n > 0 )
    {
      // if ( color == white ) javago.appendTextln( n + " white stones removed");
      // else                  javago.appendTextln( n + " black stones removed");
    }

    setState();
  }


  /**
   *  Returns if this stone is dead
   */
  public boolean isDead( int x, int y)
  {
    // parameter tests
    if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return (false );

    // mark table reset, that allows to deal only once with any
    // intersection to avoid dead locks
    initMark();

    // beeing, is the opposite of beeing alive  ;-)
    boolean ret = !isAlive( x, y );

    if ( debug > 3) System.out.println("Game.isDead ( " + x + " , " + y + ") = " + ret );

    return ( ret  );
  }


  /**
   *  If this function returns true,  the stone is alive.<br>
   *  If this function returns false, the stone status
   *  is undeterminate (for the moment)
   * <p>
   *  <b>CAUTION</b> : isAlive( x, y ) can return false, even if the stone is not dead !
   *  This function <b>MUST</b> be called throw isDead that makes the necessary init
   */
  public boolean isAlive( int x, int y)
  {
    if ( debug > 3 ) System.out.println("Game.isAlive ( " + x + " , " + y + ")");

    // Parameters test
    if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return (false );

    // Mark x,y Intersection as treated
    mark[x][y] = true;

    // test each 4 neighbours, test if one intersection is empty so
    // there is at least one liberty so (x,y) alive
    // either test is the neighbour of the same color is alive (of the
    // same chain)
    
    // Test x-1,y neighbour
    if (x > 0)
    {
        if (    p[x-1][y].isEmpty()                         ) return ( true );
        if (  ( p[x-1][y].getType() == p[x][y].getType() ) && !mark[x-1][y] )
        {
            if ( isAlive(x-1,y) ) return ( true );
        }
     }

     // Test x,y-1 neighbour
     if (y > 0)
     {
        if (    p[x][y-1].isEmpty()                         ) return ( true );
        if (  ( p[x][y-1].getType() == p[x][y].getType() ) && !mark[x][y-1] )
        {
            if ( isAlive(x,y-1) ) return ( true );
        }
     }

     // Test x+1,y neighbour
     if (x < (size-1) )
     {
        if (    p[x+1][y].isEmpty()                         ) return ( true );
        if (  ( p[x+1][y].getType() == p[x][y].getType() ) && !mark[x+1][y] )
        {
            if ( isAlive(x+1,y) ) return ( true );
        }
     }

     // Test x,y+1 neighbour
     if (y < (size-1) )
     {
        if (    p[x][y+1].isEmpty()                         ) return ( true );
        if (  ( p[x][y+1].getType() == p[x][y].getType() ) && !mark[x][y+1] )
        {
            if ( isAlive(x,y+1) ) return ( true );
        }
     }
     return ( false );
  }


  /**
   *  Score calculation
   */
  public double scorer()
  {
    if ( debug > 0 ) System.out.println("Game.scorer ()");

    javago.appendTextln("Scoring...");
    
    // territory color
    int color;

    paint_all = true; 

    if ( debug > 0 ) printAll();
    
    // init counting
    for ( int ix=0; ix < MAX_INTERSECTION_TYPES; ix++) territory[ix]=0;


    // score calculation
    for ( int ix=0; ix < size; ix++) 
    {
      for ( int iy=0; iy < size; iy++) 
      {
        Intersection pp = p[ix][iy];

        // if Intersection is unknown
        if (  pp.isUnknown() )
        {
          // mark table initialisation
          initMark();

          // test which possible type is intersection   
          pp = whichType( pp, ix, iy );
          p[ix][iy].setType( pp.getType() );
          
          if ( debug > 0 ) System.out.println("Game.scorer p[" + ix + "][" + iy + "] = " + pp.getType() );

          // all possibilities tested, transform presumption to certitude
          pp.transform();

          if ( debug > 0 ) System.out.println("Game.scorer p[" + ix + "][" + iy + "] = " + pp.getType() );
        }
        // add one intersection of this type
        territory[ pp.getType() ]++ ;
      }

    if ( debug > 0 ) printAll();
    

    }

    // score calculation
    Integer Tmp = new Integer (  getDeads(black) - getDeads(white) + 
                 territory[terr_white] - territory[terr_black] ) ;
    score = Tmp.doubleValue() + komi ;

    javago.appendTextln("white score = " + score             +
         " = (deads) (" + getDeads(black)   + "-" + getDeads(white)  + 
         ") + (territories) (" + territory[terr_white] +  "-" + territory[terr_black]  + 
         ") + (komi) (" + komi  +")"              );
    
    return ( score );
  }


  /**
   *  Returns the type of this Intersection
   */
  public Intersection whichType( Intersection color, int x, int y )
  {
    if ( debug > 3 ) System.out.println("Game before whichType( " + color.getType() + ", " + x + ", " + y + " )");

    // Return value
    Intersection ret = color ;

    // action only if this intersection is determined 
    if (  ret.isUndetermined () )    
    
    // action only if parameters test ok
    if (  (x >= 0) && (x < size) && (y >= 0) && (y < size)  ) 
    {    
      // action...

      // marks the position
      mark [x][y] = true;

      ret = whichTypeNeighbour( ret, x+1, y );
      if (  ret.isUndetermined () )    
      {
        ret = whichTypeNeighbour( ret, x-1, y );
        if (  ret.isUndetermined () )    
        {
          ret = whichTypeNeighbour( ret, x, y+1 );
          if (  ret.isUndetermined () )    
          {
              ret = whichTypeNeighbour( ret, x, y-1 );
          }
        }
      }
    }
    if ( debug > 2 ) System.out.println("Game after whichType( " + x + ", " + y + " ) = " + ret.getType() );

    return ( ret );
  }


  /**
   *  Returns the type of the neighbour or reprocess the new  position<br>
   *  MUST be called by whichType()
   */
  public Intersection whichTypeNeighbour( Intersection color, int x, int y )
  {
    if ( debug > 3 ) System.out.println("Game before whichTypeNeighbour( " + color.getType() + ", " + x + ", " + y + " )");

    // return value
    Intersection ret = color ;

    // action only if this Intersection is determined 
    if (  ret.isUndetermined () )    

    // action only if parameters test ok
    if (  (x >= 0) && (x < size) && (y >= 0) && (y < size)  ) 
    {
      // action only if not already marked
      if ( ! mark [x][y] )

      // action...

      // if determined, set ret to that value
      if ( p[x][y].isDetermined() ) ret = p[x][y];

      // if black, if previous white then dame else black
      else if ( p[x][y].isBlack() ) 
      if ( ret.isWhite() ) ret.setType(dame); 
      else ret.setType( black );

      // if white, if previous black then dame else white
      else if ( p[x][y].isWhite() ) 
      if ( ret.isBlack() ) ret.setType(dame); 
      else ret.setType( white );

      // if empty (not determined and not black and not white ), process this position
      else ret = whichType ( ret, x, y );
    }
  
    if ( debug > 3 ) System.out.println("Game after  whichTypeNeighbour( " + x + ", " + y + " ) = " + ret.getType() );

    return ( ret ) ;
  }

  
  /**
   *  Remove dead chains
   */
  public void remove( int x, int y )
  {
    if ( debug > 0 ) System.out.println("Game remove ( " + x + ", " + y + " )");

    // parameters test
    if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return ;

    javago.appendTextln("Remove deads :");

    remove_one ( x, y, p[x][y].getType() );

    setState();
  }


  /**
   *  Remove one stone in a dead chain, and then make reentrant call to eventually
   *  remove one or more of the four neighbours of the same chain
   */
  void remove_one( int x, int y, int col )
  {
    if ( debug  > 3 ) System.out.println("Game remove_one ( " + x + ", " + y + ", " + col + " )");

    // parameters test
    if (  (x < 0) || (x >= size) || (y < 0) || (y >= size)  ) return ;

    if ( debug > 0 ) System.out.println("Game.remove_one p[" + x + "][" + y + "].type = " + p[x][y].getType() );

    if ( p[x][y].getType() == col ) 
    {        
      javago.appendText( " [" + Move.getCoord( x, y, size ) + "]" );
      move[nodeXY(x,y)].kill(node);

      p[x][y].setType ( unknown );
      paint_all = true;

      // eventualy remove 4 neighbours
      remove_one ( x-1, y   , col );
      remove_one ( x+1, y   , col );
      remove_one ( x  , y+1 , col );
      remove_one ( x  , y-1 , col );
    }
  }


  /**
   *   set node : <br>
   *    if >= 0 direct acces to node, either : next, previous, <br>
   *    first or last node
   */
  public boolean setNode( int param_node )
  {
    if ( debug > 0 ) System.out.println("Game setNode( " + param_node + " ) " );

    boolean ret=true;

    // direct access to node
    if ( param_node >= 0 )
    {
      // if this node exists
      ret = (  (  move[param_node]  != null ) || ( param_node == 0 )  );
      if ( ret ) node = param_node;
    }

    // next node
    else if ( param_node == next_node )
    {
      // if next move exists
      ret = ( move[node+1] != null );
      if  ( ret )  node++;
    }

    // previous node
    else if ( param_node == previous_node )
    {
      // if previous move exists
      ret = ( node >= 1 );
      if  ( ret )  node--;
    }

    // first node
    else if ( param_node == first_node )
    {
      node = 0;
    }

    // last node
    else if ( param_node == last_node )
    {
      // find last node
      int i ;
      for ( i = 1; move[i] != null ; i++ );
      node = i-1;
    }

    Move m = move[node];
    if ( m != null )
    {
      if      ( m.isPass() )
      {
        if      ( m.getColor() == white ) javago.appendTextln("n°" + node + " : White pass" );
        else if ( m.getColor() == black ) javago.appendTextln("n°" + node + " : Black pass" );
      }
/*
      else
      {
        if      ( m.getColor() == white ) javago.appendTextln("n°" + node + " : White["+ Move.getCoord(m.getX(),m.getY(),size)+ "]" );
        else if ( m.getColor() == black ) javago.appendTextln("n°" + node + " : Black["+ Move.getCoord(m.getX(),m.getY(),size)+ "]" );
      }
*/
      if  (   ! m.info.equals("")  )  javago.appendTextln( move[node].info );
    }
    if ( ret ) setState();

    return ret;
  }


  /**
   *   Returns true if KO
   */
  public boolean ko( int x, int y )
  {
    if ( node <= 1 ) return false;

    boolean ret = false;

    int lxp   = move[node  ].getX();
    int lyp   = move[node  ].getY();

    // color of new move
    int color = otherColor ( move[node  ].getColor() );

    
    if ( debug > 0 ) System.out.println("Game KO : x/y    " + x    + " / " + y    + " ?" );
    if ( debug > 0 ) System.out.println("Game KO : lxp/lyp   " + lxp   + " / " + lyp   + " ?" );

    // AND if play where ONE stone has been killed move just before
    ret = possibleKO( x, y, node, color) ;

    if ( ret )
    {
      // KO if last stone is the ONLY killed stone for this move

      if ( debug > 0 ) System.out.println("Game KO : color " + color  );

      boolean ko=true;
      
      if (  ( (lxp + 1) < size )  &&  ! (  ( (lxp + 1) == x ) && ( lyp == y )  )    )
            if ( p[lxp+1][lyp].getType() != color )  ko = false;
      if ( debug > 0 ) System.out.println("Game KO : ko = " + ko  );

      if (  ( (lyp + 1) < size )  &&  ! (  ( lxp == x ) && ( (lyp+1) == y )  )    )
            if ( p[lxp][lyp+1].getType() != color )  ko = false;
      if ( debug > 0 ) System.out.println("Game KO : ko = " + ko  );

      if (  ( (lxp - 1) >= 0 )  &&  ! (  ( (lxp-1) == x ) && ( lyp == y )  )    )
            if ( p[lxp-1][lyp].getType() != color )  ko = false;
      if ( debug > 0 ) System.out.println("Game KO : ko = " + ko  );

      if (  ( (lyp - 1) >= 0 )  &&  ! (  ( lxp == x ) && ( (lyp-1) == y )  )    )
        if ( p[lxp][lyp-1].getType() != color )  ko = false;
      if ( debug > 0 ) System.out.println("Game KO : ko = " + ko  );

      // now if ko is still true that's mean that lxp, lyp is surrounded
      // by stone of the other color and is the only dead
      
      ret = ko;
    }
    
    if ( ret )
    {
      if ( debug  > 0 ) System.out.println("Game KO en " + x + " / " + y );
    }

    return ret;
  }


  /**
   *   possibleKO( int x, int y, int node, int color )
   */
  public boolean possibleKO(int x, int y, int node, int color)
  {
    boolean ret;
    boolean possible=false;
    int d = 0 ;

    for ( int i=1; i <= node; i++) 
    {
      if ( debug > 1) System.out.println("Game move[" + i + "] = " + move[i].getColor() +
                                             " / " + move[i].getX()     +
                                             " / " + move[i].getY()     +
                                             " / " + move[i].getDead()      );
      if ( move[i].getDead() == node )
      {
        d++;
        if ( debug  > 0 ) System.out.println("Game.possibleKO d++ = " + d );
         
        if (  ( move[i].getX() == x ) && ( move[i].getY() == y ) && ( move[i].getColor() == color ) )
        {
          if ( debug  > 0 ) System.out.println("Game.possibleKO possible...");
          possible = true;
        }
      }
    }

    ret = ( possible && ( d == 1 )  );
    
    if ( debug  > 0 ) System.out.println("Game.possibleKO( " + x + ", " + y + ", " + node + ", " + color + " ) = " + ret );

    return (ret);
  }


  /**
   *  Returns the other color (black -> white and white -> black)
   */
  public int otherColor ( int color )  { return ( black + white - color ) ; }


  /**
   *   get node
   */
  public int getNode( )
  {
    if ( debug  > 0 ) System.out.println("Game.getNode() = " + node  );

    return (node);
  }


  /**
   *   get size
   */
  public int getSize( )
  {
    if ( debug  > 0 ) System.out.println("Game.getSize() = " + size  );

    return (size);
  }


  /**
   *   get player
   */
  public int getPlayer( )
  {
    int color;

    if ( node >= 1 ) color = move[node].getColor();
    else             color = white;
    
    if ( debug  > 0 ) System.out.println("Game.getPlayer() = " + color  );

    return (color);
  }


  /**
   *   get score
   */
  public double getScore( )
  {
    if ( debug  > 0 ) System.out.println("Game.getScore() = " + score  );

    return (score);
  }


  /**
   *   get deads
   */
  public int getDeads(int color )
  {
    int ret = 0;

    for ( int i=1; i <= node; i++) 
    {
      if ( move[i].isDead( node, color )  ) ret++ ;
    }
    
    if ( debug  > 0 ) System.out.println("Game.getNode( " + color + " ) = " + ret );

    return (ret);
  }


  /**
   *   get territory
   */
  public int getTerritory(int color )
  {
    int ret = territory[color];
    
    if ( debug  > 0 ) System.out.println("Game.getTerritory( " + color + " ) = " + ret );

    return (ret);
  }


  /**
   *  reset deads
   */
  public void resetDeads( )
  {
    if ( debug > 0 ) System.out.println("Game.resetDeads( )" );

    for ( int i=1; i <= node; i++) 
    {
      move[i].resetDead( node );
    }
  }


  /**
   *   find move node with position
   */
  public int nodeXY(int x, int y)
  {
    int ret=-1;

    for ( int i=1; i <= node; i++) 
    {
      if ( move[i].isStone( node )  )
      {
        if (  ( move[i].getX() == x ) && ( move[i].getY() == y )  )
        {
            ret = i;
            break;
        }
      }
    }
    
    if ( debug  > 0 ) System.out.println("Game.nodeXY( " + x + ", " + y + " ) = " + ret );

    return (ret);
  }


  /**
   *   print all Intersection
   */
  public void printAll()
  {
    System.out.println("Game.printAll()");

    // print moves
    for ( int i=1; i <= node; i++) 
    {
      System.out.println("Game move[" + i + "] = " + move[i].getColor() +
                                             " / " + move[i].getX()     +
                                             " / " + move[i].getY()     +
                                             " / " + move[i].getDead()      );
    }

    // print intersections 
    for ( int ix=0; ix < size; ix++) 
    {
      for ( int iy=0; iy < size; iy++) 
      {
        System.out.println("Game p[" + ix + "][" + iy + "] = " + 
                            p[ix][iy].getType() + " / " + p[ix][iy].getNode()   );
      }
    }
  }
    

  /**
   *   add Information to game
   */
  public void addInfo( String param_info )
  {
    if ( debug > 2 ) System.out.println("Game.addInfo(" + info + ")");

    // info on game
    if ( move[node] == null )   info = info + "\n" + param_info;
    
    // or on move
    else                        move[node].addInfo ( param_info );   
  }


  /**
   *   SGF game file
   */
  public String SGF(  )
  {
    String ret = new String();

    for ( int i=1; i <= node; i++) 
    {
	ret = ret + " ; ";

        if ( move[i].isWhite( ) ) ret = ret + "W";
        if ( move[i].isBlack( ) ) ret = ret + "B";
	if ( move[i].isPass ( ) ) ret = ret + "[tt]" ;
        else ret = ret +  "[" + 
			coord_with_I.charAt ( move[i].getX() ) + 
			coord_with_I.charAt ( move[i].getY() ) + 
			  "]";

        if (  ( i % 10 ) == 0 ) ret = ret + "\n";
    }

    if ( debug > 2 ) System.out.println("Game.SGF( " + ret + " )");
	
    return ( ret );
  }


}


