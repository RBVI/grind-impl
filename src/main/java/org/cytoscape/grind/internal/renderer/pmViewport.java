package org.cytoscape.grind.internal.renderer;
import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import org.cytoscape.grind.internal.viewmodel.GrindGraphView;
import org.cytoscape.service.util.CyServiceRegistrar;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

public class pmViewport implements GLEventListener{
	
	// Panel that presents GL's frame buffer
	private GLJPanel panel;
	
	// Current GL context
	private GL4 gl4;
	
	// jogl renderer
	private GrindRenderer renderer;
	
	private GrindGraphView view;
	
	private CyServiceRegistrar registrar;
	
	public pmViewport(JComponent container, GrindGraphView view, CyServiceRegistrar registrar) {
		this.view = view;
		this.registrar = registrar;
		
		final GLProfile glProfile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(glProfile);
		capabilities.setDepthBits(24);
		capabilities.setSampleBuffers(true);
		capabilities.setNumSamples(8);
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);
		
		panel = new GLJPanel(capabilities);
		panel.setIgnoreRepaint(true);
		panel.addGLEventListener(this);
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
        final FPSAnimator animator = new FPSAnimator(panel, 60, true);

        animator.start();
	}
	
	@Override
    public void init(GLAutoDrawable drawable){
		gl4 = drawable.getGL().getGL4();
		gl4.glEnable(GL4.GL_DEPTH_TEST);		
		gl4.glDisable(GL4.GL_CULL_FACE);
		gl4.glDepthFunc(GL.GL_LEQUAL);
		gl4.glEnable(GL4.GL_BLEND);
		gl4.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		renderer = new GrindRenderer(view, registrar, gl4);
    }
    @Override
    public void display(GLAutoDrawable drawable){
		gl4.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
//		gl4.glClearDepthf(1.0f);
//		gl4.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
    	renderer.renderLoop();
    }
    @Override
    public void dispose(GLAutoDrawable drawable){}
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    	gl4.glViewport(x, y, width, height);
    }
}
