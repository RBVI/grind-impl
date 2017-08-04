package org.cytoscape.grind.viewmodel;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.Icon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.AddedEdgeViewsEvent;
import org.cytoscape.view.model.events.AddedNodeViewsEvent;

import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.ObjectPosition;

import org.cytoscape.grind.presentation.GrindRenderingEngine;

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

public class GrindGraphView extends AbstractGrindViewModel<CyNetwork> 
                            implements CyNetworkView, RenderingEngine<CyNetwork>,
                                       Printable {

	// This boolean is the key switch for triggering repainting.
	public boolean updateNeeded = false;

	private static Pattern CG_SIZE_PATTERN = Pattern.compile("NODE_CUSTOMGRAPHICS_SIZE_[1-9]");

	/**
	 * Enum to identify canvases - used in getCanvas(Canvas canvasId)
	 */
	public enum Canvas {
		BACKGROUND_CANVAS, NETWORK_CANVAS, FOREGROUND_CANVAS;
	}

	public enum ShapeType {
		NODE_SHAPE, LINE_TYPE, ARROW_SHAPE;
	}

	boolean servicesRegistered = false;

	/**
	 * Holds the NodeView data for the nodes that are visible. This will change
	 * as nodes are hidden from the view.
	 */
	private final Map<CyNode, GrindNodeView> nodeViewMap;
	private final Map<CyEdge, GrindEdgeView> edgeViewMap;
	@SuppressWarnings("rawtypes")
	private final Map<VisualProperty, Object> defaultMap;

	// private final Map<VisualProperty, Object> propertyMap;

	private final Properties props;

	public GrindGraphView(
			final CyNetworkView view,
			final GrindVisualLexicon grindLexicon,
			final CyServiceRegistrar registrar
	) {
		this(view.getModel(), grindLexicon, registrar);
	}

	@SuppressWarnings("rawtypes")
	public GrindGraphView(
			final CyNetwork model,
			final GrindVisualLexicon grindLexicon,
			final CyServiceRegistrar registrar
	) {
		super(model, grindLexicon, registrar);
		// propertyMap = new ConcurrentHashMap<VisualProperty, Object>();
		nodeViewMap = new ConcurrentHashMap<CyNode, GrindNodeView>(16, 0.75f, 2);
		edgeViewMap = new ConcurrentHashMap<CyEdge, GrindEdgeView>(16, 0.75f, 2);
		defaultMap = new ConcurrentHashMap<VisualProperty, Object>(16, 0.75f, 2);

		this.props = new Properties();

		// Create our renderer
		
	}

	/**
	 * Resize the network view to the size of the canvas and redraw it. 
	 */
	@Override
	public void fitContent() {
	}

	/**
	 * Redraw the canvas.
	 */
	@Override
	public void updateView() {
	}

	@Override
	public void fitSelected() {
	}

	@Override
	public Collection<View<CyNode>> getNodeViews() {
		// This cast is always safe in current implementation.
		// Also, since this is a concurrent collection, this operation is thread-safe.
		return (Collection) nodeViewMap.values();
	}

	@Override
	public Collection<View<CyEdge>> getEdgeViews() {
		// This cast is always safe in current implementation.
		// Also, since this is a concurrent collection, this operation is thread-safe.
		return (Collection) edgeViewMap.values();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<View<? extends CyIdentifiable>> getAllViews() {
		final List views = new ArrayList(nodeViewMap.size() + edgeViewMap.size() + 1);
		Collection nodeViews = nodeViewMap.values();
		views.addAll(nodeViews);
		Collection edgeViews = edgeViewMap.values();
		views.addAll(edgeViews);
		views.add(this);
		return views;
	}

	@Override
	protected <T, V extends T> void applyVisualProperty(final VisualProperty<? extends T> vpOriginal, final V value) {
		updateNeeded = true;

		/*
		final VisualProperty<?> vp = vpOriginal;

		if (vp == GrindVisualLexicon.NETWORK_NODE_SELECTION) {
			boolean b = ((Boolean) value).booleanValue();
			if (b)
				enableNodeSelection();
			else
				disableNodeSelection();
		} else if (vp == GrindVisualLexicon.NETWORK_EDGE_SELECTION) {
			boolean b = ((Boolean) value).booleanValue();
			if (b)
				enableEdgeSelection();
			else
				disableEdgeSelection();
		} else if (vp == BasicVisualLexicon.NETWORK_BACKGROUND_PAINT) {
			setBackgroundPaint((Paint) value);
		} else if (vp == BasicVisualLexicon.NETWORK_CENTER_X_LOCATION) {
			final double x = (Double) value;
			if (x != m_networkCanvas.m_xCenter)
				setCenter(x, m_networkCanvas.m_yCenter);
		} else if (vp == BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION) {
			final double y = (Double) value;
			if (y != m_networkCanvas.m_yCenter)
				setCenter(m_networkCanvas.m_xCenter, y);
		} else if (vp == BasicVisualLexicon.NETWORK_SCALE_FACTOR) {
			setZoom(((Double) value).doubleValue());
		} else if (vp == BasicVisualLexicon.NETWORK_WIDTH) {
			// This actually sets the size on the canvas, so we need to make sure
			// this runs on the AWT thread
			ViewUtil.invokeOnEDT(() -> {
				m_networkCanvas.setSize(((Double)value).intValue(), m_networkCanvas.getHeight());
			});
		} else if (vp == BasicVisualLexicon.NETWORK_HEIGHT) {
			// This actually sets the size on the canvas, so we need to make sure
			// this runs on the AWT thread
			ViewUtil.invokeOnEDT(() -> {
				m_networkCanvas.setSize(m_networkCanvas.getWidth(), ((Double)value).intValue());
			});
		}
		*/
	}

	@Override
	public void clearValueLock(final VisualProperty<?> vp) {
		directLocks.remove(vp);
		allLocks.remove(vp);
		applyVisualProperty(vp, visualProperties.get(vp)); // always apply the regular vp
	}

	@Override
	public <T> T getVisualProperty(final VisualProperty<T> vp) {
		// Object value = null;
		if (visualProperties.containsKey(vp))
			return (T) visualProperties.get(vp);

		return null;

		/*
		if (vp == GrindVisualLexicon.NETWORK_NODE_SELECTION) {
			value = nodeSelectionEnabled();
		} else if (vp == GrindVisualLexicon.NETWORK_EDGE_SELECTION) {
			value = edgeSelectionEnabled();
		} else if (vp == BasicVisualLexicon.NETWORK_BACKGROUND_PAINT) {
			value = getBackgroundPaint();
		} else if (vp == BasicVisualLexicon.NETWORK_CENTER_X_LOCATION) {
			value = getCenter().getX();
		} else if (vp == BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION) {
			value = getCenter().getY();
		} else if (vp == BasicVisualLexicon.NETWORK_SCALE_FACTOR) {
			value = getZoom();
		} else {
			value = super.getVisualProperty(vp);
		}

		return (T) value;
		*/
	}

	@Override
	public View<CyNode> getNodeView(final CyNode node) {
		return (View<CyNode>) nodeViewMap.get(node);
	}

	@Override
	public View<CyEdge> getEdgeView(final CyEdge edge) {
		return (View<CyEdge>) edgeViewMap.get(edge);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, V extends T> void setViewDefault(VisualProperty<? extends T> vp, V defaultValue) {
		if (vp.shouldIgnoreDefault())
			return;

		final Class<?> targetType = vp.getTargetDataType();

		// Visibility should be applied directly to each edge view.
		if (vp == BasicVisualLexicon.EDGE_VISIBLE) {
			// TODO: Run in parallel.  fork/join?
			applyToAllEdges(vp, defaultValue);
			return;
		}

		// In DING, there is no default W, H, and D.
		// Also, visibility should be applied directly to each node view.
		if (vp == BasicVisualLexicon.NODE_SIZE 
				|| vp == BasicVisualLexicon.NODE_WIDTH
				|| vp == BasicVisualLexicon.NODE_HEIGHT
				|| vp == BasicVisualLexicon.NODE_VISIBLE
				|| CG_SIZE_PATTERN.matcher(vp.getIdString()).matches()) {
			// TODO: Run in parallel.  fork/join?
			applyToAllNodes(vp, defaultValue);
			return;
		}

		/*
		if ((VisualProperty<?>) vp instanceof CustomGraphicsVisualProperty) {
			if (defaultValue != NullCustomGraphics.getNullObject()) {
				applyToAllNodes(vp, defaultValue);
				return;
			}
		}
		*/

		if (vp != GrindVisualLexicon.NODE_LABEL_POSITION && defaultValue instanceof ObjectPosition) {
			// This is a CustomGraphicsPosition.
			if (defaultValue.equals(ObjectPosition.DEFAULT_POSITION) == false) {
				applyToAllNodes(vp, defaultValue);
				return;
			}
		}

		if (targetType == CyNode.class) {
			// nodeViewDefaultSupport.setViewDefault((VisualProperty<V>)vp, defaultValue);
		} else if (targetType == CyEdge.class) {
			// edgeViewDefaultSupport.setViewDefault((VisualProperty<V>)vp, defaultValue);
		} else if (targetType == CyNetwork.class) {
			// For networks, just set as regular visual property value.  (No defaults)
			this.setVisualProperty(vp, defaultValue);
		}
		updateNeeded = true;
	}


	private final <T, V extends T> void applyToAllNodes(final VisualProperty<? extends T> vp, final V defaultValue) {
		final Collection<GrindNodeView> nodes = this.nodeViewMap.values();
		for (final GrindNodeView n : nodes)
			n.setVisualProperty(vp, defaultValue);
		updateNeeded = true;
	}

	private final <T, V extends T> void applyToAllEdges(final VisualProperty<? extends T> vp, final V defaultValue) {
		final Collection<GrindEdgeView> edges = this.edgeViewMap.values();
		for (final GrindEdgeView e : edges)
			e.setVisualProperty(vp, defaultValue);
		updateNeeded = true;
	}

	@Override
	public String toString() {
		return "GrindGraphView: suid=" + suid + ", model=" + model;
	}

	public void registerServices() {
		if (servicesRegistered)
			return;

		serviceRegistrar.registerAllServices(this, new Properties());
		servicesRegistered = true;
	}

	@Override
	public void dispose() {
		if (!servicesRegistered)
			return;

		serviceRegistrar.unregisterAllServices(this);
		servicesRegistered = false;

		// cyAnnotator.dispose();
		// m_networkCanvas.dispose();
	}

	@Override
	protected <T, V extends T> V getDefaultValue(VisualProperty<T> vp) {
		if (defaultMap.containsKey(vp))
			return (V)defaultMap.get(vp);
		return null;
	}

	public <V> void setDefaultValue(VisualProperty<V> vp, V value) {
		defaultMap.put(vp, value);
	}

	@Override
	public String getRendererId() {
		return GrindRenderingEngine.ID;
	}

	@Override
	public GrindGraphView getGrindGraphView() {
		return this;
	}

	private final GrindNodeView addNodeView(final CyNode node) {
		final GrindNodeView oldView = nodeViewMap.get(node);

		if (oldView != null)
			return oldView;

		final GrindNodeView gNodeView = new GrindNodeView(lexicon, this, node, serviceRegistrar);
		nodeViewMap.put(node, gNodeView);
		updateNeeded = true;
		getEventHelper().addEventPayload((CyNetworkView) this, (View<CyNode>) gNodeView, AddedNodeViewsEvent.class);

		return gNodeView;
	}

	private final GrindEdgeView addEdgeView(final CyEdge edge) {
		final GrindNodeView sourceNV;
		final GrindNodeView targetNV;
		final GrindEdgeView gEdgeView;

		final GrindEdgeView oldView = edgeViewMap.get(edge);
		if (oldView != null)
			return oldView;

		sourceNV = addNodeView(edge.getSource());
		targetNV = addNodeView(edge.getTarget());

		gEdgeView = new GrindEdgeView(lexicon, this, edge, serviceRegistrar);
		edgeViewMap.put(edge, gEdgeView);
		updateNeeded = true;
		getEventHelper().addEventPayload((CyNetworkView) this, (View<CyEdge>) gEdgeView, AddedEdgeViewsEvent.class);
		return gEdgeView;
	}


	// RenderingEngine //

	@Override
	public Image createImage(int width, int height) {
		return null;
	}

	@Override
	public void printCanvas(Graphics printCanvas) {
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int w, int h) {
		// return VisualPropertyIconFactory.createIcon(value, w, h);
		return null;
	}

	@Override
	public Printable createPrintable() {
		return this;
	}

	@Override
	public Properties getProperties() {
		return this.props;
	}

	@Override
	public VisualLexicon getVisualLexicon() {
		return lexicon;
	}

	@Override
	public CyNetworkView getViewModel() {
		return this;
	}

	public boolean updateNeeded() { return updateNeeded; }
	public void updateNeeded(boolean update) {
		updateNeeded = update;
	}

	// Printable //
  @Override
  public int print(Graphics g, PageFormat pageFormat, int page) {
		return 0;
	}

	// Internal interface //
	public Collection<GrindNodeView> getGrindNodeViews() {
		return nodeViewMap.values();
	}

	public Collection<GrindEdgeView> getGrindEdgeViews() {
		return edgeViewMap.values();
	}

	// This is for future use.  Given a bounding rectangle, return all of the nodes
	// within that rectangle.  At this point, we return all nodes, but in the future,
	// we might have a need for a QuadTree or OctTree
	public Collection<GrindNodeView> getGrindNodeViews(Rectangle2D boundingBox) {
		return getGrindNodeViews();
	}

}
