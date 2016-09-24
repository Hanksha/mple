package com.hanksha.mple.data.model.request

import groovy.transform.Canonical

@Canonical
class ChangePasswordRequest {

    String oldPassword
    String newPassword
    String newPasswordConfirmed

}
