import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

class GitHelper {

	/**
	 * Determine the ID of the current commit.
	 */
	static def currentCommitId(File gitDir) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(gitDir)
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		try {
			println 'Trying to determine current commit from Git repository: ' + repository.getDirectory()

			Iterable<RevCommit> logs = new Git(repository).log().all().call();
			
			return logs.iterator().next().id.name
		}
		catch (e) {
			println "Could not determine current commit (${e.message})"
			return null
		}
		finally {
			repository.close();
		}
	}

}