package org.cytoscape.grind.internal.renderer;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.grind.internal.viewmodel.GrindGraphView;
import org.cytoscape.pokemeow.internal.commonUtil;
import org.cytoscape.pokemeow.internal.algebra.Vector2;
import org.cytoscape.service.util.CyServiceRegistrar;

import com.jogamp.nativewindow.NativeSurface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A viewport takes care of initializing OpenGL, creating a visible control,
 * and registering user interactions with it, i. e. mouse/keyboard actions.
 *
 */
public class pmViewport implements GLEventListener, MouseListener, KeyListener
{
	// Current GL context
	private GL4 gl4;

	// Panel that presents GL's frame buffer
	private GLJPanel panel;
	
	// Create a renderer here?
	private GrindRenderer renderer = null;
	
	// reserve view and registrar
	private GrindGraphView view;
	private CyServiceRegistrar registrar;
	
	// Parameters for mouse click
	private Vector2 lastMousePosition = new Vector2(.0f,.0f);
	
	public static enum Operations {
		NO_TASK,
		ADD_NODE, ADD_EDGE, 
		SELECT_DELETE, SELECT_CHANGECOLOR,
		CHANGE_CONTROLPOINT, CHANGE_SRCDEST;
	}
	private Operations mouseState = Operations.ADD_NODE;
	
	public pmViewport(JComponent container, GrindGraphView view, CyServiceRegistrar registrar)
	{
		GLProfile profile = GLProfile.getDefault(); // Use the system's default version of OpenGL
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setDepthBits(24);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(8);
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);

		panel = new GLJPanel(capabilities);
		panel.setIgnoreRepaint(true);
		panel.addGLEventListener(this);
		panel.addMouseListener(this);
		panel.addKeyListener(this);
		
		if (container instanceof JInternalFrame)
		{
			container.setSize(600, 600);
			JInternalFrame JInframe = (JInternalFrame) container;
            JInframe.getContentPane().setLayout(new BorderLayout());
            JInframe.getContentPane().add(panel, BorderLayout.CENTER);
		}
		else
		{
			container.setLayout(new BorderLayout());
			container.add(panel, BorderLayout.CENTER);
		}

	}

	// GLEventListener methods:

	/**
	 * Callback method invoked when the GLJPanel needs to be initialized.
	 * Sets up permanent GL parameters
	 *
	 * @param drawable GLJPanel handle
	 */

	public void init(GLAutoDrawable drawable)
	{
		gl4 = drawable.getGL().getGL4();

		gl4.glEnable(GL4.GL_DEPTH_TEST);
		gl4.glDisable(GL4.GL_CULL_FACE);
		gl4.glDepthFunc(GL.GL_LEQUAL);
		gl4.glEnable(GL4.GL_BLEND);
		gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);

		gl4.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		NativeSurface surface = drawable.getNativeSurface();
		int[] windowUnits = new int[] {100, 100};
		windowUnits = surface.convertToPixelUnits(windowUnits);
		renderer = new GrindRenderer(gl4, view, registrar);		
	}

	/**
	 * Callback method invoked when the GLJPanel needs to be redrawn.
	 * Clears framebuffer and raises the ViewportDisplay event.
	 *
	 * @param drawable GLJPanel handle
	 */

	public void display(GLAutoDrawable drawable)
	{
		long timeStart = System.nanoTime();

		gl4.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl4.glClearDepthf(1.0f);
		gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

		renderer.renderLoop();

		long timeFinish = System.nanoTime();
		float FPS = 1.0f / ((float)(timeFinish - timeStart) * 1e-9f);
//		System.out.println(FPS + " fps");
	}

	/**
	 * Callback method invoked when the GLJPanel is resized.
	 *
	 * @param drawable GLJPanel handle
	 * @param x Horizontal offset of the left edge
	 * @param y Vertical offset of the top edge
	 * @param width New viewport width
	 * @param height New viewport height
	 */

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		gl4.glViewport(x, y, width, height);
	
	}

	/**
	 * Forces the viewport to redraw its contents.
	 */
	public void redraw()
	{
		panel.repaint();
	}

	/**
	 * Frees all resources associated with this viewport
	 * and raises the ViewportDispose event.
	 */

	public void dispose(GLAutoDrawable drawable)
	{
	}
	
    public void mouseClicked(MouseEvent e) {
    	lastMousePosition.x = e.getX();
        lastMousePosition.y = e.getY();
        float posx = 2 * (float) lastMousePosition.x / commonUtil.DEMO_VIEWPORT_SIZE.x - 1;
        float posy = 1.0f - (2 * (float) lastMousePosition.y / commonUtil.DEMO_VIEWPORT_SIZE.y);
        switch (mouseState){
            case NO_TASK:
                System.out.println("No task");
                break;
            case ADD_EDGE:
                System.out.println("Click to add an edge");
                renderer.addanEdge(posx, posy);
                redraw();
                break;
            case ADD_NODE:
            	System.out.println("Click to add a node");
            	renderer.addaNode(posx, posy);
            	redraw();
                break;
            case SELECT_DELETE:
            	System.out.println("Click to delete");
                renderer.checkAndDelete(posx, posy);
                redraw();
                break;
            case SELECT_CHANGECOLOR:
            	System.out.println("Click to change color");
            	renderer.hitNode(posx, posy);
            	redraw();
                break;
            default:
                break;
        }
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseWheelMoved(MouseEvent e){}
    public void mouseDragged(MouseEvent e){
        Vector2 diff;

        if (lastMousePosition == null) {
            lastMousePosition = new Vector2(e.getX(), e.getY());
        } else {
            Vector2 newPosition = new Vector2(e.getX(), e.getY());
            diff = Vector2.subtract(newPosition, lastMousePosition);
            diff.y *= -1.0f;
            lastMousePosition = newPosition;
        }
        float posx = 2 * (float) lastMousePosition.x / commonUtil.DEMO_VIEWPORT_SIZE.x - 1;
        float posy = 1.0f - (2 * (float) lastMousePosition.y / commonUtil.DEMO_VIEWPORT_SIZE.y);
        if(mouseState == Operations.ADD_EDGE)
        	renderer.checkAndResetEdge(posx, posy);
        renderer.checkAndMoveNode(posx, posy);
        		
    }
    public void mouseMoved(MouseEvent e){}
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == 'n' || e.getKeyCode() == 'N')
            mouseState = Operations.ADD_NODE;
        if(e.getKeyCode() == 'e' || e.getKeyCode() == 'E')
            mouseState = Operations.ADD_EDGE;
        if(e.getKeyCode() == 'd' || e.getKeyCode() == 'D')
            mouseState = Operations.SELECT_DELETE;
        if(e.getKeyCode() == 'c' || e.getKeyCode() == 'C')
            mouseState = Operations.SELECT_CHANGECOLOR;
        if(e.getKeyCode() == 'm' || e.getKeyCode() == 'M')
            mouseState = Operations.CHANGE_CONTROLPOINT;
        if(e.getKeyCode() == 'v' || e.getKeyCode() == 'V')
            mouseState = Operations.CHANGE_SRCDEST;
        System.out.println(mouseState);
    }
    public void keyReleased(KeyEvent e){
    	renderer.releaseCheck();
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
