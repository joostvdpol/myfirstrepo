/**
 *  <p><b>Applet JavaGo</b><br>
 *  <i>Alain Papazoglou</i>, Copyright (c) 1997
 *  <p>
 */
package JavaGO;

import java.applet.*;    
import java.awt.*; 
import java.awt.image.*; 
import java.net.URL; 

/**
 *  Main class : JavaGO
 */
public class JavaGO extends Applet implements Runnable, Constants
{
  /** Applet thread */
  protected Thread runner;

  /** Sound for a good click */
  static protected AudioClip sound_ok;

  /** Sound for a bad click */
  static protected AudioClip sound_ko;

  /** Stone images for black and white stones */
  static protected Image stone []   = new Image [2];
	
  /**  GO-ban image */
  static protected Image goban_image;

  /** Frame for a floating window */
  protected Frame frame;
  
  /**  Goban object where the game is played */
  protected Goban  goban;

  /**  Game object including all the moves of the game */
  public Game game;

  /**  ControlGame object that control the game */
  protected ControlGame control_game;

  /**  InfoScore object showing score of the current game played on the goban */
  protected InfoScore info_score  ;

  /**  InfoJavaGO object showing JavaGO information */
  protected InfoJavaGO info_javago ;

  /** Window float attribute */
  boolean float_window;

  /** Window full attribute */
  boolean full_window ;

  
  /**
   *  JavaGO init, call initMedias, repaint and then initComponent.
   */
  public void init()
  {
    if ( debug > 0 ) System.out.println("JavaGO.init()" );

    initMedias();
    repaint();
    initComponents();
  }
    
  /**
   *  Init of medias : images 
   *  <ul>
   *  <li>  gob.gif : backround goban GIF image
   *  <li>black.gif : black stone GIF image
   *  <li>white.gif : white stone GIF image
   *  </ul>
   *  and sounds :
   *  <ul>
   *  <li>ok.au : sound for correct moves
   *  <li>ko.au : sound for incorrect moves
   *  </ul>
   */
  protected void initMedias()
  {
    if ( debug > 0 ) System.out.println("JavaGO.initMedias()" );

    // Declarations
    Integer Tmpi;
    Double Tmpd;
    String parameter;

    URL url = getCodeBase();
    
    if ( debug > -1 ) System.out.println("JavaGO url = " + url );


    /**
     * Loading in memory of : GO-ban, black and white images
     */

    if ( ! local )
    {
    goban_image  = getImage( url, "gob.jpg"  );
    stone[black] = getImage( url, "black.gif" );
    stone[white] = getImage( url, "white.gif");
    try
    {
      sound_ok = getAudioClip	( new URL ( url, "ok.au"  ) );
      sound_ko = getAudioClip	( new URL ( url, "ko.au"  ) );
    }
    catch ( java.net.MalformedURLException e) {}
    // Tracker for waiting end of loading
    MediaTracker imageTracker = new MediaTracker(this) ;
    imageTracker.addImage( goban_image, 0 );	
    try { imageTracker.waitForAll(); }
    	catch ( InterruptedException e ) { System.out.println("interrupted"); }
    }
  }


  /**
   *  Init of javaGO components<br>
   *  Creation (new) of :
   *  <ul>
   *  <li>frame        : Frame object for the support of the floating window (after "o" action)
   *  <li>goban        : Goban object where the game is played
   *  <li>game         : Game object including all the moves of the game
   *  <li>control_game : ControlGame object that control the game
   *  <li>info_score   : InfoScore object showing score of the current game played on the goban
   *  <li>info_javago  : InfoJavaGO object showing JavaGO information
   *  </ul>
   */
  protected void initComponents()
  {
    if ( debug > 0 ) System.out.println("JavaGO.initComponents()" );

    // Floating frame
    frame = new Frame ( "JavaGO" );
    // frame is larger : has a menu bar + info bar + border
    frame.resize( size().width+6, size().height+52 );

    setLayout( new BorderLayout(0,0) );
    frame.setLayout( new BorderLayout(0,0) );


    float_window = false;
    full_window  = false;
    
    // must be first to allow appendText
    info_javago  = new InfoJavaGO();

    control_game = new ControlGame( this );
    
    goban = new Goban();
    game  = new Game();

    info_score   = new InfoScore( game );
    newGame();
    
    addAll( this );
  }


  /**
   *  Prepararation for the drawing of stone images with a specific sizes (use of MediaTracker). 
   *  Used when applet is resized.
   */
  void initAtSize( int stone_size )
  {
    if ( debug > 0 ) System.out.println( "JavaGO.initAtSize( " + stone_size + " )" );

    if ( ! local )
    {
	// Manip pour attendre la fin du chargement des images
	MediaTracker imageTracker = new MediaTracker( this ) ;

 	imageTracker.addImage( stone[black], 0, stone_size, stone_size);
	imageTracker.addImage( stone[white], 0, stone_size, stone_size);
	try { imageTracker.waitForAll(); }
    	catch ( InterruptedException e ) { System.out.println("Media Tracker problem : " + e ); }
    }
  }


  /**
   *  Adding of all the components in the container that is either main applet, either frame :
   *  <ul>
   *  <li>control_game : control_game added at the top (or North)
   *  <li>goban        : goban added in the center
   *  <li>info_score   : info_score added on the left (or West)
   *  <li>info_javago  : info_javago added on the bottom (or South)
   *  </ul>
   */
  protected void addAll( Container container )
  {
    container.validate();
    container.add("North" , control_game );
    container.add("South" , info_javago  );
    container.add("West"  , info_score   );
    container.add("Center", goban        );
    container.show();
    container.validate();
  }

 
  protected void addGoban(  )
  {
    frame.validate();

    goban.resize( frame.size().width-6, frame.size().height-52 );
    goban.move(3,23);

    frame.add( goban );
    frame.show();
    frame.validate();
  }
 
  protected void removeAllComponents(  )
  {
    validate();
    removeAll();
    validate();

    frame.validate();
    frame.removeAll();
    frame.hide();
    frame.validate();
  }


  /**
   *  JavaGO paint : just draw the bacground with background image.
   */
  public void paint(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("JavaGO.paint( )");

    drawBackground( g, size(), this);
  }

  
  /**
   *  JavaGO update : just call paint
   */
  public void update(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("JavaGO.update( )");

    paint(g);
  }


  /**
   *  Create a new game : retrieve game_size in control field, call game.init (JavaGO.Game.init()) 
   *  and (JavaGO.Goban.init()) goban.init.
   */
  protected void newGame( )
  {
    int new_size = control_game.gameSize();

    if ( debug > 0 ) System.out.println("JavaGO.newGame( ) : " + new_size );

    game.init( this, new_size, control_game.Handicap(), control_game.Komi() );
    goban.init( this, game, new_size );
  }


  /**
   *  Toggle full window on or off <br>
   *  If float window operate on frame object else on main applet <br>
   *  Add or remove all components except goban (ie control_game, info_score, info_javago)<br>
   */
  protected void fullWindow( )
  {
    if ( debug > 0 ) System.out.println("JavaGO.fullWindow( )" );

    // remove all components of main applet and frame
    removeAllComponents();

    if ( full_window )
    {
      addAll(frame);
    }
    else
    {
      addAll(this);
      addGoban( );
    }

    full_window = !full_window ;
    float_window = true;
  }


  /**
   *  Toggle float window or not
   *  Remove all components from one container (main applet or frame) 
   *  and add all of them on the other
   */
  protected void floatWindow( )
  {
    if ( debug > 0 ) System.out.println("JavaGO.floatWindow( )" );

    // remove all components of main applet and frame
    removeAllComponents();


    // window already floating, back to the browser
    if ( float_window )
    {
      // add all components in main applet
      addAll (this);  
    }
    // extract the applet from the browser
    else 
    {
      // add all components in frame
      addAll (frame);  
    }

    float_window = !float_window;
    full_window = false;
  }


  /**
   *  Draw one stone image of the specific size
   *  If stone image not found draw black or white full circle.
   */
  static public void drawStoneImage( Graphics g, int color,
            int x, int y, int size,  ImageObserver  observer )
  {
    if ( debug > 2 ) System.out.println(
        "JavaGO.drawStoneImage( g, " + color + ", " + x + ", " + y + ", " + size + " ) ");

    Image image = stone[color];
    // normal case
    if ( image != null )
    {
      g.drawImage( JavaGO.stone[color], x, y, size, size, observer );
    }
    // if any problems with stone image files
    else
    {
      // Stone color
      if ( color == black ) g.setColor( Color.black );
      if ( color == white ) g.setColor( Color.white );
      g.fillOval( x, y, size, size );
      g.setColor( Color.black );
    }
  }


  /**
   *  Draw the background, repeat the image as many times as necessary.
   *  If image not found draw orange rectangle as background.
   */
  static public void drawBackground( Graphics g, Dimension size, ImageObserver observer )
  {
    if ( debug > 0 ) System.out.println("JavaGO.drawBackground( g ) ");

    int      x, y, w, h;

    // Image really exists ?
    if ( goban_image != null )    
    {
      if ( debug > 1 ) System.out.println("JavaGO Goban image ok");
      // Background image width
      w = goban_image.getWidth(observer); 
      // Background image height
      h = goban_image.getHeight(observer);        

      // Width iterations
      x = size.width / w  ;   
      // Height iterations
      y = size.height / h  ;
      if ( debug > 1 ) System.out.println("JavaGO w = " + w + " h = " + h );
      if ( debug > 1 ) System.out.println("JavaGO x = " + x + " y = " + y );

      // Drawing each line
      for( int line=0; line <= y; line++ )
      {
     	// Draw for each column
        for( int column=0; column <= x; column++ )
        {
          g.drawImage( goban_image, column*w, line*h, observer );
        }
      }
    }
    else
    {
      if ( debug > 1 ) System.out.println("JavaGO Goban image ko");
      g.setColor(Color.orange);
      g.fillRect( 0, 0, size.width-1, size.height-1 );
    }
  }


  /**
   *  Play ok sound
   */
  static public void okPlay()
  {
    if ( sound_ok != null ) sound_ok.play();
  }


  /**
   *  Play ko sound
   */
  static public void koPlay()
  {
    if ( sound_ko != null ) sound_ko.play();
  }


  /**
   *  JavaGO run
   */
  public void run()
  {
    if ( debug > 0 ) System.out.println("JavaGO.run");
  }


  /**
   *  JavaGO start, start the thread
   */
  public void start()
  {
    if ( debug > 0 ) System.out.println("JavaGO.start");

    if ( runner == null )
    {
      runner = new Thread( this );
      runner.start();
    }
  }


  /**
   *  JavaGO stop, stop the thread
   */
  public void stop()
  {
    if ( debug > 0 ) System.out.println("JavaGO.stop");
    if ( runner != null )
    {
      runner.stop();
      runner = null;
    }
  }


  /**
   *  JavaGO destroy
   */
  public void destroy()
  {
    if ( debug > 0 ) System.out.println("JavaGO.destroy");
  }


  /**
   *  append Text
   */
  public void appendText( String s )
  {
    if ( debug > 0 ) System.out.println("JavaGO.appendText(" + s + " )"  );

    info_javago.appendText( s );
  }


  /**
   *  append newline + Text 
   */
  public void appendTextln( String s )
  {
    if ( debug > 0 ) System.out.println("JavaGO.appendTextln(" + s + " )"  );

    info_javago.appendTextln( s );
  }



}

