package com.example.jobsbankbd;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DaysConvert {

    public DaysConvert() {
    }

    public String daysRemain(String dateOld){

        Date datePast,dateRecent;
        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateNew = dateFormat.format(calForDate.getTime());

        String noDays="-_-";

        long remDays;
        long diff=0;
        try {
             datePast = dateFormat.parse(dateOld);
             dateRecent = dateFormat.parse(dateNew);
             diff = (dateRecent.getTime()-datePast.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (diff>0){
            return noDays;
        }else{
            remDays=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            remDays*=-1;
            return String.valueOf(remDays);
        }

    }

//    public String numToStr(String N){
//        String res="";
//        int num= Integer.parseInt(N);
//
//        while(num!=0) {
//            int d=num%10;
//            char dx=Character.valueOf((char) d);
//            res+=cToN(dx);
//            num/=10;
//        }
//        return res;
//    }

//    private String cToN(char dx) {
//        char dgts[]={'০','১','২','৩','৪','৫','৬','৭','৮','৯'};
//        String r="";
//        switch (dx){
//            case '0':
//                return String.valueOf(dgts[0]);
//            case '1':
//                return String.valueOf(dgts[1]);
//            case '2':
//                return String.valueOf(dgts[2]);
//            case '3':
//                return String.valueOf(dgts[3]);
//            case '4':
//                return String.valueOf(dgts[4]);
//            case '5':
//                return String.valueOf(dgts[5]);
//            case '6':
//                return String.valueOf(dgts[6]);
//            case '7':
//                return String.valueOf(dgts[7]);
//            case '8':
//                return String.valueOf(dgts[8]);
//            case '9':
//                return String.valueOf(dgts[9]);
//            default:
//        }
//      return r;
//    }
////			StringBuilder sBuilder=new StringBuilder(reString+ds);
////			reString=sBuilder.toString();

}
