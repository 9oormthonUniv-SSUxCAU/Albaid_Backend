package Albaid.backend.domain.member.application;

import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.member.repository.MemberRepository;
import Albaid.backend.global.response.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String providerId = authentication.getName();

        Member member = memberRepository.findByProviderId(providerId);
        if (member == null) {
            throw new CustomException(NOT_FOUND_RESOURCE, "Member not found");
        }

        return member;
    }
}
