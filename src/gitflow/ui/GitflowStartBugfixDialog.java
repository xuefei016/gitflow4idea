package gitflow.ui;

import com.intellij.openapi.project.Project;
import gitflow.GitflowConfigUtil;

public class GitflowStartBugfixDialog extends AbstractBranchStartDialog {

    public GitflowStartBugfixDialog(Project project) {
        super(project);
    }

    @Override
    protected String getLabel() {
        return "bugfix";
    }

    @Override
    protected String getDefaultBranch() {
        return GitflowConfigUtil.getDevelopBranch(getProject());
    }
}
