package org.smile;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

public class XSSMojoTest {

	@Test
	public void test1() throws Exception {
		Assert.assertTrue(hasSecurityBreach("test1"));
	}

	@Test
	public void test2() throws Exception {
		Assert.assertTrue(hasSecurityBreach("test2"));
	}

	@Test
	public void test3() throws Exception {
		Assert.assertTrue(hasSecurityBreach("test3"));
	}

	@Test
	public void test4() throws Exception {
		Assert.assertFalse(hasSecurityBreach("test4"));
	}


	private boolean hasSecurityBreach(String dirName) throws Exception {
		XSSMojo xssMojo = new XSSMojo();
		xssMojo.project = new MavenProject();

		File basedir = new File(this.getClass().getClassLoader()
				.getResource(dirName).toURI());
		xssMojo.project.setBasedir(basedir);

		try {
			xssMojo.execute();
		} catch (MojoExecutionException e) {
			return true;
		}
		return false;
	}
}
