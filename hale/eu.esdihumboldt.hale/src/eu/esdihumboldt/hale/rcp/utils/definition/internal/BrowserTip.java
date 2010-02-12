/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.definition.internal;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Browser tip
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class BrowserTip {

	private static final Logger log = Logger.getLogger(BrowserTip.class);
	
	private static final int HOVER_DELAY = 400;
	
	private final int toolTipWidth;
	
	private final int toolTipHeight;
	
	private final boolean plainText;
	
	/**
	 * The height adjustment when using the computed size
	 */
	private int heightAdjustment = 50;

	/**
	 * Constructor
	 * 
	 * @param toolTipWidth the maximum with
	 * @param toolTipHeight the maximum height
	 * @param plainText if the content will be plain text instead of html
	 */
	public BrowserTip(int toolTipWidth, int toolTipHeight, boolean plainText) {
		super();
		this.toolTipWidth = toolTipWidth;
		this.toolTipHeight = toolTipHeight;
		this.plainText = plainText;
	}

	/**
	 * Show the tool tip
	 * 
	 * @param control the tip control 
	 * @param posx the x-position 
	 * @param posy the y-position
	 * 
	 * @param toolTip the tool tip string
	 */
	public void showToolTip(Control control, int posx, int posy, String toolTip) {
		final Shell toolShell = new Shell (control.getShell(), SWT.ON_TOP | SWT.NO_FOCUS
	            | SWT.TOOL);
	    FillLayout layout = new FillLayout ();
	    toolShell.setLayout (layout);
	    try {
	    	if (plainText) {
	    		Text text = new Text(toolShell, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
	    		text.setFont(control.getDisplay().getSystemFont());
	    		text.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
	    		text.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	    		text.setText(toolTip);
	    	}
	    	else {
		    	Browser browser = new Browser(toolShell, SWT.NONE);
		    	browser.setFont(control.getDisplay().getSystemFont());
		    	browser.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		    	browser.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		    	browser.setText(toolTip);
	    	}
	    	
	    	Point pt = control.toDisplay(posx, posy);
		    
		    Rectangle bounds = control.getDisplay().getBounds();
		    
		    Point size = toolShell.computeSize( SWT.DEFAULT, SWT.DEFAULT);
		    int width = Math.min(toolTipWidth, size.x);
		    int height = Math.min(toolTipHeight, size.y + heightAdjustment );
		    
		    int x = (pt.x + width > bounds.x + bounds.width)?(bounds.x + bounds.width - width):(pt.x);
		    
		    toolShell.setBounds(x, pt.y, width, height);
		    
		    final Point initCursor = toolShell.getDisplay().getCursorLocation();
		    
		    toolShell.addMouseTrackListener(new MouseTrackAdapter() {

				@Override
				public void mouseExit(MouseEvent e) {
					hideToolTip(toolShell);
				}
		    	
		    });
		    
		    final Timer closeTimer = new Timer(true);
		    closeTimer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (toolShell != null && !toolShell.isDisposed()) {
						toolShell.getDisplay().asyncExec(new Runnable() {

							public void run() {
								// check if cursor is over tooltip
								Point cursor = toolShell.getDisplay().getCursorLocation();
								if (!cursor.equals(initCursor)) {
									Rectangle bounds = toolShell.getBounds();
									if (!bounds.contains(cursor)) {
										hideToolTip(toolShell);
										closeTimer.cancel();
									}
								}
							}
							
						});	
					}
					else {
						// disposed -> cancel timer
						closeTimer.cancel();
					}
				}
		    	
		    }, 2 * HOVER_DELAY, 1000);
		    
		    toolShell.setVisible(true);
	    }
	    catch(SWTError err) {
	      log.error(err.getMessage(), err);
	    }
	}
	
	/**
	 * Hide the tool tip
	 * 
	 * @param shell the tip shell 
	 */
	protected static void hideToolTip(Shell shell) {
		if (shell != null) {
			shell.close();
			shell.dispose();
		}
	}

	/**
	 * @return the heightAdjustment
	 */
	public int getHeightAdjustment() {
		return heightAdjustment;
	}

	/**
	 * @param heightAdjustment the heightAdjustment to set
	 */
	public void setHeightAdjustment(int heightAdjustment) {
		this.heightAdjustment = heightAdjustment;
	}

}
