/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * -------------------------------------------------------------------
 *
 * History
 *   04.02.2008 (Fabian Dill): created
 */
package org.knime.workbench.editor2;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.workbench.editor2.commands.CreateMetaNodeCommand;
import org.knime.workbench.editor2.commands.CreateNodeCommand;
import org.knime.workbench.editor2.commands.InsertNewNodeCommand;
import org.knime.workbench.editor2.commands.ReplaceNodeCommand;
import org.knime.workbench.editor2.editparts.ConnectionContainerEditPart;
import org.knime.workbench.editor2.editparts.NodeContainerEditPart;
import org.knime.workbench.editor2.editparts.WorkflowRootEditPart;
import org.knime.workbench.editor2.figures.ProgressPolylineConnection;
import org.knime.workbench.repository.NodeUsageRegistry;
import org.knime.workbench.repository.RepositoryFactory;
import org.knime.workbench.repository.model.AbstractNodeTemplate;
import org.knime.workbench.repository.model.MetaNodeTemplate;
import org.knime.workbench.repository.model.NodeTemplate;

/**
 *
 * @author Fabian Dill, University of Konstanz
 */
public class NodeTemplateDropTargetListener2 implements TransferDropTargetListener {

    /**
     *
     */
    private static final Color BLACK = new Color(null, 0, 0, 0);

    /**
     *
     */
    private static final Color RED = new Color(null, 255, 0, 0);

    private static final NodeLogger LOGGER = NodeLogger.getLogger(NodeTemplateDropTargetListener2.class);

    private final EditPartViewer m_viewer;

    private NodeContainerEditPart m_markedNode;

    private ConnectionContainerEditPart m_markedEdge;

    private int nodeCount;

    private int edgeCount;

    private NodeContainerEditPart node;

    private ConnectionContainerEditPart edge;

    public NodeTemplateDropTargetListener2(final EditPartViewer viewer) {
        m_viewer = viewer;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Transfer getTransfer() {
        return LocalSelectionTransfer.getTransfer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled(final DropTargetEvent event) {
        AbstractNodeTemplate snt = getSelectionNodeTemplate();
        if (snt != null) {
            event.feedback = DND.FEEDBACK_SELECT;
            event.operations = DND.DROP_COPY;
            event.detail = DND.DROP_COPY;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragEnter(final DropTargetEvent event) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragLeave(final DropTargetEvent event) {
        // do nothing
    }

    /**
     *
     * @param event drop target event containing the position (relative to whole display)
     * @return point converted to the editor coordinates
     */
    protected Point getDropLocation(final DropTargetEvent event) {
        /* NB: don't break in this method - it ruins the cursor location! */
        event.x = event.display.getCursorLocation().x;
        event.y = event.display.getCursorLocation().y;
        Point p =
            new Point(m_viewer.getControl().toControl(event.x, event.y).x, m_viewer.getControl().toControl(event.x,
                event.y).y);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragOperationChanged(final DropTargetEvent event) {
        // do nothing -> all is handled during "drop"
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("restriction")
    @Override
    public void dragOver(final DropTargetEvent event) {
        WorkflowManager wfm = ((WorkflowRootEditPart)m_viewer.getRootEditPart().getContents()).getWorkflowManager();
        node = null;
        edge = null;
        nodeCount = 0;
        edgeCount = 0;

        // edge-/nodedist
        double edgedist = Integer.MAX_VALUE;
        double nodedist = Integer.MAX_VALUE;
        // hitbox size: (-8 to 8 = 16) * (-8 to 8 = 16)
        for (int i = -8; i < 9; i++) {
            for (int j = -8; j < 9; j++) {
                Point dropLocation = getDropLocation(event);
                EditPart ep = m_viewer.findObjectAt(dropLocation.getTranslated(i, j));
                if (ep instanceof NodeContainerEditPart) {
                    double temp = dropLocation.getDistance(dropLocation.getTranslated(i, j));
                    // choose nearest node to mouse position
                    if (nodedist >= temp) {
                        node = (NodeContainerEditPart)ep;
                        nodedist = temp;
                    }
                    nodeCount++;
                } else if (ep instanceof ConnectionContainerEditPart) {
                    double temp = dropLocation.getDistance(dropLocation.getTranslated(i, j));
                    // choose nearest edge to mouse-position
                    if (edgedist >= temp) {
                        edge = (ConnectionContainerEditPart)ep;
                        edgedist = temp;
                    }
                    edgeCount++;
                }
            }
        }

        unmark(wfm);

        if (node != null && nodeCount >= edgeCount) {
            m_markedNode = node;
            m_markedNode.mark();
            // workaround for eclipse bug 393868 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=393868)
            WindowsDNDHelper.hideDragImage();
        } else if (edge != null) {
            m_markedEdge = edge;
            ((ProgressPolylineConnection)m_markedEdge.getFigure()).setLineWidth(3);
            m_markedEdge.getFigure().setForegroundColor(RED);

            // workaround for eclipse bug 393868 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=393868)
            WindowsDNDHelper.hideDragImage();
        }
    }

    /**
     * Unmark node and edge.
     * @param wfm the workflow manager
     */
    private void unmark(final WorkflowManager wfm) {
        if (m_markedNode != null) {
            m_markedNode.unmark();
            m_markedNode = null;

            // workaround for eclipse bug 393868 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=393868)
            WindowsDNDHelper.showDragImage();
        }

        if (m_markedEdge != null) {
            if (wfm.getNodeContainer(m_markedEdge.getModel().getSource()).getType().compareTo(NodeType.Meta) != 0
                && m_markedEdge.getModel().getSourcePort() > 0) {
                // connection from a normal node which is not flow variable port
                m_markedEdge.getFigure().setForegroundColor(BLACK);
            } else if (wfm.getNodeContainer(m_markedEdge.getModel().getSource()).getType().compareTo(NodeType.Meta) == 0) {
                // connection from a meta node
                m_markedEdge.getFigure().setForegroundColor(BLACK);
            }
            ((ProgressPolylineConnection)m_markedEdge.getFigure()).setLineWidth(1);
            m_markedEdge = null;

            // workaround for eclipse bug 393868 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=393868)
            WindowsDNDHelper.showDragImage();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drop(final DropTargetEvent event) {

        // TODO: get the Selection from the LocalSelectionTransfer
        // check instanceof NodeTemplate and fire a CreateRequest
        LOGGER.debug("drop: " + event);
        AbstractNodeTemplate ant = getSelectionNodeTemplate();
        WorkflowManager wfm = ((WorkflowRootEditPart)m_viewer.getRootEditPart().getContents()).getWorkflowManager();
        if (ant instanceof NodeTemplate) {
            NodeTemplate template = (NodeTemplate)ant;
            CreateRequest request = new CreateRequest();
            // TODO for some reason sometimes the event contains no object - but
            // this doesn't seem to matter - dragging continues as expected
            // Set the factory on the current request
            NodeFromNodeTemplateCreationFactory factory = new NodeFromNodeTemplateCreationFactory(template);
            request.setFactory(factory);

            if (node != null && nodeCount >= edgeCount) {
                // more node pixels than edge pixels found in hit-box
                m_viewer
                    .getEditDomain()
                    .getCommandStack()
                    .execute(
                        new ReplaceNodeCommand(wfm, factory.getNewObject(), node));
            } else if (edge != null) {
                // more edge pixels found
                m_viewer
                    .getEditDomain()
                    .getCommandStack()
                    .execute(
                        new InsertNewNodeCommand(wfm, factory.getNewObject(), edge, WorkflowEditor
                            .getActiveEditorSnapToGrid()));
            } else {
                // normal insert
                m_viewer
                    .getEditDomain()
                    .getCommandStack()
                    .execute(
                        new CreateNodeCommand(wfm, factory.getNewObject(), getDropLocation(event), WorkflowEditor
                            .getActiveEditorSnapToGrid()));
            }
            NodeUsageRegistry.addNode(template);
            // bugfix: 1500
            m_viewer.getControl().setFocus();
        } else if (ant instanceof MetaNodeTemplate) {
            MetaNodeTemplate mnt = (MetaNodeTemplate)ant;
            NodeID id = mnt.getManager().getID();
            WorkflowManager sourceManager = RepositoryFactory.META_NODE_ROOT;
            WorkflowCopyContent content = new WorkflowCopyContent();
            content.setNodeIDs(id);
            WorkflowPersistor copy = sourceManager.copy(content);
            m_viewer
                .getEditDomain()
                .getCommandStack()
                .execute(
                    new CreateMetaNodeCommand(wfm, copy, getDropLocation(event), WorkflowEditor
                        .getActiveEditorSnapToGrid()));
        }

        unmark(wfm);
    }

    private AbstractNodeTemplate getSelectionNodeTemplate() {
        if (LocalSelectionTransfer.getTransfer().getSelection() == null) {
            return null;
        }
        if (((IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection()).size() > 1) {
            // allow dropping a single node only
            return null;
        }

        Object template = ((IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();
        if (template instanceof AbstractNodeTemplate) {
            return (AbstractNodeTemplate)template;
        }
        // Last change: Ask adaptables for an adapter object
        if (template instanceof IAdaptable) {
            return (AbstractNodeTemplate)((IAdaptable)template).getAdapter(AbstractNodeTemplate.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropAccept(final DropTargetEvent event) {
    }
}
