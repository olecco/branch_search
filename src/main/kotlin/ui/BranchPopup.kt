package ui

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBList
import git4idea.GitBranch
import ui.contracts.BranchNameDecorator
import ui.contracts.BranchPresenter
import ui.contracts.BranchView
import ui.contracts.DefaultBranchNameDecorator
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class BranchPopup(private val project: Project): BranchView {

    private var popup: JBPopup
    private lateinit var branchListView: JBList<GitBranch>
    private lateinit var editorView: EditorTextField
    private var branchNameDecorator: BranchNameDecorator = DefaultBranchNameDecorator(project)
    override var presenter: BranchPresenter? = null
        get() = field
        set(value) { field = value }

    init {
        val rootPanel = createPopupUI()

        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(rootPanel, editorView)
            .setRequestFocus(true)
            .setCancelOnOtherWindowOpen(true)
            .setCancelOnClickOutside(true)
            .setShowBorder(true)
            .createPopup()

        popup.setSize(Dimension(700, 500))
    }

    override fun show(project: Project) {
        popup.showCenteredInCurrentWindow(project)
    }

    override fun hide() {
        popup.cancel()
    }

    override fun setBranchesData(branches: List<GitBranch>) {
        (branchListView.model as BranchListModel).data = branches
        if (!branches.isEmpty()) {
            branchListView.selectedIndex = 0
        }
    }

    override fun setLoading(isLoading: Boolean) {
        branchListView.setPaintBusy(isLoading)
    }

    private fun createPopupUI(): JPanel {
        branchListView = JBList(BranchListModel())
        branchListView.selectionMode = ListSelectionModel.SINGLE_SELECTION
        branchListView.isFocusable = false
        branchListView.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.clickCount == 2) {
                    val selectedBranchIndex = branchListView.locationToIndex(e.getPoint())
                    if (selectedBranchIndex > -1) {
                        presenter?.checkoutBranch(selectedBranchIndex)
                    }
                }
            }
        })
        branchListView.cellRenderer = object: ColoredListCellRenderer<GitBranch>() {
            override fun customizeCellRenderer(
                list: JList<out GitBranch>,
                value: GitBranch?,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                if (value!!.isRemote) {
                    branchNameDecorator.renderRemoteBranch(this, value)
                } else {
                    branchNameDecorator.renderLocalBranch(this, value)
                }
            }
        }



        editorView = EditorTextField()
        editorView.isFocusable = true
        editorView.addDocumentListener(object: DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                presenter?.setFilterString(editorView.text.trim())

            }
        })

        editorView.registerKeyboardAction(
            {
                if (!branchListView.isEmpty) {
                    branchListView.selectedIndex = presenter!!.getNextSelectedIndex(branchListView.selectedIndex, true)
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW)
        editorView.registerKeyboardAction(
            {
                if (!branchListView.isEmpty) {
                    branchListView.selectedIndex = presenter!!.getNextSelectedIndex(branchListView.selectedIndex, false)
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW)
        editorView.registerKeyboardAction(
            {
                if (!branchListView.isEmpty) {
                    presenter?.checkoutBranch(branchListView.selectedIndex)
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW)


        val fetchButton = JButton("Git Fetch")
        fetchButton.isFocusable = false
        fetchButton.addActionListener { presenter?.fetchBranches() }


        val topPanel = JPanel(BorderLayout())

        topPanel.add(fetchButton, BorderLayout.EAST)
        topPanel.add(editorView, BorderLayout.CENTER)


        val contentPanel = JPanel(BorderLayout())

        contentPanel.add(topPanel, BorderLayout.NORTH)
        //contentPanel.add(editorView, BorderLayout.NORTH)
        contentPanel.add(JScrollPane(branchListView), BorderLayout.CENTER)



        return contentPanel
    }



}