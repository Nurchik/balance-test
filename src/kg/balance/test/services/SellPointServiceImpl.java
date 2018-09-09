package kg.balance.test.services;

import kg.balance.test.dao.BalanceDAO;
import kg.balance.test.dao.BalanceDAOImpl;
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

// Здесь мы задаем сервис со scope=SINGLETON (по-умолчанию), хотя у BalanceDAO scope=PROTOTYPE.
// Это сделано умышленно, чтобы при создании синглтон-инстанса SellPointService, у нас, для каждого из BalanceDAO<>,
// создавались разные инстансы BalanceDAO (а не синглтон инстансы, если бы мы не задали SCOPE_PROTOTYPE).
@Service
public class SellPointServiceImpl implements SellPointService {

    private BalanceDAOImpl<SellPoint> sellPointRepository;

    private BalanceDAOImpl<User> userRepository;

    private BalanceDAOImpl<Company> companyRepository;

    @Autowired
    public void setUserRepository(BalanceDAOImpl<User> userRepository) {
        this.userRepository = userRepository;
        userRepository.setEntityClass(User.class);
    }

    @Autowired
    public void setCompanyRepository(BalanceDAOImpl<Company> companyRepository) {
        this.companyRepository = companyRepository;
        companyRepository.setEntityClass(Company.class);
    }

    @Autowired
    public void setSellPointRepository(BalanceDAOImpl<SellPoint> sellPointRepository) {
        this.sellPointRepository = sellPointRepository;
        sellPointRepository.setEntityClass(SellPoint.class);
    }

    @Transactional(readOnly = true)
    public SellPoint getSellPoint(Long id) throws SellPointNotFound {
        return sellPointRepository.get(id).orElseThrow(SellPointNotFound::new);
    }

    // Пока, делаем фильтрацию на всем списке. Потом, добавим динамические критерии (Criteria)
    @Transactional(readOnly = true)
    public List<SellPoint> getSellPoints(Long userId, Long companyId) {
        List<SellPoint> sellPoints = sellPointRepository.list().orElse(new ArrayList<>());
        if (companyId != null) {
            sellPoints = sellPoints
                    .stream()
                    .filter(sellPoint -> sellPoint.getCompanyId().equals(companyId))
                    .collect(Collectors.<SellPoint>toList());
        }
        if (userId != null) {
            sellPoints = sellPoints
                    .stream()
                    .filter(sellPoint -> sellPoint.getUserId().equals(userId))
                    .collect(Collectors.<SellPoint>toList());
        }
        return sellPoints;
    }

    @Transactional
    public SellPoint createSellPoint(SellPoint sellPoint) throws UserNotFound, CompanyNotFound {
        User user = userRepository.get(sellPoint.getUserId()).orElseThrow(UserNotFound::new);
        Company company = companyRepository.get(sellPoint.getCompanyId()).orElseThrow(CompanyNotFound::new);
        sellPoint.setUser(user);
        sellPoint.setCompany(company);
        sellPointRepository.add(sellPoint);
        return sellPoint;
    }

    @Transactional
    public SellPoint updateSellPoint(Boolean isAdmin, SellPoint sellPointData) throws UserNotFound, SellPointNotFound {
        SellPoint sellPoint = sellPointRepository.get(sellPointData.getId()).orElseThrow(SellPointNotFound::new);
        sellPoint.setName(sellPointData.getName());
        sellPoint.setPhoneNumber(sellPointData.getPhoneNumber());
        sellPoint.setAddress(sellPointData.getAddress());
        sellPoint.setLatitude(sellPointData.getLatitude());
        sellPoint.setLongitude(sellPointData.getLongitude());
        // Только админ может менять владельца точки
        if (user.getIsAdmin() && sellPointData.getUserId() != null) {
            User newUser = userRepository.get(sellPointData.getUserId()).orElseThrow(UserNotFound::new);
            sellPoint.setUser(newUser);
        }
        sellPointRepository.update(sellPoint);
        return sellPoint;
    }

    @Transactional
    public void deleteSellPoint(Long userId, Long id) throws AccessDeniedException, UserNotFound, SellPointNotFound {
        SellPoint sellPoint = sellPointRepository.get(id).orElseThrow(SellPointNotFound::new);
        User user = userRepository.get(userId).orElseThrow(UserNotFound::new);
        // Только владелец точки может удалять свою точку. А админ может все!!
        if (!user.getIsAdmin() && !sellPoint.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Cannot delete foreign sell point");
        }
        sellPointRepository.delete(sellPoint);
    }
}
