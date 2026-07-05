package com.finguard.core.controller;

import com.finguard.core.dto.SpendingProfileResponse;
import com.finguard.core.dto.TransactionContextResponse;
import com.finguard.core.security.UserPrincipal;
import com.finguard.core.service.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/spending")
    public SpendingProfileResponse getSpendingProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userProfileService.getSpendingProfile(userPrincipal.getId());
    }

    @GetMapping("/transaction-context")
    public TransactionContextResponse getTransactionContext(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam BigDecimal amount,
            @RequestParam String category
    ) {
        return userProfileService.buildTransactionContext(userPrincipal.getId(), amount, category);
    }
}
