/*  Copyright 2009 Tonny Kohar.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.library.swing.envisage.graph;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import org.qi4j.library.swing.envisage.graph.event.ItemSelectionEvent;
import org.qi4j.library.swing.envisage.graph.event.ItemSelectionListener;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * @author Tonny Kohar (tonny.kohar@gmail.com)
 */
public class GraphDisplay extends Display
{
    public static final String NAME_LABEL = "name";
    public static final String USER_OBJECT = "userObject";
        
    private static final String TREE = "graph";
    private static final String TREE_NODES = "graph.nodes";
    private static final String TREE_EDGES = "graph.edges";
    public static final String USES_EDGES = "uses.edges";

    private static final String FILTER_ACTION = "filter";
    private static final String REPAINT_ACTION = "repaint";
    private static final String FULL_PAINT_ACTION = "fullPaint";
    private static final String ANIMATE_PAINT_ACTION = "animatePaint";
    private static final String ANIMATE_ACTION = "animate";
    private static final String LAYOUT_ACTION = "layout";
    private static final String SUB_LAYOUT_ACTION = "subLayout";
    private static final String AUTO_ZOOM_ACTION = "autoZoom";

    private LabelRenderer nodeRenderer;
    private EdgeRenderer edgeRenderer;

    private int orientation = Constants.ORIENT_LEFT_RIGHT;

    public GraphDisplay()
    {
        super(new Visualization());
        
        Color BACKGROUND = Color.WHITE;
        Color FOREGROUND = Color.BLACK;

        // TODO put into ResourceBundle
        setBackground( BACKGROUND );
        setForeground( FOREGROUND );

        nodeRenderer = new LabelRenderer(NAME_LABEL);
        nodeRenderer.setRenderType( AbstractShapeRenderer.RENDER_TYPE_FILL);
        nodeRenderer.setHorizontalAlignment(Constants.LEFT);
        nodeRenderer.setRoundedCorner(8,8);
        edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE, Constants.EDGE_ARROW_FORWARD);

        //Predicate pDirect = (Predicate) ExpressionParser.parse("ISEDGE() AND relation == 'direct'", true);
        //Predicate pParent = (Predicate) ExpressionParser.parse("ISEDGE() AND relation == 'parent'", true);
        //Predicate pPreceding = (Predicate) ExpressionParser.parse("ISEDGE() AND relation == 'preceding'", true);

        DefaultRendererFactory rf = new DefaultRendererFactory(nodeRenderer);
        rf.add(new InGroupPredicate(TREE_EDGES), edgeRenderer);
        //rf.add(pDirect, new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_NONE));
        //rf.add(pParent, new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD));
        //rf.add(pPreceding, new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD)); 
        m_vis.setRendererFactory(rf);

        // colors
        ItemAction nodeColor = new NodeColorAction(TREE_NODES);
        ItemAction textColor = new ColorAction(TREE_NODES, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0));
        m_vis.putAction("textColor", textColor);

        ItemAction edgeColor = new ColorAction(TREE_EDGES, VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));
        ItemAction edgeArrow = new ColorAction(TREE_EDGES, VisualItem.FILLCOLOR, ColorLib.rgb(0,0,0));

        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        m_vis.putAction(REPAINT_ACTION, repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        m_vis.putAction(FULL_PAINT_ACTION, fullPaint);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(TREE_NODES));
        animatePaint.add(new RepaintAction());
        m_vis.putAction(ANIMATE_PAINT_ACTION, animatePaint);

        // create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(TREE, orientation, 50, 0, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25,300));
        //RadialTreeLayout treeLayout = new RadialTreeLayout(TREE);
        m_vis.putAction(LAYOUT_ACTION, treeLayout);

        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(TREE, orientation);
        m_vis.putAction(SUB_LAYOUT_ACTION, subLayout);

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new FisheyeTreeFilter(TREE, 2));
        filter.add(new FontAction(TREE_NODES, FontLib.getFont("Tahoma", 16)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        filter.add(edgeArrow);
        m_vis.putAction(FILTER_ACTION, filter);

        // animated transition
        AutoPanAction autoPan = new AutoPanAction();
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(autoPan);
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(TREE));
        animate.add(new LocationAnimator(TREE_NODES));
        animate.add(new ColorAnimator(TREE_NODES));
        animate.add(new RepaintAction());
        m_vis.putAction(ANIMATE_ACTION, animate);
        m_vis.alwaysRunAfter(FILTER_ACTION, ANIMATE_ACTION);

        m_vis.putAction( AUTO_ZOOM_ACTION, new AutoZoomAction() );

        // initialize the display
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new PanControl());
        addControlListener(new FocusControl(1, FILTER_ACTION));
        addControlListener(new ItemSelectionControl());

        // set orientation
        nodeRenderer.setHorizontalAlignment(Constants.LEFT);
        edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
        edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
        edgeRenderer.setVerticalAlignment1(Constants.CENTER);
        edgeRenderer.setVerticalAlignment2(Constants.CENTER);
        NodeLinkTreeLayout rtl = (NodeLinkTreeLayout)m_vis.getAction(LAYOUT_ACTION);
        CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout)m_vis.getAction(SUB_LAYOUT_ACTION);
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }

    public void run (Graph graph)
    {
        m_vis.add(TREE, graph);
        run();
        m_vis.run(AUTO_ZOOM_ACTION);

        // disable edges interactive
        m_vis.setInteractive(TREE_EDGES, null, false);

    }

    public void run()
    {
        m_vis.run(FILTER_ACTION);
    }


    /** select the specified object
     * @param object the object to select eg: Descriptor 
     * */
    public void setSelectedValue(Object object)
    {
        if (object == null)
        {
            return;
        }

        VisualItem item = null;

        Iterator iter = m_vis.items(TREE);
        while (iter.hasNext())
        {
            VisualItem tItem = (VisualItem)iter.next();
            if (!(tItem instanceof NodeItem) )
            {
                continue;
            }
            Object tObj = tItem.get(USER_OBJECT);
            if (tObj.equals( object))
            {
                item = tItem;
                break;
            }
        }

        if (item != null)
        {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            ts.setTuple(item);
            m_vis.run(FILTER_ACTION);
        }
    }

    /**
     * Adds a listener that's notified each time a change to the selection occurs.
     *
     * @param listener the ItemSelectionListener to add
     */
    public void addItemSelectionListener( ItemSelectionListener listener )
    {
        listenerList.add( ItemSelectionListener.class, listener );
    }

    /**
     * Removes a listener from the list that's notified each time a change to the selection occurs.
     *
     * @param listener the ItemSelectionListener to remove
     */
    public void removeItemSelectionListener( ItemSelectionListener listener )
    {
        listenerList.remove( ItemSelectionListener.class, listener );
    }

    protected void fireSelectionValueChanged( ItemSelectionEvent evt)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if( listeners[ i ] == ItemSelectionListener.class )
            {
                ( (ItemSelectionListener) listeners[ i + 1 ] ).valueChanged( evt );
            }
        }
    }

    public class AutoZoomAction extends Action
    {
        public void run(double frac) {
            int duration = 1000;
            int margin = 50;
            Visualization vis = getVisualization();
            Rectangle2D bounds = vis.getBounds(Visualization.ALL_ITEMS);
            GraphicsLib.expand(bounds, margin + (int)(1/getScale()));
            DisplayLib.fitViewToBounds(GraphDisplay.this, bounds, duration);
        }
    }

    public class AutoPanAction extends Action {
        private Point2D m_start = new Point2D.Double();
        private Point2D m_end   = new Point2D.Double();
        private Point2D m_cur   = new Point2D.Double();
        private int     m_bias  = 150;

        public void run(double frac) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            if ( ts.getTupleCount() == 0 )
                return;

            if ( frac == 0.0 ) {
                int xbias=0, ybias=0;

                xbias = m_bias;
                switch ( orientation ) {
                case Constants.ORIENT_LEFT_RIGHT:

                    break;
                case Constants.ORIENT_RIGHT_LEFT:
                    xbias = -m_bias;
                    break;
                case Constants.ORIENT_TOP_BOTTOM:
                    ybias = m_bias;
                    break;
                case Constants.ORIENT_BOTTOM_TOP:
                    ybias = -m_bias;
                    break;
                }

                VisualItem vi = (VisualItem)ts.tuples().next();
                m_cur.setLocation(getWidth()/2, getHeight()/2);
                getAbsoluteCoordinate(m_cur, m_start);
                m_end.setLocation(vi.getX()+xbias, vi.getY()+ybias);
            } else {
                m_cur.setLocation(m_start.getX() + frac*(m_end.getX()-m_start.getX()),
                                  m_start.getY() + frac*(m_end.getY()-m_start.getY()));
                panToAbs(m_cur);
            }
        }
    }

    public class NodeColorAction extends ColorAction
    {

        public NodeColorAction(String group)
        {
            super(group, VisualItem.FILLCOLOR);
        }

        public int getColor(VisualItem item) {
            if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS) )
                return ColorLib.rgb(255,190,190);
            else if ( m_vis.isInGroup(item, Visualization.FOCUS_ITEMS) )
                return ColorLib.rgb(198,229,229);
            else if ( item.getDOI() > -1 )
                return ColorLib.rgb(164,193,193);
            else
                return ColorLib.rgba(255,255,255,0);
        }
    }

    public class ItemSelectionControl extends ControlAdapter
    {
        public final void itemClicked( VisualItem anItem, MouseEvent anEvent )
        {
            if (!anItem.canGet( USER_OBJECT, Object.class ))
            {
                 return;
            }
            Object object =  anItem.get( USER_OBJECT );
            ItemSelectionEvent evt = new ItemSelectionEvent( this, object);
            fireSelectionValueChanged( evt );
        }
    }
    
}
