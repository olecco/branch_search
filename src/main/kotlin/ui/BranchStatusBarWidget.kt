package ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import ui.contracts.BranchView
import java.awt.Component
import java.awt.event.MouseEvent

class BranchStatusBarWidget(private val project: Project): StatusBarWidget, StatusBarWidget.TextPresentation {

    override fun ID(): String {
        return "BranchStatusBarWidget"
    }

    override fun getPresentation(type: StatusBarWidget.PlatformType): StatusBarWidget.WidgetPresentation? {
        return this
    }

    override fun install(statusBar: StatusBar) { }

    override fun dispose() { }

    override fun getTooltipText(): String? {
        return "Branches"
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return Consumer {
            val branchView: BranchView = BranchPopup(project)

            BranchPresenterImpl(branchView, project)

            branchView.show(project)
        }
    }

    override fun getAlignment(): Float {
        return Component.CENTER_ALIGNMENT
    }

    override fun getText(): String {
        return "BRANCHES"
    }

}