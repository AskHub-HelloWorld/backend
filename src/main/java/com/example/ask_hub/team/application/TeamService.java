package com.example.ask_hub.team.application;

import com.example.ask_hub.team.domain.dto.response.TeamCreateResponse;
import com.example.ask_hub.team.domain.dto.response.TeamDownloadResponse;
import com.example.ask_hub.team.domain.entity.Convention;
import com.example.ask_hub.team.infrastructure.ConventionRepository;
import com.example.ask_hub.global.client.AiServerClient;
import com.example.ask_hub.global.client.S3Service;
import com.example.ask_hub.global.exception.BusinessException;
import com.example.ask_hub.global.exception.ErrorCode;
import com.example.ask_hub.team.domain.dto.response.TeamGetResponse;
import com.example.ask_hub.team.domain.entity.Session;
import com.example.ask_hub.team.domain.entity.Team;
import com.example.ask_hub.team.infrastructure.SessionRepository;
import com.example.ask_hub.team.infrastructure.TeamRepository;
import com.example.ask_hub.user.domain.entity.User;
import com.example.ask_hub.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ConventionRepository conventionRepository;

    private final S3Service s3Service;
    private final AiServerClient aiServerClient;

    public TeamCreateResponse create(String name, List<Long> userIds, List<MultipartFile> multipartFileList, Long userId) {

        User captain = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Team team = Team.builder()
                .name(name)
                .user(captain)
                .build();
        teamRepository.save(team);

        for (MultipartFile file : multipartFileList) {
            String url = s3Service.upload(file);

            conventionRepository.save(Convention.builder()
                    .fileUrl(url)
                    .name(file.getOriginalFilename())
                    .team(team)
                    .build());
        }


        Long sessionId = sessionRepository.save(Session.builder()
                .user(captain)
                .team(team)
                .uuid(aiServerClient.createSession(captain.getId(), team.getId(), team.getName()).getSessionId())
                .build()).getId();


        for (Long id : userIds){
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            if (captain.getId().equals(user.getId())){
                continue;
            }

            sessionRepository.save(Session.builder()
                    .user(user)
                    .team(team)
                    .uuid(aiServerClient.createSession(user.getId(), team.getId(), team.getName()).getSessionId())
                    .build());
        }

        return new TeamCreateResponse(team.getId(), sessionId);
    }


    public TeamGetResponse get(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

        List<Convention> conventionList = conventionRepository.findAllByTeamId(teamId);
        List<Long> conventionIds= new ArrayList<>();
        for (Convention convention : conventionList) {
            conventionIds.add(convention.getId());
        }

        List<Session> sessionList = sessionRepository.findAllByTeamId(teamId);
        List<String> userNameList = new ArrayList<>();
        for (Session session : sessionList) {
            userNameList.add(session.getUser().getName());
        }

        return TeamGetResponse.from(team, conventionIds, userNameList, team.getUser().getName());
    }

    public Long addConvention(MultipartFile file, Long teamId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

        if (!team.getUser().equals(user)) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_USER);
        }

        List<Session> sessionList = sessionRepository.findAllByTeamId(teamId);

        String sourceId;
        for (Session session : sessionList) {
            sourceId = aiServerClient.uploadFile(
                    session.getUser().getId(),
                    teamId,
                    session.getUuid(),
                    file
            );
        }

        Convention convention = conventionRepository.save(Convention.builder()
                .name(file.getOriginalFilename())
                .team(team)
                .fileUrl(s3Service.upload(file))
                .build());

        return convention.getId();
    }

    public TeamDownloadResponse download(Long conventionId) {
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONVENTION_NOT_FOUND));

        return TeamDownloadResponse.from(s3Service.convertToPresignedUrl(convention.getFileUrl()));
    }

    public void deleteConvention(Long teamId, Long conventionId, Long userId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONVENTION_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Session> sessionList = sessionRepository.findAllByTeamId(teamId);

        if (!team.getUser().equals(user)) {
            throw new BusinessException(ErrorCode.NOT_ALLOWED_USER);
        }

        if (!convention.getTeam().equals(team)) {
            throw new BusinessException(ErrorCode.CONVENTION_NOT_LINKED);
        }

        s3Service.delete(convention.getFileUrl());

        for (Session session : sessionList) {
            aiServerClient.deleteFile(session.getUser().getId(), teamId, convention.getSourceId());
        }

        conventionRepository.delete(convention);
    }
}
