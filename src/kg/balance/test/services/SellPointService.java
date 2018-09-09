package kg.balance.test.services;

import kg.balance.test.dao.BalanceDAO;
import kg.balance.test.exceptions.CompanyNotFound;
import kg.balance.test.exceptions.SellPointNotFound;
import kg.balance.test.exceptions.UserNotFound;
import kg.balance.test.models.Company;
import kg.balance.test.models.SellPoint;
import kg.balance.test.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface SellPointService {
    public SellPoint getSellPoint (Long id) throws SellPointNotFound;
    public List<SellPoint> getSellPoints (Long userId, Long companyId);
    public SellPoint createSellPoint (User currentUser, SellPoint sellPoint) throws UserNotFound, CompanyNotFound;
    public SellPoint updateSellPoint (User user, Long sellPointId, SellPoint sellPoint) throws UserNotFound, SellPointNotFound;
    public void deleteSellPoint (Long userId, Long id) throws AccessDeniedException, UserNotFound, SellPointNotFound, CompanyNotFound;
}

