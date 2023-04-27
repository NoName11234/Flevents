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
}

export default new DatetimeService(VLocaleProvider.locale);
