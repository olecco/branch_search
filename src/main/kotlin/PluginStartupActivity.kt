import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.WindowManager
import ui.BranchStatusBarWidget

class PluginStartupActivity: StartupActivity {

    override fun runActivity(project: Project) {
        val statusBar: StatusBar? = WindowManager.getInstance().getStatusBar(project)
        if (statusBar != null) {

            val widget = BranchStatusBarWidget(project)
            statusBar.addWidget(widget, "after " + StatusBar.StandardWidgets.POSITION_PANEL)
            statusBar.updateWidget(widget.ID())

        }

    }

}