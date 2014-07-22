package org.smile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which validates JSP for XSS security breaches. An XSS breach in JSP is
 * an expression evaluation (i.e.: ${expressionToEvaluate}) outside a taglib (i.e.:
 * inside an HTML tag or outside any tag)
 * 
 * @goal xss
 * @execute phase=compile
 * @author marob
 *
 */
public class XSSMojo extends AbstractMojo {
	/**
	 * The maven project.
	 * 
	 * @parameter property="project"
	 * @readonly
	 * @required
	 */
	MavenProject project;

	// TODO: add the case where an expression is outside any tag
	Pattern regexp = Pattern
			.compile("<[a-zA-Z]+\\s[^<>]*\\$\\{[^}]*\\}[^<>]*>");

	public void execute() throws MojoExecutionException {
		File baseDir = project.getBasedir();

		Collection<File> jspFiles = FileUtils.listFiles(baseDir,
				new String[] { "jsp" }, true);

		boolean allFilesValid = true;
		for (File jspFile : jspFiles) {
			allFilesValid &= validateFileForXSS(jspFile);
		}

		if (!allFilesValid) {
			throw new MojoExecutionException(
					"Your project contains XSS security breaches (see previous logs).");
		}
	}

	private boolean validateFileForXSS(File jspFile)
			throws MojoExecutionException {
		String fileContent;
		try {
			fileContent = FileUtils.readFileToString(jspFile);
		} catch (IOException e) {
			throw new MojoExecutionException("Error while reading file "
					+ jspFile.getPath(), e);
		}

		boolean valid = true;

		Matcher matcher = regexp.matcher(fileContent);
		while (matcher.find()) {
			String group = matcher.group();

			String fixed = group.replace("${", "<c:out value=\"${");
			fixed = fixed.replace("}", "}\" />");

			getLog().error(
					"File \"" + jspFile.getPath()
							+ "\" has an XSS security breach arround:\n"
							+ group + "\nYou should replace it by:\n" + fixed);

			valid = false;
		}

		return valid;
	}

}
