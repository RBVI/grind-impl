package org.cytoscape.grind.internal.viewmodel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.values.HandleFactory;

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

public class GrindViewModelFactory implements CyNetworkViewFactory {

	private final GrindVisualLexicon grindLexicon;
	private final CyServiceRegistrar registrar;

	// private final AnnotationFactoryManager annMgr;
	// private final DingGraphLOD dingGraphLOD;
	// private final HandleFactory handleFactory;

	public GrindViewModelFactory(
			final GrindVisualLexicon grindLexicon, 
			/* final ViewTaskFactoryListener vtfListener,
			final AnnotationFactoryManager annMgr,
			final DingGraphLOD dingGraphLOD,
			final HandleFactory handleFactory, */
			final CyServiceRegistrar registrar
	) {
		this.grindLexicon = grindLexicon;
		/*
		this.vtfListener = vtfListener;
		this.annMgr = annMgr;
		this.dingGraphLOD = dingGraphLOD;
		this.handleFactory = handleFactory;
		*/
		this.registrar = registrar;
	}

	@Override
	public CyNetworkView createNetworkView(final CyNetwork network) {
		if (network == null)
			throw new IllegalArgumentException("Cannot create view without model.");

		final GrindGraphView dgv = new GrindGraphView(network, grindLexicon, registrar);

		dgv.registerServices();

		return dgv;
	}
}
