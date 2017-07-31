package org.cytoscape.grind.viewmodel;

import java.awt.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;


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

public class GrindEdgeView extends AbstractGrindViewModel<CyEdge> 
                           implements View<CyEdge> {

	final GrindGraphView gView;
	
	public GrindEdgeView(final GrindVisualLexicon grindLexicon, final GrindGraphView gView, 
	                     final CyEdge edge, final CyServiceRegistrar serviceRegistrar) {
		super(edge, grindLexicon, serviceRegistrar);
		this.gView = gView;
	}

	@Override
	protected <T, V extends T> V getDefaultValue(final VisualProperty<T> vp) {
		return gView.getDefaultValue(vp);
	}

	@Override
	protected <T, V extends T> void applyVisualProperty(final VisualProperty<? extends T> vp, final V value) {
		gView.updateNeeded(true);
		return;
	}

	@Override
	public GrindGraphView getGrindGraphView() { return gView; }

}
