package ui

import checkoutLocalBranch
import checkoutRemoteBranch
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import fetchAll
import getAllBranches
import getNewBranchNameValidator
import git4idea.GitBranch
import git4idea.GitRemoteBranch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ui.contracts.BranchPresenter
import ui.contracts.BranchView

class BranchPresenterImpl(override var view: BranchView, private val project: Project) : BranchPresenter {

    private var allBranches: MutableList<GitBranch>
    private var filteredBranches: MutableList<GitBranch> = mutableListOf()
    private var filterString: String = ""

    init {
        view.presenter = this
        allBranches = ArrayList(getAllBranches(project))
        setFilterString("")
    }

    override fun setFilterString(filterString: String) {
        this.filterString = filterString
        //val branches: List<GitBranch> = getAllBranches(project)
        filteredBranches.clear()
        if (!filterString.isNullOrEmpty()) {
            for (branch in allBranches) {
                if (branch.name.contains(filterString, true)) {
                    filteredBranches.add(branch)
                }
            }
        } else {
            filteredBranches.addAll(allBranches)
        }
        view.setBranchesData(filteredBranches)
    }

    override fun checkoutBranch(branchIndex: Int) {
        view.hide()

        val gitBranch: GitBranch = filteredBranches[branchIndex]
        if (gitBranch.isRemote) {
            val remoteBranch = gitBranch as GitRemoteBranch

            val inputDialog = Messages.InputDialog(project, "New branch name:", project.name, null,
                remoteBranch.nameForRemoteOperations,  getNewBranchNameValidator(project))

            if (inputDialog.showAndGet() && !inputDialog.inputString.isNullOrEmpty()) {
                checkoutRemoteBranch(project, remoteBranch, inputDialog.inputString!!)
            }
        } else {
            checkoutLocalBranch(project, gitBranch)
        }
    }

    override fun getNextSelectedIndex(currentSelectedIndex: Int, isDown: Boolean): Int {
        return if (isDown) {
            if (currentSelectedIndex < filteredBranches.size - 1) { currentSelectedIndex + 1 } else { currentSelectedIndex }
        } else {
            if (currentSelectedIndex > 0) { currentSelectedIndex - 1 } else { currentSelectedIndex }
        }
    }

    override fun fetchBranches() {

        view.setLoading(true)

        GlobalScope.launch(Dispatchers.IO) {

            fetchAll(project)
            allBranches = ArrayList(getAllBranches(project))

            ApplicationManager.getApplication().invokeLater {
                view.setLoading(false)
                setFilterString(filterString)
            }

        }
    }

}