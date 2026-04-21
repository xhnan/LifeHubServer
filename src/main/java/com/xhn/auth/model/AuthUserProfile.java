package com.xhn.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserProfile {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
}
