package org.cytoscape.grind.internal.renderer;

/*
 * #%L
 * Cytoscape Grind View/Presentation Impl (grind-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2016 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.cytoscape.service.util.CyServiceRegistrar;

import com.jogamp.opengl.*;

import java.net.URL;

import org.cytoscape.grind.internal.viewmodel.GrindGraphView;
import org.cytoscape.pokemeow.internal.SampleUsage.Demo;
import org.cytoscape.pokemeow.internal.algebra.Vector4;
import org.cytoscape.pokemeow.internal.nodeshape.pmBasicNodeShape;
import org.cytoscape.pokemeow.internal.nodeshape.pmNodeShapeFactory;
import org.cytoscape.pokemeow.internal.rendering.pmShaderParams;
import org.cytoscape.pokemeow.internal.utils.GLSLProgram;

public class GrindRenderer {
	protected final GrindGraphView view;
	final CyServiceRegistrar registrar;
	private pmShaderParams gshaderParam;
	private pmBasicNodeShape mtriangle;
    private pmNodeShapeFactory factory;
    private GL4 gl4;
    int program;
	public GrindRenderer(GrindGraphView view, CyServiceRegistrar registrar, GL4 gl4) {
		this.view = view;
		this.registrar = registrar;
		this.gl4 = gl4;
		// Create the necessary drawing surfaces

		// Start the rendering engine.
		URL pathUV = Demo.class.getResource("shader/flat.vert");
		URL pathFG = Demo.class.getResource("shader/flat.frag");
        program = GLSLProgram.CompileProgram(gl4,
        		pathUV,
                null,null,null,
                pathFG);
        gshaderParam = new pmShaderParams(gl4, program);
        factory = new pmNodeShapeFactory(gl4);
        mtriangle = factory.createNode(gl4,pmNodeShapeFactory.SHAPE_RECTANGLE);
        mtriangle.setColor(new Vector4(1.0f,.0f,.0f,1.0f),
                            new Vector4(.0f,.0f,1.0f,1.0f),(byte)3);
	}
	
	public void renderLoop() {
		// The list of GrindNodeViews is available
		// using view.getGrindNodeViews(), or (optionally)
		// view.getGrindNodeViews(Rectangle2D boundingBox).  The latter is
		// actually for future uses and at this point just returns all nodes.
		//
		// Similarly to get all edge views, you would use view.getGrindEdgeViews().
		// GrindGraphView, GrindNodeView, and GrindEdgeView all implement the View
		// interface, so it's straightforward to get the various visual properties.
		// In the longer term, complicated visual properties (e.g. NODE_LABEL_POSITION) should
		// be calculated and cached in the appropriate view.
        gl4.glUseProgram(program);
        gl4.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);
        factory.drawNode(gl4,mtriangle,gshaderParam);
        
		if (view.updateNeeded()) {
			// Render

			view.updateNeeded(false);
		}
	}
}
