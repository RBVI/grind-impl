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
import java.net.URL;

import org.cytoscape.grind.internal.viewmodel.GrindGraphView;
import org.cytoscape.pokemeow.internal.SampleUsage.Demo;
import org.cytoscape.pokemeow.internal.algebra.Vector4;
import org.cytoscape.pokemeow.internal.edge.pmEdgeFactory;
import org.cytoscape.pokemeow.internal.nodeshape.pmBasicNodeShape;
import org.cytoscape.pokemeow.internal.nodeshape.pmNodeShapeFactory;
import org.cytoscape.pokemeow.internal.rendering.pmShaderParams;
import org.cytoscape.pokemeow.internal.utils.GLSLProgram;
import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import org.cytoscape.pokemeow.internal.algebra.Vector2;
import org.cytoscape.pokemeow.internal.algebra.Vector4;
import org.cytoscape.pokemeow.internal.commonUtil;
import org.cytoscape.pokemeow.internal.edge.pmEdge;
import org.cytoscape.pokemeow.internal.edge.pmEdgeFactory;
import org.cytoscape.pokemeow.internal.line.pmLineFactory;
import org.cytoscape.pokemeow.internal.line.pmLineVisual;
import org.cytoscape.pokemeow.internal.nodeshape.pmBasicNodeShape;
import org.cytoscape.pokemeow.internal.nodeshape.pmNodeShapeFactory;
import org.cytoscape.pokemeow.internal.rendering.pmShaderParams;
import org.cytoscape.pokemeow.internal.utils.GLSLProgram;
import org.w3c.dom.NodeList;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GrindRenderer {
	// GL4 Context
	private GL4 gl4;
	
	// virtual network
	protected final GrindGraphView view;
	final CyServiceRegistrar registrar;
	
	// Node Factory to create / draw nodes
    private pmNodeShapeFactory nodeFactory;
    // Edge Factory to create / draw edges
    private pmEdgeFactory edgeFactory;
    
    // ArrayList for edge/node to keep active edge/node
    private ArrayList<pmEdge> edgeList;
    private ArrayList<pmBasicNodeShape> nodeList;
    
    // program for node / edge
    private int programNode;
    private int programEdge;
    
    // Shader parameterObject for node / edge
    private pmShaderParams gshaderParamNode;
	private pmShaderParams gshaderParamEdge;
    
	



    

    // Temprately keep parameters in program
    private Vector4[] colorList = {
            new Vector4(0.97f, 0.67f, 0.65f, 1.0f),
            new Vector4(0.69f, 0.88f, 0.9f, 1.0f)
    };
    private Integer reactNodeId = -1;
    private pmEdge activeEdge;
    private boolean afterDrag = false;
    private int numOfNodes = 0;
    private int numOfEdges = 0;
    
    private Random random;
    private boolean needFirstCheck = true;
    // For fully covered nodes check
    private IntBuffer queryIDs;
    private ArrayList<Integer> nodeNeedToCheck;
    
	public GrindRenderer(GL4 gl4, GrindGraphView view, CyServiceRegistrar registrar) {
		this.view = view;
		this.registrar = registrar;
		this.gl4 = gl4;
		
		InitializeNodeAndEdgeInterface();
        
		createNodes();
		
		createEdges();
		
//		DoDepthQuery();
	}
	
	private void InitializeNodeAndEdgeInterface() {
        programEdge = GLSLProgram.CompileProgram(gl4,
                Demo.class.getResource("shader/arrow.vert"),
                null, null, null,
                Demo.class.getResource("shader/arrow.frag"));
        programNode = GLSLProgram.CompileProgram(gl4,
                Demo.class.getResource("shader/flat.vert"),
                null,null,null,
                Demo.class.getResource("shader/flat.frag"));
        
        gshaderParamEdge = new pmShaderParams(gl4, programEdge);
        gshaderParamNode = new pmShaderParams(gl4, programNode);
        
        edgeList = new ArrayList();
        nodeList = new ArrayList();
        nodeFactory = new pmNodeShapeFactory(gl4);
        edgeFactory = new pmEdgeFactory(gl4);
        nodeNeedToCheck = new ArrayList();
        random = new Random();
	}
	
	private void createNodes() {
        for(int i=0;i<10;i++){
            pmBasicNodeShape node = nodeFactory.createNode((byte)(numOfNodes%10));
            node.setOrigin(new Vector2(random.nextFloat()-0.5f, random.nextFloat() - 0.5f));
            node.setScale(random.nextFloat() * 0.5f + 0.2f);
            node.setColor(colorList[0]);
            numOfNodes++;
            nodeNeedToCheck.add(i);
            nodeList.add(node);
        }
	}
	
	private void createEdges() {
		int srcNodeId, destNodeId;
		for(int i=0;i<5;i++) {
			srcNodeId = random.nextInt(10);
			destNodeId = random.nextInt(10);
			if(destNodeId == srcNodeId)
				destNodeId = (srcNodeId+1)%10;
			activeEdge = edgeFactory.createEdge((byte)(numOfEdges%13), pmLineVisual.LINE_STRAIGHT, 
					nodeList.get(srcNodeId).origin.x, nodeList.get(srcNodeId).origin.y,
					nodeList.get(destNodeId).origin.x, nodeList.get(destNodeId).origin.y, false);
            edgeList.add(activeEdge);
            numOfEdges++;
		}
	}
	
    private void DoDepthQuery(){
        gl4.glUseProgram(programNode);
        gl4.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);
        gl4.glColorMask(false,false,false,false);
        gl4.glDepthMask(false);
        queryIDs = Buffers.newDirectIntBuffer(new int[numOfNodes]);
        gl4.glGenQueries(numOfNodes, queryIDs);
        IntBuffer tmp = Buffers.newDirectIntBuffer(new int[1]);
        for (int i=0;i<numOfNodes;i++){
            gl4.glBeginQuery(GL4.GL_SAMPLES_PASSED, queryIDs.get(i));

            nodeFactory.drawNode(gl4, nodeList.get(i), gshaderParamNode, true);
            gl4.glEndQuery(GL4.GL_SAMPLES_PASSED);
            gl4.glGetQueryObjectiv(queryIDs.get(i), GL4.GL_QUERY_RESULT, tmp);
            if (tmp.get(0) == 0)
                nodeList.get(i).visible = false;
        }
        gl4.glColorMask(true,true,true,true);
        gl4.glDepthMask(true);
     }

	private void DoDepthQuery(pmBasicNodeShape refNode, int refIdx){
        gl4.glUseProgram(programNode);
        gl4.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);
        gl4.glColorMask(false,false,false,false);
        gl4.glDepthMask(false);
        queryIDs = Buffers.newDirectIntBuffer(new int[numOfNodes]);
        gl4.glGenQueries(numOfNodes, queryIDs);
        IntBuffer tmp = Buffers.newDirectIntBuffer(new int[1]);
        pmBasicNodeShape node;
        for (int i=0; i<numOfNodes; i++){
            node = nodeList.get(i);
            if(i==refIdx)
                continue;
            if(refNode.isOutsideBoundingBox(node.origin.x, node.origin.y))
                continue;
            gl4.glBeginQuery(GL4.GL_SAMPLES_PASSED, queryIDs.get(i));
            nodeFactory.drawNode(gl4, node, gshaderParamNode);
            gl4.glEndQuery(GL4.GL_SAMPLES_PASSED);
            gl4.glGetQueryObjectiv(queryIDs.get(i), GL4.GL_QUERY_RESULT, tmp);
            if (tmp.get(0) == 0)
                nodeList.get(i).visible = false;
        }
//	        nodeList.get(refIdx).visible = true;
        gl4.glColorMask(true,true,true,true);
        gl4.glDepthMask(true);
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
		gl4.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);
		gl4.glUseProgram(programEdge);
		for (pmEdge edge: edgeList)
            edgeFactory.drawEdge(edge, gshaderParamEdge);
		
		gl4.glUseProgram(programNode);
        for(pmBasicNodeShape node: nodeList) {
            if (node.visible)
                nodeFactory.drawNode(gl4, node, gshaderParamNode, true);
        }
		if (view.updateNeeded()) {
			// Render

			view.updateNeeded(false);
		}
	}
	public void addaNode(float posx, float posy) {
      numOfNodes++;
      pmBasicNodeShape node = nodeFactory.createNode((byte)(numOfNodes%10));
      node.setOrigin(new Vector2(posx, posy));
      node.setColor(colorList[numOfNodes%2]);
      node.setScale(0.5f);
      node.visible = true;
      nodeList.add(node);
      DoDepthQuery(node, numOfNodes-1);
	}
	public void addanEdge(float posx, float posy) {
      activeEdge = edgeFactory.createEdge((byte)(numOfNodes%13), pmLineVisual.LINE_CUBIC_CURVE, .0f,.0f,posx,posy,false);
      edgeList.add(activeEdge);
      if(needFirstCheck){
          if(edgeList.get(numOfEdges)._destArrow != null){
              needFirstCheck = false;
              edgeList.get(numOfEdges)._destArrow.isfirst = true;
          }
          else if(edgeList.get(numOfEdges)._line.patternList!=null){
              needFirstCheck = false;
              edgeList.get(numOfEdges)._line.patternList[0].isfirst = true;
          }
      }

      numOfEdges ++;
	}
	public void checkAndDelete(float posx, float posy){
        for(int i=numOfNodes-1; i>-1; i-- ){
            pmBasicNodeShape tmp = nodeList.get(i);
            if(tmp.isHit(posx,posy)){
                nodeList.remove(i);
                numOfNodes--;
                nodeFactory.deleteNode(gl4, tmp);
            }
        }
        for(int i=numOfEdges-1; i>-1; i-- ){
            pmEdge tmp = edgeList.get(i);
            if(tmp.isHit(posx,posy)){
                edgeList.remove(i);
                numOfEdges--;
                edgeFactory.deleteEdge(gl4, tmp);
            }
        }
    }
	public void hitNode(float posx, float posy) {
        int idx = 0;
        for (pmBasicNodeShape node : nodeList) {
            if (node.isHit(posx, posy)) {
                node.setColor(new Vector4(1.0f,.0f,.0f,1.0f));
                reactNodeId =  idx;
            }
            idx++;
        }
        reactNodeId = -1;
    }
	public void releaseCheck() {
        if(afterDrag && reactNodeId!=-1){
            DoDepthQuery(nodeList.get(reactNodeId), reactNodeId);
            reactNodeId = -1;
            afterDrag = false;
            System.out.println("Drag Release Query");
        }
	}
	public void checkAndMoveNode(float posx, float posy) {
		if(reactNodeId == -1)
			return;
        nodeList.get(reactNodeId).setOrigin(new Vector2(posx, posy));
//        for (Integer index : NodeEdgeMap.get(reactNodeId)) {
//            //change src of edge
//            if (index >= 0)
//                edgeList.get(index).resetSrcAndDest(posx, posy, 1);
//            else//change dest
//                edgeList.get(-index).resetSrcAndDest(posx, posy, 0);
//        }
        afterDrag = true;
	}
	public void checkAndResetEdge(float posx, float posy) {
		activeEdge.resetSrcAndDest(posx,posy,false);
	}
}
