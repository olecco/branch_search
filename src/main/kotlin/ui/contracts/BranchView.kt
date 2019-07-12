package ui.contracts

import com.intellij.openapi.project.Project
import git4idea.GitBranch

interface BranchView {

    var presenter: BranchPresenter?

    fun show(project: Project)

    fun hide()

    fun setBranchesData(branches: List<GitBranch>)

    fun setLoading(isLoading: Boolean)

}