package com.timecapsule.memberservice.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RequestRes {
    private int friendId;
    private int memberId;
    private String nickname;
    private String profileImageUrl;

    @Builder
    public RequestRes(int friendId, int memberId, String nickname, String profileImageUrl) {
        this.friendId = friendId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}