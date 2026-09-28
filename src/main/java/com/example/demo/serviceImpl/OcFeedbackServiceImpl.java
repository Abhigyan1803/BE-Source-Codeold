package com.example.demo.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.model.AuthTable;
import com.example.demo.model.Cadet;
import com.example.demo.model.OcFeedback;
import com.example.demo.payload.OcFeedbackRequest;
import com.example.demo.repository.AdminCadetRepo;
import com.example.demo.repository.LoginRepository;
import com.example.demo.repository.OcFeedbackRepo;
import com.example.demo.service.OcFeedbackService;

@Service
public class OcFeedbackServiceImpl implements OcFeedbackService {

    private static final String[] YES_NO = {"Yes", "No"};
    private static final String[] BANKING = {"Yes", "No", "More ATMs / e-Lobbies reqd"};

    @Autowired private OcFeedbackRepo feedbackRepo;
    @Autowired private LoginRepository loginRepo;
    @Autowired private AdminCadetRepo cadetRepo;

    @Override
    public OcFeedback submit(OcFeedbackRequest r, String username) {
        validate(r);

        AuthTable auth = loginRepo.findByUsername(username);
        if (auth == null) {
            throw new IllegalArgumentException("Authenticated cadet could not be found.");
        }

        Cadet cadet = cadetRepo.findByUsernameAndStatus(username, 1);
        if (cadet == null) {
            throw new IllegalArgumentException("Cadet record could not be found.");
        }

        OcFeedback f = new OcFeedback();
        f.setSubmittedAt(new Date());
        f.setTermYear((safe(cadet.getTermSession()) + " " + safe(cadet.getYear())).trim());
        f.setLoginId(auth.getLoginId());
        f.setAcadNo(r.getAcadNo().trim());
        f.setRank(r.getRank().trim());
        f.setName(cadet.getName());
        f.setBn(cadet.getBattalian());
        f.setCoy(cadet.getCompany());
        f.setCabinNo(r.getCabinNo().trim());

        f.setFurnitureIssued(r.getFurnitureIssued());
        f.setElectricalGadgets(r.getElectricalGadgets());
        f.setElectricityHotWater(r.getElectricityHotWater());
        f.setLeakageSeepage(r.getLeakageSeepage());
        f.setMessFoodQuality(r.getMessFoodQuality());
        f.setMessMenuVariety(r.getMessMenuVariety());
        f.setMessQuantity(r.getMessQuantity());
        f.setMessStaffBehaviour(r.getMessStaffBehaviour());
        f.setMessOverall(r.getMessOverall());
        f.setKapoorQuality(r.getKapoorQuality());
        f.setKapoorPrice(r.getKapoorPrice());
        f.setKapoorStitching(r.getKapoorStitching());
        f.setMecQuality(r.getMecQuality());
        f.setMecPrice(r.getMecPrice());
        f.setMecOverall(r.getMecOverall());
        f.setOrdIssueServiceable(r.getOrdIssueServiceable());
        f.setBicycleCondition(r.getBicycleCondition());
        f.setBicycleMaintenance(r.getBicycleMaintenance());
        f.setMedicalAccess(r.getMedicalAccess());
        f.setMedicalBehaviour(r.getMedicalBehaviour());
        f.setBankingFacilities(r.getBankingFacilities());
        f.setCsdStores(r.getCsdStores());
        f.setCsdBillingCounters(r.getCsdBillingCounters());
        f.setSuggestion1(trim(r.getSuggestion1()));
        f.setSuggestion2(trim(r.getSuggestion2()));
        f.setSuggestion3(trim(r.getSuggestion3()));

        return feedbackRepo.save(f);
    }

    @Override
    public List<OcFeedback> getAll() {
        return feedbackRepo.findAllByOrderBySubmittedAtDesc();
    }

    @Override
    public byte[] exportExcel() {
        List<OcFeedback> list = getAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("OC Feedback");
            String[] headers = {
                "Submitted At", "Term/Year", "Login ID", "Acad No", "Rank", "Name", "Bn", "Coy", "Cabin No",
                "Q1.1 Furniture Issued", "Q1.2 Serviceability of electrical gadgets",
                "Q1.3 Electricity & Hot water supply", "Q1.4 Leakage/Seepage in cabin/washroom",
                "Q2.1 VB Mess - Quality of food", "Q2.2 VB Mess - Variety in Menu",
                "Q2.3 VB Mess - Quantity & Adequacy of food", "Q2.4 VB Mess - Staff Behaviour & Service attitude",
                "Q2.5 VB Mess - Overall Satisfaction with Mess functioning",
                "Q3.1 Kapoor & Co. - Quality of items", "Q3.2 Kapoor & Co. - Reasonable Price",
                "Q3.3 Kapoor & Co. - Stitching/Fitting Quality",
                "Q4.1 Mec Gear - Quality of items", "Q4.2 Mec Gear - Reasonable Price",
                "Q4.3 Mec Gear - Overall satisfaction",
                "Q5. Were all ord issue items & web eqpt issued in serviceable condition?",
                "Q6.1 Bicycles - Condition on issue", "Q6.2 Bicycles - Maintenance standards",
                "Q7.1 Medical Support - Ease of access during Trg hrs",
                "Q7.2 Medical Support - Behaviour of Medical staff",
                "Q9.1 Banking facilities", "Q9.2.1 CSD Extn Counter - Adequacy of stores",
                "Q9.2.2 CSD Extn Counter - No. of Billing Counter",
                "Q12.1 Suggestion", "Q12.2 Suggestion", "Q12.3 Suggestion"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

            int rowNum = 1;
            for (OcFeedback f : list) {
                Row row = sheet.createRow(rowNum++);
                int c = 0;
                row.createCell(c++).setCellValue(f.getSubmittedAt() == null ? "" : f.getSubmittedAt().toString());
                row.createCell(c++).setCellValue(safe(f.getTermYear()));
                row.createCell(c++).setCellValue(f.getLoginId() == null ? "" : String.valueOf(f.getLoginId()));
                row.createCell(c++).setCellValue(safe(f.getAcadNo()));
                row.createCell(c++).setCellValue(safe(f.getRank()));
                row.createCell(c++).setCellValue(safe(f.getName()));
                row.createCell(c++).setCellValue(safe(f.getBn()));
                row.createCell(c++).setCellValue(safe(f.getCoy()));
                row.createCell(c++).setCellValue(safe(f.getCabinNo()));
                c = put(row, c, f.getFurnitureIssued(), f.getElectricalGadgets(), f.getElectricityHotWater(), f.getLeakageSeepage());
                c = put(row, c, f.getMessFoodQuality(), f.getMessMenuVariety(), f.getMessQuantity(), f.getMessStaffBehaviour(), f.getMessOverall());
                c = put(row, c, f.getKapoorQuality(), f.getKapoorPrice(), f.getKapoorStitching());
                c = put(row, c, f.getMecQuality(), f.getMecPrice(), f.getMecOverall());
                row.createCell(c++).setCellValue(safe(f.getOrdIssueServiceable()));
                c = put(row, c, f.getBicycleCondition(), f.getBicycleMaintenance());
                c = put(row, c, f.getMedicalAccess(), f.getMedicalBehaviour());
                row.createCell(c++).setCellValue(safe(f.getBankingFacilities()));
                c = put(row, c, f.getCsdStores(), f.getCsdBillingCounters());
                row.createCell(c++).setCellValue(safe(f.getSuggestion1()));
                row.createCell(c++).setCellValue(safe(f.getSuggestion2()));
                row.createCell(c++).setCellValue(safe(f.getSuggestion3()));
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create feedback Excel file.", e);
        }
    }

    private void validate(OcFeedbackRequest r) {
        if (r == null || !StringUtils.hasText(r.getAcadNo()) || !StringUtils.hasText(r.getRank())
                || !StringUtils.hasText(r.getCabinNo())) {
            throw new IllegalArgumentException("Acad No, Rank and Cabin No are required.");
        }
        if (!Boolean.TRUE.equals(r.getConfirmation())) {
            throw new IllegalArgumentException("Please confirm that the responses are accurate and given by you.");
        }
        Integer[] ratings = {
            r.getFurnitureIssued(), r.getElectricalGadgets(), r.getElectricityHotWater(),
            r.getMessFoodQuality(), r.getMessMenuVariety(), r.getMessQuantity(), r.getMessStaffBehaviour(), r.getMessOverall(),
            r.getKapoorQuality(), r.getKapoorPrice(), r.getKapoorStitching(),
            r.getMecQuality(), r.getMecPrice(), r.getMecOverall(),
            r.getBicycleCondition(), r.getBicycleMaintenance(), r.getMedicalAccess(), r.getMedicalBehaviour(),
            r.getCsdStores(), r.getCsdBillingCounters()
        };
        for (Integer rating : ratings) {
            if (rating == null || rating < 1 || rating > 5) {
                throw new IllegalArgumentException("All rating questions must be answered from 1 to 5.");
            }
        }
        if (!contains(YES_NO, r.getLeakageSeepage()) || !contains(YES_NO, r.getOrdIssueServiceable())) {
            throw new IllegalArgumentException("Yes/No questions must be answered.");
        }
        if (!contains(BANKING, r.getBankingFacilities())) {
            throw new IllegalArgumentException("Please select a valid banking facilities response.");
        }
        validateWords(r.getSuggestion1());
        validateWords(r.getSuggestion2());
        validateWords(r.getSuggestion3());
    }

    private void validateWords(String value) {
        if (StringUtils.hasText(value) && value.trim().split("\\s+").length > 30) {
            throw new IllegalArgumentException("Each suggestion must not exceed 30 words.");
        }
    }

    private boolean contains(String[] values, String value) {
        if (value == null) return false;
        for (String v : values) if (v.equals(value)) return true;
        return false;
    }

    private int put(Row row, int c, Object... values) {
        for (Object value : values) {
            if (value instanceof Number) row.createCell(c++).setCellValue(((Number) value).doubleValue());
            else row.createCell(c++).setCellValue(safe(value == null ? null : value.toString()));
        }
        return c;
    }

    private String trim(String value) { return value == null ? null : value.trim(); }
    private String safe(String value) { return value == null ? "" : value; }
}
