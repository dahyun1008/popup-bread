package com.bread.popupbread.domain.popup.controller;

import com.bread.popupbread.common.api.ApiResponse;
import com.bread.popupbread.domain.popup.dto.*;
import com.bread.popupbread.domain.popup.service.PopupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {
    private final PopupService popupService;

    @PostMapping
    public ResponseEntity<ApiResponse<PopupCreateResult>> create(
            @Valid @RequestBody PopupCreateRequest req) {

        PopupCreateResult result = popupService.create(req);

        URI location = URI.create("/popups/" + result.getPopupId());

        return ResponseEntity
                .created(location)
                .body(ApiResponse.success("팝업 등록에 성공했습니다.", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PopupSummary>>> get(
        @Valid @ModelAttribute PopupListRequest req) {
        PopupListResult result = popupService.getPopups(req);

        return ResponseEntity
                .ok(ApiResponse.successWithMeta("팝업 목록 조회에 성공했습니다.",
                        result.getData(),
                        result.getMeta()));
    }
}
