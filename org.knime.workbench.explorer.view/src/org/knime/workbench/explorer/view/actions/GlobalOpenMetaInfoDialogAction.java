/* ------------------------------------------------------------------
  * This source code, its documentation and all appendant files
  * are protected by copyright law. All rights reserved.
  *
  * Copyright, 2008 - 2011
  * KNIME.com, Zurich, Switzerland
  *
  * You may not modify, publish, transmit, transfer or sell, reproduce,
  * create derivative works from, distribute, perform, display, or in
  * any way exploit any of the content, in whole or in part, except as
  * otherwise expressly permitted in writing by the copyright owner or
  * as specified in the license file distributed with this product.
  *
  * If you have any questions please contact the copyright holder:
  * website: www.knime.com
  * email: contact@knime.com
  * ---------------------------------------------------------------------
  *
  * History
  *   May 27, 2011 (morent): created
  */

package org.knime.workbench.explorer.view.actions;

import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.workbench.explorer.filesystem.ExplorerFileStore;
import org.knime.workbench.explorer.view.dnd.DragAndDropUtils;
import org.knime.workbench.ui.metainfo.model.MetaInfoFile;

/**
 *
 * @author Dominik Morent, KNIME.com, Zurich, Switzerland
 *
 */
public class GlobalOpenMetaInfoDialogAction extends ExplorerAction {
    private static final NodeLogger LOGGER = NodeLogger.getLogger(
            GlobalOpenMetaInfoDialogAction.class);

    /** ID of the global rename action in the explorer menu. */
    public static final String METAINFO_ACTION_ID =
        "org.knime.workbench.explorer.action.openMetaInfo";

    /**
     * @param viewer the associated tree viewer
     */
    public GlobalOpenMetaInfoDialogAction(final TreeViewer viewer) {
        super(viewer, "Edit Meta Information...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
       return METAINFO_ACTION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        IStructuredSelection selection = getSelection();
        List<ExplorerFileStore> stores =
                DragAndDropUtils.getExplorerFileStores(selection);
        ExplorerFileStore srcFileStore = stores.get(0);
        ExplorerFileStore metaInfo =
                srcFileStore.getChild(WorkflowPersistor.METAINFO_FILE);
        try {
            if (!metaInfo.fetchInfo().exists()) {
                // create a new meta info file if it does not exist
                MetaInfoFile.createMetaInfoFile(
                        srcFileStore.toLocalFile(EFS.NONE, null),
                        ExplorerFileStore.isWorkflow(srcFileStore));
            }
            IFileStore localFS = new LocalFile(
                    metaInfo.toLocalFile(EFS.NONE,null));
            IDE.openEditorOnFileStore(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage(), localFS);
        } catch (Exception e) {
            LOGGER.error("Could not open meta info editor.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        // only a single selected workflow or workflow group can be used
        List<ExplorerFileStore> fileStores
                = DragAndDropUtils.getExplorerFileStores(
                getSelection());
        if (fileStores == null || fileStores.size() != 1) {
            return false;
        }
        ExplorerFileStore fileStore = fileStores.get(0);
        if (!(ExplorerFileStore.isWorkflow(fileStore)
                || ExplorerFileStore.isWorkflowGroup(fileStore))) {
            return false;
        }
        return true;
    }

}
