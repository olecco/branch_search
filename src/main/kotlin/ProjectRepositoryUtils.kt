import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import git4idea.GitBranch
import git4idea.branch.GitBranchUtil
import git4idea.branch.GitBranchesCollection
import git4idea.repo.GitRepositoryManager
import git4idea.branch.GitBrancher
import com.sun.javafx.scene.CameraHelper.project
import git4idea.GitLocalBranch
import git4idea.GitRemoteBranch
import git4idea.commands.Git
import git4idea.fetch.GitFetchResult
import git4idea.fetch.GitFetchSupport
import git4idea.repo.GitRepository
import git4idea.validators.GitNewBranchNameValidator


fun getAllBranches(project: Project): List<GitBranch> {
    val gitRepositoryManager = GitRepositoryManager.getInstance(project)
    val gitRepositories = gitRepositoryManager.repositories

    val result: MutableList<GitBranch> = mutableListOf()

    for (repository in gitRepositories) {

        val branches: GitBranchesCollection = repository.branches

        for (branch in branches.localBranches) {
            result.add(branch)
        }

        for (branch in branches.remoteBranches) {
            result.add(branch)
        }
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
    GitFetchSupport.fetchSupport(project).fetch(gitRepositoryManager.repositories)
}