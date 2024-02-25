package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
	public static final String DDMMYYYY="dd/MM/yyyy";
	public static final String YYYYMMDD="yyyy-MM-dd";

	// Method to parse date from string
	public static Date parseDate(String dateString) {
		try {
			return new SimpleDateFormat(DDMMYYYY).parse(dateString);
		} catch (ParseException e) {
			System.err.print(Constants.DETAILS_ERROR);
			System.exit(0);
			return null;
		}
	}
	
	//Method to format date 
	public static String formatDate(Date date) {
		try {
			return new SimpleDateFormat(DDMMYYYY).format(date);
		} catch (Exception e) {
			System.err.print(Constants.DETAILS_ERROR);
			System.exit(0);
			return null;
		}
	}
    // method to convert java.util.date date to java.sql.Date date
	public static java.sql.Date toSQLDate(java.util.Date date) {
		if (date == null) {
			return null;
		}
		return new java.sql.Date(date.getTime());
	}
	// method to convert java.sql.Date  date to  java.util.date date
	public static java.util.Date toUtilDate(java.sql.Date date) {
		if (date == null) {
			return null;
		}
		return new java.util.Date(date.getTime());
	}
	
	public static java.sql.Date toSQLDate(String s){
		java.util.Date d = parseDate(s);
		return toSQLDate(d);
		}
	
	/**
	 * calculate the number of days between 2 dates
	 */
	public static long getDifferenceDays(Date d1, Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	
	/**
	 * calculate the number of days in given month and year
	 */
	public static int getNumberOfDaysInMonth(int year,int month){
	YearMonth yearMonthObject1 = YearMonth.of(year,month);
	 int daysInMonth = yearMonthObject1.lengthOfMonth();
	 return daysInMonth;
	}
	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	public static LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
	    return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
	}
	public static Date convertLocalDateToUtil(LocalDate currentMonth){
		if (currentMonth == null) {
			return null;
		}
		
			try {
				return new SimpleDateFormat(YYYYMMDD).parse(currentMonth+"");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.err.print(Constants.DETAILS_ERROR);
				System.exit(0);
			}
		return null;
	}
	/**
	 *Returns true if given dates are in same month
	 */
    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    /**
	 *Returns the number of days in given Month
	 */
    public static int getNumberOfDaysInMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    /**
	 *Returns the date after one month from given Date
	 */
    public static Date addMonths(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }
    public static long noOfMonthsBtwGivenDates(Date date1,Date date2){
    DateFormat formatter = new SimpleDateFormat(YYYYMMDD);
	String startDate = formatter.format(date1);
	String endDate = formatter.format(date2);
	long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(startDate)),
			YearMonth.from(LocalDate.parse(endDate)));
	return monthsBetween;
    }

    
}
