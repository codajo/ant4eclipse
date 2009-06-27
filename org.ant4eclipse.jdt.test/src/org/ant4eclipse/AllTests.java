package org.ant4eclipse;

import org.ant4eclipse.jdt.ant.ExecuteJdtProjectTest;
import org.ant4eclipse.jdt.ant.GetJdtClassPathTest;
import org.ant4eclipse.jdt.ant.GetJdtClassPath_UnkownContainerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { GetJdtClassPathTest.class, GetJdtClassPath_UnkownContainerTest.class,
    ExecuteJdtProjectTest.class })
public class AllTests {
}