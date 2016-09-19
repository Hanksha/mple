package com.hanksha.mple.exception

class CommitNotFoundException extends Exception {

    CommitNotFoundException(String projectName, String commit) {
        super("No such a commit '$commit' in project $projectName")
    }

}
