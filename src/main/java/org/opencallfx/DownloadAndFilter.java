package org.opencallfx;

import com.monitorjbl.xlsx.StreamingReader;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class DownloadAndFilter extends Service<XSSFWorkbook> {

    private String regionToFilter;
    private    ArrayList<String> countriesToFilter;
    private ArrayList<String> branchesToFilter;

    private  String reportTimeEST; //0
    private  String region; // 2
    private  String country; // 4
    private String branch; // 7
    private  String territory; // 8
    private  String csrCode; //9
    private  String workOrderNumber; //12
    private  String ageBucket; // 13
    private  String woType; // 24
    private  String masterCustomerNumber; // 26
    private  String customer; // 27
    private String createDateAndTime; // 32
    private String lob; // 50
    private  String estimated; // 53
    private  String loca; // 54
    private   String withinSLA; //55
    private  String thirdParty; // 56
    private  String edw; // 63
    private  String badLoca; //64


//setting the region to filter String and cloning countries and branches lists. Lists are being cloned to avoid null point ex.
//original lists content can be altered by interacting with gui while the task runs
    public void setter(String regionToFilter, ArrayList<String> countriesToFilter, ArrayList<String> branchesToFilter) {
        this.regionToFilter = regionToFilter;
        this.countriesToFilter = new ArrayList<>();
        this.branchesToFilter = new ArrayList<>();

        Iterator<String> iterator = countriesToFilter.iterator();
        while (iterator.hasNext()) {
            this.countriesToFilter.add(iterator.next());
        }
        iterator = branchesToFilter.iterator();
        while (iterator.hasNext()) {
            this.branchesToFilter.add(iterator.next());
        }
    }




    @Override
    protected Task<XSSFWorkbook> createTask() {
        return new Task<XSSFWorkbook>() {
            @Override
            protected XSSFWorkbook call() throws Exception {

/* initialization of FilteringLists class,
 class constructor can trow an exception if ini file is missing or file structure is corrupted
 so the call method trows an exception because it cannot instantiate the class  */

                FilteringLists lists = new FilteringLists();

//setting the output sheet name according to region/countries/branches

                String sheetName = "";
                switch (regionToFilter) {
                    case ("WCS - North America"): {
                        if (countriesToFilter.get(0) == "USA") {
                            for (int i = 0; i < branchesToFilter.size(); i++) {
                                sheetName += ((i == 0) ? branchesToFilter.get(i) : ", " + branchesToFilter.get(i));
                            }
                        } else {
                            sheetName = "Canada";
                        }
                        break;
                    }
                    case ("WCS - Europe"): {
                            for (int i = 0; i < countriesToFilter.size(); i++) {
                                sheetName += ((i == 0) ? countriesToFilter.get(i) : ", " + countriesToFilter.get(i));
                            }

                        break;
                    }
                    case ("WCS - Asia Pacific"): { sheetName = "APAC"; break; }
                }
/* initialization and opening of the url connection
this part is trowing an exception if url string is invalid or downloading/opening fails */
                URL url = new URL(lists.getUrlString());
                URLConnection urlConnection = url.openConnection();

//initializing streaming workbook from urlConnection stream and getting the required sheet
                Workbook inputWorkbook = StreamingReader.builder().rowCacheSize(10).bufferSize(4096).open(urlConnection.getInputStream());
                Sheet inputSheet = inputWorkbook.getSheet("Data_SR_Level");

//initializing output workbook to return and creating the sheet
                XSSFWorkbook generatedWorkbookOut = new XSSFWorkbook();
                XSSFSheet sheetOut = generatedWorkbookOut.createSheet(sheetName);

//Creating head row and styling for it
                XSSFCellStyle headerStyle = generatedWorkbookOut.createCellStyle();
                Font headerFont = generatedWorkbookOut.createFont();
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setFont(headerFont);

                Row headRow = headRow(sheetOut, headerStyle);

/* iterating through rows of the input workbook and fetches specific cell in a row,
shouldn't trow an exception as all cells are being fetched with CRATE_NULL_AS_BLANK policy
checks the cell against content of filtering  lists. if all conditions are met, row is being written in the output sheet, if not, iteration continues
this is the most time consuming part of the application as the input workbook contains 300-500k of rows,
condition checking uses nested switch and ifs which contributes to higher BigO. Still trying to improve on this one... :( */
                int count = 1;
                for (Row rowIn : inputSheet) {
                    reportTimeEST = rowIn.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    region = rowIn.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    country = rowIn.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    branch = rowIn.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    territory = rowIn.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    csrCode = rowIn.getCell(9, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    workOrderNumber = rowIn.getCell(12, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    ageBucket = rowIn.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    woType = rowIn.getCell(24, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    masterCustomerNumber = rowIn.getCell(26, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    customer = rowIn.getCell(27, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    createDateAndTime = rowIn.getCell(32, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    lob = rowIn.getCell(50, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    estimated = rowIn.getCell(53, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    loca = rowIn.getCell(54, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    withinSLA = rowIn.getCell(55, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    thirdParty = rowIn.getCell(56, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    edw = rowIn.getCell(63, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    badLoca = rowIn.getCell(64, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    if (!region.equals(regionToFilter) || !countriesToFilter.contains(country) || !lists.getAgeBucket().contains(ageBucket)
                            || !lists.getWoTypes().contains(woType) || !lists.getLob().contains(lob) ||
                            (!woType.equals("1") && !lob.equals("FIN") && (ageBucket.equals("12-24 Hrs") || ageBucket.equals("1-2 Days")))) {
                        continue;
                    }
                    switch (regionToFilter) {
                        case ("WCS - North America") : {
                            if (countriesToFilter.get(0).equals("USA")) {
                                if (!branchesToFilter.contains(branch) || lists.getUsCustomers().contains(masterCustomerNumber)) {
                                    continue;
                                } else {
                                    writeRow(sheetOut, count++);
                                }
                            } else if (countriesToFilter.get(0).equals("Canada")) {
                                if (!lists.getCanBranches().contains(branch)) {
                                    continue;
                                } else {
                                    writeRow(sheetOut, count++);
                                }
                            }
                            break;
                        }
                        case ("WCS - Europe") : {
                            if (!countriesToFilter.contains(country) || lists.getEuCustomers().contains(masterCustomerNumber)
                                    || !thirdParty.equals("N")|| !loca.equals("Yes")) {
                                continue;
                            } else {
                                switch (country) {
                                    case ("Austria") : {
                                        if (!lists.getAustriaBranches().contains(branch))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                    case ("Belgium") : {
                                        if (!lists.getBelgiumBranches().contains(branch))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                    case ("France") : {
                                        if (!lists.getFranceBranches().contains(branch))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                    case ("Germany") : {
                                        if (!lists.getGermanyBranches().contains(branch))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                    case ("Switzerland") : {
                                        if(!lists.getSwitzerlandBranches().contains(branch))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                    case ("United Kingdom") : {
                                        if (!lists.getUkiBranches().contains(branch) || estimated.equals("On Time"))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                    case ("Spain") : {
                                        if (!lists.getSpainBranches().contains(branch) || lists.getSpainTerritory().contains(territory)
                                        || lists.getSpainCSRcode().contains(csrCode))
                                            continue;
                                        else writeRow(sheetOut, count++);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case ("WCS - Asia Pacific") : {
                            if (!lists.getApacCountries().contains(country) || lists.getApacBranches().contains(branch)
                            || lists.getApacCSRcode().contains(csrCode) || estimated.equals("On Time")
                            || !loca.equals("Yes") || !thirdParty.equals("N")) {
                                continue;
                            } else {
                                writeRow(sheetOut, count++);
                            }
                            break;
                        }
                    }
                }
                urlConnection.getInputStream().close();
                inputWorkbook.close();
                return generatedWorkbookOut;
            }
        };
    }


    private Row headRow(Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);

        Cell cell0 = row.createCell(0);
        Cell cell1 = row.createCell(1);
        Cell cell2 = row.createCell(2);
        Cell cell3 = row.createCell(3);
        Cell cell4 = row.createCell(4);
        Cell cell5 = row.createCell(5);
        Cell cell6 = row.createCell(6);
        Cell cell7 = row.createCell(7);
        Cell cell8 = row.createCell(8);
        Cell cell9 = row.createCell(9);
        Cell cell10 = row.createCell(10);
        Cell cell11 = row.createCell(11);
        Cell cell12 = row.createCell(12);
        Cell cell13 = row.createCell(13);
        Cell cell14 = row.createCell(14);
        Cell cell15 = row.createCell(15);
        Cell cell16 = row.createCell(16);

        cell0.setCellValue("ADW Extract Run Time EST");
        cell1.setCellValue("Country Name");
        cell2.setCellValue("Branch");
        cell3.setCellValue("Territory Code");
        cell4.setCellValue("CSR Code");
        cell5.setCellValue("Work Order Number");
        cell6.setCellValue("Comment");
        cell7.setCellValue("SR Age Bucket");
        cell8.setCellValue("WO Type (lcl)");
        cell9.setCellValue("Master Customer Name");
        cell10.setCellValue("Create Date and Time (lcl)");
        cell11.setCellValue("LOB");
        cell12.setCellValue("Include in LOCA");
        cell13.setCellValue("Within_SLA");
        cell14.setCellValue("Third_Party_Flag");
        cell15.setCellValue("Rptg SR Status");
        cell16.setCellValue("Bad LOCA Flag");

        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);
        cell4.setCellStyle(style);
        cell5.setCellStyle(style);
        cell6.setCellStyle(style);
        cell7.setCellStyle(style);
        cell8.setCellStyle(style);
        cell9.setCellStyle(style);
        cell10.setCellStyle(style);
        cell11.setCellStyle(style);
        cell12.setCellStyle(style);
        cell13.setCellStyle(style);
        cell14.setCellStyle(style);
        cell15.setCellStyle(style);
        cell16.setCellStyle(style);

        return row;
    }

    private void writeRow(Sheet sheetOut, int count) {
        Row rowOut = sheetOut.createRow(count);

        Cell cell0 = rowOut.createCell(0);
        Cell cell1 = rowOut.createCell(1);
        Cell cell2 = rowOut.createCell(2);
        Cell cell3 = rowOut.createCell(3);
        Cell cell4 = rowOut.createCell(4);
        Cell cell5 = rowOut.createCell(5);
        Cell cell6 = rowOut.createCell(6);
        Cell cell7 = rowOut.createCell(7);
        Cell cell8 = rowOut.createCell(8);
        Cell cell9 = rowOut.createCell(9);
        Cell cell10 = rowOut.createCell(10);
        Cell cell11 = rowOut.createCell(11);
        Cell cell12 = rowOut.createCell(12);
        Cell cell13 = rowOut.createCell(13);
        Cell cell14 = rowOut.createCell(14);
        Cell cell15 = rowOut.createCell(15);
        Cell cell16 = rowOut.createCell(16);

        cell0.setCellValue(reportTimeEST);
        cell1.setCellValue(country);
        cell2.setCellValue(branch);
        cell3.setCellValue(territory);
        cell4.setCellValue(csrCode);
        cell5.setCellValue(workOrderNumber);
        cell6.setCellValue("");
        cell7.setCellValue(ageBucket);
        cell8.setCellValue(woType);
        cell9.setCellValue(customer);
        cell10.setCellValue(createDateAndTime);
        cell11.setCellValue(lob);
        cell12.setCellValue(loca);
        cell13.setCellValue(withinSLA);
        cell14.setCellValue(thirdParty);
        cell15.setCellValue(edw);
        cell16.setCellValue(badLoca);
    }

    @Override
    protected void succeeded() {
        reset();
    }

    @Override
    protected void failed() {
        reset();
    }

}
