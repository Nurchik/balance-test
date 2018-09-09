package kg.balance.test.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.balance.test.dto.BaseResponse;
import kg.balance.test.dto.Result;
import kg.balance.test.exceptions.CompanyNotFound;
import kg.balance.test.exceptions.SellPointNotFound;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.User;
import kg.balance.test.security.UserPrincipal;
import kg.balance.test.services.SellPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import kg.balance.test.models.SellPoint;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sellpoints")
public class SellPointController {

    @Autowired
    SellPointService sellPointService;

    @GetMapping("/")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> getSellPoints (@RequestParam(name = "company_id", required = false) Long company_id) {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isAdmin = up.getUser().getIsAdmin();
        Long userId = null;
        if (!isAdmin) {
            userId = up.getUser().getId();
        }
        // Админ может видеть все точки продаж, а агент - только свои. Также, если задан company_id, то фильтруем точки только этой компании
        List<SellPoint> sellPointsList = sellPointService.getSellPoints(userId, company_id);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            @JsonProperty("sellpoints")
            public List<SellPoint> sellPoints = sellPointsList;
        }));
    }

    @GetMapping("/{sellpoint_id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> getSellPoint (@PathVariable Long sellpoint_id) throws SellPointNotFound, AccessDeniedException {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SellPoint sellPointData = sellPointService.getSellPoint(sellpoint_id);
        // Если данная точка продаж не принадлежит данному пользователю, то ничего не показываем
        if (!up.getUser().getId().equals(sellPointData.getUserId())) {
            throw new AccessDeniedException("Cannot access to foreign sell point");
        }
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            @JsonProperty("sellpoint")
            public SellPoint sellPoint = sellPointData;
        }));
    }

    @PostMapping("/")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> createSellPoint (@Valid @RequestBody SellPoint sellPointData) throws UserNotFound, CompanyNotFound {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = up.getUser();
        SellPoint newSellPoint = sellPointService.createSellPoint(currentUser, sellPointData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            @JsonProperty("sellpoint")
            public SellPoint sellPoint = newSellPoint;
        }));
    }

    @PutMapping("/{sellpoint_id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> editSellPoint (@PathVariable Long sellpoint_id, @RequestBody SellPoint sellPointData) throws UserNotFound, SellPointNotFound {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SellPoint updatedSellPoint = sellPointService.updateSellPoint(up.getUser(), sellpoint_id, sellPointData);
        return ResponseEntity.ok(new BaseResponse("ok", null, new Result() {
            @JsonProperty("sellpoint")
            public SellPoint sellPoint = updatedSellPoint;
        }));
    }

    @DeleteMapping("/{sellpoint_id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteSellPoint (@PathVariable Long sellpoint_id) throws AccessDeniedException, UserNotFound, SellPointNotFound, CompanyNotFound {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sellPointService.deleteSellPoint(up.getUser().getId(), sellpoint_id);
        return ResponseEntity.ok(new BaseResponse("ok", null));
    }
}
