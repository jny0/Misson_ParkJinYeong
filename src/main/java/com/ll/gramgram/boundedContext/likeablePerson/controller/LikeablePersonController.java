package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = likeablePersonService.findByFromInstaMemberId(instaMember.getId());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }


    @GetMapping("/delete/{id}")
    public String likeablePersonDelete(@PathVariable("id") Long id) {
        InstaMember instaMember = rq.getMember().getInstaMember();
        LikeablePerson likeablePerson = likeablePersonService.findById(id).orElse(null);

        if(likeablePerson == null) {
            return rq.historyBack("이미 삭제된 항목입니다");
        }

        // 삭제 항목 소유권 체크
        // 현재 로그인한 회원의 인스타 정보와 삭제하려는 상대를 등록한 회원의 인스타 정보가 같지 않으면 오류
        // Objects.equals 객체의 주소를 비교
        if (!Objects.equals(likeablePerson.getFromInstaMember().getId(), instaMember.getId())) {
            return rq.historyBack("삭제 권한이 없습니다.");
        }

        RsData<LikeablePerson> deleteRsData = likeablePersonService.delete(likeablePerson);

        if(deleteRsData.isFail()) return rq.historyBack((deleteRsData));

        return rq.redirectWithMsg("/likeablePerson/list", deleteRsData);
    }
}
