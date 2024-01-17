package Team.project.itda.Service;

import Team.project.itda.DTO.PayDTO;
import Team.project.itda.Entity.PayEntity;
import Team.project.itda.Repository.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PayRepository payRepository;


    // AccountService.java
    public void savePay(PayDTO payDTO) {
        Long depositMoney = (payDTO.getDepositMoney() != null) ? payDTO.getDepositMoney() : 0;
        Long withdrawMoney = (payDTO.getWithdrawMoney() != null) ? payDTO.getWithdrawMoney() : 0;
        PayEntity lastPayEntity = payRepository.findFirstByOrderByIdDesc();
        Long lastTotalMoney = (lastPayEntity != null) ? lastPayEntity.getTotalMoney() : 0;
        Long totalMoney = lastTotalMoney + depositMoney - withdrawMoney;

        PayEntity payEntity = new PayEntity(
                payDTO.getId(),
                depositMoney,
                payDTO.getDepositDetails(),
                payDTO.getDepositTime(),
                withdrawMoney,
                payDTO.getWithdrawDetails(),
                payDTO.getWithdrawTime(),
                totalMoney
        );
        payRepository.save(payEntity);
    }


    public Page<PayDTO> getPay(Pageable pageable) { //계좌 불러오기
        Page<PayEntity> accountEntityPage = payRepository.findAll(pageable);
        return accountEntityPage.map(PayDTO::toPay);
    }


    public void deletePay(Long id) { //계좌 내역 삭제
        payRepository.deleteById(id);
    }

    //Api 설계를 위한 Service
    public Long saveApiPay(PayDTO payDTO) {
        PayEntity lastPayEntity = payRepository.findFirstByOrderByIdDesc();
        Long lastTotalMoney = (lastPayEntity != null) ? lastPayEntity.getTotalMoney() : 0;
        Long totalMoney = lastTotalMoney + payDTO.getDepositMoney() - payDTO.getWithdrawMoney();

        PayEntity payEntity = new PayEntity(
                payDTO.getId(),
                payDTO.getDepositMoney(),
                payDTO.getDepositDetails(),
                payDTO.getDepositTime(),
                payDTO.getWithdrawMoney(),
                payDTO.getWithdrawDetails(),
                payDTO.getWithdrawTime(),
                totalMoney
        );
        payRepository.save(payEntity);
        return payEntity.getId();
    }

    public List<PayDTO> getApiPay() {    //계좌 불러오기
        List<PayEntity> payEntity = payRepository.findAll();
        return payEntity.stream().map(PayDTO::toPay).collect(Collectors.toList());
    }
}