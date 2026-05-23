package com.example.ask_hub.team.application;

import com.example.ask_hub.global.client.dto.response.CreateSessionResponse;
import com.example.ask_hub.global.client.dto.response.SendMessageResponse;
import com.example.ask_hub.team.domain.dto.request.MessageCreateRequest;
import com.example.ask_hub.team.domain.dto.response.MessageCreateResponse;
import com.example.ask_hub.team.domain.dto.response.MessageGetResponse;
import com.example.ask_hub.team.domain.entity.Message;
import com.example.ask_hub.team.domain.enums.Role;
import com.example.ask_hub.team.infrastructure.MessageRepository;
import com.example.ask_hub.global.client.AiServerClient;
import com.example.ask_hub.global.domain.SliceResponse;
import com.example.ask_hub.global.exception.BusinessException;
import com.example.ask_hub.global.exception.ErrorCode;
import com.example.ask_hub.team.domain.dto.request.SessionCreateRequest;
import com.example.ask_hub.team.domain.dto.response.SessionGetResponse;
import com.example.ask_hub.team.domain.entity.Session;
import com.example.ask_hub.team.domain.entity.Team;
import com.example.ask_hub.team.infrastructure.SessionRepository;
import com.example.ask_hub.team.infrastructure.TeamRepository;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    private final AiServerClient aiServerClient;

    public void invite(SessionCreateRequest request) {

        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

        List<User> userList = userRepository.findAllByIdIn((request.userIds()));

        for (User user : userList) {
            if (sessionRepository.findById(user.getId()).isEmpty()) {
                continue;
            }

            CreateSessionResponse response = aiServerClient.createSession(user.getId(), team.getId(), team.getName());
            sessionRepository.save(Session.builder()
                    .team(team)
                    .user(user)
                    .uuid(response.getSessionId())
                    .build());
        }

    }

    public SliceResponse<SessionGetResponse> get(Long userId, Pageable pageable) {

        userRepository.findById(userId) // just checking existence
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Slice<Session> slice = sessionRepository.findAllByUserId(userId, pageable);

        Slice<SessionGetResponse> responses = slice.map(
                session -> SessionGetResponse.from(
                        session.getTeam().getId(),
                        session.getId(),
                        session.getTeam().getName(),
                        messageRepository.findTopBySessionIdOrderByCreatedAtDesc(session.getId())
                                .map(Message::getContent)
                                .orElse(null),
                        session.getTeam().getUser().getName()));

        return SliceResponse.from(responses);
    }

    public MessageCreateResponse sendMessage(List<MultipartFile> files, MessageCreateRequest request, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        teamRepository.findById(request.teamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

        Session session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getTeam().getId().equals(request.teamId())) {
            throw new BusinessException(ErrorCode.SESSION_UNMATCH);
        }

        SendMessageResponse response;

        if (files != null && !files.isEmpty()) {
            List<String> fileIds = new ArrayList<>();

            for (MultipartFile file : files) {
                fileIds.add(aiServerClient.uploadFile(userId, request.teamId(), null, file, "chat_attachment"));
            }

            response = aiServerClient.sendMessage(
                    userId,
                    request.teamId(),
                    session.getUuid(),
                    request.message().isBlank() ? "올려준 파일들 설명해줘" : request.message() ,
                    fileIds
            );
        } else {
            response = aiServerClient.sendMessage(
                    userId,
                    request.teamId(),
                    session.getUuid(),
                    request.message(),
                    null
            );
        }

        if (response.getAnswerable() == null ) {
            throw new BusinessException(ErrorCode.ANSWER_FAIL);
        }

        Message requestMessage = messageRepository.save(Message.builder()
                .session(session)
                .uuid(response.getUserMessageId())
                .content(request.message())
                .role(Role.ASKER)
                .build());

        Message responesMessage = messageRepository.save(Message.builder()
                .session(session)
                .uuid(response.getAssistantMessageId())
                .content(response.getAnswer())
                .role(Role.AI)
                .build());

        return MessageCreateResponse.from(requestMessage, responesMessage);
    }

    public SliceResponse<MessageGetResponse> getMessages(Long sessionId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        if (!user.equals(session.getUser())) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_USER);
        }

        Slice<Message> slice = messageRepository.findAllBySessionIdOrderByCreatedAt(sessionId);

        Slice<MessageGetResponse> responses = slice.map(
                message -> MessageGetResponse.from(message.getId(), message.getContent())
        );

        return SliceResponse.from(responses);

    }

    public void delete(Long sessionId, Long userId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_USER);
        }

        messageRepository.deleteAllBySessionId(sessionId);

        sessionRepository.delete(session);
    }

    public SliceResponse<SessionGetResponse> search(String keyword, Long userId, Pageable pageable) {

        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Slice<Session> slice = sessionRepository.findByUserIdAndTeamName(userId, keyword, pageable);

        Slice<SessionGetResponse> responses = slice.map(
                session -> SessionGetResponse.from(
                        session.getTeam().getId(),
                        session.getId(),
                        session.getTeam().getName(),
                        messageRepository.findTopBySessionIdOrderByCreatedAtDesc(session.getId())
                                .map(Message::getContent)
                                .orElse(null),
                        session.getTeam().getUser().getName()));

        return SliceResponse.from(responses);
    }
}
