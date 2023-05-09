import {VLocaleProvider} from "vuetify/components";


class DatetimeService {
  locale = "DE-de";
  constructor(locale: string) {
    this.locale = locale;
  }
  getDate(date: Date) {
    return date.toLocaleDateString(this.locale, {
      dateStyle: 'medium'
    });
  }
  getTime(date: Date) {
    return date.toLocaleTimeString(this.locale, {
      timeStyle: 'short'
    });
  }
  getDateTime(date: Date) {
    return date.toLocaleString(this.locale, {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  }

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
      return start.toLocaleString("DE-de", long)
        + " - "
        + end.toLocaleString("DE-de", short);
    }
    return start.toLocaleString("DE-de", long)
      + " - "
      + end.toLocaleString("DE-de", long);
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
