//package com.example.Board_basic.Service;
//
//import com.example.Board_basic.Repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
//
//        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, naver 등
//        String providerId = oAuth2User.getAttribute("sub") != null
//                ? oAuth2User.getAttribute("sub") // google
//                : oAuth2User.getAttribute("id"); // naver
//        String username = provider + "_" + providerId;
//        String email = oAuth2User.getAttribute("email");
//        String nickname = oAuth2User.getAttribute("name");
//
//        Optional<User> userOptional = userRepository.findByUsername(username);
//        User user = userOptional.orElseGet(() -> userRepository.save(
//                User.builder()
//                        .username(username)
//                        .email(email)
//                        .nickname(nickname)
//                        .role(Role.USER)
//                        .password("") // 소셜 로그인은 패스워드 없음
//                        .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
//                        .build()
//        ));
//
//        return new PrincipalDetails(user, oAuth2User.getAttributes());
//    }
//}

