/**********************************************************************
 * Copyright (c) 2005-2008 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.lib.platform.model.team.cvssupport.project;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.core.logging.A4ELogging;
import org.ant4eclipse.lib.platform.model.resource.EclipseProject;
import org.ant4eclipse.lib.platform.model.resource.ProjectNature;
import org.ant4eclipse.lib.platform.model.resource.role.ProjectRole;
import org.ant4eclipse.lib.platform.model.resource.role.ProjectRoleIdentifier;
import org.ant4eclipse.lib.platform.model.team.cvssupport.CvsRoot;

import java.util.Set;

/**
 * Identifier for a CVS role.
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class CvsRoleIdentifier implements ProjectRoleIdentifier {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRoleSupported(EclipseProject project) {
    return CvsParser.isCvsProject(project);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProjectRole createRole(EclipseProject project) {
    A4ELogging.trace("CvsRoleIdentifier.applyRole(%s)", project);
    Assure.notNull("project", project);
    CvsRoot cvsRoot = CvsParser.readCvsRoot(project);
    String repositoryName = CvsParser.readCvsRepositoryName(project);
    String tag = CvsParser.readTag(project);
    CvsProjectRole cvsProjectRole = new CvsProjectRole(project, repositoryName, cvsRoot, tag);
    return cvsProjectRole;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void postProcess(final EclipseProject project) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getPriority() {
    return null;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void reset() {
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Set<ProjectNature> getNatures() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getNatureNicknames() {
    return null;
  }
  
} /* ENDCLASS */
