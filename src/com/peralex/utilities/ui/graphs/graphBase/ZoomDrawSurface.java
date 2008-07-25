package com.peralex.utilities.ui.graphs.graphBase;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JMenuItem;

/**
 *
 * @author  Andre
 */
public abstract class ZoomDrawSurface extends CursorDrawSurface
{
	  
  private static final AlphaComposite ZOOM_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
  
  /**
   * Stores all the Zoom Listeners.
   */
  private final List<IZoomListener> oZoomListeners = new ArrayList<IZoomListener>();
	
	/** 
   * Default unit range values for the X Axis.
   */
	private float fMinimumXZoomLimit = getMinimumX(), fMaximumXZoomLimit = getMaximumX();
  
	/** 
   * Default unit range values for the Y Axis.
   */
	private float fMinimumYZoomLimit = getMinimumY(), fMaximumYZoomLimit = getMaximumY();
    
  /**
   * This is the rectangle that gets drawn.
   */
	private final Rectangle oZoomPaintRectangle = new Rectangle();
  
  /**
   * This Stack stores all the Zooms that the user has made, excluding the current one.
   */
  private final Stack<float []> oZoomStack = new Stack<float []>();
	
	/**
	 * This is the Timer that will repaint the rectangle while dragged.
	 * This helps to make the UI seem snappier because less repaint requests
	 * are generated than if we repaint on every mouse move.
	 */
  private final javax.swing.Timer oRepaintTimer;
	private int mousePositionModifierCnt = 0;

	/**
	 * This flag indicates whether the zoom is currently enabled or not.
	 */
	private boolean bZoomEnabled = true;
	
	/**
	 * This flag indicates whether the Square zoom is currently enabled or not.
	 */
	private boolean bSquareZoomEnabled = false;	
	
	private Color zoomRectangleColor = Color.WHITE;

  /**
   * if false, the mouse wheel will zoom along the horizontal axis.
   */
	private boolean bMouseWheelsZoomsVertical = true;

  /**
   * if true, zoomOut will zoom out around the current position
   */
  private boolean bZoomOutOnPosition = false;

  private final JMenuItem oResetZoomItem;
  
  /**
   * factor used to calculate mouse wheel zooming
   */
  private static final float HORIZONTAL_WHEEL_ZOOM = 0.7f;
  private static final float VERTICAL_WHEEL_ZOOM = 0.9f;
  
	private float fMinimumXZoomResolution = -1;
  
  private final AnimationTimer oAnimationTimer;
  
  /**
   * Creates a new instance of cZoomDrawSurface
   */
  protected ZoomDrawSurface()
  {
    // Clear the zoom indicator rectangle.
		oZoomPaintRectangle.setLocation(-1, -1);
    oZoomPaintRectangle.setSize(0, 0);
    
    oPopupMenu.addSeparator();

    // Add the Reset zoom item to the PopUpMenu.
    oResetZoomItem = new JMenuItem(textRes.getString("Reset_zoom"));
    oPopupMenu.add(oResetZoomItem);  
    oResetZoomItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        resetZoom();
      }
    });
		
		oRepaintTimer = new javax.swing.Timer(50, new ActionListener()
		{
			/**
			 * we do some fancy calculations to minimise the amount of redraw work we do
			 */
			private Rectangle previousPaintRectangle = null;
			private int lastModifierCnt = -1;
			
			public void actionPerformed(ActionEvent e)
			{
				// if nothing has changed, don't redraw
				if (lastModifierCnt==mousePositionModifierCnt) {
					return;
				}
				lastModifierCnt = mousePositionModifierCnt;
				
				// make a copy to prevent thread issues
				final Rectangle tmpRect = new Rectangle(oZoomPaintRectangle);
				
				if (previousPaintRectangle!=null) {
					// if we painted before, we need to repaint both the previous painted rectangle, 
					// as well as the new rectangle to handle the case where the rectangle shrinks.
					repaint(previousPaintRectangle);
				}
				repaint(tmpRect);
				previousPaintRectangle = tmpRect;
			}
		});
		oRepaintTimer.setRepeats(true);
		
		oAnimationTimer = new AnimationTimer();
  }

  public void setZoomRectangleColor(Color c) {
  	this.zoomRectangleColor = c;
  }
  
  /**
   * This paints the rectangle.
   */
	@Override
	protected void paint(Graphics g, int iDrawSurfaceID)
  {
    if (ZOOM_DRAWSURFACE != iDrawSurfaceID)
		{
			super.paint(g, iDrawSurfaceID);
			return;
		}
    final Graphics2D g2 = (Graphics2D) g;
    
    if (bZoomEnabled)
		{
			g2.setColor(zoomRectangleColor);
			g2.setComposite(ZOOM_COMPOSITE);
			g2.fillRect(oZoomPaintRectangle.x, oZoomPaintRectangle.y, oZoomPaintRectangle.width, oZoomPaintRectangle.height);
		}
  }

  /**
   * This method is used for zooming IN.
   */
  private void internalZoomIn(float fZoomMinimumX, float fZoomMaximumX, float fZoomMinimumY, float fZoomMaximumY)
  {
  	// clamp the values to prevent the user zooming in too far.
  	if (fMinimumXZoomResolution!=-1)
  	{
	  	final float fMinimumXZoomRange = getWidth() * fMinimumXZoomResolution;
	  	if (Math.abs(fZoomMinimumX - fZoomMaximumX) < fMinimumXZoomRange)
	  	{
	  		float midPoint = fZoomMinimumX + (fZoomMaximumX - fZoomMinimumX) / 2f;
	  		fZoomMinimumX = midPoint - (fMinimumXZoomRange)/2;
	  		fZoomMaximumX = midPoint + (fMinimumXZoomRange)/2;
	  	}
  	}

  	// check for weird parameter values
  	if (fZoomMinimumX>fZoomMaximumX) 
  		throw new IllegalArgumentException("fZoomMinimumX>fZoomMaximumX " + fZoomMinimumX + " " + fZoomMaximumX);
  	if (fZoomMinimumY>fZoomMaximumY) 
  		throw new IllegalArgumentException("fZoomMinimumY>fZoomMaximumY " + fZoomMinimumY + " " + fZoomMaximumY);

  	// clamp values to legal range
		if (fZoomMinimumX<fMinimumXZoomLimit) fZoomMinimumX = fMinimumXZoomLimit;
		if (fZoomMaximumX<fMinimumXZoomLimit) fZoomMaximumX = fMinimumXZoomLimit;
		if (fZoomMinimumX>fMaximumXZoomLimit) fZoomMinimumX = fMaximumXZoomLimit;
		if (fZoomMaximumX>fMaximumXZoomLimit) fZoomMaximumX = fMaximumXZoomLimit;
		if (fZoomMinimumY<fMinimumYZoomLimit) fZoomMinimumY = fMinimumYZoomLimit;
		if (fZoomMaximumY<fMinimumYZoomLimit) fZoomMaximumY = fMinimumYZoomLimit;
		if (fZoomMinimumY>fMaximumYZoomLimit) fZoomMinimumY = fMaximumYZoomLimit;
		if (fZoomMaximumY>fMaximumYZoomLimit) fZoomMaximumY = fMaximumYZoomLimit;
		
    // Clear the zoom indicator rectangle.
		oZoomPaintRectangle.setLocation(-1, -1);
    oZoomPaintRectangle.setSize(0, 0);
    
  	if (fZoomMinimumY != fZoomMaximumY && fZoomMinimumX != fZoomMaximumX)
		{
			// Store the current display settings
			final float[] afZoomValues = new float[4];
			afZoomValues[0] = getMinimumX();
			afZoomValues[1] = getMaximumX();
			afZoomValues[2] = getMinimumY();
			afZoomValues[3] = getMaximumY();
			oZoomStack.push(afZoomValues);
			
			oAnimationTimer.start(fZoomMinimumX, fZoomMaximumX, fZoomMinimumY, fZoomMaximumY); 
		} 
  	else
  	{
  		// if we don't trigger the animation timer, then there might be a zoom rectangle that
  		// needs clearing
			repaint();
		}
  }
  
  /**
   * This method is used for zooming OUT.
   */
  public void zoomOut() {
  	internalZoomOut();
  }
  
  /**
   * This method is used for zooming OUT.
   */
  private void internalZoomOut()
  {
    if (!oZoomStack.isEmpty())
    {      
      float[] afZoomValues = oZoomStack.pop();
      oAnimationTimer.start(afZoomValues[0], afZoomValues[1], afZoomValues[2], afZoomValues[3]);
    }
    else if (getMinimumX() != fMinimumXZoomLimit || getMaximumX() != fMaximumXZoomLimit
    		|| getMinimumY() != fMinimumYZoomLimit || getMaximumY() != fMaximumYZoomLimit)
    {
    	oAnimationTimer.start(fMinimumXZoomLimit, fMaximumXZoomLimit, fMinimumYZoomLimit, fMaximumYZoomLimit);
    }
  }
  
  /**
   * This method will reset the graph to its original zoom limit.
   */
  public void resetZoom()
  {
    oZoomStack.clear();
    oAnimationTimer.start(fMinimumXZoomLimit, fMaximumXZoomLimit, fMinimumYZoomLimit, fMaximumYZoomLimit);
  }

  /**
   * reset only the X zoom factor, leaving the Y-axis alone.
   * 
   * This is useful when a user has zoomed into a frequency range and the frequency range changes
   *  - it allows them to scan through a band while being zoomed into a part of the vertical axis.
   */
  private void resetXZoom()
  {
    oZoomStack.clear();
    
    if (oAnimationTimer.fMinimumY!=fMinimumYZoomLimit || oAnimationTimer.fMaximumY!=fMaximumYZoomLimit)
    {
  		// Store the current display settings
			final float[] afZoomValues = new float[4];
			afZoomValues[0] = fMinimumXZoomLimit;
			afZoomValues[1] = fMaximumXZoomLimit;
			afZoomValues[2] = oAnimationTimer.fMinimumY;
			afZoomValues[3] = oAnimationTimer.fMaximumY;
			oZoomStack.push(afZoomValues);
    }

		oAnimationTimer.start(fMinimumXZoomLimit, fMaximumXZoomLimit, oAnimationTimer.fMinimumY, oAnimationTimer.fMaximumY);
  }
  
  /**
   * This method will set the Zoom function enabled or disabled.
   */
  public void setZoomEnabled(boolean bEnabled)
  {
    this.bZoomEnabled = bEnabled;
  }
	
  /**
   * This method will set the Square Zoom function enabled or disabled.
   * This is mostly used by the ConstellationGraph.
   */
  public void setSquareZoomEnabled(boolean bEnabled)
  {
    this.bSquareZoomEnabled = bEnabled;
  }
	
  /**
   * Is graph currently zoomed.
	 *
	 * @return Is graph currently zoomed.
   */
  public boolean isZoomed()
  {
    return !oZoomStack.isEmpty();
  }
	
  /**
   * This methods sets the maximum and minimum on the Axis.
   */
	@Override
  public void setGridMinMax(float fMinimumX, float fMaximumX, float fMinimumY, float fMaximumY)
  {
  	final boolean bXChanged = fMinimumXZoomLimit!=fMinimumX || fMaximumXZoomLimit!=fMaximumX;
  	final boolean bYChanged = fMinimumYZoomLimit!=fMinimumY || fMaximumYZoomLimit!=fMaximumY;
  	
    this.fMinimumXZoomLimit = fMinimumX;
    this.fMaximumXZoomLimit = fMaximumX; 
    this.fMinimumYZoomLimit = fMinimumY;
    this.fMaximumYZoomLimit = fMaximumY;    
		
    super.setGridMinMax(fMinimumXZoomLimit, fMaximumXZoomLimit, fMinimumYZoomLimit, fMaximumYZoomLimit);
    /* If only the Y min/max changed, leave any Y-zooming alone. 
     * Useful when the graph is skipping across the x range i.e. scanning */
    if (bXChanged && !bYChanged)
    {
    	resetXZoom();
    }
    /* Just in case we were zoomed in to anything, reset the zoom, otherwise we could end up un-zooming
     * to a place no longer on the graph. */
    else if (bXChanged || bYChanged)
    {
    	resetZoom();
    }
  }
	
  /**
   * This methods sets the maximum and minimum on the X Axis.
   */
	@Override
  public void setGridXMinMax(float fMinimumX, float fMaximumX)
  {
  	final boolean bChanged = fMinimumXZoomLimit!=fMinimumX || fMaximumXZoomLimit!=fMaximumX;
  	if (!bChanged) return;
  	
    this.fMinimumXZoomLimit = fMinimumX;
    this.fMaximumXZoomLimit = fMaximumX; 
		
    super.setGridMinMax(fMinimumXZoomLimit, fMaximumXZoomLimit, fMinimumYZoomLimit, fMaximumYZoomLimit);
    /* Just in case we were zoomed in to anything, reset the zoom, otherwise we could up unzooming
     * to a place no longer on the graph. */
   	resetXZoom();
  }
	
  /**
   * This methods sets the maximum and minimum on the Y Axis.
   */
	@Override
  public void setGridYMinMax(float fMinimumY, float fMaximumY)
  {
  	final boolean bChanged = fMinimumYZoomLimit!=fMinimumY || fMaximumYZoomLimit!=fMaximumY;
  	if (!bChanged) return;
  	
    this.fMinimumYZoomLimit = fMinimumY;
    this.fMaximumYZoomLimit = fMaximumY;   

		super.setGridYMinMax(fMinimumY, fMaximumY);
    /* Just in case we were zoomed in to anything, reset the zoom, otherwise we could up unzooming
     * to a place no longer on the graph. */
    resetZoom();
  }	

	/**
	 * returns true if the user is currently dragging a zoom rectangle around
	 */
	public final boolean isZoomingNow()
	{
		return oRepaintTimer.isRunning();
	}
	
  /**
   * Zoom around a point on the x-axis, reducing the x range of the graph by half.
   * 
   * @param xCoord the x-coordinate in pixels
   */
  public void zoomAlongX(int xCoord)
  {
    final float fXval = pixelToUnitX(xCoord);
    final float fWidth = (oAnimationTimer.fMinimumX - oAnimationTimer.fMaximumX) / 4;

    zoomXRange(fXval - fWidth, fXval + fWidth);
  }
  
  /**
   * Zoom to a range on the x-axis
   * 
   * @param fXval1 the start of the range to zoom to, in units
   * @param fXval2 the end of the range to zoom to, in units
   */
  public void zoomXRange(float fXval1, float fXval2)
  {
    internalZoomIn(fXval1, fXval2, oAnimationTimer.fMinimumY, oAnimationTimer.fMaximumY);
  }

  /**
   * Zoom to a range on the y-axis
   * 
   * @param fYval1 the start of the range to zoom to, in units
   * @param fYval2 the end of the range to zoom to, in units
   */
  public void zoomYRange(float fYval1, float fYval2)
  {
    internalZoomIn(oAnimationTimer.fMinimumX, oAnimationTimer.fMaximumX, fYval1, fYval2);
  }
  
  /**
   * Zoom the graph.
   */
  public void zoomIn(float fMinX, float fMaxX, float fMinY, float fMaxY)
  {
    internalZoomIn(fMinX, fMaxX, fMinY, fMaxY);
  }
  
  /**
   * Zoom out around the centre of the range. This is equivalent to a mouse-wheel
   * zoom out.
   */
  public void zoomOutHorizontalCentred()
  {
    final int xCoord = getWidth() / 2;
    mouseWheelHorizontalZoomOut(xCoord);
  }
  
  /**
   * Event for mouseDragged.
   */ 
	@Override
  public void mouseDragged(MouseEvent e)
  {
    super.mouseDragged(e);
    
    if (bZoomEnabled && isDown(e, MouseEvent.BUTTON1_DOWN_MASK))
    {
    	/* Ignore very small zooms because they are probably accidental, and 
    	 * if we do zoom, the graph appears to be empty. Mostly a problem with
    	 * systems where the operator is using a touchpad. */
  		if (Math.abs(iMousePressedX-e.getX())>10 || Math.abs(iMousePressedY-e.getY())>10)
  		{
	      oZoomPaintRectangle.setLocation(Math.min(iMousePressedX, e.getX()), Math.min(iMousePressedY, e.getY()));
	      oZoomPaintRectangle.setSize(Math.abs(e.getX() - iMousePressedX), Math.abs(e.getY() - iMousePressedY));
	      mousePositionModifierCnt++;
	      oRepaintTimer.start();
  		}
    }
  }

  /**
   * Event for mouseReleased.
   */  
	@Override
  public void mouseReleased(MouseEvent e)
  {
    super.mouseReleased(e);
    
    if (bZoomEnabled)
    {
      oRepaintTimer.stop();
      if (Math.abs(iMousePressedX - iMouseReleasedX) > 10 || Math.abs(iMousePressedY - iMouseReleasedY) > 10)
      {
        final float fX1 = pixelToUnitX(iMousePressedX);
        final float fX2 = pixelToUnitX(iMouseReleasedX);

        final float fY1 = pixelToUnitY(iMousePressedY);
        final float fY2 = pixelToUnitY(iMouseReleasedY);

        /* The previous code has an assumption that the user will selected from 
         * the left top corner to the right bottom corner. However, a user can select 
         * in another 3 direction and this can cause the code to throw exceptions.
         * Now the following code will swap values to fit the assumption. */ 
        
        final float fMinimumX = Math.min(fX1, fX2);
        final float fMaximumX = Math.max(fX1, fX2);

        final float fMinimumY = Math.min(fY1, fY2);
        final float fMaximumY = Math.max(fY1, fY2);
        
        internalZoomIn(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
      }
      else
      {
        // Clear the Drawn rectangle and repaint.
				oZoomPaintRectangle.setLocation(-1, -1);
        oZoomPaintRectangle.setSize(0, 0);
        repaint();
      }
    }
  }  
  
  /**
   * Event for mouseWheelMoved.
   */  
	@Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    super.mouseWheelMoved(e);
    
		if (!bZoomEnabled)
		{
			return;
		}
		
		if (e.getWheelRotation() > 0)
		{
			for (int i=0; i<e.getWheelRotation(); i++)
			{
        if (bSquareZoomEnabled)
        {
          internalZoomOut();
        }
        else if (bMouseWheelsZoomsVertical)
        {
          internalZoomOut();
        }
        else // zoom horizontal
        {
          mouseWheelHorizontalZoomOut(e.getX());
        }
			}
		}
		else
		{
			/* Holding the shift key while moving the mouse wheel inverts the horizontal/vertical choice
			 * of zooming. */
			final boolean bShiftDown = e.isShiftDown();
			
			for (int i=0; i>e.getWheelRotation(); i--)
			{
				if (bSquareZoomEnabled)
				{
          final float fMouseXValue = pixelToUnitX(e.getX());            
          final float fMouseYValue = pixelToUnitY(e.getY());           
          final float fZoomMinimumX = fMouseXValue - ((fMouseXValue - getMinimumX()) * VERTICAL_WHEEL_ZOOM);
          final float fZoomMaximumX = fMouseXValue + ((getMaximumX() - fMouseXValue) * VERTICAL_WHEEL_ZOOM);
          final float fZoomMinimumY = fMouseYValue - ((fMouseYValue - getMinimumY()) * VERTICAL_WHEEL_ZOOM);
          final float fZoomMaximumY = fMouseYValue + ((getMaximumY() - fMouseYValue) * VERTICAL_WHEEL_ZOOM);
          internalZoomIn(fZoomMinimumX, fZoomMaximumX, fZoomMinimumY, fZoomMaximumY);           
				}
				else if ((bMouseWheelsZoomsVertical && !bShiftDown) || (!bMouseWheelsZoomsVertical && bShiftDown))
				{
          final float fMouseYValue = pixelToUnitY(e.getY());           
          final float fZoomMinimumY = fMouseYValue - ((fMouseYValue - getMinimumY()) * VERTICAL_WHEEL_ZOOM);
          final float fZoomMaximumY = fMouseYValue + ((getMaximumY() - fMouseYValue) * VERTICAL_WHEEL_ZOOM);
          internalZoomIn(getMinimumX(), getMaximumX(), fZoomMinimumY, fZoomMaximumY);
				}
        else // zoom horizontal
        {
          final float fMouseXValue = pixelToUnitX(e.getX());            
          final float fZoomMinimumX = fMouseXValue - ((fMouseXValue - getMinimumX()) * HORIZONTAL_WHEEL_ZOOM);
          final float fZoomMaximumX = fMouseXValue + ((getMaximumX() - fMouseXValue) * HORIZONTAL_WHEEL_ZOOM);
          internalZoomIn(fZoomMinimumX, fZoomMaximumX, getMinimumY(), getMaximumY());
        }
			}
		}
	}

  private void mouseWheelHorizontalZoomOut(int xCoord)
  {
    if (!oZoomStack.isEmpty())
    {      
      oZoomStack.pop();
    }
    if (getMinimumY() != fMinimumYZoomLimit || getMaximumY() != fMaximumYZoomLimit
      || getMinimumX() != fMinimumXZoomLimit || getMaximumX() != fMaximumXZoomLimit)
    {
      // this is the inverse of the zoomIn calculations
      final float fMouseXValue = pixelToUnitX(xCoord);
      final float fZoomMinimumX = Math.max(fMouseXValue + (getMinimumX() - fMouseXValue) / HORIZONTAL_WHEEL_ZOOM, fMinimumXZoomLimit);
      final float fZoomMaximumX = Math.min(fMouseXValue + (getMaximumX() - fMouseXValue) / HORIZONTAL_WHEEL_ZOOM, fMaximumXZoomLimit);
      
      oAnimationTimer.start(fZoomMinimumX, fZoomMaximumX, getMinimumY(), getMaximumY());
    }
  }
	
  /**
   * Adds a Zoom listener.
   */
  public void addZoomListener(IZoomListener oZoomListener)
  {
    oZoomListeners.add(oZoomListener);
  }
  
  /**
   * Removes a Zoom Listener.
   */
  public void removeZoomListener(IZoomListener oZoomListener)
  {
    oZoomListeners.remove(oZoomListener);
  }
	
  /**
   * This fires all the Zoom Listeners.
   */
	private void fireZoomListeners()
  {
  	for (IZoomListener oZoomListener : oZoomListeners)
  	{
      oZoomListener.graphZoomChanged(getMinimumX(), getMaximumX(), getMinimumY(), getMaximumY());
			oZoomListener.graphZoomStatusChanged(isZoomed());
    }   
  }

  public float getMaximumXZoomLimit() {
    return fMaximumXZoomLimit;
  }

  public float getMinimumXZoomLimit() {
    return fMinimumXZoomLimit;
  }

  public float getMaximumYZoomLimit() {
    return fMaximumYZoomLimit;
  }

  public float getMinimumYZoomLimit() {
    return fMinimumYZoomLimit;
  }
  
  public boolean isMouseWheelsZoomsVertical() {
    return bMouseWheelsZoomsVertical;
  }

  /**
   * Note that this interacts with squareZoom - if you set squareZoom to true, that will take precedence.
   */
  public void setMouseWheelsZoomsVertical(boolean mouseWheelsZoomsVertical) {
    this.bMouseWheelsZoomsVertical = mouseWheelsZoomsVertical;
  }

  /**
   * if true, zoomOut will zoom out around the current position
   */
  public boolean isZoomOutOnPosition() {
    return bZoomOutOnPosition;
  }

  /**
   * if true, zoomOut will zoom out around the current position
   */
  public void setZoomOutOnPosition(boolean zoomOutOnPosition) {
    bZoomOutOnPosition = zoomOutOnPosition;
  }
  
  @Override
  protected void localeChanged()
  {
  	super.localeChanged();
    oResetZoomItem.setText(textRes.getString("Reset_zoom"));
  }

  /**
   * set the smallest X resolution that a user can zoom to, -1 indicates that zoom resolution limiting is off.
   */
  public void setMinimumXZoomResolution(float f)
  {
  	this.fMinimumXZoomResolution = f;
  }
  
  /**
   * get the smallest X resolution that a user can zoom to, -1 indicates that zoom resolution limiting is off.
   */
  public float setMinimumXZoomResolution()
  {
  	return this.fMinimumXZoomResolution;
  }

  /**
   * Animates the zoom.
   */
  private class AnimationTimer {
  	
  	private final javax.swing.Timer timer;
  	public float fMinimumX = getMinimumX(), fMaximumX=getMaximumX(), fMinimumY=getMinimumY(), fMaximumY=getMaximumY();
  	private int iStepCounter = 0;
  	
  	public AnimationTimer()
  	{
  		timer = new javax.swing.Timer(50, new ActionListener() {
  			public void actionPerformed(ActionEvent e)
  			{
  				step();
  			}
  		});
  		timer.setRepeats(true);
  	}
  	
    public synchronized void start(float _fMinimumX, float _fMaximumX, float _fMinimumY, float _fMaximumY)
    {
    	if (this.fMinimumX==_fMinimumX && this.fMaximumX==_fMaximumX 
    			&& this.fMinimumY==_fMinimumY && this.fMaximumY==_fMaximumY)
    	{
    		return;
    	}
    	timer.stop();
    	this.iStepCounter = 0;
    	this.fMinimumX = _fMinimumX;
    	this.fMaximumX = _fMaximumX;
    	this.fMinimumY = _fMinimumY;
    	this.fMaximumY = _fMaximumY;
    	timer.start();
    	
    	// notify the zoom animation listeners
    	for (IZoomListener oZoomListener : oZoomListeners)
    	{
   			oZoomListener.zoomAnimationStart(_fMinimumX, _fMaximumX, _fMinimumY, _fMaximumY);
      }   
    }
    
		private void step()
		{
			// check for termination
			if (iStepCounter>10)
			{
				// There are probably more elegant ways of stopping the animation, but this approach is (a) simple and (b) bulletproof.
				timer.stop();
				ZoomDrawSurface.super.setGridMinMax(fMinimumX, fMaximumX, fMinimumY, fMaximumY);
				// fire the listeners AFTER the animation, so that they don't see any intermediate states which could confuse them.
				fireZoomListeners();
				return;
			}
			ZoomDrawSurface.super.setGridMinMax(
					interim(getMinimumX(), fMinimumX), 
					interim(getMaximumX(), fMaximumX), 
					interim(getMinimumY(), fMinimumY), 
					interim(getMaximumY(), fMaximumY));
			iStepCounter++;
		}

		/** Calculate an interim step between the current zoom and the destination zoom.
		 * This calculation creates a zoom that starts fast and ends slow.
		 */
		private float interim(float start, float end)
		{
			return start + ((end-start)/2f);
		}
  }
}