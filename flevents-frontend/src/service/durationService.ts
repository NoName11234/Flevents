import {Duration} from "@/models/duration";

class DurationService {

  /**
   * Creates a duration out of a number of hours.
   * @param hours the number of hours
   */
  toDuration(hours: number): Duration {
    return `PT${hours}H`;
  }

  /**
   * Extracts the number of hours out of a duration.
   * @param duration the duration
   */
  extractHours(duration: Duration) {
    try {
      return Number.parseInt(
        duration
          .replace('PT', '')
          .replace('H', '')
      );
    } catch (e) {
      return 0;
    }
  }
}

/**
 * Util-class for managing durations.
 * @author David Maier
 * @since Weekly Build 4
 */
export default new DurationService();
