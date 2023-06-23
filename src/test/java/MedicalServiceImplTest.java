import org.junit.jupiter.api.Test;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MedicalServiceImplTest {
    HealthInfo health1 = new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)); //это якобы записно в файле
    PatientInfo patient2 = mock(PatientInfo.class);
    PatientInfoFileRepository pIFRep = mock(PatientInfoFileRepository.class);
//    SendAlertService aServ = new SendAlertServiceImpl();
    SendAlertService aServ1 = mock(SendAlertServiceImpl.class);
    MedicalService mService = new MedicalServiceImpl(pIFRep, aServ1);
    String id1;
    BloodPressure currentPressure = new BloodPressure(60, 120); //это у реального человека
    BloodPressure currentPressure2 = new BloodPressure(120, 80);
    BigDecimal currentTemperature = new BigDecimal("33.0"); //если пониженная температура, то должно быть сообщение
    BigDecimal currentTemperature2 = new BigDecimal("40.0");//если повышенная, то не должно быть сообщения

    MedicalServiceImplTest() {
        when(patient2.getId()).thenReturn("14e5fd78-f981-4cb4-ad0c-3bf0aff881db");
        when(patient2.getHealthInfo()).thenReturn(health1);

        when(pIFRep.add(any())).thenReturn("14e5fd78-f981-4cb4-ad0c-3bf0aff881db");
        when(pIFRep.getById(any())).thenReturn(patient2);
        id1 = pIFRep.add(any());
    }

    @Test
    public void test1() { //давление сообщение выводится
        mService.checkBloodPressure(id1, currentPressure);  //здесь сравнивает реального человека и файл
        verify(aServ1).send(any());
    }
    @Test
    public void test2() { //температура сообщение выводится
        //проверка температуры
        mService.checkTemperature(id1, currentTemperature);
        verify(aServ1).send(any());
    }
    @Test
    public void test3() { //давление и температура сообщение не выводится
        mService.checkBloodPressure(id1, currentPressure2);
        mService.checkTemperature(id1, currentTemperature2);
//        verify(aServ1).send(any());
        verifyNoInteractions(aServ1);
    }
}
