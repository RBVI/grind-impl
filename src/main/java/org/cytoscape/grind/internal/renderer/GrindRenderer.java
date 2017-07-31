package org.cytoscape.grind.renderer;

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

import org.cytoscape.grind.viewmodel.GrindGraphView;

public class GrindRenderer {
	protected final GrindGraphView view;
	final CyServiceRegistrar registrar;

	public GrindRenderer(GrindGraphView view, CyServiceRegistrar registrar) {
		this.view = view;
		this.registrar = registrar;

		// Create the necessary drawing surfaces

		// Start the rendering engine.  
	}

	private void renderLoop() {
		if (view.updateNeeded()) {
			// Render

			view.updateNeeded(false);
		}
	}
}
