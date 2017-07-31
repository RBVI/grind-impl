package org.cytoscape.grind.presentation;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.property.values.HandleFactory;

import org.cytoscape.grind.viewmodel.GrindGraphView;
import org.cytoscape.grind.viewmodel.GrindVisualLexicon;

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

public class GrindRenderingEngineFactory implements RenderingEngineFactory<CyNetwork> {
	
	private final GrindVisualLexicon grindLexicon;
	// private final AnnotationFactoryManager annMgr;
	private final CyServiceRegistrar registrar;
	
	// private ViewTaskFactoryListener vtfListener;
	
	// private GrindGraphLOD dingGraphLOD;
	
	// private final HandleFactory handleFactory; 

	public GrindRenderingEngineFactory(
			final GrindVisualLexicon grindLexicon,
			// final ViewTaskFactoryListener vtfListener,
			// final AnnotationFactoryManager annMgr,
			// final GrindGraphLOD dingGraphLOD,
			// final HandleFactory handleFactory,
			final CyServiceRegistrar registrar
	) {
		this.grindLexicon = grindLexicon;
		// this.annMgr = annMgr;
		// this.handleFactory = handleFactory;
		// this.vtfListener = vtfListener;
		// this.dingGraphLOD = dingGraphLOD;
		this.registrar = registrar;
	}

	/**
	 * Render given view model by Grind rendering engine.
	 */
	@Override
	public RenderingEngine<CyNetwork> createRenderingEngine(final Object presentationContainer,
			final View<CyNetwork> view) {
		// Validate arguments
		if (presentationContainer == null)
			throw new IllegalArgumentException("Container is null.");

		if (view == null)
			throw new IllegalArgumentException("Cannot create presentation for null view model.");

		if (view instanceof CyNetworkView == false)
			throw new IllegalArgumentException("Grind accepts CyNetworkView only.");

		final CyNetworkView targetView = (CyNetworkView) view;
		GrindGraphView dgv = null;
		
		if (presentationContainer instanceof JComponent || presentationContainer instanceof RootPaneContainer) {
			if (view instanceof GrindGraphView) {
				dgv = (GrindGraphView) view;				
			} else {
				dgv = new GrindGraphView(targetView, grindLexicon, /*vtfListener, annMgr, dingGraphLOD, handleFactory,*/
						registrar);
				dgv.registerServices();
			}
			
			if (presentationContainer instanceof RootPaneContainer) {
				final RootPaneContainer container = (RootPaneContainer) presentationContainer;
				// final InternalFrameComponent ifComp = new InternalFrameComponent(container.getLayeredPane(), dgv);
				// container.setContentPane(ifComp);
			} else {
				final JComponent component = (JComponent) presentationContainer;
				// component.setLayout(new BorderLayout());
				// component.add(dgv.getComponent(), BorderLayout.CENTER);
			}
		} else {
			throw new IllegalArgumentException(
					"frame object is not of type JComponent or RootPaneContainer, which is invalid for this implementation of PresentationFactory");
		}

		return dgv;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return grindLexicon;
	}	
}
