package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.commands.GitCommandResult;
import gitflow.ui.GitflowStartBugfixDialog;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;

public class StartBugfixAction extends GitflowAction {

    public StartBugfixAction() {
        super("Start Bugfix");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        super.actionPerformed(e);

        GitflowStartBugfixDialog dialog = new GitflowStartBugfixDialog(myProject);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) return;

        final String bugfixName = dialog.getNewBranchName();
        final String baseBranchName = dialog.getBaseBranchName();

        this.runAction(e.getProject(), baseBranchName, bugfixName);
    }

    public void runAction(Project project, final String baseBranchName, final String bugfixName){
        super.runAction(project, baseBranchName, bugfixName);

        new Task.Backgroundable(myProject, "Starting bugfix " + bugfixName, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                createBugfixBranch(baseBranchName, bugfixName);
            }
        }.queue();
    }

    private void createBugfixBranch(String baseBranchName, String bugfixName) {
        GitflowErrorsListener errorListener = new GitflowErrorsListener(myProject);
        GitCommandResult result = myGitflow.startBugfix(repo, bugfixName, baseBranchName, errorListener);

        if (result.success()) {
            String startedFeatureMessage = String.format("A new branch '%s%s' was created, based on '%s'", featurePrefix, bugfixName, baseBranchName);
            NotifyUtil.notifySuccess(myProject, bugfixName, startedFeatureMessage);
        } else {
            NotifyUtil.notifyError(myProject, "Error", "Please have a look at the Version Control console for more details");
        }

        repo.update();
        virtualFileMananger.asyncRefresh(null); //update editors
    }
}