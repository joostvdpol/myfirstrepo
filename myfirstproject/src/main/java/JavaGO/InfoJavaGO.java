package JavaGO;
  
import java.awt.*; 
import java.applet.*; 


/**
 *  InfoJavaGO class
 */
public class InfoJavaGO extends Panel implements Constants
{
  private TextArea info;

  private int pos=0;

  
  /**
   *  constructor
   */
  InfoJavaGO()
  {
    if ( debug > 0 ) System.out.println("InfoJavaGO.InfoJavaGO()" );

    // LEFT alignment
    setLayout( new FlowLayout( FlowLayout.LEFT, 5, 0 )  );

    // set fixed size of the InfoJavaGO panel
    // resize ( 300, 60 );

    // info = new TextArea( );
    info = new TextArea( 3, text_width );
    
    add( info );
    appendTextln( copyright );
  }


  /**
   *  init
   */
  public void init()
  {
    if ( debug > 0 ) System.out.println("InfoJavaGO.init()" );
  }

  
  /**
   *  paint
   */
  public void paint(Graphics g) 
  {
    if ( debug > 0 ) System.out.println("InfoJavaGO.paint( g )");

    // draw background
    JavaGO.drawBackground(g, size(), this);

  }


  /**
   *  append Text
   */
  public void appendTextln( String str )
  {
    if ( debug > 0 ) System.out.println("InfoJavaGO.appendTextln(" + str + " )"  );

    info.appendText( "\n" );
    pos = 0;
    appendText( str );
  }

  
  /**
   *  append Text
   */
  public void appendText( String str )
  {
    if ( debug > 0 ) System.out.println("InfoJavaGO.appendText(" + str + " )"  );

    String tr = str;
    
    while ( (pos+tr.length()) >= text_width )
    {
      String st;

      // str = st + tr
      st =  tr.substring(0, (text_width-pos) );
      
      // append first part     
      info.appendText( st + "\n" );

      // reset tr with end of the string
      tr = tr.substring(text_width-pos);

      if ( debug > 2 ) System.out.println("InfoJavaGO.appendText : cut at " + pos + " /" + st + "/" + tr  );

      pos = 0;
    }

    pos += tr.length();
    info.appendText( tr );
  }

}


