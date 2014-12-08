package JavaGO;


import java.net.*;
import java.io.*;
  
/**
 *  SgfFile class
 */
public class SgfFile implements Constants
{   
  /**
   * Rules :
   * =======
   *
   *   "..." : terminal symbols
   *   [...] : option: occurs at most once
   *   {...} : repetition: any number of times, including zero
   *   (...) : grouping
   *     |   : exclusive-or
   *
   *    Collection      = {GameTree}.
   *    GameTree        = "(" Sequence {GameTree} ")".
   *    Sequence        = Node {Node}.
   *    Node            = ";" {Property}
   *
   *    Property        = PropIdent PropValue {PropValue}.
   *    PropIdent       = UpperCase [UpperCase | Digit].
   *    PropValue       = "[" [Number | Text | Real | Triple
   *                              | Color | Move | Point | ... ] "]"
   *    Number          = ["+" | "-"] Digit {Digit}.
   *    Text            = { any character; "\]" = "]", "\\" = "\"}.
   *    Real            = { Number ["." {Digit}].
   *    Triple          = ("1" | "2").
   *    Color           = ("B" | "W").
   *
   * Field description :
   * ===================
   *
   * B  = Black move                [move, game-specific]
   * W  = White move                [move, game-specific]
   * C  = Comment                   [text]
   * N  = Node name                 [text]
   * V  = Node value                [number]
   * M  = Marked points             [point list, game-specific]
   * L  = Letters on points         [point list, game-specific]
   *
   * CH = CHeck mark                [triple]
   * GB = Good for Black            [triple]
   * GW = Good for White            [triple]
   * TE = TEsuji                    [triple]
   * BM = Bad Move                  [triple]
   *
   * BL = time Left for Black       [real]
   * WL = time Left for White       [real]
   * FG = figure                    [none]
   *
   * AB = add black stones          [point list, game specific]
   * AW = add white stones          [point list, game specific]
   * AE = add empty stones          [point list, game specific]
   * PL = player to play first      [color]
   *
   * GN = Game Name                 [text]
   * GC = Game Comment              [text]
   * EV = EVent                     [text]
   * RO = ROund                     [text]
   * DT = DaTe                      [text]
   * PC = PlaCe                     [text]
   * PW = name White player         [text]
   * PB = name Black player         [text]
   * RE = REsult, outcome           [text]
   * US = USer (who entered game)   [text]
   * TM = TiMe limit per player     [text]
   * SO = SOurce (book, journal...) [text]
   *
   * GM = GaMe [number] (Go=1, Othello=2, chess=3, Nine Mens Morris=5)
   * SZ = board SiZe                [number]
   * VW = partial VieW              [point list, game-specific]
   * BS = Black Species             [number] (human=0, modem=-1, computer>0)
   * WS = White Wpecies             [number]
   *
   * EL = EvaLuation of computer move       [number]
   * EX = EXpected next move                [move, game-specific]
   *
   * SL = SeLected points                   [point list, game-specific]
   *
   * BR = Black's Rank              [text]
   * WR = White's Rank              [text]
   * HA = HAndicap                  [number]
   * KM = KoMi                      [real]
   *
   * TB = Black's territory         [point list]
   * TW = White's territory         [point list]
   * SC = SeCure stones             [point list]
   * RG = ReGion of the board       [point list]
   *
   *
   * CP = CoPyright
   * NW = ? White
   * NB = ? Black
   * PC = server name
   * LT = ?
   * BL = Black move time
   * WL = White move time
   * TR = TRiangle
   */


  // SGF command : 1 or 2 character
  char c1;
  char c2;
  // number of characters
  int n;

  // inside []
  boolean inside;

  // param start position
  int i_start;

  // last move played 'W' = White, 'B' = Black, 'P' = Pass
  String last;

  // game read
  Game game;

  String          param;

  JavaGO javago;

  /*
   * constructor
   */
  SgfFile ( Game param_game, URL file, JavaGO param_javago )
  {
    javago = param_javago ;
    game = param_game;
    
    int debug=0;

    if ( debug > 0 ) System.out.println("SgfFile.SgfFile()" );

    InputStream 	file_in;
    DataInputStream data_in;
    String 		    line_in;
    char            c;

    try 
    {
      file_in = file.openStream();
      data_in = new DataInputStream(file_in);

      while (  ( line_in = data_in.readLine() ) != null) 
      {

    	if ( debug > 1 ) System.out.println("SgfFile.SgfFile : " + line_in );

        i_start = 0;
        
    	for (int i = 0; i < line_in.length(); i++)
        {
          c = line_in.charAt(i);
          if ( debug > 2 ) System.out.println("SgfFile.SgfFile : " + c );

          // if uppercase and not inside [ ]
          if ( Character.isUpperCase(c) && !inside)
          {
            n++;
            if      ( n == 1 ) c1 = c;
            else if ( n == 2 ) c2 = c;
          }
          else if ( c == '[' )
          {
              inside = true;
		      i_start = i+1;
          }
          else if ( c == ']' )
          {
            String com ;
            if      ( c1 == 0 )  com = ""          ;
            else if ( c2 == 0 )  com = "" + c1     ;
            else                 com = "" + c1 + c2;


            param = param + line_in.substring(i_start,i);
            
            sgfCommand( com, param );
            init();
          }
          else if ( c == ';' )
          {
            init();
          }    

        }
        // param on more than one line
        if ( i_start != -1 ) param = param + line_in.substring( i_start, line_in.length() );
      }
      game.setNode ( first_node );
      data_in.close();
      file_in.close();
    } 
    catch (MalformedURLException me) 
    {
      javago.appendTextln("Bad URL file : " + file );
      System.out.println("MalformedURLException: " + me);
    } 
    catch (IOException ioe) 
    {
      javago.appendTextln("Problem reading URL file : " + file );
      System.out.println("IOException: " + ioe);
    }
    
 

  }


  /*
   *  init
   */
  public void init ()
  {
    inside  = false;
    n  = 0;
    c1 = 0;
    c2 = 0;
    i_start = -1;
    last  = "";
    param = "";
  }


  /*
   *  sgfCommand interpretation
   */
  public void sgfCommand( String command, String param )
  {
    if ( debug > 2 ) System.out.println("SgfFile.sgfCommand : " + command + "[" + param + "]" );

    // black or white AND inside [ ]
    if (  ( command.equals( "B" )  )  || ( command.equals("W") )  ) 
    {
      int x,y;

      // if move = tt : pass
      if ( param.equals( "tt" ) ) game.actionPass();
      else
      {
        // previous passed      
        if ( command.equals( last ) ) game.actionPass();

        // new move
        x = coord_with_I.indexOf ( param.charAt(0) );
        y = coord_with_I.indexOf ( param.charAt(1) );

    	if ( debug > 2 ) System.out.println("SgfFile.sgfCommand : Move : " + x + " , " + y );

        game.actionMove( x, y );
        last = command;
      }
    }
    else if ( command.equals( "HA" ) )
    {
      Integer tmp_int = new Integer ( param );
      int hand = tmp_int.intValue();

      if ( debug > 0 ) System.out.println("SgfFile.sgfCommand : Handicap : " + hand );
      game.setHandicaps( hand );
      game.addInfo( "Handicap " + hand );
    }
    else if ( command.equals( "C" ) )
    {
      game.addInfo( "Comment : " + param );
    }
    else if ( command.equals( "WL" ) || command.equals( "BL" ))  { /* time indication */ }
    else if ( command.equals( "" ) )  { /* time indication */ }
    else 
    {
      game.addInfo( command + " : " + param );
    }
  }

}


