package com.timecapsule.memberservice.controller;

import com.timecapsule.memberservice.api.response.*;
import com.timecapsule.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/mypage/{memberId}")
    public SuccessRes<MypageRes> getMyCapsule(@PathVariable("memberId") int memberId) {
        return memberService.getMypage(memberId);
    }

    @GetMapping("/friend/{memberId}")
    public SuccessRes<List<FriendRes>> getFriend(@PathVariable("memberId") int memberId) {
        return memberService.getFriendList(memberId);
    }

    @PostMapping("/request/{fromMemberId}/{toMemberId}")
    public SuccessRes<Integer> requestFriend(@PathVariable("fromMemberId") int fromMemberId,
                                   @PathVariable("toMemberId") int toMemberId) {
        return memberService.requestFriend(fromMemberId, toMemberId);
    }

    @DeleteMapping("/request/cancel/{friendId}")
    public CommonRes cancelRequest(@PathVariable("friendId") int friendId) {
        return memberService.cancelRequest(friendId);
    }

    @DeleteMapping("/request/refuse/{friendId}")
    public CommonRes refuseRequest(@PathVariable("friendId") int friendId) {
        return memberService.refuseRequest(friendId);
    }

    @PatchMapping("/request/accept/{friendId}")
    public CommonRes acceptRequest(@PathVariable("friendId") int friendId) {
        return memberService.acceptRequest(friendId);
    }

    @GetMapping("/request/list/{memberId}")
    public SuccessRes<List<RequestRes>> getRequestList(@PathVariable("memberId") int memberId) {
        return memberService.getRequestList(memberId);
    }
}
