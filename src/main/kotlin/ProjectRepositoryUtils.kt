import com.intellij.openapi.project.Project
import git4idea.GitBranch
import git4idea.GitRemoteBranch
import git4idea.branch.GitBrancher
import git4idea.branch.GitBranchesCollection
import git4idea.fetch.GitFetchSupport
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import git4idea.validators.GitNewBranchNameValidator
import java.util.*


fun getAllBranches(project: Project): List<GitBranch> {
    val gitRepositoryManager = GitRepositoryManager.getInstance(project)
    val gitRepositories = gitRepositoryManager.repositories

    val result: MutableList<GitBranch> = mutableListOf()

    for (repository in gitRepositories) {

        val branches: GitBranchesCollection = repository.branches
        val comparator: Comparator<GitBranch> = Comparator{ b1, b2 -> b1.name.compareTo(b2.name, true) }

        val localBranchesList = ArrayList<GitBranch>(branches.localBranches)
        //localBranchesList.sortedWith(comparator)

        val remoteBranchesList = ArrayList<GitBranch>(branches.remoteBranches)
        //remoteBranchesList.sortedWith(comparator)

        result.addAll(localBranchesList.sortedWith(comparator))
        result.addAll(remoteBranchesList.sortedWith(comparator))
    }

    return result
}

fun checkoutLocalBranch(project: Project, gitBranch: GitBranch) {
    val gitRepositoryManager = GitRepositoryManager.getInstance(project)
    val brancher = GitBrancher.getInstance(project)
    brancher.checkout(gitBranch.name, false, gitRepositoryManager.repositories, null)
}

fun checkoutRemoteBranch(project: Project, gitBranch: GitRemoteBranch, localBranchName: String) {
    val gitRepositoryManager = GitRepositoryManager.getInstance(project)
    val brancher = GitBrancher.getInstance(project)
    brancher.checkoutNewBranchStartingFrom(localBranchName, gitBranch.nameForLocalOperations,
        gitRepositoryManager.repositories, null)
}

fun getNewBranchNameValidator(project: Project): GitNewBranchNameValidator {
    val gitRepositoryManager = GitRepositoryManager.getInstance(project)
    return GitNewBranchNameValidator.newInstance(gitRepositoryManager.repositories)
}

fun fetchAll(project: Project) {
    val gitRepositoryManager = GitRepositoryManager.getInstance(project)
    val gitFetchSupport: GitFetchSupport = GitFetchSupport.fetchSupport(project)
    for (repository: GitRepository in gitRepositoryManager.repositories) {
        val gitRemote: GitRemote? = gitFetchSupport.getDefaultRemoteToFetch(repository)
        if (gitRemote != null) {
            gitFetchSupport.fetch(repository, gitRemote)
        }
    }
}