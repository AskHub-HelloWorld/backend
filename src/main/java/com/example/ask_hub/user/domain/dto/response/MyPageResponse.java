package com.example.ask_hub.user.domain.dto.response;

import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.user.domain.entity.PointHistory;
import com.example.ask_hub.user.domain.enums.Position;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MyPageResponse (

        String name,
        String company,
        Position position,
        String email,
        LocalDate joinedDate,

        Integer postCount,
        Integer myCommentCount,
        Integer point,

        List<MyPost> myPostList, // size: 3
        List<MyPointHistory> myPointHistory

) {

    public record MyPost (
            String title,
            LocalDateTime createdAt,
            Integer commentCount,
            Boolean isResolved
    ){
        public static MyPost from(Post post, Integer commentCount) {
            return new MyPost(
                    post.getTitle(),
                    post.getCreatedAt(),
                    commentCount,
                    post.getIsResolved()
            );
        }
    }


    public record MyPointHistory (
            Integer point,
            LocalDateTime dateTime
    ){
        public static MyPointHistory from(PointHistory pointHistory){
            return new MyPointHistory(
                    pointHistory.getPoint(),
                    pointHistory.getCreatedAt()
            );
        }
    }
}
