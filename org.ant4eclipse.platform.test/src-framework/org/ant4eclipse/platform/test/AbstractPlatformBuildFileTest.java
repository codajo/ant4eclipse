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
package org.ant4eclipse.platform.test;

import java.io.File;

import org.ant4eclipse.core.service.ServiceRegistry;
import org.ant4eclipse.platform.test.builder.FileHelper;
import org.apache.tools.ant.BuildFileTest;

/**
 * Baseclass for all buildfile-based tests in the platform layer
 * 
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
public class AbstractPlatformBuildFileTest extends BuildFileTest {

  /**
   * The testenvironment.
   * 
   */
  private TestEnvironment _testEnvironment;

  /**
   * The workspace directory
   * 
   */
  private File            _workspaceDir;

  /**
   * Returns the name of the build file for a test case.
   * 
   * <p>
   * The build file must stay in the same folder as the test class
   * 
   * @param unqualifiedName
   *          The name of the build file without folders
   * @return The name of the build file
   */
  protected String getProjectBuildFile(String unqualifiedName) {
    return getClass().getPackage().getName().replace('.', '/') + "/" + unqualifiedName;
  }

  /**
   * Creates the Test Environment before execution of a test case
   */
  @Override
  public void setUp() throws Exception {
    _testEnvironment = new TestEnvironment();
    _workspaceDir = getTestEnvironment().createSubDirectory("workspace");
  }

  @Override
  protected void runTest() throws Throwable {
    try {
      super.runTest();
    } catch (Throwable t) {
      System.err.println(getName() + " throws exception (" + t + "). Output:");
      System.err.println(getError());
      throw t;
    }
  }

  /**
   * Copies the given build.xml-file from the classpath to the testenvironment's root directory and configures the ant
   * project
   * 
   * <p>
   * This methods sets the build project property <tt>workspaceDir</tt> to the workspace directory
   * 
   * @param unqualifiedBuildFileName
   *          the unqualified name of the build file, that must be accessible from classpath
   * @throws Exception
   * @see {@link #configureProject(String)}
   */
  protected void setupBuildFile(String unqualifiedBuildFileName) throws Exception {
    String qualifiedBuildFileName = getProjectBuildFile(unqualifiedBuildFileName);
    String buildFileContent = FileHelper.getResource(qualifiedBuildFileName);
    File buildFile = _testEnvironment.createFile(unqualifiedBuildFileName, buildFileContent);
    configureProject(buildFile.getAbsolutePath());
    getProject().setProperty("workspaceDir", _workspaceDir.getAbsolutePath());
  }

  /**
   * Disposes the test environment and resets the {@link ServiceRegistry}
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    _testEnvironment.dispose();
    ServiceRegistry.reset();
  }

  /**
   * Returns a {@link TestEnvironment} for this test case.
   * 
   * @return
   */
  protected TestEnvironment getTestEnvironment() {
    return this._testEnvironment;
  }

  /**
   * Returns the workspace directory that will be created for each test case
   * 
   * @return
   */
  protected File getWorkspaceDir() {
    return this._workspaceDir;
  }

}
