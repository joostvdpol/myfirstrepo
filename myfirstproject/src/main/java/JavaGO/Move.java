package JavaGO;
  
import java.awt.*; 


/**
 *  Move class : 2 colors possible, for each pass or dead possible
 */
public class Move implements Constants
{
  // Move position on the board and color
  private int x, y;
  private int color;
  
  // Node of the game, when the move is dead
  private int dead_node=MAX_STONES+1;

  // pass value
  private static final int PASS = -99;


  // information on the move
  String info;

  
  /**
   *  constructor, for a stone move
   */
  Move (int param_x, int param_y, int param_color )
  {
    if ( debug > 0 ) System.out.println("Move.Move( " + param_x + ", " + param_y + ", " + param_color + " ) STONE" );

    x     = param_x   ;
    y     = param_y   ;
    color = param_color;
    info = "";
  }


  /**
   *  constructor, for a pass move 
   */
  Move ( int param_color )
  {
    if ( debug > 0 ) System.out.println("Move.Move( " + param_color + " ) PASS" );

    x = PASS; y = PASS;
    color = param_color;
    info = "";
  }


  /**
   *  reset dead
   */
  public void resetDead( int node )
  {
    if ( debug > 0 ) System.out.println("Move.resetDead( )" );

    if ( dead_node >= node ) dead_node = MAX_STONES+1;
  }


  /**
   *  get color
   */
  public int getColor( )
  {
    if ( debug > 3 ) System.out.println("Move.getColor( ) = " + color);

    return ( color );
  }


  /**
   *  get X
   */
  public int getX( )
  {
    if ( debug > 3 ) System.out.println("Move.getX( ) = " + x );

    return ( x );
  }


  /**
   *  get Y
   */
  public int getY( )
  {
    if ( debug > 3 ) System.out.println("Move.getY( ) = " + y );

    return ( y );
  }


  /**
   *  get coordinates
   */
  public String getCoord( int size )
  {
    String ret = getCoord( x, y, size);
   
    if ( debug > 3 ) System.out.println( "Move.getCoord( " + size + " ) = " + ret );

    return ( ret );
  }


  /**
   *  get coordinates
   */
  public static String getCoord( int pos_x, int pos_y, int size )
  {
    String ret;


    if    ( pos_x == PASS ) 	ret = "pass";
    else  
    {
      if ( (size - pos_y) < 10 ) 	ret =  ""  + coord_no_I.charAt(pos_x) + (size - pos_y) ;
      else 			                ret =  ""  + coord_no_I.charAt(pos_x) + (size - pos_y) ;
    }

    if ( debug > 3 ) System.out.println( "Move.getCoord( " + pos_x + ", " + pos_y + ", " + size + " ) = " + ret );

    return ( ret );
  }


  /**
   *  get dead_node
   */
  public int getDead( )
  {
    if ( debug > 3 ) System.out.println("Move.getDead( ) = " + dead_node );

    return ( dead_node );
  }


  /**
   *  kill stone 
   */
  public void kill(int node )
  {
    if ( debug > 0 ) System.out.println("Move.kill( " + node + " )" );

    dead_node = node;
  }


  /**
   *  is this stone dead at this node
   */
  public boolean isDead( int node )
  {
    boolean cr = ( dead_node <= node ) ;

    if ( debug > 3 ) System.out.println("Move.isDead( " + x + ", " + y + " at " + node + " ) = " + cr );

    return cr;
  }


  /**
   *  is this stone dead at this node of this color
   */
  public boolean isDead( int node, int col )
  {
    boolean cr = (  ( dead_node <= node ) && ( col == color )  );

    if ( debug > 3 ) System.out.println("Move.isDead( " + x + ", " + y + " at " + node + " ) = " + cr );

    return cr;
  }


  /**
   *  is this stone alive at this node ?
   */
  public boolean isAlive( int node )
  {
    boolean cr = ! isDead( node ) ;

    if ( debug > 3 ) System.out.println("Move.isAlive( " + x + ", " + y + " at " + node + " ) = " + cr );

    return cr;
  }


  /**
   *  is this a stone at this node ?
   */
  public boolean isStone( int node )
  {
    boolean cr;

    cr = (  ( color == black ) || ( color == white )  )  && isAlive( node ) && !isPass();

    if ( debug > 3 ) System.out.println("Move.isStone( " + x + ", " + y + " ) at " + node + "  = " + cr );

    return cr;
  }


  /**
   *  is this a stone of that color at this node ?
   */
  public boolean isStone( int node, int c )
  {
    boolean cr;

    cr = ( color == c ) && isAlive( node ) && !isPass();

    if ( debug > 3 ) System.out.println("Move.isStone( " + x + ", " + y + " ) color = " + c + " / " + node + "  = " + cr );

    return cr;
  }


  /**
   *  is this is pass at this node ?
   */
  public boolean isPass( )
  {
    boolean cr;

    cr = ( x == PASS );

    if ( debug > 2 ) System.out.println("Move.isPass(  ) = " + cr );
    return cr;
  }


  /**
   *  is this node Black ?
   */
  public boolean isBlack( )
  {
    boolean cr;

    cr = ( color == black );

    if ( debug > 2 ) System.out.println("Move.isBlack(  ) = " + cr );
    return cr;
  }


  /**
   *  is this node White ?
   */
  public boolean isWhite( )
  {
    boolean cr;

    cr = ( color == white );

    if ( debug > 2 ) System.out.println("Move.isWhite(  ) = " + cr );
    return cr;
  }


  /**
   *   add Information to move
   */
  public void addInfo( String param_info )
  {
    if ( debug > 2 ) System.out.println("Move.addInfo(" + info + ")");

    info = info + "\n" + param_info ;   
  }

}


