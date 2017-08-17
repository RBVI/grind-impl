package org.cytoscape.grind.internal.presentation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.presentation.RenderingEngineFactory;

public class GrindRenderingEngine implements NetworkViewRenderer {
	public static final String ID = "org.cytoscape.grind";
    public static final String DISPLAY_NAME = "Cytoscape Grind 2D";

	private CyNetworkViewFactory viewFactory;
	private Map<String, RenderingEngineFactory<CyNetwork>> renderingEngineFactories;

	private static GrindRenderingEngine instance = new GrindRenderingEngine();

	public static GrindRenderingEngine getInstance() {
		return instance;
	}

	public GrindRenderingEngine() {
		renderingEngineFactories = new ConcurrentHashMap<String, RenderingEngineFactory<CyNetwork>>(16, 0.75f, 2);
	}

	public void registerNetworkViewFactory(CyNetworkViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	public void registerRenderingEngineFactory(String contextId,
	                                           RenderingEngineFactory<CyNetwork> engineFactory) {
		renderingEngineFactories.put(contextId, engineFactory);
	}

	@Override
	public RenderingEngineFactory<CyNetwork> getRenderingEngineFactory(String contextId) {
		RenderingEngineFactory<CyNetwork> factory = renderingEngineFactories.get(contextId);
		if (factory != null)
			return factory;

		return renderingEngineFactories.get(DEFAULT_CONTEXT);
	}

	@Override
	public CyNetworkViewFactory getNetworkViewFactory() {
		return viewFactory;
	}

	@Override
	public String getId() { return ID; }

	@Override
	public String toString() { return DISPLAY_NAME; }
}
