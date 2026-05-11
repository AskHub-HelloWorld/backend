package com.example.ask_hub.team.application;

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

    public Long create(String name, List<Long> userIds, List<MultipartFile> multipartFileList, Long userId) {

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


        sessionRepository.save(Session.builder()
                .user(captain)
                .team(team)
                .uuid(aiServerClient.createSession(captain.getId(), team.getId(), team.getName()).getSessionId())
                .build());


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

        return team.getId();
    }


    public TeamGetResponse get(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

        List<Convention> conventionList = conventionRepository.findAllByTeamId(teamId);
        List<TeamGetResponse.ConventionGetResponse> conventions = new ArrayList<>();
        for (Convention convention : conventionList) {
            conventions.add(new TeamGetResponse.ConventionGetResponse(convention.getId(), convention.getFileUrl()));
        }

        List<Session> sessionList = sessionRepository.findAllByTeamId(teamId);
        List<String> userNameList = new ArrayList<>();
        for (Session session : sessionList) {
            userNameList.add(session.getUser().getName());
        }

        return TeamGetResponse.from(team, conventions, userNameList, team.getUser().getName());
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

        for (Session session : sessionList) {
            aiServerClient.uploadFile(
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
}
