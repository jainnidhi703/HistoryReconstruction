import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRUtils {
    public static final String DATE_REGEX = "\\b((jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|june?|july?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)\\s([0-9]{1,2})(,\\s([0-9]{2,4}))?)\\b";
    public static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEX, Pattern.CASE_INSENSITIVE); // Case insensitive is to match also "mar" and not only "Mar" for March


    public static String[] extractDate(String str, String title) {
        Matcher matcher = DATE_PATTERN.matcher(str);
        List<String> lst = new ArrayList<String>();
        while(matcher.find()) {
            String month = matcher.group(2);
            String date = matcher.group(12);
            String year = matcher.group(14);
            if(year == null) {
                int yrTmp = yearFromFileName(title);
                if (yrTmp != -1)
                    year = String.valueOf(yrTmp);
                else
                    year = "";
            }
            if(!date.isEmpty() && !month.isEmpty() && !year.isEmpty()) {
                String dt = dateToString(date, month, year);
                lst.add(dt);
            }
        }
        if (lst.isEmpty()) {
            if(title.endsWith(".utf8")) {
                return null;
            } else {
                String s = dateFromFileName(title);
                if(s != null)
                    lst.add(s);
            }
        }
        return lst.toArray(new String[lst.size()]);
    }

    //    en.13.3.1.2009.6.11
    public static int yearFromFileName(String str) {
        try {
            return Integer.parseInt(str.split("\\.")[4]);
        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
            return -1;
        }
    }

    //    en.13.3.1.2009.6.11
    public static String dateFromFileName(String str) {
        try {
            String[] toks = str.split("\\.");
            String date = toks[6];
            String month = toks[5];
            String year = toks[4];
            if(date.length() == 1) date = "0" + date;
            if(month.length() == 1) month = "0" + month;
            return year+month+date;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    // mmdd
    public static String dateToString(String date, String month) {
        if(date.length() == 1) date = "0"+date;
        return monthAlphaToNum(month) + date;
    }

    // yyyymmdd
    public static String dateToString(String date, String month, String year) {
        if(year == null) return dateToString(date, month);
        if(date.length() == 1) date = "0"+date;
        return year + monthAlphaToNum(month) + date;
    }

    private static String monthAlphaToNum(String str) {
        str = str.toLowerCase();
        if (str.equals("jan") || str.equals("january")) {
            return "01";
        } else if (str.equals("feb") || str.equals("february")) {
            return "02";
        } else if (str.equals("mar") || str.equals("march")) {
            return "03";
        } else if (str.equals("apr") || str.equals("april")) {
            return "04";
        } else if (str.equals("may")) {
            return "05";
        } else if (str.equals("jun") || str.equals("june")) {
            return "06";
        } else if (str.equals("jul") || str.equals("july")) {
            return "07";
        } else if (str.equals("aug") || str.equals("august")) {
            return "08";
        } else if (str.equals("sep") || str.equals("september")) {
            return "09";
        } else if (str.equals("oct") || str.equals("october")) {
            return "10";
        } else if (str.equals("nov") || str.equals("november")) {
            return "11";
        } else if (str.equals("dec") || str.equals("december")) {
            return "12";
        }
        return null;
    }

    public static String yearFromDate(String date) {
        String dt = date.substring(0,4);
        return dt;
    }
}
