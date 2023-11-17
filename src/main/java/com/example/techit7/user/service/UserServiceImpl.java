package com.example.techit7.user.service;


import com.example.techit7.user.dto.UserRequestDto;
import com.example.techit7.user.entity.User;
import com.example.techit7.user.repository.UserRepository;
import com.example.techit7.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public void updateUserById(Long id) {

    }

    @Override
    public boolean login(UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response){

        // id, password 확인 과정
        User user = userRepository.findByLoginId(userRequestDto.getLoginId())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));

        String receivePw = userRequestDto.getPassword();

        if(receivePw != user.getPassword()){
            // 로그인 실패
            return false;
        }

        // 쿠키 및 세션 설정
        Cookie cookie = new Cookie("loginedUserId", user.getLoginId());
        cookie.setPath("/");
        response.addCookie(cookie);

        HttpSession session = request.getSession();
        session.setAttribute("loginedUserId", user.getLoginId());
        return true;
    }
    @Override
    public boolean postUser(UserRequestDto userRequestDto) {
        //닉네임 중복체크
        userRepository.findByNickname(userRequestDto.getNickName()).ifPresent(user -> {
            throw new RuntimeException();
        });

        // 아이디 중복체크
        userRepository.findByLoginId(userRequestDto.getLoginId()).ifPresent(user -> {
            throw new RuntimeException();
        });

        // 이메일 인증 (추후 추가)
        // 비밀번호 암호화 (추후 추가)

        // 저장
        User user = userRepository.save(
                User.builder()
                        .loginId(userRequestDto.getLoginId())
                        .password(userRequestDto.getPassword())
                        .nickname(userRequestDto.getNickName())
                        .email(userRequestDto.getEmail())
                        .build());

        return true;
    }
}
