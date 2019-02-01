/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Jan 14, 2019 (awalter): created
 */
package org.knime.workbench.repository.nodalizer;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * POJO for KNIME Node information.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
public class NodeInfo {

    private String m_title;
    private List<String> m_path;
    private String m_id;
    private String m_extensionName;
    private String m_extensionVersion;
    private String m_updateSiteURL;
    private String m_description;
    private List<DialogOptionGroup> m_dialog;
    private List<NamedField> m_views;
    private NamedField m_interactiveView;
    private String[] m_moreInfoLinks;
    private String m_icon;
    private String m_nodeType;
    private boolean m_deprecated;
    private boolean m_streamable;
    private PortInfo[] m_inPorts;
    private PortInfo[] m_outPorts;

    /**
     * Returns the title of this node.
     *
     * @return the title of the node
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Returns a {@code List} of this node's path components.
     *
     * @return a {@code List} of path components
     */
    public List<String> getPath() {
        return m_path;
    }

    /**
     * Returns a unique ID for this node (factory class + factory_settings).
     *
     * @return a unique ID for this node (factory class + factory_settings)
     */
    public String getId() {
        return m_id;
    }

    /**
     * Returns the node's extension name.
     *
     * @return the node's extension name
     */
    public String getExtensionName() {
        return m_extensionName;
    }

    /**
     * Returns the node's extension version.
     *
     * @return the node's extension version
     */
    public String getExtensionVersion() {
        return m_extensionVersion;
    }

    /**
     * Returns the update site which includes this node.
     *
     * @return the update site which includes this node
     */
    public String getUpdateSiteURL() {
        return m_updateSiteURL;
    }

    /**
     * Returns this node's description.
     *
     * @return the node's description, may contain HTML
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * Returns the node's dialog groups' (tabs) information.
     *
     * @return the node's dialog groups' (tabs) information
     */
    public List<DialogOptionGroup> getDialog() {
        return m_dialog;
    }

    /**
     * Returns a {@code List} of this node's views.
     *
     * @return the node's views (name and description)
     */
    public List<NamedField> getViews() {
        return m_views;
    }

    /**
     * Returns the node's interactive view metadata.
     *
     * @return the node's interactive view (name and description)
     */
    public NamedField getInteractiveView() {
        return m_interactiveView;
    }

    /**
     * Returns a array of more information links.
     *
     * @return a array of more information links as {@code <a>} html tags
     */
    public String[] getMoreInfoLinks() {
        return m_moreInfoLinks;
    }

    /**
     * Returns the node's icon as base64 encoded string.
     *
     * @return the node's icon as a base64 encoded string
     */
    public String getIcon() {
        return m_icon;
    }

    /**
     * Returns the node's type.
     *
     * @return the node's type
     */
    public String getNodeType() {
        return m_nodeType;
    }

    /**
     * Returns {@code true} if the node is deprecated.
     *
     * @return {@code true} if the node is deprecated
     */
    public boolean getDeprecated() {
        return m_deprecated;
    }

    /**
     * Returns {@code true} if the node is streamable.
     *
     * @return {@code true} if the node is streamable
     */
    public boolean getStreamable() {
        return m_streamable;
    }

    /**
     * Returns an array of {@link PortInfo} for node each of the node's inports.
     *
     * @return an array of {@link PortInfo} for node each of the node's inports
     */
    public PortInfo[] getInPorts() {
        return m_inPorts;
    }

    /**
     * Returns an array of {@link PortInfo} for node each of the node's outports.
     *
     * @return an array of {@link PortInfo} for node each of the node's outports
     */
    public PortInfo[] getOutPorts() {
        return m_outPorts;
    }

    /**
     * Sets the node's title.
     *
     * @param title the title
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * Sets the node's path
     *
     * @param path a {@code List<String>} containing the node's path, where the element at index 0 is the path root
     */
    public void setPath(final List<String> path) {
        List<String> p = path;
        if (p == null) {
            p = Collections.emptyList();
        }
        m_path = p;
    }

    /**
     * Sets the node's unique id
     *
     * @param id a unique id (factory class + factory_settings)
     */
    public void setId(final String id) {
        m_id = id;
    }

    /**
     * Sets the node's extension name
     *
     * @param extensionName name of the node's extension
     */
    public void setExtensionName(final String extensionName) {
        m_extensionName = extensionName;
    }

    /**
     * Sets the node's extension version
     *
     * @param extensionVersion version of extension to which this node belongs
     */
    public void setExtensionVersion(final String extensionVersion) {
        m_extensionVersion = extensionVersion;
    }

    /**
     * Sets the node's update site
     *
     * @param updateSiteURL the update site from which this node can be retrieved
     */
    public void setUpdateSiteURL(final String updateSiteURL) {
        m_updateSiteURL = updateSiteURL;
    }

    /**
     * Sets the node's description
     *
     * @param description the node's description, may contain HTML
     */
    public void setDescription(final String description) {
        m_description = description;
    }

    /**
     * Sets the node's dialog information
     *
     * @param dialog a {@code List} of {@link DialogOptionGroup}s representing the dialog options per tab for the node
     */
    public void setDialog(final List<DialogOptionGroup> dialog) {
        List<DialogOptionGroup> dog = dialog;
        if (dog == null) {
            dog = Collections.emptyList();
        }
        m_dialog = dog;
    }

    /**
     * Sets the node's views
     *
     * @param views the node's views (name and description)
     */
    public void setViews(final List<NamedField> views) {
        List<NamedField> v = views;
        if (v == null) {
            v = Collections.emptyList();
        }
        m_views = v;
    }

    /**
     * Sets the node's interactive view
     *
     * @param interactiveView the node's interactive view (name and description)
     */
    public void setInteractiveView(final NamedField interactiveView) {
        m_interactiveView = interactiveView;
    }

    /**
     * Sets the node's more information links
     *
     * @param moreInfoLinks an array of {@code <a>} tags representing the more information links for this node
     */
    public void setMoreInfoLinks(final String[] moreInfoLinks) {
        String[] mil = moreInfoLinks;
        if (mil == null) {
            mil = new String[0];
        }
        m_moreInfoLinks = mil;
    }

    /**
     * Sets the node's icon.
     *
     * @param icon the base64 encoded representation of the icon
     */
    public void setIcon(final String icon) {
        m_icon = icon;
    }

    /**
     * Sets the node's type.
     *
     * @param nodeType the node type to set
     */
    public void setNodeType(final String nodeType) {
        m_nodeType = nodeType;
    }

    /**
     * Sets if the node is deprecated or not.
     *
     * @param deprecated if the node is deprecated or not
     */
    public void setDeprecated(final boolean deprecated) {
        m_deprecated = deprecated;
    }

    /**
     * Sets if the node is streamable or not.
     *
     * @param streamable if the node is streamable or not
     */
    public void setStreamable(final boolean streamable) {
        m_streamable = streamable;
    }

    /**
     * Sets the metadata for the node's inports.
     *
     * @param inPorts an array of {@link PortInfo} representing the node's inports
     */
    public void setInPorts(final PortInfo[] inPorts) {
        PortInfo[] pi = inPorts;
        if (pi == null) {
            pi = new PortInfo[0];
        }
        m_inPorts = pi;
    }

    /**
     * Sets the metadata for the node's outports.
     *
     * @param outPorts an array of {@link PortInfo} representing the node's outports
     */
    public void setOutPorts(final PortInfo[] outPorts) {
        PortInfo[] pi = outPorts;
        if (pi == null) {
            pi = new PortInfo[0];
        }
        m_outPorts = pi;
    }
}
