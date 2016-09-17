package com.hanksha.mple.data.model

import javax.validation.constraints.NotNull

class LoginRequest {

    @NotNull
    String username

    @NotNull
    String password

}
