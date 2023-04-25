package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @GetMapping("/like")
    public String showLike() {
        return "usr/likeablePerson/like";
    }

    @AllArgsConstructor
    @Getter
    public static class LikeForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/like")
    public String like(@Valid LikeForm likeForm) {
        RsData<LikeablePerson> rsData = likeablePersonService.like(rq.getMember(), likeForm.getUsername(), likeForm.getAttractiveTypeCode());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", rsData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            List<LikeablePerson> likeablePeople = instaMember.getFromLikeablePeople();
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }


    @DeleteMapping("/{id}")
    public String cancel(@PathVariable("id") Long id) {

        LikeablePerson likeablePerson = likeablePersonService.findById(id).orElse(null);

        RsData canCancelRsDate = likeablePersonService.canCancel(rq.getMember(), likeablePerson);

        if(canCancelRsDate.isFail()) return rq.historyBack(canCancelRsDate);

        RsData cancelRsData = likeablePersonService.cancel(likeablePerson);

        if (cancelRsData.isFail()) return rq.historyBack((cancelRsData));

        return rq.redirectWithMsg("/likeablePerson/list", cancelRsData);
    }
}
