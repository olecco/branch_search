package ui.contracts

interface BranchPresenter {

    var view: BranchView

    fun setFilterString(filterString: String)

    fun checkoutBranch(branchIndex: Int)

    fun getNextSelectedIndex(currentSelectedIndex: Int, isDown: Boolean): Int

    fun fetchBranches()

}