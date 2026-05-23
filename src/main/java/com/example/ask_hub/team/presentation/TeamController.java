package com.example.ask_hub.team.presentation;

import com.example.ask_hub.global.domain.CommonResponse;
import com.example.ask_hub.team.application.SessionService;
import com.example.ask_hub.team.application.TeamService;
import com.example.ask_hub.team.domain.dto.request.ConventionGithubRequest;
import com.example.ask_hub.team.domain.dto.request.SessionCreateRequest;
import com.example.ask_hub.team.domain.dto.response.TeamCreateResponse;
import com.example.ask_hub.team.domain.dto.response.TeamDownloadResponse;
import com.example.ask_hub.team.domain.dto.response.TeamGetResponse;
import com.example.ask_hub.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "프로젝트 팀 도메인")
public class TeamController {

    private final TeamService teamService;
    private final SessionService sessionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새 팀 생성")
    public ResponseEntity<CommonResponse<TeamCreateResponse>> create(
            @RequestParam @NotNull(message = "팀 이름은 필수입니다.")
            String name,

            @RequestParam(required = false)
            List<Long> userIds,

            @RequestParam(value = "multipartFileList", required = false)
            List<MultipartFile> multipartFileList,

            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok(teamService.create(name, userIds, multipartFileList, user.getId())));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "세션 내부 정보 조회(팀 별로 공통됨)")
    public ResponseEntity<CommonResponse<TeamGetResponse>> get(
            @PathVariable Long teamId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(teamService.get(teamId)));
    }

    @PostMapping("/sessions")
    @Operation(summary = "참여자 초대(세션 생성)")
    public ResponseEntity<CommonResponse<Void>> invite(
            @RequestBody SessionCreateRequest request
    ) {
        sessionService.invite(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok());
    }

    @PostMapping(path = "/{teamId}/conventions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "컨벤션 파일 추가(방장만)")
    public ResponseEntity<CommonResponse<Void>> addConvention(
            @RequestParam List<MultipartFile> files,
            @PathVariable Long teamId,
            @AuthenticationPrincipal User user
    ){
        teamService.addConvention(files, teamId, user.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok());
    }

    @PostMapping(path = "/{teamId}/conventions/github", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "컨벤션 파일 추가(방장만) - github repository")
    public ResponseEntity<CommonResponse<Void>> addConvention(
            @RequestBody ConventionGithubRequest request,
            @PathVariable Long teamId,
            @AuthenticationPrincipal User user
    ){
        teamService.addConvention(request, teamId, user.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok());
    }

    @GetMapping("/conventions/{conventionId}")
    @Operation(summary = "컨벤션 파일 다운로드")
    public ResponseEntity<CommonResponse<TeamDownloadResponse>> download(
            @PathVariable Long conventionId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok(teamService.download(conventionId)));
    }

    @DeleteMapping("/{teamId}/conventions/{conventionId}")
    @Operation(summary = "컨벤션 파일 삭제(방장만)")
    public ResponseEntity<CommonResponse<Void>> deleteConvention(
            @PathVariable Long teamId,
            @PathVariable Long conventionId,
            @AuthenticationPrincipal User user
    ) {
        teamService.deleteConvention(teamId, conventionId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.ok());
    }

}
