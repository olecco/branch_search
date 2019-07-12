package ui.contracts

import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import git4idea.GitBranch
import git4idea.GitLocalBranch
import git4idea.branch.GitBranchUtil.getCurrentRepository
import git4idea.repo.GitRepository

interface BranchNameDecorator {

    fun renderLocalBranch(renderer: ColoredListCellRenderer<GitBranch>, gitBranch: GitBranch)

    fun renderRemoteBranch(renderer: ColoredListCellRenderer<GitBranch>, gitBranch: GitBranch)

}

class DefaultBranchNameDecorator(private val project: Project): BranchNameDecorator {

    override fun renderLocalBranch(renderer: ColoredListCellRenderer<GitBranch>, gitBranch: GitBranch) {
        renderer.append(gitBranch.name)
        val currentRepository: GitRepository? = getCurrentRepository(project)
        if (currentRepository != null) {
            val gitLocalBranch: GitLocalBranch = gitBranch as GitLocalBranch
            val gitRemoteBranch = gitLocalBranch.findTrackedBranch(currentRepository)
            if (gitRemoteBranch != null) {
                renderer.append("      " + gitRemoteBranch.name, SimpleTextAttributes.GRAYED_ATTRIBUTES)
            }
        }
    }

    override fun renderRemoteBranch(renderer: ColoredListCellRenderer<GitBranch>, gitBranch: GitBranch) {
        renderer.append("(R)  ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        renderer.append(gitBranch.name)
    }

}