/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.lib.pde.tools;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.pde.model.buildproperties.PluginBuildProperties;
import org.ant4eclipse.lib.pde.model.buildproperties.PluginBuildProperties.Library;
import org.ant4eclipse.lib.pde.model.pluginproject.PluginProjectRole;
import org.ant4eclipse.lib.pde.tools.PluginProjectChecker.Issue.IssueLevel;
import org.ant4eclipse.lib.platform.model.resource.EclipseProject;

/**
 * <p>
 * The {@link PluginProjectChecker} can be used to check if a plug-in project contains inconsistent or erroneous build
 * property entries.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class PluginProjectChecker {

  /** the ERRONEOUS_PROJECT_DEFINITION_MSG */
  private static String       ERRONEOUS_PROJECT_DEFINITION_MSG                = "Inconsistent or erroneous plug-in project definition for project '%1$s'. Reason:\n";

  /** the MISSING_MANIFEST_FILE_MSG */
  private static String       MISSING_MANIFEST_FILE_MSG                       = ERRONEOUS_PROJECT_DEFINITION_MSG
                                                                                  + "- Project must contain a bundle manifest file,\n"
                                                                                  + "- e.g. '%2$s" + File.separator
                                                                                  + "META-INF" + File.separator
                                                                                  + "MANIFEST.MF'.";

  /** the MISSING_BIN_INCLUDE_ENTRY_FOR_MANIFEST_FILE_MSG */
  private static String       MISSING_BIN_INCLUDE_ENTRY_FOR_MANIFEST_FILE_MSG = ERRONEOUS_PROJECT_DEFINITION_MSG
                                                                                  + "- Inconsistent build properties file '%2$s'.\n"
                                                                                  + "- The build properties file must contain a 'bin.includes' entry for the META-INF directory ('META-INF/'). To fix this issue, please add an entry for the META-INF directory to the bin.includes list (e.g. 'bin.includes = META-INF/').\n";

  /** the BIN_EXCLUDE_ENTRY_FOR_MANIFEST_FILE_MSG */
  private static String       BIN_EXCLUDE_ENTRY_FOR_MANIFEST_FILE_MSG         = ERRONEOUS_PROJECT_DEFINITION_MSG
                                                                                  + "- Inconsistent build properties file '%2$s'.\n"
                                                                                  + "- The build properties must not contain a 'bin.excludes' entry for the manifest file ('bin.excludes = META-INF/MANIFEST.MF'). To fix this issue, please remove the META-INF/MANIFEST.MF entry from the bin.excludes list.\n";

  /** the LIBRARY_WITHOUT_SOURCE_DIRECTORY */
  private static final String LIBRARY_WITHOUT_SOURCE_DIRECTORY                = ERRONEOUS_PROJECT_DEFINITION_MSG
                                                                                  + "- Inconsistent build properties file '%2$s'.\n"
                                                                                  + "- The build properties contains a library '%3$s' that doesn't contain a source directory. To fix this issue, please add a source directory to the library's source list 'source.%3$s'.\n";

  /** the LIBRARY_WITHOUT_OUTPUT_DIRECTORY */
  @SuppressWarnings("unused")
  private static final String LIBRARY_WITHOUT_OUTPUT_DIRECTORY                = ERRONEOUS_PROJECT_DEFINITION_MSG
                                                                                  + "- Inconsistent build properties file '%2$s'.\n"
                                                                                  + "- The build properties contains a library '%3$s' that doesn't contain a output directory. To fix this issue, please add a output directory to the library's output list 'output.%3$s'.\n";

  /** the eclipse project to check */
  private EclipseProject      _eclipseProject;

  /** the list of issues */
  private List<Issue>         _issues;

  private String              _projectName;

  /**
   * <p>
   * Creates a new instance of type {@link PluginProjectChecker}.
   * </p>
   * 
   * @param eclipseProject
   *          the eclipse project to check.
   */
  public PluginProjectChecker(EclipseProject eclipseProject) {
    Assure.notNull("eclipseProject", eclipseProject);

    this._eclipseProject = eclipseProject;
    this._issues = new LinkedList<Issue>();

    this._projectName = this._eclipseProject.getSpecifiedName();
  }

  /**
   * <p>
   * Checks the given eclipse plug-in project and returns a list of issues.
   * </p>
   * 
   * @return a list of issues.
   */
  public List<Issue> checkPluginProject() {

    // does the project has the PluginProjectRole?
    final boolean hasPluginProjectRole = this._eclipseProject.hasRole(PluginProjectRole.class);
    errorAssert(hasPluginProjectRole, "Project '%s' must have role 'PluginProjectRole'.", this._projectName);

    if (hasPluginProjectRole) {
      // it only makes sense to validate more, if the current project is a plug-in project

      PluginProjectRole pluginProjectRole = this._eclipseProject.getRole(PluginProjectRole.class);

      // make sure we have a build.properties-file
      final boolean hasBuildProperties = pluginProjectRole.hasBuildProperties();
      errorAssert(hasBuildProperties, "Project '%s' must have a 'build.properties' file in it's root folder",
          this._projectName);
      if (hasBuildProperties) {

        final PluginBuildProperties buildProperties = pluginProjectRole.getBuildProperties();

        validateManifestRelatedEntries(buildProperties);
        validateLibrariesRelatedEntries(buildProperties);
      }
    }

    // return the issue list
    return this._issues;
  }

  /**
   * 
   */
  private void validateManifestRelatedEntries(PluginBuildProperties buildProperties) {

    // does the project contain a manifest?
    errorAssert(this._eclipseProject.hasChild("META-INF/MANIFEST.MF"), MISSING_MANIFEST_FILE_MSG, this._projectName,
        this._eclipseProject.getFolder());

    final List<String> binaryIncludesList = Arrays.asList(buildProperties.getBinaryIncludes());
    final List<String> binaryExcludesList = Arrays.asList(buildProperties.getBinaryExcludes());

    // does the build property file contain an entry for the META-INF directory?
    errorAssert(binaryIncludesList.contains("META-INF"), MISSING_BIN_INCLUDE_ENTRY_FOR_MANIFEST_FILE_MSG,
        this._projectName, this._eclipseProject.getChild("build.properties").getAbsolutePath());

    // doesn't the binary excludes list contain an entry for the META-INF/MANIFEST.MF directory?
    errorAssert(!binaryExcludesList.contains("META-INF/MANIFEST.MF"), BIN_EXCLUDE_ENTRY_FOR_MANIFEST_FILE_MSG,
        this._projectName, this._eclipseProject.getChild("build.properties").getAbsolutePath());
  }

  /**
   * <p>
   * </p>
   */
  private void validateLibrariesRelatedEntries(PluginBuildProperties buildProperties) {

    // get all libraries
    Library[] libraries = buildProperties.getLibraries();

    // iterate over the libraries
    for (Library library : libraries) {
      warningAssert(library.hasSource(), LIBRARY_WITHOUT_SOURCE_DIRECTORY, this._projectName, this._eclipseProject
          .getChild("build.properties").getAbsolutePath(), library.getName());

      // warningAssert(library.hasOutput(), LIBRARY_WITHOUT_OUTPUT_DIRECTORY, _projectName, _eclipseProject.getChild(
      // "build.properties").getAbsolutePath(), library.getName());
    }
  }

  /**
   * @param condition
   * @param msg
   * @param args
   */
  private void errorAssert(boolean condition, String msg, Object... args) {
    validate(IssueLevel.ERROR, condition, msg, args);
  }

  /**
   * <p>
   * Adds an issue to the issue list if the given condition is false.
   * <p>
   * 
   * @param condition
   *          the condition
   * @param msg
   *          the message
   * @param args
   *          the args
   */
  private void warningAssert(boolean condition, String msg, Object... args) {
    validate(IssueLevel.WARNING, condition, msg, args);
  }

  /**
   * @param level
   * @param condition
   * @param msg
   * @param args
   */
  private void validate(IssueLevel level, boolean condition, String msg, Object... args) {
    if (!condition) {
      this._issues.add(new Issue(level, String.format(msg, args)));
    }
  }

  /**
   * <p>
   * Represents an issue.
   * </p>
   * 
   * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
   */
  public static class Issue {

    /**
     * @author GWUETHER
     */
    enum IssueLevel {
      WARNING, ERROR;
    }

    /** the message */
    private String     _message;

    /** the level */
    @SuppressWarnings("unused")
    private IssueLevel _level;

    /**
     * <p>
     * Creates a new instance of type {@link Issue}.
     * </p>
     * 
     * @param message
     *          the message.
     */
    public Issue(IssueLevel issueLevel, String message) {
      this._level = issueLevel;
      this._message = message;
    }

    /**
     * <p>
     * The message of the issue.
     * </p>
     * 
     * @return the message.
     */
    public String getMessage() {
      return this._message;
    }
  }
}
