package com.example.ask_hub.user.application;

import com.example.ask_hub.global.exception.BusinessException;
import com.example.ask_hub.global.exception.ErrorCode;
import com.example.ask_hub.post.infrastructure.CommentRepository;
import com.example.ask_hub.post.infrastructure.PostRepository;
import com.example.ask_hub.user.domain.dto.request.UserModifyRequest;
import com.example.ask_hub.user.domain.dto.response.MyPageResponse;
import com.example.ask_hub.user.domain.dto.response.UserSearchResponse;
import com.example.ask_hub.user.domain.dto.response.UserSummaryResponse;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.infrastructure.PointHistoryRepository;
import com.example.ask_hub.user.infrastructure.RefreshTokenRepository;
import com.example.ask_hub.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public MyPageResponse myPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Integer postCount = postRepository.countByUserId(userId);
        Integer commentCount = postRepository.countByUserId(userId);

        List<MyPageResponse.MyPost> myPosts = postRepository.findTop3ByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(post -> MyPageResponse.MyPost.from(post, commentRepository.countByPostId(post.getId())))
                .toList();

        List<MyPageResponse.MyPointHistory> myPointHistories = pointHistoryRepository.findRecentByUserId(user.getId(), LocalDateTime.now().minusWeeks(1))
                .stream()
                .map(MyPageResponse.MyPointHistory::from)
                .toList();


        return MyPageResponse.builder()
                .name(user.getName())
                .company(user.getCompany())
                .position(user.getPosition())
                .email(user.getEmail())
                .joinedDate(user.getJoinedDate())

                .postCount(postCount)
                .myCommentCount(commentCount)
                .point(user.getPoint())

                .myPostList(myPosts)
                .myPointHistory(myPointHistories)
                .build();
    }

    public void signout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        user.signout();
    }

    public UserSearchResponse search(String company) {

        List<User> userList = userRepository.findAllByCompany(company);

        List<UserSearchResponse.UserNamePosition> userNamePositions = new ArrayList<>();

        for (User user : userList) {
            userNamePositions.add(UserSearchResponse.UserNamePosition.from(user.getId(), user.getName(), user.getPosition()));
        }

        return new UserSearchResponse(userNamePositions);
    }

    public void modify(UserModifyRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.modifyInfo(
                request.name(),
                request.company(),
                request.position(),
                request.email(),
                request.joinedDate()
        );

    }

    public UserSummaryResponse summary(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Integer postCount = postRepository.countByUserId(userId);
        Integer commentCount = postRepository.countByUserId(userId);

        return new UserSummaryResponse(postCount, commentCount, user.getPoint());
    }
}
