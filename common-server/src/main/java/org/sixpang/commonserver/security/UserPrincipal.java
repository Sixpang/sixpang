package org.sixpang.commonserver.security;

/**로그인한 사용자 정보를 담는 객체**/
//토큰 처리는 게이트웨이 에서 하고 서비스에는 userid , role 만 전달한다
public class UserPrincipal {

    private final Long userId;
    private final String role;

    public UserPrincipal(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
