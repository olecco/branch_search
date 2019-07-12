package ui

import git4idea.GitBranch
import javax.swing.AbstractListModel

class BranchListModel: AbstractListModel<GitBranch>() {

    var data: List<GitBranch> = listOf()
        set(value) {
            field = value
            fireContentsChanged(this, 0, value.size - 1)
        }

    override fun getElementAt(index: Int): GitBranch {
        return data[index]
    }

    override fun getSize(): Int {
        return data.size
    }

}