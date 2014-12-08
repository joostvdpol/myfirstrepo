package JavaGO;

  

/**
 *  Intersection 
 */
public class Intersection implements Constants 
{
  /**
   *  Intersection node and type
   *  possible types : unknown, terr_white, terr_black, dame, dead or pass
   */
  private int node;
  private int type;


  /**
   *  constructor by default, type unknown
   */
  Intersection()
  {
    if ( debug > 3 ) System.out.println("Intersection.Intersection( )");

    type = unknown ;
    node = -1;
  }


  /**
   *  Set intersection stone node
   */
  public void  setNode ( int i, int color )
  {
    if ( debug > 2 ) System.out.println("Intersection.setNode( " + i + " )");

    // Set node and color as type
    node = i;
    type = color;
  }


  /**
   *  Set intersection pass node
   */
  public void  setPass ( int i )
  {
	if ( debug > 0 ) System.out.println("Intersection.setNode( " + i + " )");

    // Set node and pass as type
    node = i;
    type = pass;
  }


  /**
   *  Set intersection type
   */
  public void setType ( int i )
  {
	if ( debug > 3 ) System.out.println("Intersection.setType( " + i + " )");

    // Set type
    type = i;

    if ( !isStone() ) node = -1;
  }


  /**
   *   get intersection node
   */
  public int getNode ()
  {
  	if ( debug > 3 ) System.out.println("Intersection.getNode( ) = " + node );

    return ( node );
  }


  /**
   *  get intersection type
   */
  public int getType ()
  {
  	if ( debug > 3 ) System.out.println("Intersection.getType( ) = " + type );

    return ( type );
  }


  /**
    *  Transform presumption ident to certitude
    */
  public void transform ( )
  {
  	if ( debug > 2 ) System.out.println("Intersection.transform( ) : " + type );

    if ( isUnknown() ) setType(dame);
    if ( isBlack()   ) setType(terr_black);
    if ( isWhite()   ) setType(terr_white);

  	if ( debug > 2 ) System.out.println("Intersection.transform( ) : " + type );
  }


  /**
    *  is Stone ?  
    */
  public boolean isStone ()
  {
    boolean cr = ( isBlack() || isWhite () );
    
  	if ( debug > 3 ) System.out.println("Intersection.stone( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *  is empty ?
    */
  public boolean isEmpty ()
  {
    boolean cr = ! isStone ();
    
  	if ( debug > 3 ) System.out.println("Intersection.empty( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *  is white ?
    */
  public boolean isWhite ()
  {
    boolean cr =  ( type == white );

  	if ( debug > 3 ) System.out.println("Intersection.white( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *  is black ?
    */
  public boolean isBlack ()
  {
    boolean cr =  ( type == black );

  	if ( debug > 3 ) System.out.println("Intersection.black( ) = " + cr );

    return cr;
  }


  /**
    *  is unknown ?  
    */
  public boolean isUnknown ()
  {
    boolean cr = ( type == unknown );
    
  	if ( debug > 3 ) System.out.println("Intersection.unknown( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *   is white territory ?  
    */
  public boolean isTerr_white ()
  {
    boolean cr = ( type == terr_white );
    
  	if ( debug > 3 ) System.out.println("Intersection.terr_white( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *  is black  territory ?
    */

  public boolean isTerr_black ()
  {
    boolean cr = ( type == terr_black );
    
  	if ( debug > 3 ) System.out.println("Intersection.terr_black( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *   is dame ?  
    */
  public boolean isDame ()
  {
    boolean cr = ( type == dame );
    
  	if ( debug > 3 ) System.out.println("Intersection.terr_dame( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *    is determined ?
    */
  public boolean isDetermined ()
  {
    boolean cr = ( isTerr_black() || isTerr_white() || isDame() );
    
  	if ( debug > 3 ) System.out.println("Intersection.determined( ) = " + cr );

    return (  cr  ) ;
  }


  /**
    *  is undetermined ?  
    */
  public boolean isUndetermined ()
  {
    boolean cr = ( ! isDetermined() );
    
  	if ( debug > 3 ) System.out.println("Intersection.undetermined( ) = " + cr );

    return (  cr  ) ;
  }

}



