package org.cytoscape.grind.internal;

import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_AFTER;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.IN_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_NETWORK_PANEL_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.NETWORK_ADD_MENU;
import static org.cytoscape.work.ServiceProperties.NETWORK_DELETE_MENU;
import static org.cytoscape.work.ServiceProperties.NETWORK_EDIT_MENU;
import static org.cytoscape.work.ServiceProperties.NETWORK_GROUP_MENU;
import static org.cytoscape.work.ServiceProperties.NETWORK_SELECT_MENU;
import static org.cytoscape.work.ServiceProperties.NODE_ADD_MENU;
import static org.cytoscape.work.ServiceProperties.PREFERRED_ACTION;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.VisualLexicon;

import org.osgi.framework.BundleContext;

// import org.cytoscape.grind.presentation.GrindNavigationRenderingEngineFactory;
import org.cytoscape.grind.internal.presentation.GrindRenderingEngine;
import org.cytoscape.grind.internal.presentation.GrindRenderingEngineFactory;
// import org.cytoscape.grind.presentation.GrindThumbnailRenderingEngineFactory;
import org.cytoscape.grind.internal.viewmodel.GrindViewModelFactory;
import org.cytoscape.grind.internal.viewmodel.GrindVisualLexicon;


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

public class CyActivator extends AbstractCyActivator {
	
	@Override
	public void start(BundleContext bc) {
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
		// startCharts(bc, serviceRegistrar);
		// startGradients(bc, serviceRegistrar);
		// startCustomGraphicsMgr(bc, serviceRegistrar);
		startPresentation(bc, serviceRegistrar);
	}

	private void startPresentation(final BundleContext bc,
	                               final CyServiceRegistrar serviceRegistrar) {

		GrindVisualLexicon gVisualLexicon = new GrindVisualLexicon(/* custom graphics manager? */);
		Properties gVisualLexiconProps = new Properties();
    gVisualLexiconProps.setProperty(ID, "grind");
    registerService(bc, gVisualLexicon, VisualLexicon.class, gVisualLexiconProps);

		GrindViewModelFactory grindNetworkViewFactory = new GrindViewModelFactory(gVisualLexicon, serviceRegistrar);

		GrindRenderingEngineFactory grindRenderingEngineFactory = 
						new GrindRenderingEngineFactory(gVisualLexicon, serviceRegistrar);

		Properties grindRenderingEngineFactoryProps = new Properties();
		grindRenderingEngineFactoryProps.setProperty(ID, "grind");
		registerAllServices(bc, grindRenderingEngineFactory, grindRenderingEngineFactoryProps);


		GrindRenderingEngine renderingEngine = GrindRenderingEngine.getInstance();
		renderingEngine.registerNetworkViewFactory(grindNetworkViewFactory);
		renderingEngine.registerRenderingEngineFactory(NetworkViewRenderer.DEFAULT_CONTEXT, grindRenderingEngineFactory);
		registerService(bc, renderingEngine, NetworkViewRenderer.class, new Properties());
	}

}
