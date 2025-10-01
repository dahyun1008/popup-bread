package com.bread.popupbread.domain.popup.controller;

import com.bread.popupbread.domain.popup.dto.PopupDetail;
import com.bread.popupbread.domain.popup.dto.PopupListRequest;
import com.bread.popupbread.domain.popup.dto.PopupListResult;
import com.bread.popupbread.domain.popup.dto.PopupSummary;
import com.bread.popupbread.domain.popup.service.PopupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/popups")
public class PopupPageController {
    @Value("${popups.list.page-size}")
    private int pageSize;
    private final PopupService popupService;

    public PopupPageController(PopupService popupService) {
        this.popupService = popupService;
    }

    @GetMapping
    public String listPage(Model model) {
        PopupListRequest req = PopupListRequest.builder()
                .limit(pageSize)
                .build();
        PopupListResult result = popupService.getPopups(req);

        model.addAttribute("initialPopups", result.getData());
        model.addAttribute("paging", result.getMeta().getPaging());
        return "popups/list";
    }

    @GetMapping("/new")
    public String newPage(Model model) {
        return "popups/new";
    }

    @GetMapping("/{id}")
    public String detailPage(Model model, @PathVariable Long id) {
        PopupDetail popupDetail = popupService.getPopupDetail(id);
        model.addAttribute("popupInfo", popupDetail);
        return "popups/detail"; // 목업
    }
}
