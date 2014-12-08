package JavaGO;


/**
 * 	Interface Cconstants
 */
public interface Constants 
{
  /** Applet copyright and version */
  final String copyright = new String
  (
    "Copyright © Alain Papazoglou  1997 - V0.17  16 July 1997"
  );

  // text area width
  final int text_width = 80;
  
  // Debug index 
  int debug = 0;

  // due to a strange configuration on my PC,
  // in local mode appletviewer wants to access internet
  // and does nothing until it get it, even if the URL are local !?
  final boolean local=false;
  //final boolean local=true;
  
  /**
   *  Uppercase coordinates letters with no I for display
   */
  final String coord_no_I = "ABCDEFGHJKLMNOPQRST";

  /**
   *  Lowercase coordinates letters with I for SGF format
   */
  final String coord_with_I = "abcdefghijklmnopqrs";

  // set node commands
  final int first_node    =  -1 ;
  final int next_node     =  -2 ;
  final int previous_node =  -3 ;
  final int last_node     =  -4 ;

  // move and intersection types
  final int black = 0;
  final int white = 1;

  // other intersection types
  final int unknown    =  2 ;
  final int terr_black =  3 ;
  final int terr_white =  4 ;
  final int dame       =  5 ;
  final int MAX_INTERSECTION_TYPES = 6;
  
  // other move types
  final int dead  = 2;
  final int pass  = 3;
  

  // maximum number of stones in a game
  final int MAX_STONES = 800;


  // A Go-Ban is'nt square, a bit longer in the direction of view
  // Small and big ratios  45cm/43cm
  final int rg = 45;
  final int rp = 43;
}
