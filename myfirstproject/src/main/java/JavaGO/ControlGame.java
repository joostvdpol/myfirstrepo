package JavaGO;

import java.awt.*; 
import java.awt.image.*; 
import java.net.*;
import java.io.*;

/**
 *  controlGame derives from Panel
 *  control the game 
 */
public class ControlGame extends Panel implements Constants
{
  TextArea text; 
  Frame frame_sgf;
  boolean frame_on;

  // state of control bar
  boolean control_new;
  boolean control_load;

  private URL url_dir;
  private String name_of_dir_games;
  
  /**
   *  buttons
   */
  private Button new_game;
  private Button load;
  private Button save;
  private Button refresh;
  private Button pass;
  private Button full;
  private Button floating;


  private Button first;
  private Button prev;
  private Button next;
  private Button last;
  private Button to_n;

  private TextField n;


  /**
   *  List choice
   */
  private Choice url_list;

  private Choice size_list;
  private Choice komi_list;
  private Choice hand_list;


  /*
   * Javago applet
   */
  private JavaGO javago;
  

  /**
   *  constructor
   */
  ControlGame( JavaGO param_javago )
  {
    if ( debug > 0 ) System.out.println("ControlGame.ControlGame( " + param_javago + " )" );
    
    javago = param_javago ;

    controlNew();
    controlMain();
    controlLoad();

    control_new  = false;
    control_load = false;
 
    init();

    setLayout( new FlowLayout( FlowLayout.LEFT, 4, 0 ) );
  }
  

  /**
   *  init
   */
  public void init()
  {
    if ( debug > 0 ) System.out.println("ControlGame.init()");

    // remove all the components
    removeAll();

    if      ( control_new  ) init_new ();
    else if ( control_load ) init_load();
    else		     init_main();

    validate();

    // text for SGF frame

    text = new TextArea( ); 
    frame_sgf = new Frame();
    frame_sgf.resize(600,250);
    frame_sgf.setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 )  );
    frame_sgf.add(text);

    frame_on = false;
    
  }


  /**
   *  init main controls of the game
   */
  public void init_main()
  {
    if ( debug > 0 ) System.out.println("ControlGame.init_main()");

    add(new_game);     
    add(load);     
    add(save);     
    // add(refresh);
    add(pass); 	
    add(first); 	
    add(prev); 	
    add(next); 	
    add(last); 	
    add(to_n); 	
    add(n); 	
    add(full); 	
    add(floating); 	
  }


  /**
   *  init new controls
   */
  public void init_new()
  {
    if ( debug > 0 ) System.out.println("ControlGame.add_new()");

      add(new_game);     
      add(load);     
      add(save);     

      // add init buttons
      add(size_list);
      add(hand_list);
      add(komi_list);
  }


  /**
   *  init load controls
   */
  public void init_load()
  {
    if ( debug > 0 ) System.out.println("ControlGame.init_load()");
   
    add(new_game);     
    add(load);
    add(save);     

    add(url_list);

  }
  


  /**
   *  paint
   */
  public void paint(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("ControlGame.paint( )");

    JavaGO.drawBackground(g, size(), this);
  }


  /**
   *   initiate node field
   */
  public void setNodeField( int node )
  {
    if ( debug > 0 ) System.out.println("ControlGame.setNodeField( " + n + " )" );

    n.setText( "" + node );
  }


  /**
   *   controls for a new game
   */
  public void controlNew( )
  {
    if ( debug > 0 ) System.out.println("ControlGame.controlNew( )" );
    
    // Init size_list and komi_list
    size_list = new Choice(); 
    komi_list = new Choice(); 
    hand_list = new Choice(); 

    // Add size_list possible choices
    size_list.addItem("size");
    size_list.addItem("19");    size_list.addItem("18");    size_list.addItem("17");
    size_list.addItem("16");    size_list.addItem("15");    size_list.addItem("14");
    size_list.addItem("13");    size_list.addItem("12");    size_list.addItem("11");
    size_list.addItem("10");    size_list.addItem("9" );    

    // Add komi_list possible choices
    komi_list.addItem("komi") ;
    komi_list.addItem("9.5" ) ;   komi_list.addItem("8.5" ) ;    komi_list.addItem("7.5" ) ;
    komi_list.addItem("6.5" ) ;   komi_list.addItem("5.5" ) ;    komi_list.addItem("4.5" ) ;
    komi_list.addItem("3.5" ) ;   komi_list.addItem("2.5" ) ;    komi_list.addItem("1.5" ) ;
    komi_list.addItem("0.5" ) ;   komi_list.addItem("-0.5") ;    komi_list.addItem("-1.5") ;
    komi_list.addItem("-2.5") ;   komi_list.addItem("-3.5") ;    komi_list.addItem("-4.5") ;
    komi_list.addItem("-5.5") ;   komi_list.addItem("-6.5") ;    komi_list.addItem("-7.5") ;
    komi_list.addItem("-8.5") ;   komi_list.addItem("-9.5") ;    

    // Add hand_list possible choices
    hand_list.addItem("hand");
    hand_list.addItem("2"  ) ;   hand_list.addItem("3"   ) ;
    hand_list.addItem("4"  ) ;   hand_list.addItem("5"  ) ;    hand_list.addItem("6"   ) ;
    hand_list.addItem("7"  ) ;   hand_list.addItem("8"  ) ;    hand_list.addItem("9"   ) ;
    hand_list.addItem("10" ) ;   hand_list.addItem("11" ) ;    hand_list.addItem("12"  ) ;
    hand_list.addItem("13" ) ;   hand_list.addItem("14" ) ;    hand_list.addItem("15"  ) ;
    hand_list.addItem("16" ) ;   hand_list.addItem("17" ) ;    

    // Defaults choices
    size_list.select("size"); 
    hand_list.select("hand"); 
    komi_list.select("komi"); 

    // Buttons labels
    new_game = new Button("New");
    load     = new Button("Load");
    save     = new Button("Save");
  }


  /**
   *   main controls for a game
   */
  public void controlMain( )
  {
    if ( debug > 0 ) System.out.println("ControlGame.controlMain( )" );

    /**
     *  buttons labels
     */
    refresh  = new Button("Refresh");
    pass     = new Button("Pass");
    full     = new Button("x");
    floating = new Button("o");

    first    = new Button("|<");
    prev     = new Button("<");
    next     = new Button(">");
    last     = new Button(">|");

    to_n     = new Button(">#");
    n        = new TextField( 2 );
  }


  /**
   *   controls for loading a new game
   */
  public void controlLoad( )
  {
    if ( debug > 0 ) System.out.println("ControlGame.controlLoad( )" );

    url_list = new Choice(); 
    String name_of_dir_javago = javago.getCodeBase().toString();

    // getCodeBase doesn't return the same thing on diffrent browser !
    // some return the full URL, other only the directory !
    //
    int i = name_of_dir_javago.lastIndexOf ( '/' );

    if ( debug > 2 ) System.out.println("i : " + i );
    if ( debug > 2 ) System.out.println("name_of_dir_javago : " + name_of_dir_javago );

    // suppress the name of file (usully "javago.html")
    if ( i >= 0 )    name_of_dir_javago = name_of_dir_javago.substring(0, i+1);
    if ( debug > 0 ) System.out.println("name_of_dir_javago : " + name_of_dir_javago );

    // add GoGames
    name_of_dir_games = name_of_dir_javago + "GOGames/";
    if ( debug > 2 ) System.out.println("name_of_dir_games : " + name_of_dir_games );

    // Add url_list first possible choices
    url_list.addItem( name_of_dir_games  );

    readURL();

    // Add url_list first possible choices
    url_list.addItem("http://www.mygale.org/~al62/GOGames/");
    url_list.addItem("http://stekt.oulu.fi/~suopanki/go/games/");
    url_list.addItem("file://C|/");
    url_list.addItem("file://D|/");
    url_list.addItem("file://E|/");

  }



  /**
   *  action
   */
  public boolean action( Event e, Object arg)
  {
    if ( debug > 0 ) System.out.println("ControlGame.action( " + e + " , " + arg + "  )" );

    if      ( e.target == new_game  ) 
    {
      // not in control mode
      control_load = false;

      if ( control_new )    
      {
	javago.appendTextln("New game");
        javago.newGame();
      }
      // else    javago.appendTextln("New mode ON");
      
      control_new = !control_new;
      init();
    }

    else if ( e.target == load  ) 
    {
      // not in new mode
      control_new  = false;
      
      if ( control_load )
      {
        URL url = readURL();

        // if url ok 
        if ( url != null ) 
        {
          // if url not directory
          if (   !( url.toString().endsWith("/") )  )
          {
            javago.appendTextln("Load new game : " + url + " ..." );
            javago.newGame(); 

            // load SGF file
            new SgfFile( javago.game, url, javago );

            javago.appendText(" ok");

            // exit control mode
            control_load = false;
          }
        }
        // url ko, reinit control
        else
        {
          controlLoad();
        }
      }
      else
      {
        // javago.appendTextln("Load mode ON");
        control_load = true;
      }
      
      init();
    }

    else if ( e.target == save     )    actionSave();

    else if ( e.target == full     ) javago.fullWindow( );
    else if ( e.target == floating ) javago.floatWindow();

    else if ( e.target == refresh  ) javago.goban.repaintAll();
    else if ( e.target == pass     ) javago.goban.actionPass();

    else if ( e.target == first    ) javago.goban.setNode( first_node );
    else if ( e.target == prev     ) javago.goban.setNode( previous_node );
    else if ( e.target == next     ) javago.goban.setNode( next_node );
    else if ( e.target == last     ) javago.goban.setNode( last_node );
    else if ( e.target == to_n     ) 
    {
      try 
      {
        String n_string = n.getText();
        Integer node_n = new Integer ( n_string );
        int param_n;
        param_n = node_n.intValue();
        javago.goban.setNode( node_n.intValue() );     
      }
      catch ( java.lang.NumberFormatException nfe) {}
    }

    return true;
  }


  /**
   *  action save
   */
  public void actionSave()
  {
    if ( debug > 0 ) System.out.println("ControlGame.actionSave()" );

    if ( frame_on ) 	frame_sgf.hide();
    else 		
    {
	String sgf_game = new String( "(\n;GaMe[1] VieW[] SiZe[19] Comment[ Created by JavaGO ] ; \n");


	sgf_game = sgf_game + javago.game.SGF();

	sgf_game = sgf_game + " \n)\n";

	text.setText( sgf_game );
	 
	frame_sgf.show();
    }
  
    frame_on = ! frame_on;

  }


  /**
   *  read URL of game file
   */
  public URL readURL( ) 
  {
    if ( debug > 0 ) System.out.println("ControlGame.readURL()" );

    String name_of_url;
    URL url;

    name_of_url  = url_list.getSelectedItem();

    if ( debug > 0 ) System.out.println("ControlGame.readURL() : " + name_of_url );

    try
    {
      url = new URL ( name_of_url ) ;

      // if directory
      if ( name_of_url.endsWith( "/" ) )
      {
        url_dir = url;
        if ( ! setDir( ) )
        {
          javago.appendTextln("Bad URL directory : " + url );
          url = null;
        }
      }
      else javago.appendTextln("Read game : " + url );
    }
    catch ( java.net.MalformedURLException me )
    {
      System.out.println("ControlGame.readURL:MalformedURLException" + me );
      url = null;
    }

    if ( debug > 0 ) System.out.println("ControlGame.readURL() : return = " + url );
    return ( url );      
  }
    

  /**
   *   set Dir = list of file in this directory
   */
  public boolean setDir( )
  {
    if ( debug > 0 ) System.out.println("ControlGame.setDir( " + url_dir + " )" );

    javago.appendTextln("List directory : " + url_dir );

    InputStream 	file_in;
    DataInputStream data_in;
    String 		    line_in;
    String name = "";
    char            c;

    char c1[] = new char[6];
    boolean href = false;
    boolean ret  = true;

    String name_of_url_dir;

    // get URL selected in url_list
    if ( url_dir != null )  name_of_url_dir = url_dir.toString();
    else                    name_of_url_dir = "";

    url_list = new Choice(); 
    try 
    {
      file_in = url_dir.openStream();
      data_in = new DataInputStream(file_in);

      while (  ( line_in = data_in.readLine() ) != null) 
      {

        if ( debug > 0 ) System.out.println("ControlGame.setDir:line_in = " + line_in );

       
        int i0=0;
        int i1, i2;
        String sub = line_in;
        String subtmp ;
        
        while ( i0 != -1 )
        {
	  // syntax : <a href="URL">  (in fact href="URL" is ok)

          // find href
          i0 = sub.indexOf( "href" );
          if ( i0 == -1 )
          {
            i0 = sub.indexOf( "HREF" );
            if ( i0 == -1 ) continue;
          }
          if ( debug > 2 ) System.out.println("ControlGame.setDir: i0 = " + i0 );

          // suppression of href=
          sub = sub.substring( i0 + 5 );

	  // find first "
          i1 = sub.indexOf ( '\"' );
          sub = sub.substring( i1 + 1 );
	  
	  // find second "
          i2 = sub.indexOf ( '\"' );
          subtmp = sub.substring( 0, i2 );
          sub = sub.substring( i2+1 );

	  // in certain browser | is changed to %7C   :(
	  // %7C => |
	  i1 = subtmp.indexOf( "%7C" );

	  // for IE "file:/C:/" for NN "file:/C|/"  
	  char delim = name_of_url_dir.charAt(7);
          if (  ( delim != '|' ) && ( delim != ':' )  ) delim = '|';
	  if ( i1 != -1 ) subtmp = subtmp.substring(0,i1) + delim + subtmp.substring(i1+3) ;

	  // in certain browser " " is changed to %20   :(
	  // %20 => " "
	  i1 = subtmp.indexOf( "%20" );
	  if ( i1 != -1 ) subtmp = subtmp.substring(0,i1) + " " + subtmp.substring(i1+3) ;

	  name = subtmp;
          if ( debug > 0 ) System.out.println("ControlGame.setDir:addItem " + name );


	  // if start with / it's a local file
          if (  name.startsWith("/") )
          {
            url_list.addItem( name_of_url_dir.substring(0,5) + name );
          }
          // if http: of file: simply add  (directory include in the URL)
          else if (  name.startsWith("http") ||  name.startsWith("file")  )
          {
            url_list.addItem( name );
          }
          // in this case add the directory name
          else
          {
            url_list.addItem( name_of_url_dir + name );
          }
          javago.appendText(".");
        }
      	
      }
      javago.appendText(" ok");

      data_in.close();
      file_in.close();
      url_list.select(0);
    }
    catch (Exception e)
    {
      System.out.println("Error: " + e.toString());
      ret = false;
    }
    url_list.addItem( name_of_dir_games  );


    return ( ret );
  }


  /**
   *  game size ?
   */
  public int gameSize()
  {
    int ret;

    // get size selected in list
    String Tmp_field   = size_list.getSelectedItem();
    if (  ( Tmp_field == "size" ) || ( control_load )  ) ret = 19;
    else
    {
      Integer Integer_Tmp = new Integer ( Tmp_field );
      ret   = Integer_Tmp.intValue();
    }
    if ( debug > 0 ) System.out.println("ControlGame.gameSize() = " + ret );

    return ret;
  }


  /**
   *  handicap  ?
   */
  public int Handicap()
  {
    int ret;
    
    // get handicap selected in list
    String Tmp_field   = hand_list.getSelectedItem();
    if ( Tmp_field == "hand" ) ret = 0;
    else
    {
      Integer Integer_Tmp = new Integer ( Tmp_field );
      ret   = Integer_Tmp.intValue();
    }
    if ( debug > 0 ) System.out.println("ControlGame.Handicap() = " + ret );
    return ret;
  }


  /**
   *  komi  ?
   */
  public double Komi()
  {
    double ret;
    
    // get komi selected in list
    String Tmp_field  = komi_list.getSelectedItem();
    if ( Tmp_field == "komi" )
    {
      if ( Handicap() > 1  ) ret = 0.5 ;
      else                   ret = 5.5 ;
    }
    else 	     
    {
      Double Double_Tmp = new Double ( Tmp_field );
      ret  = Double_Tmp.doubleValue();
    }
    if ( debug > 0 ) System.out.println("ControlGame.Komi() = " + ret );
    return ret;
  }




}


