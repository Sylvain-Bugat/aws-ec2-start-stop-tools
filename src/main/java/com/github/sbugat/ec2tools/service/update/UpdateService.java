package com.github.sbugat.ec2tools.service.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Simple generic version checker on GitHub, inpect target jar and local jar build date to determinated if an update is available.
 * 
 * The checker use an independant thread to check and download the file.
 * 
 * @author Sylvain Bugat
 * 
 */
@Singleton
public final class UpdateService implements Runnable {

	/** SLF4J XLogger. */
	private static final XLogger LOG = XLoggerFactory.getXLogger(UpdateService.class);

	/** Jar extension. */
	private static final String JAR_EXTENSION = ".jar"; //$NON-NLS-1$
	/** Tmp extension. */
	private static final String TMP_EXTENSION = ".tmp"; //$NON-NLS-1$
	/** Target directory in zipball releases. */
	private static final String TARGET_DIRECTORY = "target"; //$NON-NLS-1$

	/** Root URL of the GitHub project to update. */
	public static final String gitHubUser = "Sylvain-Bugat"; //$NON-NLS-1$
	/** GitHub repository. */
	public static final String gitHubRepository = "aws-ec2-start-stop-tools"; //$NON-NLS-1$
	/** Maven artifact identifier. */
	private static final String mavenArtifactId = gitHubRepository;

	/**
	 * GitHub repository service.
	 */
	@Inject
	private RepositoryService repositoryService;

	/**
	 * Background thread launched to check the version on GitHub.
	 */
	@Override
	public void run() {

		LOG.entry();

		final String currentJar = currentJar();

		if (null == currentJar) {
			LOG.exit();
			return;
		}

		try {

			final Repository repository = repositoryService.getRepository(gitHubUser, gitHubRepository);

			final String currentVersion = 'v' + currentJar.replaceFirst("^" + mavenArtifactId + '-', "").replaceFirst(JAR_EXTENSION + '$', ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			RepositoryTag recentRelease = null;
			for (final RepositoryTag tag : repositoryService.getTags(repository)) {

				if (null != recentRelease && tag.getName().compareTo(recentRelease.getName()) > 0) {
					recentRelease = tag;
				}
				else if (tag.getName().compareTo(currentVersion) > 0) {
					recentRelease = tag;
				}
			}

			if (null == recentRelease) {
				LOG.exit();
				return;
			}

			findAndDownloadReleaseJar(recentRelease);

			LOG.exit();
		}
		catch (final Exception e) {

			// Ignore any error during update process
			// Just delete the temporary file
			cleanOldAndTemporaryJar();
			LOG.exit(e);
		}
	}

	/**
	 * Find a jar in release and if it is a newer version, ask to download it.
	 * 
	 * @param release GitHub last release to use
	 * @return true if a new release jar has been found
	 * @throws IOException in case of reading error
	 */
	private boolean findAndDownloadReleaseJar(final RepositoryTag release) throws IOException {

		LOG.entry(release);

		try (final InputStream remoteJarInputStream = new URL(release.getZipballUrl()).openStream()) {

			final ZipInputStream zipInputStream = new ZipInputStream(remoteJarInputStream);

			ZipEntry entry = zipInputStream.getNextEntry();

			while (null != entry) {

				if (entry.getName().matches(".*/" + TARGET_DIRECTORY + '/' + mavenArtifactId + "-[0-9\\.]*" + JAR_EXTENSION)) { //$NON-NLS-1$ //$NON-NLS-2$

					final String jarFileBaseName = entry.getName().replaceFirst("^.*/", ""); //$NON-NLS-1$ //$NON-NLS-2$

					downloadFile(zipInputStream, jarFileBaseName + TMP_EXTENSION);
					Files.move(Paths.get(jarFileBaseName + TMP_EXTENSION), Paths.get(jarFileBaseName));

					LOG.exit(true);
					return true;
				}

				entry = zipInputStream.getNextEntry();
			}
		}

		LOG.exit(false);
		return false;
	}

	/**
	 * Clean the old jar and any existing temporary file.
	 */
	private void cleanOldAndTemporaryJar() {

		LOG.entry();

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("."))) { //$NON-NLS-1$

			for (final Path path : directoryStream) {

				final String fileName = path.getFileName().toString();
				if (fileName.startsWith(mavenArtifactId) && fileName.endsWith(JAR_EXTENSION + TMP_EXTENSION)) {

					deleteJar(path);
				}
			}

			LOG.exit();
		}
		catch (final IOException e) {
			// Ignore any error during the delete process
			LOG.exit(e);
		}
	}

	/**
	 * Delete a jar file.
	 * 
	 * @param jarFileToDelete file to delete
	 */
	private void deleteJar(final Path jarFileToDelete) {

		LOG.entry();

		if (Files.exists(jarFileToDelete)) {

			try {
				Files.delete(jarFileToDelete);
				LOG.exit();
			}
			catch (final IOException e) {

				// Ignore any error during the delete process
				LOG.exit(e);
			}
		}
	}

	/**
	 * Return the current executed jar.
	 * 
	 * @return the name of the current executed jar
	 */
	private String currentJar() {

		LOG.entry();

		String currentJar = null;
		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("."))) { //$NON-NLS-1$

			for (final Path path : directoryStream) {

				final String fileName = path.getFileName().toString();
				if (fileName.startsWith(mavenArtifactId) && fileName.endsWith(JAR_EXTENSION) && (null == currentJar || currentJar.compareTo(fileName) < 0)) {

					currentJar = fileName;
				}
			}
		}
		catch (final IOException e) {
			// Ignore any error during the process
			currentJar = null;
		}

		LOG.exit(currentJar);
		return currentJar;
	}

	/**
	 * Download a file/URL and write it to a destination file.
	 * 
	 * @param inputStream source stream
	 * @param destinationFile destination file
	 * @throws IOException in case of copy error
	 */
	private void downloadFile(final InputStream inputStream, final String destinationFile) throws IOException {

		Files.copy(inputStream, Paths.get(destinationFile));
	}
}