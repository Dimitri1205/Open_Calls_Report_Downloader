package org.opencallfx;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CallsBarChart extends Service<BarChart<String, Number>> {

    private XSSFWorkbook workbook;

    private String name;
    private String pullDate;//0
    private String ageBucket; // 13
    private String lob; // 50
    private String edw; //63


    private int fin7, fin67, fin56, fin45, fin34, fin23, fin12, fin1224;
    private int ret7, ret67, ret56, ret45, ret34, ret23, ret12, ret1224;
    private int oth7, oth67, oth56, oth45, oth34, oth23, oth12, oth1224;
    private int finTotal, retTotal, othTotal, noRecords;
    private int total;





    public CallsBarChart() {}

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }



    @Override
    protected Task<BarChart<String, Number>> createTask() {
       return new Task<BarChart<String, Number>>() {
           @Override
           protected BarChart<String, Number> call() throws Exception {
               fin7 = fin67 = fin56 = fin45 = fin34 = fin23 = fin12 = fin1224 = 0;
               ret7 = ret67 = ret56 = ret45 = ret34 = ret23 = ret12 = ret1224 = 0;
               oth7 = oth67 = oth56 = oth45 = oth34 = oth23 = oth12 = oth1224 = 0;
               finTotal = retTotal = othTotal = noRecords = total = 0;
               Sheet sheet = workbook.getSheetAt(0);
               name = workbook.getSheetName(0);

               for (Row row: sheet) {
                   ageBucket = row.getCell(7, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                   lob = row.getCell(11, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                   edw = row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                   pullDate = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                   if (edw.equals("SR currently not in EDW")){
                       noRecords++;
                   } else {
                       if (lob.equals("FIN")) {
                           switch (ageBucket) {
                               case ("7+ Days"): { fin7++;break; }
                               case ("6-7 Days"): { fin67++;break; }
                               case ("5-6 Days"): { fin56++;break; }
                               case ("4-5 Days"): { fin45++;break; }
                               case ("3-4 Days"): { fin34++;break; }
                               case ("2-3 Days"): { fin23++;break; }
                               case ("1-2 Days"): { fin12++;break; }
                               case ("12-24 Hrs"): { fin1224++;break; }
                           }
                       } else if (lob.equals("RET")) {
                           switch (ageBucket) {
                               case ("7+ Days"): { ret7++;break; }
                               case ("6-7 Days"): { ret67++;break; }
                               case ("5-6 Days"): { ret56++;break; }
                               case ("4-5 Days"): { ret45++;break; }
                               case ("3-4 Days"): { ret34++;break; }
                               case ("2-3 Days"): { ret23++;break; }
                               case ("1-2 Days"): { ret12++;break; }
                               case ("12-24 Hrs"): { ret1224++;break; }
                           }
                       } else {
                           switch (ageBucket) {
                               case ("7+ Days"): { oth7++;break; }
                               case ("6-7 Days"): { oth67++;break; }
                               case ("5-6 Days"): { oth56++;break; }
                               case ("4-5 Days"): { oth45++;break; }
                               case ("3-4 Days"): { oth34++;break; }
                               case ("2-3 Days"): { oth23++;break; }
                               case ("1-2 Days"): { oth12++;break; }
                               case ("12-24 Hrs"): { oth1224++;break; }
                           }
                       }
                   }
               }
               finTotal = fin7 + fin67 + fin56 + fin45 + fin34 + fin23 + fin12 + fin1224;
               retTotal = ret7 + ret67 + ret56 + ret45 + ret34 + ret23 + ret12 + ret1224;
               othTotal = oth7 + oth67 + oth56 + oth45 + oth34 + oth23 + oth12 + oth1224;
               total = finTotal + retTotal + othTotal + noRecords;

               CategoryAxis hAxis = new CategoryAxis();

               NumberAxis vAxis = new NumberAxis();
               vAxis.setTickUnit(0);
               vAxis.setTickLabelFormatter(new StringConverter<Number>() {
                   @Override
                   public String toString(Number object) {
                       if(object.intValue()!=object.doubleValue())
                           return "";
                       return ""+(object.intValue());
                   }

                   @Override
                   public Number fromString(String string) {
                       Number val = Double.parseDouble(string);
                       return val.intValue();
                   }
               });



               BarChart<String, Number> chart = new BarChart<>(hAxis, vAxis);
               chart.setTitle(name + " Total calls: " + total + "\nReport date and time: " + pullDate + " EST");

               XYChart.Series<String, Number> financial = new XYChart.Series<>();
               XYChart.Series<String, Number> retail = new XYChart.Series<>();
               XYChart.Series<String, Number> other = new XYChart.Series<>();
               XYChart.Series<String, Number> records = new XYChart.Series<>();

               financial.setName("FIN: " + finTotal);
               retail.setName("RET: " + retTotal);
               other.setName("Other: " + othTotal);
               records.setName("No Records ~ " + noRecords);

               financial.getData().add(new XYChart.Data<String, Number>("7+ Days", fin7));
               financial.getData().add(new XYChart.Data<String, Number>("6-7 Days", fin67));
               financial.getData().add(new XYChart.Data<String, Number>("5-6 Days", fin56));
               financial.getData().add(new XYChart.Data<String, Number>("4-5 Days", fin45));
               financial.getData().add(new XYChart.Data<String, Number>("3-4 Days", fin34));
               financial.getData().add(new XYChart.Data<String, Number>("2-3 Days", fin23));
               financial.getData().add(new XYChart.Data<String, Number>("1-2 Days", fin12));
               financial.getData().add(new XYChart.Data<String, Number>("12-24 Hrs", fin1224));

               retail.getData().add(new XYChart.Data<String, Number>("7+ Days", ret7));
               retail.getData().add(new XYChart.Data<String, Number>("6-7 Days", ret67));
               retail.getData().add(new XYChart.Data<String, Number>("5-6 Days", ret56));
               retail.getData().add(new XYChart.Data<String, Number>("4-5 Days", ret45));
               retail.getData().add(new XYChart.Data<String, Number>("3-4 Days", ret34));
               retail.getData().add(new XYChart.Data<String, Number>("2-3 Days", ret23));
               retail.getData().add(new XYChart.Data<String, Number>("1-2 Days", ret12));
               retail.getData().add(new XYChart.Data<String, Number>("12-24 Hrs", ret1224));

               other.getData().add(new XYChart.Data<String, Number>("7+ Days", oth7));
               other.getData().add(new XYChart.Data<String, Number>("6-7 Days", oth67));
               other.getData().add(new XYChart.Data<String, Number>("5-6 Days", oth56));
               other.getData().add(new XYChart.Data<String, Number>("4-5 Days", oth45));
               other.getData().add(new XYChart.Data<String, Number>("3-4 Days", oth34));
               other.getData().add(new XYChart.Data<String, Number>("2-3 Days", oth23));
               other.getData().add(new XYChart.Data<String, Number>("1-2 Days", oth12));
               other.getData().add(new XYChart.Data<String, Number>("12-24 Hrs", oth1224));

               chart.getData().add(financial);
               chart.getData().add(retail);
               chart.getData().add(other);
               chart.getData().add(records);

               return chart;
           }
       };
    }

    @Override
    protected void succeeded() { reset(); }

    @Override
    protected void failed() { reset(); }
}
