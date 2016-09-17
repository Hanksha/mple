package com.hanksha.mple.exception

class ProjectPermissionDeniedException extends Exception {

    ProjectPermissionDeniedException(String projectName) {
        super("$projectName could not be deleted, only the owner of the project or an admin can delete a project")
    }

}
