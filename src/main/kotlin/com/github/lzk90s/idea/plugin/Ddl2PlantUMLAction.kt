package com.github.lzk90s.idea.plugin

import com.github.lzk90s.idea.plugin.ddl2plantuml.FileReader
import com.github.lzk90s.idea.plugin.ddl2plantuml.StringBuilderWriter
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilBase
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.nio.file.Path


/**
 *
 * @author : qihang.liu
 * @date 2021-11-08
 */
class Ddl2PlantUMLAction : AnAction() {
    private val notificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("ddl2plantuml.NotificationGroup")

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabledAndVisible = isSqlFile(psiFile!!)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val project = e.project
        val psiFile = PsiUtilBase.getPsiFileInEditor(editor!!, project!!)

        if (!isSqlFile(psiFile!!)) {
            notifyMessage(project, "File must be sql ddl file!!", NotificationType.ERROR)
            return
        }

        val selection = StringSelection(buildPlantuml(psiFile!!))
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)

        notifyMessage(
            project,
            "Convert ${psiFile.virtualFile.path} to plantuml success, copied to clipboard.",
            NotificationType.INFORMATION
        )
    }

    private fun buildPlantuml(psi: PsiFile): String {
        val sb = StringBuilder()
        FileReader(Path.of(psi.virtualFile.path)).read().apply { StringBuilderWriter(sb, this).write() }
        return sb.toString()
    }

    private fun notifyMessage(project: Project, msg: String, type: NotificationType) {
        val success = notificationGroup.createNotification(msg, type)
        Notifications.Bus.notify(success, project)
    }

    private fun isSqlFile(psi: PsiFile): Boolean {
        return psi.virtualFile.path.endsWith(".sql")
    }
}
