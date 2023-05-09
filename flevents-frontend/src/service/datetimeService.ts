import {VLocaleProvider} from "vuetify/components";

class DatetimeService {

  locale = "DE-de";

  constructor(locale: string) {
    this.locale = locale;
  }

  /**
   * Gets the date part of a date as formatted string.
   * @param date the date
   */
  getDate(date: Date) {
    return date.toLocaleDateString(this.locale, {
      dateStyle: 'medium'
    });
  }

  /**
   * Gets the time part of a date as formatted string.
   * @param date the date
   */
  getTime(date: Date) {
    return date.toLocaleTimeString(this.locale, {
      timeStyle: 'short'
    });
  }

  /**
   * Gets both, date and time part, of a date as formatted string.
   * @param date the date
   */
  getDateTime(date: Date) {
    return date.toLocaleString(this.locale, {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  }

  /**
   * Formats a date string using two dates.
   * If both are on the same day, the second date's dd.MM.YYYY is omitted.
   * @param from the starting date string
   * @param to the ending date string
   */
  formatDateRange(from: any, to: any) {
    let short = {
      timeStyle: "short",
    } as Intl.DateTimeFormatOptions;
    let long = {
      dateStyle: "long",
      timeStyle: "short",
    } as Intl.DateTimeFormatOptions;
    let start = new Date(from);
    let end = new Date(to);
    if (this.isSameDay(start, end)) {
      return start.toLocaleString(this.locale, long)
        + " - "
        + end.toLocaleString(this.locale, short);
    }
    return start.toLocaleString(this.locale, long)
      + " - "
      + end.toLocaleString(this.locale, long);
  }

  /**
   * Determines whether two dates are on the same day.
   * @param a a date
   * @param b another date
   */
  isSameDay(a: Date, b: Date) {
    let sameYear = a.getFullYear() === b.getFullYear();
    let sameMonth = a.getMonth() === b.getMonth();
    let sameDay = a.getDate() === b.getDate();
    return sameYear && sameMonth && sameDay;
  }
}

/**
 * Util-class for managing date and time.
 * @author David Maier
 * @since Weekly Build 3
 */
export default new DatetimeService(VLocaleProvider.locale);
